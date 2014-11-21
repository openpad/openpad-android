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
 * Created by ethan on 11/20/14.
 */
public class DpadObject extends ControlObject {
    int mDpadImageRest, mControlId;

    public DpadObject(Context c) {
        super(c);
    }

    @Override
    public void load(JSONObject data, View parent, RelativeLayout layout) {
        try {
            mDpadImageRest = R.drawable.dpad;
            Rect frame = getRekt(data.getJSONObject("frame"));
            int width = Math.min(frame.width(), frame.height());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
            params.leftMargin = frame.left;
            params.topMargin = frame.top;

            setImage(mDpadImageRest);
            layout.addView(this, params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // press down
                NetworkManager.joinedGame.sendPadUpdate(mControlId, determineDirection(new
                        Point((int) event.getX(), (int) event.getY())), 1);
                return true;
            case MotionEvent.ACTION_UP: // release
                NetworkManager.joinedGame.sendPadUpdate(mControlId, determineDirection(new
                        Point((int) event.getX(), (int) event.getY())), 0);
                return true;
            case MotionEvent.ACTION_MOVE: // move finger
                NetworkManager.joinedGame.sendPadUpdate(mControlId, determineDirection(new
                        Point((int) event.getX(), (int) event.getY())), 2);
                return true;
        }
        return false;
    }

    public Point determineDirection(Point point) {
        float dx = point.x - getX();
        float dy = point.y - getY();

        int x = 0;
        int y = 0;

        if (dx < 1/3) x = -1;
        else if (dx > 2/3) x = 1;

        if (dy < 1/3) y = -1;
        else if (dy > 2/3) y = 1;

        return new Point(x, y);
    }
}
