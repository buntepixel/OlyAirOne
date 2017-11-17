package com.example.mail.fragmenttest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraConnectionListener;
import jp.co.olympus.camerakit.OLYCameraKitException;
import jp.co.olympus.camerakit.OLYCameraPropertyListener;


public class CameraActivity extends FragmentActivity
        implements TriggerFragment.OnTriggerFragmInteractionListener, LiveViewFragment.OnLiveViewInteractionListener,
        MasterSlidebarFragment.sliderValue, SettingsFragment.OnSettingsFragmInteractionListener,
        OLYCameraConnectionListener, OLYCameraPropertyListener {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final String FRAGMENT_TAG_SETTINGS = "Settings";
    private static final String FRAGMENT_TAG_TRIGGER = "Trigger";
    private static final String FRAGMENT_TAG_LIVEVIEW = "LiveView";


    public static final String CAMERA_SETTINGS = "OlyAirOneCamSettings";

    public static final String CAMERA_PROPERTY_TAKE_MODE = "TAKEMODE";

    public static final String CAMERA_PROPERTY_APERTURE_VALUE = "APERTURE";
    public static final String CAMERA_PROPERTY_SHUTTER_SPEED = "SHUTTER";
    public static final String CAMERA_PROPERTY_EXPOSURE_COMPENSATION = "EXPREV";
    public static final String CAMERA_PROPERTY_ISO_SENSITIVITY = "ISO";
    public static final String CAMERA_PROPERTY_WHITE_BALANCE = "WB";


    List<String> takeModeStrings;

    //Boolean isActive = false;
    Executor connectionExecutor = Executors.newFixedThreadPool(1);
    int currDriveMode = 0;
    String currExpApart1;
    String currExpApart2;
    FragmentManager fm;
    TriggerFragment fTrigger;
    LiveViewFragment fLiveView;
    SettingsFragment fSettings;
    ApartureFragment apartureFragment;
    IsoFragment isoFragment;
    WbFragment wbFragment;
    ShutterFragment shutterSpeedFragment;
    ExposureCorrFragment exposureCorrFragment;

    public static OLYCamera camera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        fm = getSupportFragmentManager();
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        //Log.d(TAG, "ONCreate");
        if (savedInstanceState != null) {
            fSettings = (SettingsFragment) fm.getFragment(savedInstanceState, FRAGMENT_TAG_SETTINGS);
            fTrigger = (TriggerFragment) fm.getFragment(savedInstanceState, FRAGMENT_TAG_TRIGGER);
            fLiveView = (LiveViewFragment) fm.getFragment(savedInstanceState, FRAGMENT_TAG_LIVEVIEW);

       /*     shutterSpeedFragment = (ShutterFragment) fm.getFragment(savedInstanceState, CAMERA_PROPERTY_SHUTTER_SPEED);
            apartureFragment = (ApartureFragment) fm.getFragment(savedInstanceState, CAMERA_PROPERTY_APERTURE_VALUE);
            exposureCorrFragment = (ExposureCorrFragment) fm.getFragment(savedInstanceState, CAMERA_PROPERTY_EXPOSURE_COMPENSATION);
            isoFragment = (IsoFragment) fm.getFragment(savedInstanceState, CAMERA_PROPERTY_ISO_SENSITIVITY);
            wbFragment = (WbFragment) fm.getFragment(savedInstanceState, CAMERA_PROPERTY_WHITE_BALANCE);*/
            return;
        }
        //Log.d(TAG, "onCreate__" + "Creating Camera Object");
        camera = new OLYCamera();
        camera.setContext(getApplicationContext());
        camera.setConnectionListener(this);
        //add Trigger,LiveView Fragment
        //Log.d(TAG, "onCreate__" + "Creating Fragments,setting olycam to fragments");

        fTrigger = new TriggerFragment();
        fTrigger.SetOLYCam(camera);

        fSettings = new SettingsFragment();
        fSettings.SetOLYCam(camera);

        fLiveView = new LiveViewFragment();
        fLiveView.SetOLYCam(camera);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fm.putFragment(outState, FRAGMENT_TAG_LIVEVIEW, fLiveView);
        fm.putFragment(outState, FRAGMENT_TAG_SETTINGS, fSettings);
        fm.putFragment(outState, FRAGMENT_TAG_TRIGGER, fTrigger);
        outState.putInt("currDriveMode", currDriveMode);

       /* fm.putFragment(outState, CAMERA_PROPERTY_SHUTTER_SPEED, shutterSpeedFragment);
        fm.putFragment(outState, CAMERA_PROPERTY_APERTURE_VALUE, apartureFragment);
        fm.putFragment(outState, CAMERA_PROPERTY_EXPOSURE_COMPENSATION, exposureCorrFragment);
        fm.putFragment(outState, CAMERA_PROPERTY_ISO_SENSITIVITY, isoFragment);
        fm.putFragment(outState, CAMERA_PROPERTY_WHITE_BALANCE, wbFragment);*/

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currDriveMode = savedInstanceState.getInt("currDriveMode");
    }

    @Override
    public void onShootingModeButtonPressed(int currDriveMode) {
        try {
            try {
                camera.setCameraPropertyValue(CAMERA_PROPERTY_TAKE_MODE, takeModeStrings.get(currDriveMode));
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.currDriveMode = currDriveMode;
            SetShootingModeButtons(currDriveMode);
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);
        }
    }

    @Override
    public void onEnabledFocusLock(Boolean focusLockState) {

    }

    @Override
    public void onEnabledTouchShutter(Boolean touchShutterState) {

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(parent, name, context, attrs);
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //reset Cam if we had a orientation change
        fLiveView.SetOLYCam(camera);
        fTrigger.SetOLYCam(camera);
        fSettings.SetOLYCam(camera);
        //create slider fragments.
        apartureFragment = new ApartureFragment();
        apartureFragment.SetOLYCam(camera);
        shutterSpeedFragment = new ShutterFragment();
        shutterSpeedFragment.SetOLYCam(camera);
        exposureCorrFragment = new ExposureCorrFragment();
        exposureCorrFragment.SetOLYCam(camera);
        isoFragment = new IsoFragment();
        isoFragment.SetOLYCam(camera);
        wbFragment = new WbFragment();
        wbFragment.SetOLYCam(camera);
        //add fragments to fragmentManager if here the first time
        if (fm.findFragmentByTag(FRAGMENT_TAG_TRIGGER) == null) {
            Log.d(TAG, "onResume__" + "bevoreCommit");
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.fl_FragCont_Trigger, fTrigger, FRAGMENT_TAG_TRIGGER);
            fragmentTransaction.add(R.id.fl_FragCont_Settings, fSettings, FRAGMENT_TAG_SETTINGS);
            fragmentTransaction.commit();
            //Log.d(TAG, "onResume__" + "AfterCommit");
        }
        if (!camera.isConnected()) {
            //Log.d(TAG, "onResume__" + "connecting");
            startConnectingCamera();
        } else {
            // Log.d(TAG, "onResume__" + "connecting");
            onConnectedToCamera();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        FragmentTransaction ft = fm.beginTransaction();
        if (currExpApart2 != null && !currExpApart2.equals("")) {
            ft.remove(getSupportFragmentManager().findFragmentByTag(currExpApart2));
            currExpApart2 = "";
        }
        if (currExpApart1 != null && !currExpApart1.equals("")) {
            ft.remove(getSupportFragmentManager().findFragmentByTag(currExpApart1));
            currExpApart1 = "";
        }
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onDriveModeChange(String propValue) {
        //fLiveView.updateFocusMode(propValue);
    }

    @Override
    public void onButtonsInteraction(int settingsType) {
        // Toast.makeText(getParent(), settingsType, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "settingsType: " + settingsType);
        Log.d(TAG, "CurrentDriveMode" + currDriveMode);
        FragmentManager fm = getSupportFragmentManager();
        //if there is a fragment loaded remove it
        if (fm.findFragmentById(R.id.fl_FragCont_ExpApart1) != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(getSupportFragmentManager().findFragmentByTag(currExpApart1));
            currExpApart1 = "";
            if (fm.findFragmentById(R.id.fl_FragCont_ExpApart2) != null) {
                ft.remove(getSupportFragmentManager().findFragmentByTag(currExpApart2));
                currExpApart2 = "";
            }
            ft.commit();
        }

        //Log.d(TAG,"visFrag"+ fm.getFragments().toString());
        int fragLayout;
        Log.d(TAG, "currdriveMode: " + currDriveMode);
        //if Manual mode we have 2 fragment sliders
        if (currDriveMode == 4 && settingsType <= 1) {
            //ExposurePressed(ft, R.id.fl_FragCont_ExpApart2, 2);
            generalPressed(shutterSpeedFragment, CAMERA_PROPERTY_SHUTTER_SPEED, R.id.fl_FragCont_ExpApart2,fm, 1);
            generalPressed(apartureFragment, CAMERA_PROPERTY_APERTURE_VALUE, R.id.fl_FragCont_ExpApart1,fm, 0);
            Log.d(TAG, "A_nr1: " + currExpApart1 + " nr2: " + currExpApart2);
        } else {//all other modes
            switch (settingsType) {
                case 0:
                    //currExpApart1 = ExposurePressed(ft, R.id.fl_FragCont_ExpApart1, 1);
                    currExpApart1 = generalPressed(shutterSpeedFragment, CAMERA_PROPERTY_SHUTTER_SPEED, R.id.fl_FragCont_ExpApart1, fm,0);
                    break;
                case 1:
                    //AparturePressed(ft);
                    generalPressed(apartureFragment, CAMERA_PROPERTY_APERTURE_VALUE, R.id.fl_FragCont_ExpApart1,fm, 0);
                    break;
                case 2:
                    generalPressed(exposureCorrFragment, CAMERA_PROPERTY_EXPOSURE_COMPENSATION, R.id.fl_FragCont_ExpApart1, fm,0);
                    break;
                case 3:
                    //IsoPressed(ft);
                    generalPressed(isoFragment, CAMERA_PROPERTY_ISO_SENSITIVITY, R.id.fl_FragCont_ExpApart1,fm, 0);
                    break;
                case 4:
                    //WbPressed(ft);
                    generalPressed(wbFragment, CAMERA_PROPERTY_WHITE_BALANCE, R.id.fl_FragCont_ExpApart1,fm, 0);
                    break;
            }
        }
    }


    @Override
    public void onShutterTouched(MotionEvent event) {
        fLiveView.onShutterTouched(event);
    }


    //connecting Camera
    private void startConnectingCamera() {
        Log.d(TAG, "startConnectingCamera__" + "Adding trigger fragment to View");
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CameraActivity.this);
                Boolean canConnect = false;
                while (!canConnect) {
                    canConnect = camera.canConnect(OLYCamera.ConnectionType.WiFi, 0);
                }
                Log.d(TAG, "startConnectingCamera__" + "OLYCamera.ConnectionType.WiFi");
                try {
                    camera.connect(OLYCamera.ConnectionType.WiFi);
                } catch (OLYCameraKitException e) {
                    alertConnectingFailed(e);
                    return;
                }
                /*try {
                    camera.changeLiveViewSize(toLiveViewSize(preferences.getString("live_view_quality", "QVGA")));
                } catch (OLYCameraKitException e) {
                    Log.w(TAG, "You had better uninstall this application and install it again.");
                    alertConnectingFailed(e);
                    return;
                }*/
                Log.d(TAG, "startConnectingCamera__" + "OLYCamera.RunMode.Recording");
                try {
                    camera.changeRunMode(OLYCamera.RunMode.Recording);
                } catch (OLYCameraKitException e) {
                    alertConnectingFailed(e);
                    return;
                }
                Log.d(TAG, "startConnectingCamera__" + "Restores my settings");
                // Restores my settings.
                if (camera.isConnected()) {
                    Map<String, String> values = new HashMap<String, String>();
                    for (String name : Arrays.asList(
                            "TAKEMODE",
                            "TAKE_DRIVE",
                            "APERTURE",
                            "SHUTTER",
                            "EXPREV",
                            "WB",
                            "ISO",
                            "RECVIEW"
                    )) {
                        String value = preferences.getString(name, null);
                        if (value != null) {
                            values.put(name, value);
                        }
                    }
                    if (values.size() > 0) {
                        try {
                            camera.setCameraPropertyValues(values);
                        } catch (OLYCameraKitException e) {
                            Log.w(TAG, "To change the camera properties is failed: " + e.getMessage());
                        }
                    }
                }

                if (!camera.isAutoStartLiveView()) { // Please refer a document about OLYCamera.autoStartLiveView.
                    // Start the live-view.
                    // If you forget calling this method, live view will not be displayed on the screen.
                    try {
                        camera.startLiveView();
                    } catch (OLYCameraKitException e) {
                        Log.w(TAG, "To start the live-view is failed: " + e.getMessage());
                        return;
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onConnectedToCamera();
                    }
                });
            }
        });
    }

    private void onConnectedToCamera() {
        Log.d(TAG, "Connected to Cam");
        //add fragments to fragment manager if here first time
        if (fm.findFragmentByTag(FRAGMENT_TAG_LIVEVIEW) == null) {
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.fl_FragCont_cameraLiveImageView, fLiveView, FRAGMENT_TAG_LIVEVIEW);
            //Todo: maybe only commit();
            //fragmentTransaction.commitAllowingStateLoss();
            fragmentTransaction.commit();
        }

        try {
            takeModeStrings = camera.getCameraPropertyValueList(CAMERA_PROPERTY_TAKE_MODE);
            List<String> valueList = camera.getCameraPropertyValueList(CAMERA_PROPERTY_EXPOSURE_COMPENSATION);

            /*fSettings.SetExposureCorrValues(valueList);
            fSettings.UpdateValues();*/
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return;
        }


    }

    private void alertConnectingFailed(Exception e) {
        final Intent myIntent = new Intent(this, ConnectToCamActivity.class);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Connect failed")
                .setMessage(e.getMessage() != null ? e.getMessage() : "Unknown error")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //startScanningCamera();
                        startActivity(myIntent);
                    }
                });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.show();
            }
        });
    }

    private OLYCamera.LiveViewSize toLiveViewSize(String quality) {
        if (quality.equalsIgnoreCase("QVGA")) {
            return OLYCamera.LiveViewSize.QVGA;
        } else if (quality.equalsIgnoreCase("VGA")) {
            return OLYCamera.LiveViewSize.VGA;
        } else if (quality.equalsIgnoreCase("SVGA")) {
            return OLYCamera.LiveViewSize.SVGA;
        } else if (quality.equalsIgnoreCase("XGA")) {
            return OLYCamera.LiveViewSize.XGA;
        }
        return OLYCamera.LiveViewSize.QVGA;
    }

    private void SetShootingModeButtons(int mode) {
        Log.d(TAG, "Mode: " + mode);
        fSettings = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_SETTINGS);

        if (fSettings != null) {
            switch (mode) {
                case 0://iAuto
                    fSettings.SetTakeMode(mode);
                    fTrigger.SetTakeMode(mode);
                    Log.d(TAG, "Iauto");
                    break;
                case 1://Programm
                    fSettings.SetTakeMode(mode);
                    fTrigger.SetTakeMode(mode);
                    Log.d(TAG, "Programm");
                    break;
                case 2://Aparture
                    fSettings.SetTakeMode(mode);
                    fTrigger.SetTakeMode(mode);
                    Log.d(TAG, "Aparture");
                    break;
                case 3://Speed
                    fSettings.SetTakeMode(mode);
                    fTrigger.SetTakeMode(mode);
                    Log.d(TAG, "Speed");
                    break;
                case 4://Manual
                    fSettings.SetTakeMode(mode);
                    fTrigger.SetTakeMode(mode);
                    Log.d(TAG, "Manual");
                    break;
                case 5://Art
                    Log.d(TAG, "Art");
                    break;
                case 6://Movie
                    fSettings.SetTakeMode(mode);
                    fTrigger.SetTakeMode(mode);
                    Log.d(TAG, "Movie");
                    break;
            }
        } else {
            Log.w(TAG, "couldn't find Fragment with tag: Trigger");
        }
        //refresh view
        Fragment frgSettings = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_SETTINGS);
        Fragment frgTrigger = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_TRIGGER);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frgSettings);
        ft.attach(frgSettings);
        ft.detach(frgTrigger);
        ft.attach(frgTrigger);

        //remove fragments that are still on

        //ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);

        if (currExpApart1 != null && currExpApart1 != "") {
            ft.remove(getSupportFragmentManager().findFragmentByTag(currExpApart1));
            currExpApart1 = "";
        }
        if (currExpApart2 != null && currExpApart2 != "") {
            ft.remove(getSupportFragmentManager().findFragmentByTag(currExpApart2));
            currExpApart2 = "";
        }
        ft.commit();
    }

    private String generalPressed(MasterSlidebarFragment myFragment, final String propertyName, int frameLayoutToAppear,FragmentManager fm, int frameLayoutId) {

        //getting possible values
        List<String> valueList;
        try {
            valueList = camera.getCameraPropertyValueList(propertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return null;
        }
        if (valueList == null || valueList.size() == 0) return null;
        //Todo: this is ugly find other way
        //set possible values for display
        fSettings.SetExposureCorrValues(valueList);

        myFragment.setBarStringArr(valueList.toArray(new String[0]));
        //get Value
        String value;
        try {
            value = camera.getCameraPropertyValue(propertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return null;
        }
        if (value == null) return null;

        String currFlName;
        //if 2nd row
        if (frameLayoutId == 1)
            currFlName = currExpApart2;
        else
            currFlName = currExpApart1;
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
        Log.d(TAG, "framLayoutToappear: "+ frameLayoutToAppear+" fragment: "+ myFragment+" propName: "+ propertyName);

        if ((getSupportFragmentManager().findFragmentByTag(propertyName)) != null) {
            Log.d(TAG, "Exists");
            Log.d(TAG, "framLayoutToappear: "+ frameLayoutToAppear+" fragment: "+ myFragment+" propName: "+ propertyName);
            ft.replace(frameLayoutToAppear, myFragment, propertyName);
            currFlName = propertyName;
        } else {
            Log.d(TAG, "New");
            Log.d(TAG, "framLayoutToappear: "+ frameLayoutToAppear+" fragment: "+ myFragment+" propName: "+ propertyName);
            myFragment.setSliderValueListener(new MasterSlidebarFragment.sliderValue() {
                @Override
                public void onSlideValueBar(String value) {

                    fSettings.SetSliderResult(value, propertyName);
                }
            });
            ft.add(frameLayoutToAppear, myFragment, propertyName);
            currFlName = propertyName;
        }
        ft.commit();

        //todo:find better way of doing
        if (frameLayoutId == 1) {
            currExpApart2 = currFlName;
        }
        else {
            currExpApart1 = currFlName;
        }
        return propertyName;
    }

    @Override
    public void onDisconnectedByError(OLYCamera olyCamera, OLYCameraKitException e) {
        Toast.makeText(this, "Connection to Camera Lost, please Reconnect", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }


    @Override
    public void onUpdateCameraProperty(OLYCamera olyCamera, String s) {

    }

    @Override
    public void onSlideValueBar(String value) {
        String propValue = value;
        String property = extractProperty(value);
        fSettings.SetSliderResult(property, propValue);

    }

    private String extractProperty(String value) {
        String[] myStringArr = value.split("/");
        String extractedString = myStringArr[0].substring(1);
        return extractedString;
    }
}



