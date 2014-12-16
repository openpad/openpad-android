package com.hw.openpad.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.hw.openpad.android.model.NetworkManager;
import com.hw.openpad.android.view.ButtonObject;
import com.hw.openpad.android.view.ControlObject;
import com.hw.openpad.android.view.DpadObject;
import com.hw.openpad.android.view.JoystickObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ControllerActivity extends Activity implements GameConnection.ControllerDelegate {

    private ArrayList<ControlObject> controlsArray = new ArrayList<ControlObject>();
    private RelativeLayout mRelativeLayout;
    private boolean mBlackBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //Remove notification bar
        setContentView(R.layout.activity_controller);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.controller_layout);
        loadSettings();
        NetworkManager.joinedGame.setDelegate(this);
        if (NetworkManager.joinedGame.mPadConfig != null){
            setPadConfig(NetworkManager.joinedGame.mPadConfig);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.controller, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadSettings() { // load user settings and make changes based on them
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mBlackBackground = sharedPreferences.getBoolean("pref_black", false);
        if (mBlackBackground) mRelativeLayout.setBackgroundColor(Color.BLACK);
        else mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.dark_grey));


    }

    @Override
    public void setPadConfig(JSONObject padConfig) {
        try {
            JSONArray controls = padConfig.getJSONArray("controls");
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.controller_layout);


            for (int i = 0; i < controls.length(); i++) {
                JSONObject ctrl = controls.getJSONObject(i);
                switch (ctrl.getInt("type")) {
                    case 0: // button
                        ButtonObject button = new ButtonObject(this);
                        button.load(ctrl, layout, layout);
                        break;
                    case 1: // dpad
                        DpadObject dpad = new DpadObject(this);
                        dpad.load(ctrl, layout, layout);
                        break;
                    case 2: // joystick
                        JoystickObject joystick = new JoystickObject(this);
                        joystick.load(ctrl, layout, layout);
                        break;
                    default:
                        Log.d("openpad", "Not a button");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
