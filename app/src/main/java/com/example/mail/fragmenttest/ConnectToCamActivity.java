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


public class ConnectToCamActivity extends Activity {
    private static final String TAG = ConnectToCamActivity.class.getSimpleName();
    //private String mSavedSsid, mSavedPw;
    private SharedPreferences settings;
    private WifiManager mWifiManager;


    private ImageView waitconnect;

    private ScanForWifiAcessPoints wifiScanReceiver;


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
        if (mWifiManager.isWifiEnabled()) {
            //Log.d(TAG, "enter on Resume");
            if (wifiScanReceiver == null) {
                Log.d(TAG, "creating Broadcast filter");
                wifiScanReceiver = new ScanForWifiAcessPoints();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(mWifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(wifiScanReceiver, intentFilter);
            }
            try {
                List<String> credentials = getWifiCredentials(this);
                if (credentials == null) {
                    showWifiCredentialsDialog();
                } else {

                    //refreshing the availiable networks
                    mWifiManager.startScan(); //getting the result in a broadcast receiver
                }
            } catch (Exception e) {
                String stackTrace = Log.getStackTraceString(e);
                System.err.println(TAG + e.getMessage());
                Log.d(TAG, stackTrace);
            }
        } else {
            goToWifiSettingsDialogue("Wifi NOT enabled!\ngo to Wifi settings?\n", "Yes", "No");
            /*Toast.makeText(this, R.string.ToastEnableWifi, Toast.LENGTH_SHORT).show();
            finish();*/
        }
        super.onResume();
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
            public void OnSaveCredentials(String ssid, String pw) {
                //SharedPreferences mySettings = getSharedPreferences(getResources().getString(R.string.pref_wifinetwork), Context.MODE_PRIVATE);
                Log.d(TAG, "OtherSide: " + ssid + "  " + pw);
                WificredentialsDialogueFragment prev = (WificredentialsDialogueFragment) getFragmentManager().findFragmentByTag("WifiCredDialogue");
                if (prev != null)
                    prev.dismiss();
                //Toast.makeText(getParent(), "SSid: "+ssid+"   pw: "+pw,Toast.LENGTH_LONG).show();
                connectToCamWifi();
            }
        });

    }


    private List<String> getWifiCredentials(Context context) {
        Log.d(TAG, "Entered checkWifiCredetialsExist");
        //get Log in Data From Pref File if exist
        SharedPreferences mySettings = context.getSharedPreferences(context.getResources().getString(R.string.pref_wifinetwork), Context.MODE_PRIVATE);
        String ssid = mySettings.getString(context.getResources().getString(R.string.pref_ssid), null);
        String pw = mySettings.getString(context.getResources().getString(R.string.pref_Pw), null);
        List<String> credentials = Arrays.asList(ssid, pw);
        if (ssid == null || pw == null) {
            return null;
           /* showWifiCredentialsDialog();
            Log.d(TAG, "NO CredentialsFound");
            ssid = mySettings.getString(context.getResources().getString(R.string.pref_ssid), null);
            pw = mySettings.getString(context.getResources().getString(R.string.pref_Pw), null);
            if (ssid != null)
                credentials.set(0, ssid);
            if (pw != null)
                credentials.set(1, pw);*/
        }
        else {
            Log.d(TAG, "CredentialsFound");
        }

        Log.d(TAG, "EXIT checkWifiCredetialsExist");
        return credentials;
    }


    //Todo whats going on
    private void connectToCamWifi() {
        Log.d(TAG, "Enter connect to Cam Wifi");
        //getting current wifi network
        String activeSSID = mWifiManager.getConnectionInfo().getSSID();
        Boolean foundTargetNwActive = false;
        Log.d(TAG, "Active Wifi::" + activeSSID);

        List<String> credentials = getWifiCredentials(this);
        String ssid = credentials.get(0);
        String pw = credentials.get(1);


        Log.d(TAG, "Currently saved Credentials: " + ssid + "::" + pw);
        try {
            String mySSID = "\"" + ssid + "\"";
            Intent switchToCamActivtiy = new Intent(this, CameraActivity.class);
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
                    if (scanSSID != null && scanSSID == ssid) {
                        foundTargetNwActive = true;
                        Log.d(TAG, "FoundTatgetNetwork::" + scanSSID);

                        for (WifiConfiguration config : myWifiConfigList) {

                            if (config.SSID != null && (config.SSID == mySSID)) {
                                Log.d(TAG, "Enter config search: " + mySSID);
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
                    Log.d(TAG, "Setting Target Network" + myWifiConfig.networkId);
                    if (myWifiConfig == null) {
                        myWifiConfig = new WifiConfiguration();
                        myWifiConfig.SSID = mySSID;
                        myWifiConfig.wepKeys[0] = "\"" + pw + "\"";
                    }
                    mWifiManager.enableNetwork(myWifiConfig.networkId, true);

                    //Connection sucessful switching to camera Activity
                    Log.d(TAG, "Connected SwitchingtoCamActivity");
                    startActivity(switchToCamActivtiy);

                } else {
                    Log.d(TAG, "Target network:" + myWifiConfig.SSID + "seems to be not active\n" +
                            "check your camera");

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
                Log.d(TAG, "GettingResponse");
                connectToCamWifi();
            }
        }
    }
}
