package com.hw.openpad.android.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hw.openpad.android.R;
import com.hw.openpad.android.model.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ethan on 12/1/14.
 */
public class JoystickObject extends ControlObject {
    int mJoystickCircleImage, mJoystickKnobImage, mControlId;
    View joystickCircle;

    public JoystickObject(Context c) {
        super(c);
    }

    @Override
    public void load(JSONObject data, View parent, RelativeLayout layout) {
        try {
            mJoystickKnobImage = R.drawable.joystick_knob;
            Rect frame = getRekt(data.getJSONObject("frame"));
            int width = Math.min(frame.width(), frame.height());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width / 2,
                    width / 2);
            params.leftMargin = frame.left;
            params.topMargin = frame.top;


            mJoystickCircleImage = R.drawable.joystick_circle;
            joystickCircle = new View(context);
            joystickCircle.setBackground(getResources().getDrawable(mJoystickCircleImage));
            layout.addView(joystickCircle, params);

            setImage(mJoystickKnobImage);
            layout.addView(this, params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point moveTo;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // press down
                moveTo = new Point((int) event.getX(), (int) event.getY());
                NetworkManager.joinedGame.sendPadUpdate(mControlId, determineDirection(moveTo), 1);
                moveKnob(moveTo);
                return true;
            case MotionEvent.ACTION_UP: // release
                moveTo = new Point(0, 0);
                NetworkManager.joinedGame.sendPadUpdate(mControlId, moveTo, 0);
                moveKnob(moveTo);
                return true;
            case MotionEvent.ACTION_MOVE: // move finger
                moveTo = new Point((int) event.getX(), (int) event.getY());
                NetworkManager.joinedGame.sendPadUpdate(mControlId, determineDirection(moveTo), 2);
                moveKnob(moveTo);
                return true;
        }
        return false;
    }

    public Point determineDirection(Point point) {
        Point center = new Point((int) getX() + getWidth() / 2, (int) getY() + getHeight() / 2);
        return new Point((point.x - center.x) / getWidth() * 2, (point.y - center.y) / getWidth() * 2);
    }

    private void moveKnob(Point moveTo) {
        this.setX(moveTo.x);
        this.setY(moveTo.y);
    }

}
