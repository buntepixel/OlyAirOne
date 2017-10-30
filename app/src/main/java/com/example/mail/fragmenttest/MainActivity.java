package com.example.mail.fragmenttest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnCamera, btnViewImages, btnCamSettings;
    private BroadcastReceiver mReceiver;
    private WifiManager mWifiManager;

    static SharedPreferences preferences;

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent connectWifiIntent = new Intent(getBaseContext(), ConnectToCamActivity.class);
                startActivity(connectWifiIntent);
            }
        });
        btnViewImages = (Button) findViewById(R.id.btnViewImages);
        btnViewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnCamSettings = (Button) findViewById(R.id.btnCamSettings);
        btnCamSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent camSettingsIntent = new Intent(getBaseContext(),SettingsActivity.class);
            startActivity(camSettingsIntent);
            }
        });

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);



    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View myView = super.onCreateView(parent, name, context, attrs);
        return myView;
    }



}



