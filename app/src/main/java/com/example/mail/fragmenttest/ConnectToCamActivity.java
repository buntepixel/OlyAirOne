package com.example.mail.fragmenttest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


public class ConnectToCamActivity extends Activity {
    private static final String TAG = ConnectToCamActivity.class.getSimpleName();


    private String mSavedSsid, mSavedPw;
    private SharedPreferences settings;
    private WifiManager mWifiManager;
    private List<ScanResult> myScanResults;
    private List<WifiConfiguration> myWifiConfig;
    private ImageView waitconnect;

    private ScanForWifiAcessPoints wifiScanReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_cam);

        //initializes necessary components
        initControl();


    }

    @Override
    protected void onResume() {

        Log.d(TAG, "enter on Resume");
        if (wifiScanReceiver == null) {
            Log.d(TAG, "creating Broadcast filter");
            wifiScanReceiver = new ScanForWifiAcessPoints();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(mWifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(wifiScanReceiver, intentFilter);
        }
        try {
            List<String> credentials = checkWifiCredentialExist(this);
            String ssid = credentials.get(0);
            String Pw = credentials.get(1);
            //refreshing the availiable networks
            mWifiManager.startScan(); //getting the result in a broadcast receiver

        } catch (Exception e) {

            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);

        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiScanReceiver);
    }

    private void initControl() {
        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(this.WIFI_SERVICE);
        //Animation Icon
        waitconnect = (ImageView) findViewById(R.id.iv_waitconnect);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.waitconnect);
        waitconnect.startAnimation(myAnim);



    }

    //Todo whats going on
    private void connectToCamWifi() {
        //getting current wifi network
        String activeSSID = mWifiManager.getConnectionInfo().getSSID();
        WifiConfiguration wifiConfiguration = null;
        Log.d(TAG, "Active Wifi::" + activeSSID);
        //getting the Scan results of last scan
        myScanResults = mWifiManager.getScanResults();

        Log.d(TAG, "Enter connect to Cam Wifi");
        try {

            String mySSID = "\"" + mSavedSsid + "\"";
            Intent switchToCamActivtiy = new Intent(this, CameraActivity.class);
            // switching wifi if not already connected too the correct wifi
            if (activeSSID.equals(mySSID)) {
                Toast.makeText(this, "Already Connected to: " + mySSID, Toast.LENGTH_SHORT).show();
                //Already Connectecd switching to camera Activity
                Log.d(TAG,"Already Connected SwitchingtoCamActivity");
                startActivity(switchToCamActivtiy);
            } else {
                //getting the correct config out of the List of known networks as we set it bevore
                //if it wasn't there
                Log.d(TAG, "Connected to: " + mWifiManager.getConnectionInfo().getSSID());
                myWifiConfig = mWifiManager.getConfiguredNetworks();
                for (WifiConfiguration i : myWifiConfig) {

                    //Log.d(TAG, "i==  " + i.SSID);
                   // Log.d(TAG, "mySSID==  " + mySSID);
                    if (i.SSID != null && i.SSID.equals(mySSID)) {
                        Log.d(TAG, "Enter config search: " + mySSID);
                        //parsing it to a empty wificonfiguration object
                        wifiConfiguration = i;
                        Log.d(TAG, "FoundConfig::" + i.SSID);
                        break;
                    }
                    else {
                        Log.d(TAG, "FoundNOConfig::" + i.SSID);
                    }
                }
                Boolean foundTargetNwActive = false;
                //comparing the SSID to the last Scan results for check if active
                for (ScanResult i : myScanResults) {

                    String item =wifiConfiguration.SSID;
                    String scanSSID = "\""+ i.SSID+"\"";
                    Log.d(TAG, "WifiSSID: "+ item );
                    Log.d(TAG, "ScanSSID: "+ scanSSID );
                    if (myScanResults != null && item.equals(scanSSID)) {
                        Log.d(TAG, "Enter scanResult search: " + scanSSID);
                        foundTargetNwActive = true;
                        Log.d(TAG, "FoundTatgetNetwork::" + scanSSID);
                        break;
                    }
                }
                if (foundTargetNwActive) {
                    Log.d(TAG, "Setting Target Network" + wifiConfiguration.networkId);
                    mWifiManager.enableNetwork(wifiConfiguration.networkId, true);

                    //Connection sucessful switching to camera Activity
                    Log.d(TAG,"Connected SwitchingtoCamActivity");
                    startActivity(switchToCamActivtiy);
                } else {
                    Log.d(TAG, "Target network:" + wifiConfiguration.SSID + "seems to be not active\n" +
                            "check your camera");
                    Toast.makeText(this, "Camwifi seems not active, please enable", Toast.LENGTH_LONG).show();
                    this.finish();
                }
            }




           // return;
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);


        }
    }

    // TODO: 10/10/2016 Delete Method
    private Boolean enableWifi(Context context) {
        //check if wifi is enabled
        Log.d(TAG, "bevore checkenabled");
        Boolean enabled = false;
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
            Toast.makeText(context, "Wifi not enabled, enabeling Wifi", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "bevore wait");
        //Waiting for wifi to enable
        int count = 0;
        while (!mWifiManager.isWifiEnabled()) {
            if (count >= 10) {
                Log.i(TAG, "Took too long to enable wi-fi, quitting");
                return enabled;
            }
            Log.i(TAG, "Still waiting for wi-fi to enable...");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ie) {
                // continue
            }
            count++;
        }
        enabled = true;
        return enabled;
    }

    private WifiConfiguration setWifiConfig(Context context, String ssid, String Pw) {
        Log.d(TAG, "Entered setWifiConfig");
        WifiConfiguration camWifiConfig = new WifiConfiguration();
        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        Boolean foundConfiguration = false;
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                foundConfiguration = true;
                camWifiConfig = i;
                break;
            }
        }
        if (!foundConfiguration) {
            camWifiConfig.SSID = String.format("\"%s\"", ssid);
            camWifiConfig.preSharedKey = String.format("\"%s\"", Pw);
            mWifiManager.addNetwork(camWifiConfig);
            return camWifiConfig;
        }
        Log.d(TAG, "Exit   setWifiConfig");
        return camWifiConfig;
    }

    private List<String> checkWifiCredentialExist(Context context) {
        Log.d(TAG, "Entered checkWifiCredetialsExist");
        //get Log in Data From Pref File if exist
        SharedPreferences mySettings = context.getSharedPreferences(context.getResources().getString(R.string.pref_wifinetwork), Context.MODE_PRIVATE);
        mSavedSsid = mySettings.getString(context.getResources().getString(R.string.pref_ssid), "");
        mSavedPw = mySettings.getString(context.getResources().getString(R.string.pref_Pw), "");

        Log.d(TAG, "MySavedPw" + mSavedPw.isEmpty() + "MySavedSSID" + mSavedSsid);
        List<String> credentials = Arrays.asList(mSavedSsid, mSavedPw);
        // if now credetials saved promt to settings Activity
        if (mSavedPw.isEmpty()) {
            Log.d(TAG, "Nothing is Saved");
            Intent goToSettingsActivity = new Intent(context, AppSettingsActivity.class);
            context.startActivity(goToSettingsActivity);
            //If credentials exist get them and return them
        } else {
            Log.d(TAG, "CredentialsFound");
            credentials.set(0, mSavedSsid);
            credentials.set(1, mSavedPw);
            Log.d(TAG, "Pw Is:" + mySettings.getString(context.getResources().getString(R.string.pref_Pw), null));
        }
        Log.d(TAG, "EXIT checkWifiCredetialsExist");
        return credentials;

    }


    private String getWifiInfo(Context context) {
        String currSsid = "";
        if (mWifiManager.isWifiEnabled()) {
            currSsid = mWifiManager.getConnectionInfo().getSSID();
            Log.d(TAG, "WifiEnabled");
        } else {
            Toast.makeText(context, "Wifi Off, Enabeling Wifi", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Wifi NOT Enabled");
        }
        return currSsid;
    }

    //Broadcast receiver
    private class ScanForWifiAcessPoints extends BroadcastReceiver {

        public static final String CONNECTED_KEY = "connected";
        WifiConfiguration wifiConfiguration;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(mWifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Log.d(TAG,"GettingResponse");
                connectToCamWifi();

            }



        }

    }


}
