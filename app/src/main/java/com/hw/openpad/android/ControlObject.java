package com.hw.openpad.android;

import android.content.Context;
import android.view.View;

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

    public void load(JSONObject data, View parent) {

    }
}
