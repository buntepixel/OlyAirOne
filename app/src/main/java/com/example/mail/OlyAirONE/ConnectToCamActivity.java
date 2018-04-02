package com.example.mail.OlyAirONE;

import android.Manifest;
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
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;


public class ConnectToCamActivity extends Activity {
    private static final String TAG = ConnectToCamActivity.class.getSimpleName();
    //private String mSavedSsid, mSavedPw;

    private WifiManager mWifiManager;
    private ScanForWifiAcessPoints wifiScanReceiver;
    private Boolean isScanningWifi;


    private String target;
    private int connectionAttempt = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_connect_to_cam);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            String[] PERMS_INITIAL = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
            ActivityCompat.requestPermissions(this, PERMS_INITIAL, 127);
        }

        //initializes necessary components
        target = getIntent().getExtras().getString("target", "none");
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            if (mWifiManager.isWifiEnabled()) {
                Log.i(TAG, "enter on Resume");
                String credentials = getWifiCredentials();
                if (credentials == null || credentials == "") {
                    showWifiCredentialsDialog();
                } else
                    connectToCamWifi();

            } else {
                alertDialogueBuilder("Wifi NOT enabled!\ngo to Wifi settings?", "Yes", "No", new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.i(TAG, stackTrace);
        }
    }

    private void scanForWifiNetworks() {
        connectionAttempt++;
        Log.i(TAG, "connection attempt: " + connectionAttempt);
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                alertDialogueBuilder("Since you seem to be on Android Marshmallow or higher, you need to activate your Gps to get the wifi scan results in this app!" +
                        "\nAlternatively you can manually connect to your cameras wifi", "Get me to the Gps Settings", "Get me out of here", new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }
        if (wifiScanReceiver == null) {
            Log.i(TAG, "creating Broadcast filter");
            wifiScanReceiver = new ScanForWifiAcessPoints();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
            registerReceiver(wifiScanReceiver, intentFilter);
        }
        Log.i(TAG, "starting scan:");
        mWifiManager.startScan(); //getting the result in a broadcast receiver

    }

    @Override
    protected void onPause() {
        super.onPause();
        connectionAttempt = 0;
        try {

            if (wifiScanReceiver != null){
                unregisterReceiver(wifiScanReceiver);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private void init() {
        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        //Animation Icon
        ImageView waitconnect = findViewById(R.id.iv_waitconnect);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.waitconnect);
        waitconnect.startAnimation(myAnim);

    }

    private void alertDialogueBuilder(String text, String btn_Pos, String btn_Neg, final Intent posIntent) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(text);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                btn_Pos,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(posIntent);
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
        FragmentWificredentialsDialogue wifiFragDialogue = FragmentWificredentialsDialogue.newInstance();
        wifiFragDialogue.show(ft, "WifiCredDialogue");

        wifiFragDialogue.setSaveCredentialsListener(new FragmentWificredentialsDialogue.SaveCredentialsListener() {
            @Override
            public void OnSaveCredentials(String ssid) {
                //SharedPreferences mySettings = getSharedPreferences(getResources().getString(R.string.pref_wifinetwork), Context.MODE_PRIVATE);
                Log.i(TAG, "OtherSide: " + ssid);
                FragmentWificredentialsDialogue prev = (FragmentWificredentialsDialogue) getFragmentManager().findFragmentByTag("WifiCredDialogue");
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
            if (ssid == null || ssid.equals("")) {
                return null;
            } else {
                Log.i(TAG, "CredentialsFound");
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

    private Boolean connectToCamWifi() {
        try {
            Log.i(TAG, "connecting to camWifi");
            //getting current wifi network
            String activeSSID = mWifiManager.getConnectionInfo().getSSID();
            Boolean foundTargetNwActive = false;


            String ssid = getWifiCredentials();

            String mySSID = "\"" + ssid + "\"";
            Log.i(TAG, "Active Wifi::" + activeSSID + " Saved Wifi: " + mySSID);

            // switch to cam if already connected
            if (activeSSID.equals(mySSID)) {
                Toast.makeText(this, "Already Connected to: " + mySSID, Toast.LENGTH_SHORT).show();
                //Already Connectecd switching to camera Activity
                Log.i(TAG, "Already Connected Switching toCamActivity");
                startNextActivity(this);
                return true;
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
                    Log.i(TAG, "FoundTargetNetwork::" + scanSSID);
                    foundTargetNwActive = true;
                    for (WifiConfiguration config : myWifiConfigList) {
                        if (config.SSID != null && (config.SSID.equals(mySSID))) {
                            //parsing it to a empty wificonfiguration object
                            Log.i(TAG, "FoundConfig::" + config.SSID);
                            myWifiConfig = config;
                            //connecting to found Network
                            Log.i(TAG, "Setting Target Network: " + myWifiConfig.networkId);
                            mWifiManager.enableNetwork(myWifiConfig.networkId, true);
                            return true;
                        }
                    }
                }
            }

            if (myWifiConfig == null && foundTargetNwActive) {
                Log.i(TAG, "Go to wifiSettings");
                alertDialogueBuilder("Seems you're connecting the first time. Please enter your Password in the wifisettings", "Switch to wifi settings", "CANCEL", new Intent(Settings.ACTION_WIFI_SETTINGS));
            } else {
                if (connectionAttempt == 0) {
                    isScanningWifi = true;
                    scanForWifiNetworks();

                    Toast.makeText(this, "Couldn't find a network that matches your saved configuration\n\n initializing network scan", Toast.LENGTH_LONG).show();
                } else if (!isScanningWifi) {
                    Toast.makeText(this, "Couldn't find a network that matches your saved configuration. " +
                            "Please check if Cams Wifi is enabled,\nor check your credentials in the Settings page.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            // return;
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.i(TAG, stackTrace);
        }
        return false;
    }

    private void startNextActivity(Context context) {
        Intent switchToNextActivtiy;
        Log.i(TAG, "starting next Activtiy TARGET: " + target);
        if (target.equals("cam")) {
            switchToNextActivtiy = new Intent(context, CameraActivity.class);
        } else if (target.equals("imageView")) {
            switchToNextActivtiy = new Intent(context, ImageViewActivity.class);
        } else if (target.equals("scanNotReceived")) {
            switchToNextActivtiy = new Intent(context, MainActivity.class);
        } else {
            Toast.makeText(getBaseContext(), "Something went wrong Returning to main screen", Toast.LENGTH_SHORT).show();
            switchToNextActivtiy = new Intent(context, MainActivity.class);
        }
        //switchToNextActivtiy.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(switchToNextActivtiy);
        finish();
    }

    private class ScanForWifiAcessPoints extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "ReceiveBroadcast: " + intent.getAction().toString());
            // Scan completed process Results
            String credentials = getWifiCredentials();

            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Log.i(TAG, "GettingResponse: SCAN_RESULTS_AVAILABLE_ACTION");
               /* if (credentials == null || credentials.equals("")) {
                    showWifiCredentialsDialog();
                } else*/
                isScanningWifi = false;
                connectToCamWifi();
            } //connection changed
            else if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                Log.i(TAG, "SupplicantState: " + intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
                //Connection status completed
                if (intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE) == (SupplicantState.COMPLETED)) {
                    String connectedSSID = mWifiManager.getConnectionInfo().getSSID();
                    Log.i(TAG, "ssid: " + connectedSSID + " cred: ");
                    //check if correct network
                    if (credentials != null && connectedSSID.equals("\"" + credentials + "\"")) {
                        Log.i(TAG, "starting to connect to cam");
                        startNextActivity(context);
                    } else
                        Log.i(TAG, "wrong ssid, ssid is: " + connectedSSID.toString() + " needed ssid: " + credentials);
                }
            }
        }
    }


}
