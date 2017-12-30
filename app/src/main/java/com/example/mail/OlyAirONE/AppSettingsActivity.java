package com.example.mail.OlyAirONE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;


/**
 * Created by mail on 30/09/2016.
 */

public class AppSettingsActivity extends Activity {
    private static final String TAG = AppSettingsActivity.class.getSimpleName();
    private EditText mPassword, mSsid;

//Todo: Delete this class
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appsettings_activity);

        SharedPreferences mySettings = this.getSharedPreferences(getString(R.string.pref_SharedPrefs), Context.MODE_PRIVATE);
        Log.d(TAG, "MySetting ScharedPref::" + mySettings);
        mPassword = (EditText) findViewById(R.id.etPw);
        mSsid = (EditText) findViewById(R.id.etSsid);
        if (mySettings.getString(getString(R.string.pref_ssid), null) != null) {
            mSsid.setText(mySettings.getString(getResources().getString(R.string.pref_ssid), null));
            mPassword.setText(mySettings.getString(getResources().getString(R.string.pref_Pw), null));
            return;
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences mySettings = this.getSharedPreferences(getString(R.string.pref_SharedPrefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySettings.edit();
        mPassword = (EditText) findViewById(R.id.etPw);
        mSsid = (EditText) findViewById(R.id.etSsid);
        editor.putString(getResources().getString(R.string.pref_ssid), String.valueOf(mSsid.getText()));
        editor.putString(getResources().getString(R.string.pref_Pw), String.valueOf(mPassword.getText()));
        editor.commit();
        Log.d(TAG, "Ssid_is now::: " + mySettings.getString(getResources().getString(R.string.pref_ssid), null));
        Log.d(TAG, "Pwis now::: " + mySettings.getString(getResources().getString(R.string.pref_Pw), null));
    }
}