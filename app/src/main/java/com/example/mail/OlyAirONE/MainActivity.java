package com.example.mail.OlyAirONE;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraConnectionListener;
import jp.co.olympus.camerakit.OLYCameraKitException;


public class MainActivity extends Activity implements View.OnClickListener, OLYCameraConnectionListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView iv_TurnOff, iv_Camera, iv_ViewImages, iv_CamSettings;
    private BroadcastReceiver mReceiver;
    private WifiManager mWifiManager;
    private OLYCamera camera;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    Executor connectionExecutor = Executors.newFixedThreadPool(2);

    private SharedPreferences preferences;
    public static final String PREFS_NAME = "AirOnePrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);/**/
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Log.d(TAG, "SET PrefsObj");
        iv_Camera = findViewById(R.id.iv_Camera);
        iv_ViewImages = findViewById(R.id.iv_viewImages);
        iv_CamSettings = findViewById(R.id.iv_CamSettings);
        iv_TurnOff = findViewById(R.id.btn_turnOff);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        camera = new OLYCamera();
        camera.setContext(this);
        camera.setConnectionListener(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        iv_Camera.setOnClickListener(this);
        iv_ViewImages.setOnClickListener(this);
        iv_CamSettings.setOnClickListener(this);
        iv_TurnOff.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        iv_Camera.setOnClickListener(null);
        iv_ViewImages.setOnClickListener(null);
        iv_CamSettings.setOnClickListener(null);
        iv_TurnOff.setOnClickListener(null);
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
        if (view == iv_Camera) {
            intent = new Intent(getBaseContext(), ConnectToCamActivity.class);
            intent.putExtra("target", "cam");
            startActivity(intent);
        } else if (view == iv_ViewImages) {
            intent = new Intent(getBaseContext(), ConnectToCamActivity.class);
            intent.putExtra("target", "imageView");
            startActivity(intent);

        } else if (view == iv_CamSettings) {
            intent = new Intent(getBaseContext(), CamSettingsActivity.class);
            intent.putExtra("target", "settings");
            startActivity(intent);
        } else if (view == iv_TurnOff) {
            Toast.makeText(this, "Camera will turn off in a second", Toast.LENGTH_SHORT).show();
            if (!camera.isConnected()) {
                WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
                String ssid = mWifiManager.getConnectionInfo().getSSID();
                SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.pref_SharedPrefs), MODE_PRIVATE);
                String targetSSID = prefs.getString(getResources().getString(R.string.pref_ssid), "none");
                Log.d(TAG, "target: " + targetSSID + "curr: " + ssid);
                if (!ssid.equals("\"" + targetSSID + "\"")) {
                    Toast.makeText(this, "Your Wifi is currently not connected to the wifi provided in your network settings\nconnect to your cam or check your settings", Toast.LENGTH_LONG).show();
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



