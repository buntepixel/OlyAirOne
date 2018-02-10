package com.example.mail.OlyAirONE;

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
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;


public class ConnectToCamActivity extends Activity {
    private static final String TAG = ConnectToCamActivity.class.getSimpleName();
    //private String mSavedSsid, mSavedPw;

    private SharedPreferences settings;
    private WifiManager mWifiManager;
    private ScanForWifiAcessPoints wifiScanReceiver;

    private ImageView waitconnect;
    private String target;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_cam);

        //initializes necessary components

        target = getIntent().getExtras().getString("target", "none");
        init();
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
                    intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                    intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
                    registerReceiver(wifiScanReceiver, intentFilter);

                }
                mWifiManager.startScan(); //getting the result in a broadcast receiver
            } else {
                goToWifiSettingsDialogue("Wifi NOT enabled!\ngo to Wifi settings?\n", "Yes", "No");
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
        /*if (testReceiver != null)
            unregisterReceiver(testReceiver);*/
    }

    private void init() {
        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        //Animation Icon
        waitconnect = findViewById(R.id.iv_waitconnect);
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


    private String getWifiCredentials() {
        try {
            //Log.d(TAG, "Entered checkWifiCredetialsExist");
            //get Log in Data From Pref File if exist
            SharedPreferences mySettings = getSharedPreferences(getResources().getString(R.string.pref_SharedPrefs), Context.MODE_PRIVATE);
            String ssid = mySettings.getString(getResources().getString(R.string.pref_ssid), null);
            //String pw = mySettings.getString(getResources().getString(R.string.pref_Pw), null);
            if (ssid == null) {
                return null;
            } else {
                Log.d(TAG, "CredentialsFound");
            }
            //Log.d(TAG, "EXIT checkWifiCredetialsExist");
            return ssid;
        } catch (Error e) {
            Log.e(TAG, "exception: " + e.getMessage());
            Log.e(TAG, "cause: " + e.getCause());
            Log.e(TAG, "exception: " + Log.getStackTraceString(e));
        }
        return null;
    }

    private void connectToCamWifi() {
        try {
            //getting current wifi network
            String activeSSID = mWifiManager.getConnectionInfo().getSSID();
            Boolean foundTargetNwActive = false;


            String ssid = getWifiCredentials();

            String mySSID = "\"" + ssid + "\"";
            Log.d(TAG, "Active Wifi::" + activeSSID + " Saved Wifi: " + mySSID);

            // switch to cam if already connected
            if (activeSSID.equals(mySSID)) {
                Toast.makeText(this, "Already Connected to: " + mySSID, Toast.LENGTH_SHORT).show();
                //Already Connectecd switching to camera Activity
                Log.d(TAG, "Already Connected SwitchingtoCamActivity");
                startNextActivity(this);
                return;
            }

            //getting the Scan results of last scan
            //Log.d(TAG, "getting Scan Results " + mySSID);
            List<ScanResult> myScanResults = mWifiManager.getScanResults();
            List<WifiConfiguration> myWifiConfigList = mWifiManager.getConfiguredNetworks();
            WifiConfiguration myWifiConfig = null;
            //Log.d(TAG, "Enter scanResults search: " + mySSID);
            for (ScanResult result : myScanResults) {
                String scanSSID = "\"" + result.SSID + "\"";
                //Log.d(TAG, "scanned SSid: " + result.SSID);
                if (scanSSID != null && scanSSID.equals(mySSID)) {
                    Log.d(TAG, "FoundTargetNetwork::" + scanSSID);
                    foundTargetNwActive = true;
                    for (WifiConfiguration config : myWifiConfigList) {
                        if (config.SSID != null && (config.SSID.equals(mySSID))) {
                            //parsing it to a empty wificonfiguration object
                            Log.d(TAG, "FoundConfig::" + config.SSID);
                            myWifiConfig = config;
                            //connecting to found Network
                            Log.d(TAG, "Setting Target Network: " + myWifiConfig.networkId);
                            mWifiManager.enableNetwork(myWifiConfig.networkId, true);
                            return;
                        }
                    }
                }
            }

            if (myWifiConfig == null && foundTargetNwActive) {
                Log.d(TAG, "Go to wifiSettings");
                goToWifiSettingsDialogue("Seems you're connecting the first time. Please enter your Password in the wifisettings", "Switch to wifi settings", "CANCEL");
            } else {
                Toast.makeText(this, "Couldn't find a network that matches your saved configuration. " +
                        "Please check if Cams Wifi is enabled,\nor check your credentials in the Settings page.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            // return;
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);
        }
    }

    public void startNextActivity(Context context) {
        Intent switchToNextActivtiy;
        Log.d(TAG, "starting next Activtiy TARGET: " + target);
        if (target.equals("cam")) {
            switchToNextActivtiy = new Intent(context, CameraActivity.class);
        } else if (target.equals("imageView")) {
            switchToNextActivtiy = new Intent(context, ImageViewActivity.class);
        } else {
            Toast.makeText(getBaseContext(),"Something went wrong Returning to main screen",Toast.LENGTH_SHORT).show();
            switchToNextActivtiy = new Intent(context, MainActivity.class);
        }

        //switchToNextActivtiy.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(switchToNextActivtiy);
        finish();
    }

    private class ScanForWifiAcessPoints extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "ReceiveBroadcast: " + intent.getAction().toString());
            // Scan completed process Results
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                //Log.d(TAG, "GettingResponse: SCAN_RESULTS_AVAILABLE_ACTION");
                String credentials = getWifiCredentials();
                if (credentials == null || credentials.equals("")) {
                    showWifiCredentialsDialog();
                } else {
                    connectToCamWifi();
                }
            } //connection changed
            else if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                Log.d(TAG, "SupplicantState: " + intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
                //Connection status completed
                if (intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE) == (SupplicantState.COMPLETED)) {
                    String connectedSSID = mWifiManager.getConnectionInfo().getSSID();
                    String credentials = getWifiCredentials();
                    //check if correct network
                    if (credentials != null && connectedSSID.equals("\"" + credentials + "\"")) {
                        Log.d(TAG, "starting to connect to cam");
                        startNextActivity(context);
                    }
                }
            }
        }
    }


}
