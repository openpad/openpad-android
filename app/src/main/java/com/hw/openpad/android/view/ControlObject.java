package com.hw.openpad.android.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.hw.openpad.android.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ethan on 11/11/14.
 */
public class ControlObject extends View {
    protected Context context;



    public ControlObject(Context c) {
        super(c);
        context = c;
    }

    protected Rect getRekt(JSONObject frame){
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            double x = frame.getDouble("x");
            double y = frame.getDouble("y");
            double w = frame.getDouble("w");
            double h = frame.getDouble("h");

            int width = (int) (size.x * w);
            int height = (int) (size.y * h);
            int left = (int) (x * size.x - width / 2);
            int top = (int) (y * size.y - height / 2);

            return new Rect(left, top, left + width, top + height);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public void load(JSONObject data, View parent, RelativeLayout layout) {
        try {
            Rect frame = getRekt(data.getJSONObject("frame"));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(frame.width(),
                    frame.height());
            params.leftMargin = frame.left;
            params.topMargin = frame.top;

            this.setBackgroundColor(getResources().getColor(R.color.dark_grey));
            layout.addView(this, params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void setImage(int id) {
        this.setBackgroundResource(id);

    }
}
