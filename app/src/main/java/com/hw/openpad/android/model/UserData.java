package com.hw.openpad.android.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ethan on 10/27/14.
 */
public class UserData {
    private static JSONObject data;
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    public static void load(Activity activity) {
        if (data == null) {
            sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

            data = new JSONObject();
            String s = sharedPref.getString("userdata", "");
            System.out.println("s: " + s);
            try {
                data = new JSONObject(s);
            } catch (JSONException e) {
                data = new JSONObject();
            }
        }
    }

    public static String getValue(String key) {
        try {
            return data.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void put(String key, String val) {
        try {
            data.put(key, val);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        String json = data.toString();
        System.out.println("json: " + json);
        editor = sharedPref.edit();
        editor.putString("userdata", json);
        editor.commit();

    }
}
