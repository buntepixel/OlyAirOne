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
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraConnectionListener;
import jp.co.olympus.camerakit.OLYCameraKitException;


public class MainActivity extends Activity implements View.OnClickListener, OLYCameraConnectionListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnCamera, btnViewImages, btnCamSettings, btnTurnOff, btnTurnOn;
    private BroadcastReceiver mReceiver;
    private WifiManager mWifiManager;
    private OLYCamera camera;
    Executor connectionExecutor = Executors.newFixedThreadPool(2);

    private SharedPreferences preferences;
    public static final String PREFS_NAME = "AirOnePrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Log.d(TAG, "SET PrefsObj");
        btnCamera = findViewById(R.id.btnCamera);
        btnViewImages = findViewById(R.id.btnViewImages);
        btnCamSettings = findViewById(R.id.btnCamSettings);
        btnTurnOff = findViewById(R.id.btn_turnOff);
        btnTurnOn = findViewById(R.id.btn_turnOn);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        camera = new OLYCamera();
        camera.setContext(this);
        camera.setConnectionListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        btnCamera.setOnClickListener(this);
        btnViewImages.setOnClickListener(this);
        btnCamSettings.setOnClickListener(this);
        btnTurnOff.setOnClickListener(this);
        btnTurnOn.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        btnCamera.setOnClickListener(null);
        btnViewImages.setOnClickListener(null);
        btnCamSettings.setOnClickListener(null);
        btnTurnOff.setOnClickListener(null);
        btnTurnOn.setOnClickListener(null);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View myView = super.onCreateView(parent, name, context, attrs);
        return myView;
    }

    private void startConnectingCamera() {
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Boolean canConnect = false;
                while (!canConnect) {
                    canConnect = camera.canConnect(OLYCamera.ConnectionType.WiFi, 0);
                }
                try {
                    camera.connect(OLYCamera.ConnectionType.WiFi);
                } catch (OLYCameraKitException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (view == btnCamera) {
            intent = new Intent(getBaseContext(), ConnectToCamActivity.class);
            intent.putExtra("target", "cam");
            startActivity(intent);
        } else if (view == btnViewImages) {

        } else if (view == btnCamSettings) {
            intent = new Intent(getBaseContext(), CamSettingsActivity.class);
            intent.putExtra("target", "settings");
            startActivity(intent);
        } else if (view == btnTurnOn) {
            //nothing here yet
        } else if (view == btnTurnOff) {
            Toast.makeText(this, "Camera should turn off in a second", Toast.LENGTH_SHORT).show();
            if (!camera.isConnected()) {
                WifiManager wifiManager= (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
                String ssid= mWifiManager.getConnectionInfo().getSSID();
                SharedPreferences prefs=getSharedPreferences(getResources().getString(R.string.pref_SharedPrefs), MODE_PRIVATE);
                String targetSSID= prefs.getString(getResources().getString(R.string.pref_ssid),"none");
                Log.d(TAG,"target: "+targetSSID+"curr: "+ssid);
                if(!ssid.equals("\""+targetSSID+"\"")){
                    Toast.makeText(this,"Your Wifi is currently not connected to the wifi provided in your network settings\nconnect to your cam or check your settings",Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d(TAG, "connecting to cam");
                startConnectingCamera();
            }
            connectionExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "cam is null: " + (camera == null));
                    Log.d(TAG, "entering runnable: " + camera.isConnected());

                    while (!camera.isConnected()) {

                    }
                    try {
                        Log.d(TAG, "trying to disconnect");

                        camera.disconnectWithPowerOff(true);
                    } catch (OLYCameraKitException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onDisconnectedByError(OLYCamera olyCamera, OLYCameraKitException e) {
        Log.d(TAG, "LostConnection");
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getBaseContext(), "Connection to Camera Lost, please Reconnect", Toast.LENGTH_LONG).show();
            }
        });
        finish();
    }
}



