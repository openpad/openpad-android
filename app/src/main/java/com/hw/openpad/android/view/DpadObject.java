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
        Point center = new Point((int) getX() + getWidth() / 2, (int) getY() + getHeight() / 2);
        float dx = point.x - (getLeft() + getWidth() / 2);
        float dy = point.y - (getTop() + getHeight() / 2);

        int x = 0, y = 0;

//        Old way of determining direction to travel (bad for 4-way Dpad)
//        if (dx < .4) x = -1;
//        else if (dx > .6) x = 1;
//
//        if (dy < .4) y = 1;
//        else if (dy > .6) y = -1;

        double ax = Math.abs(dx), ay = Math.abs(dy);
        System.out.println(dx+","+dy);
        if (ax > ay) {
            x = dx > 0 ? 1 : -1;
        } else {
            y = dy > 0 ? 1 : -1;
        }

        return new Point(x, y);
    }
}
