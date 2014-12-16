package com.hw.openpad.android.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Pair;
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
    int mControlId;
    View mJoystickCircle, mJoystickKnob;
    RelativeLayout mJoystickLayout;
    Point center;

    public JoystickObject(Context c) {
        super(c);
    }

//    @Override
//    public void load(JSONObject data, View parent, RelativeLayout layout) {
//        try {
//            mJoystickKnobImage = R.drawable.joystick_knob;
//            Rect frame = getRekt(data.getJSONObject("frame"));
//            int width = Math.min(frame.width(), frame.height());
//
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
//            params.leftMargin = frame.left;
//            params.topMargin = frame.top;
//
//            mJoystickCircleImage = R.drawable.joystick_circle;
//            setImage(mJoystickCircleImage);
//            layout.addView(this, params);
//
//            mJoystickKnob = new View(context);
//            mJoystickKnob.setBackground(getResources().getDrawable(mJoystickKnobImage));
//            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(width / 2,
//                    width / 2);
//            params2.topMargin = frame.top + width / 2;
//            params2.leftMargin = frame.left + width / 2;
//            layout.addView(mJoystickKnob, params);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//

    @Override
    public void load(JSONObject data, View parent, RelativeLayout layout) {
        try {
            Rect frame = getRekt(data.getJSONObject("frame"));
            int width = Math.min(frame.width(), frame.height());


            mJoystickLayout = new RelativeLayout(context); // will add knob and circle to this layout

            setImage(R.drawable.joystick_circle); // draw the circle

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
            params.leftMargin = frame.left;
            params.topMargin = frame.top;
            mJoystickLayout.addView(this, params);

            mJoystickKnob = new View(context);
            mJoystickKnob.setBackground(getResources().getDrawable(R.drawable.joystick_knob));

            RelativeLayout.LayoutParams knobParams = new RelativeLayout.LayoutParams(width, width);
            knobParams.topMargin = frame.top;
            knobParams.leftMargin = frame.left;
            mJoystickKnob.setLayoutParams(knobParams);
            mJoystickLayout.addView(mJoystickKnob);
//            centerKnob();

            layout.addView(mJoystickLayout);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point moveTo = new Point((int) event.getX(), (int) event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // press down
                handleTouch(moveTo);
                return true;
            case MotionEvent.ACTION_UP: // release
                moveTo = new Point(mJoystickLayout.getWidth() / 2, mJoystickLayout.getHeight() / 2);
                NetworkManager.joinedGame.sendPadUpdate(mControlId, determineDirection(moveTo), 0);
                centerKnob();
                return true;
            case MotionEvent.ACTION_MOVE: // move finger
                handleTouch(moveTo);
                return true;
        }
        return false;
    }

    private void handleTouch(Point loc) {
        if (!isValidTouch(loc)) return;
        Pair<Double, Double> dir = determineDirection(loc);
        NetworkManager.joinedGame.sendPadUpdate(mControlId, dir, 1);
        System.out.println("x: " + loc.x + ", y: " + loc.y + ", left: " +
                this.getLeft() + ", top: " + this.getTop() + ", right: " + this.getRight() +
                ", bottom: " + this.getBottom());
        System.out.println("dx: " + dir.first + ", dy: " + dir.second);
        moveKnob(loc);
    }

    private Pair<Double, Double> determineDirection(Point point) {
        if (center == null)
//            center = new Point(mJoystickLayout.getWidth() * 3/8, mJoystickLayout.getWidth() * 2);
            center = new Point(getLeft() + getWidth() * 3/8, getTop() + getWidth() * 3/8);
        center = new Point(getLeft() + getWidth() / 2, getTop() + getWidth() / 2);
        return new Pair<>((double) (point.x - center.x) / (double) getWidth(),
                (double) (center.y - point.y) / (double) getWidth());
    }

//    public Pair<Double, Double> determineDirection(Point point) {
//        Point center = new Point((int) getLeft() + getWidth() / 2, (int) getTop() - getHeight() / 2);
//        double cx = (double) getLeft() + (double) (getWidth()) / 2, cy = (double) getTop() +
//                (double) getHeight() / 2;
//
//        return new Pair<Double, Double>((double) (point.x - center.x) / (double) getWidth() * 2,
//                (double) (center.y - point.y) / (double) getWidth() * 2);
//    }
//

    private void moveKnob(Point moveTo) {
        mJoystickKnob.setX(moveTo.x + center.x);
        mJoystickKnob.setY(moveTo.y + center.y + mJoystickKnob.getWidth());
    }

    private boolean isValidTouch(Point pt) {
        return pt.x >= getLeft() && pt.x <= getRight() && pt.y <= getBottom() && pt.y >= getTop();
    }

//    private Pair<Double, Double> makeLocationValid(Pair<Double, Double> ) {
//
//    }

    private void centerKnob() {
        if (center == null)
//            center = new Point(mJoystickLayout.getWidth() / 2, mJoystickLayout.getWidth() / 2);
            center = new Point(getLeft() + getWidth() * 3/8, getTop() + getWidth() * 3/8);
        moveKnob(new Point(center.x, center.y));
    }


}
