package com.hw.openpad.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.hw.openpad.android.model.UserData;

import java.util.UUID;

public class InfoActivity extends Activity {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private String mFirstName, mLastName, mUsername, mUuid;
    private EditText mFirstNameText, mLastNameText, mUsernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        mFirstNameText = (EditText) findViewById(R.id.firstname);
        mLastNameText = (EditText) findViewById(R.id.lastname);
        mUsernameText = (EditText) findViewById(R.id.username);

        UserData.load(this);
        mFirstName = UserData.getValue("firstname");
        mLastName = UserData.getValue("lastname");
        mUsername = UserData.getValue("username");

        mFirstNameText.setText(mFirstName);
        mLastNameText.setText(mLastName);
        mUsernameText.setText(mUsername);
    }

    public void next(View v) {
        mFirstName = mFirstNameText.getText().toString();
        mLastName = mLastNameText.getText().toString();
        mUsername = mUsernameText.getText().toString();
        mUuid = UUID.randomUUID().toString();

        Log.d("PlayPhone", "generated UUID: "+ mUuid);
        UserData.put("firstname", mFirstName);
        UserData.put("lastname", mLastName);
        UserData.put("username", mUsername);
        UserData.put("phoneID", mUuid);
        UserData.put("fbuid", "");
        UserData.save();

        Intent i = new Intent(this, GameListActivity.class);
        startActivity(i);
    }
}