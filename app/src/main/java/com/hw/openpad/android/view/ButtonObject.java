package com.hw.openpad.android.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hw.openpad.android.R;
import com.hw.openpad.android.model.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ethan on 11/19/14.
 */
public class ButtonObject extends ControlObject {
    int mButtonImageUp, mButtonImageDown, mButtonType, mControlId;

    public ButtonObject(Context c) {
        super(c);
    }

    @Override
    public void load(JSONObject data, View parent, RelativeLayout layout) {
        try {
            mButtonType = data.getInt("btntype");
            switch (mButtonType) {
                case 0:
                    mButtonImageUp = R.drawable.a_up;
                    mButtonImageDown = R.drawable.a_down;
                    break;
                case 1:
                    mButtonImageUp = R.drawable.b_up;
                    mButtonImageDown = R.drawable.b_down;
                    break;
                case 2:
                    mButtonImageUp = R.drawable.x_up;
                    mButtonImageDown = R.drawable.x_down;
                    break;
                case 3:
                    mButtonImageUp = R.drawable.y_up;
                    mButtonImageDown = R.drawable.y_down;
                    break;
                default:
                    Log.d("OpenPad", "Unknown button type");
            }

            mControlId = data.getInt("id");

            Rect frame = getRekt(data.getJSONObject("frame"));
            int width = Math.min(frame.width(), frame.height());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
            params.leftMargin = frame.left;
            params.topMargin = frame.top;

            setImage(mButtonImageUp);
            layout.addView(this, params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setImage(mButtonImageDown);
                NetworkManager.joinedGame.sendPadUpdate(mControlId, new Point(0,0), 1);
                return true;
            case MotionEvent.ACTION_UP:
                setImage(mButtonImageUp);
                NetworkManager.joinedGame.sendPadUpdate(mControlId, new Point(0,0), 0);
                return true;
        }
        return false;
    }

}
