package com.example.mail.fragmenttest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;


public class ConnectToCamActivity extends Activity  {
    private static final String TAG = ConnectToCamActivity.class.getSimpleName();
    //private String mSavedSsid, mSavedPw;

    private boolean isActive = false;
    private SharedPreferences settings;
    private Executor connectionExecutor = Executors.newFixedThreadPool(1);
    private WifiManager mWifiManager;
    private ScanForWifiAcessPoints wifiScanReceiver;

    private ImageView waitconnect;
    private OLYCamera camera = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_cam);

        //initializes necessary components
        init();
        settings = getSharedPreferences("WifiPrefs", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mWifiManager.isWifiEnabled()) {
                //Log.d(TAG, "enter on Resume");
                if (wifiScanReceiver == null) {
                    Log.d(TAG, "creating Broadcast filter");
                    wifiScanReceiver = new ScanForWifiAcessPoints();
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(mWifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                    intentFilter.addAction(mWifiManager.EXTRA_SUPPLICANT_CONNECTED);
                    registerReceiver(wifiScanReceiver, intentFilter);
                }
                mWifiManager.startScan(); //getting the result in a broadcast receiver
            } else {
                goToWifiSettingsDialogue("Wifi NOT enabled!\ngo to Wifi settings?\n", "Yes", "No");
            /*Toast.makeText(this, R.string.ToastEnableWifi, Toast.LENGTH_SHORT).show();
            finish();*/
            }
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (wifiScanReceiver != null)
            unregisterReceiver(wifiScanReceiver);
    }

    private void init() {
        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(this.WIFI_SERVICE);
        //Animation Icon
        waitconnect = (ImageView) findViewById(R.id.iv_waitconnect);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.waitconnect);
        waitconnect.startAnimation(myAnim);

    }

    private void goToWifiSettingsDialogue(String text, String btn_Pos, String btn_Neg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(text);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                btn_Pos,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        finish();
                    }
                });

        builder1.setNegativeButton(
                btn_Neg,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void showWifiCredentialsDialog() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("WifiCredDialogue");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        WificredentialsDialogueFragment wifiFragDialogue = WificredentialsDialogueFragment.newInstance();
        wifiFragDialogue.show(ft, "WifiCredDialogue");

        wifiFragDialogue.setSaveCredentialsListener(new WificredentialsDialogueFragment.SaveCredentialsListener() {
            @Override
            public void OnSaveCredentials(String ssid) {
                //SharedPreferences mySettings = getSharedPreferences(getResources().getString(R.string.pref_wifinetwork), Context.MODE_PRIVATE);
                Log.d(TAG, "OtherSide: " + ssid);
                WificredentialsDialogueFragment prev = (WificredentialsDialogueFragment) getFragmentManager().findFragmentByTag("WifiCredDialogue");
                if (prev != null)
                    prev.dismiss();
                connectToCamWifi();
            }
        });

    }


    private List<String> getWifiCredentials() {
        try {
            Log.d(TAG, "Entered checkWifiCredetialsExist");
            //get Log in Data From Pref File if exist
            SharedPreferences mySettings = getSharedPreferences(getResources().getString(R.string.pref_wifinetwork), Context.MODE_PRIVATE);
            String ssid = mySettings.getString(getResources().getString(R.string.pref_ssid), null);
            //String pw = mySettings.getString(getResources().getString(R.string.pref_Pw), null);
            List<String> credentials = Arrays.asList(ssid);
            if (ssid == null ) {
                return null;
            } else {
                Log.d(TAG, "CredentialsFound");
            }
            Log.d(TAG, "EXIT checkWifiCredetialsExist");
            return credentials;
        } catch (Error e) {
            Log.e(TAG, "exception: " + e.getMessage());
            Log.e(TAG, "cause: " + e.getCause());
            Log.e(TAG, "exception: " + Log.getStackTraceString(e));
        }
        return  null;
    }


    private void connectToCamWifi() {
        Log.d(TAG, "Enter connect to Cam Wifi");
        //getting current wifi network
        String activeSSID = mWifiManager.getConnectionInfo().getSSID();
        Boolean foundTargetNwActive = false;
        Log.d(TAG, "Active Wifi::" + activeSSID);
        Intent switchToCamActivtiy = new Intent(this, CameraActivity.class);

        List<String> credentials = getWifiCredentials();
        String ssid = credentials.get(0);
        Log.d(TAG, "Currently saved Credentials: " + ssid );
        try {
            String mySSID = "\"" + ssid + "\"";

            // switching wifi if not already connected too the correct wifi
            if (activeSSID.equals(mySSID)) {
                Toast.makeText(this, "Already Connected to: " + mySSID, Toast.LENGTH_SHORT).show();
                //Already Connectecd switching to camera Activity
                Log.d(TAG, "Already Connected SwitchingtoCamActivity");
                startActivity(switchToCamActivtiy);
            } else {
                //getting the Scan results of last scan
                List<ScanResult> myScanResults = mWifiManager.getScanResults();
                List<WifiConfiguration> myWifiConfigList = mWifiManager.getConfiguredNetworks();
                WifiConfiguration myWifiConfig = null;
                for (ScanResult result : myScanResults) {
                    String scanSSID = "\"" + result.SSID + "\"";
                    Log.d(TAG, "Enter scanResults search: " + mySSID);
                    Log.d(TAG, "ScanSSID: " + scanSSID + " mySSID: " + mySSID);
                    if (scanSSID != null && scanSSID.equals(mySSID)) {
                        foundTargetNwActive = true;
                        Log.d(TAG, "FoundTatgetNetwork::" + scanSSID);
                        for (WifiConfiguration config : myWifiConfigList) {
                            Log.d(TAG, "Enter config search: " + mySSID);

                            if (config.SSID != null && (config.SSID.equals(mySSID))) {
                                //parsing it to a empty wificonfiguration object
                                Log.d(TAG, "FoundConfig::" + config.SSID);
                                myWifiConfig = config;
                                break;
                            } else {
                                Log.d(TAG, "FoundNOConfig::" + config.SSID);
                            }
                        }
                        break;
                    }
                }
                if (foundTargetNwActive) {
                    if (myWifiConfig == null) {
                        Log.d(TAG, "Go to wifiSettings");
                        goToWifiSettingsDialogue("Seems you're connecting the first time. Please enter your Password in the wifisettings", "Switch to wifi settings", "CANCEL");

                    } else {
                        Log.d(TAG, "Setting Target Network" + myWifiConfig.networkId);
                        mWifiManager.enableNetwork(myWifiConfig.networkId, true);

                        //Connection sucessful switching to camera Activity
                        Log.d(TAG, "Connected SwitchingtoCamActivity");
                        //TODO: get intent that network is connected
                        //startConnectingCamera();
                        //startActivity(switchToCamActivtiy);
                    }
                } else {
                    Toast.makeText(this, "Couldn't find a network that matches your saved configuration. " +
                            "Please check if Cams Wifi is enabled,\nor check your credentials in the Settings page.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            // return;
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);
        }
    }



    //Broadcast receiver
    private class ScanForWifiAcessPoints extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "BroadcastReceiver: " + intent.getAction().toString());
            if (intent.getAction().equals(mWifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Log.d(TAG, "GettingResponse: SCAN_RESULTS_AVAILABLE_ACTION");
                List<String> credentials = getWifiCredentials();
                if (credentials == null) {
                    showWifiCredentialsDialog();
                } else {
                    connectToCamWifi();
                }
            } else if (intent.getAction().equals(mWifiManager.EXTRA_SUPPLICANT_CONNECTED)) {
                String connectedSSID = mWifiManager.getConnectionInfo().getSSID();
                List<String> credentials = getWifiCredentials();
                Log.d(TAG, "GettingResponse: SUPPLICANT_CONNECTION_CHANGE_ACTION");
                if (credentials != null && connectedSSID.equals("\"" + credentials.get(0) + "\"")) {
                    Log.d(TAG, "starting to connect to cam");
                    Intent switchToCamActivtiy = new Intent(context, CameraActivity.class);
                    startActivity(switchToCamActivtiy);
                }
            }
        }
    }
}
