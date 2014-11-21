package com.hw.openpad.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.hw.openpad.android.model.UserData;

import java.util.UUID;

public class InfoActivity extends Activity {

    private String mFirstName, mLastName, mUsername, mUuid;
    private EditText mFirstNameText, mLastNameText, mUsernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mFirstNameText = (EditText) findViewById(R.id.firstname);
        mUsernameText = (EditText) findViewById(R.id.username);
        mLastNameText = (EditText) findViewById(R.id.lastname);

        UserData.load();
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

        Intent i = new Intent(this, GameListActivity.class);
        startActivity(i);
    }
}