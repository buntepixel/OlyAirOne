package com.example.mail.OlyAirONE;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
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
// Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        setContentView(R.layout.activity_main);/**/
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


    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG, "callbackType: " + callbackType + " result: " + result);
        }
    };

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.getBluetoothLeScanner().startScan(leScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);

        }
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
            try {
                // you can selectively disable BLE-related features.
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show();
                    finish();
                }
                scanLeDevice(true);
                //camera.setBluetoothDevice(mBluetoothAdapter);
                /*BluetoothDevice bluetoothDevice1 = new BluetoothDevice();
                camera.setBluetoothDevice();*/
                camera.wakeup();
            } catch (OLYCameraKitException ex) {
                ex.printStackTrace();
            }
            //nothing here yet
        } else if (view == btnTurnOff) {
            Toast.makeText(this, "Camera should turn off in a second", Toast.LENGTH_SHORT).show();
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



