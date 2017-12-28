package com.example.mail.OlyAirONE;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnCamera, btnViewImages, btnCamSettings;
    private BroadcastReceiver mReceiver;
    private WifiManager mWifiManager;

    private  SharedPreferences preferences;
    public static final String PREFS_NAME = "AirOnePrefs";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences=getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        Log.d(TAG,"SET PrefsObj");
        btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(this);
        btnViewImages = findViewById(R.id.btnViewImages);
        btnViewImages.setOnClickListener(this);
        btnCamSettings = findViewById(R.id.btnCamSettings);
        btnCamSettings.setOnClickListener(this);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View myView = super.onCreateView(parent, name, context, attrs);
        return myView;
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        if(view==btnCamera){
            intent = new Intent(getBaseContext(), ConnectToCamActivity.class);
            intent.putExtra("target","cam");
            startActivity(intent);
        }else if(view == btnViewImages){

        }else if(view == btnCamSettings){
            intent = new Intent(getBaseContext(),CamSettingsActivity.class);
            intent.putExtra("target","settings");
            startActivity(intent);
        }
    }
}



