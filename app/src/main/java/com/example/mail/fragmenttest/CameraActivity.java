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

    public static final String CAMERA_PROPERTY_FOCUS_STILL = "FOCUS_STILL";
    public static final String CAMERA_PROPERTY_FOCUS_MOVIE = "FOCUS_MOVIE";
    public static final String CAMERA_PROPERTY_BATTERY_LEVEL = "BATTERY_LEVEL";

    public static final int SHOOTING_MODE_IAUTO = 0;
    public static final int SHOOTING_MODE_P = 1;
    public static final int SHOOTING_MODE_A = 2;
    public static final int SHOOTING_MODE_S = 3;
    public static final int SHOOTING_MODE_M = 4;
    public static final int SHOOTING_MODE_ART = 5;
    public static final int SHOOTING_MODE_MOVIE = 6;


    public static final String CAMERA_SETTINGS = "OlyAirOneCamSettings";

    public static final String CAMERA_PROPERTY_TAKE_MODE = "TAKEMODE";
    public static final String CAMERA_PROPERTY_DRIVE_MODE = "TAKE_DRIVE";
    public static final String CAMERA_PROPERTY_METERING_MODE = "AE";

    public static final String CAMERA_PROPERTY_APERTURE_VALUE = "APERTURE";
    public static final String CAMERA_PROPERTY_SHUTTER_SPEED = "SHUTTER";
    public static final String CAMERA_PROPERTY_EXPOSURE_COMPENSATION = "EXPREV";
    public static final String CAMERA_PROPERTY_ISO_SENSITIVITY = "ISO";
    public static final String CAMERA_PROPERTY_WHITE_BALANCE = "WB";


    static List<String> takeModeStrings;

    Executor connectionExecutor = Executors.newFixedThreadPool(1);
    int currDriveMode = 0;

    FragmentManager fm;
    TriggerFragment fTrigger;
    LiveViewFragment fLiveView;
    SettingsFragment fSettings;
    ApertureFragment apartureFragment;
    IsoFragment isoFragment;
    WbFragment wbFragment;
    ShutterFragment shutterSpeedFragment;
    ExposureCorrFragment exposureCorrFragment;

    public static OLYCamera camera = null;

    //-----------------
    //   Setup
    //-----------------
    public static List<String> getTakeModeStrings() {
        return takeModeStrings;
    }

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


            return;
        }
        Log.d(TAG, "onCreate__" + "Creating Camera Object");
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
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(parent, name, context, attrs);
        return view;
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeVisibleSliderFragments();
        saveCamSettings();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fm.putFragment(outState, FRAGMENT_TAG_LIVEVIEW, fLiveView);
        fm.putFragment(outState, FRAGMENT_TAG_SETTINGS, fSettings);
        fm.putFragment(outState, FRAGMENT_TAG_TRIGGER, fTrigger);
        outState.putInt("currDriveMode", currDriveMode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "START Resume");
        //reset Cam if we had a orientation change
        fLiveView.SetOLYCam(camera);
        fTrigger.SetOLYCam(camera);
        fSettings.SetOLYCam(camera);


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
        Log.d(TAG, "END Resume");

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currDriveMode = savedInstanceState.getInt("currDriveMode");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    //-----------------
    //   Interaction
    //-----------------
    @Override
    public void onShutterTouched(MotionEvent event) {
        fLiveView.onShutterTouched(event);
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
            setShootingModeButtons(currDriveMode);
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
    public void updateDriveModeImage(String propValue) {
        fTrigger.updateDrivemodeImageView(propValue);
    }

    /*   @Override
       public void updateDrivemodeImage() {

       }
   */
    @Override
    public void onDriveModeChange(String propValue) {
        fTrigger.updateAfterCamConnection();
        //fLiveView.updateFocusMode(propValue);
    }

    @Override
    public void onButtonsInteraction(int settingsType) {
        // Toast.makeText(getParent(), settingsType, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "settingsType: " + settingsType);
        Log.d(TAG, "CurrentDriveMode" + currDriveMode);
        //removeVisibleSliderFragments();

        try {
            //Log.d(TAG,"visFrag"+ fm.getFragments().toString());
            int fragLayout;
            Log.d(TAG, "currdriveMode: " + currDriveMode);
            //if Manual mode we have 2 fragment sliders
            if (currDriveMode == 4 && settingsType <= 1) {
                //ExposurePressed(ft, R.id.fl_FragCont_ExpApart2, 2);
                generalPressed(shutterSpeedFragment, CAMERA_PROPERTY_SHUTTER_SPEED, R.id.fl_FragCont_ExpApart2);
                generalPressed(apartureFragment, CAMERA_PROPERTY_APERTURE_VALUE, R.id.fl_FragCont_ExpApart1);

            } else {//all other modes
                switch (settingsType) {
                    case 0:
                        //currExpApart1 = ExposurePressed(ft, R.id.fl_FragCont_ExpApart1, 1);
                        generalPressed(shutterSpeedFragment, CAMERA_PROPERTY_SHUTTER_SPEED, R.id.fl_FragCont_ExpApart1);
                        break;
                    case 1:
                        //AparturePressed(ft);
                        generalPressed(apartureFragment, CAMERA_PROPERTY_APERTURE_VALUE, R.id.fl_FragCont_ExpApart1);
                        break;
                    case 2:
                        generalPressed(exposureCorrFragment, CAMERA_PROPERTY_EXPOSURE_COMPENSATION, R.id.fl_FragCont_ExpApart1);
                        break;
                    case 3:
                        //IsoPressed(ft);
                        generalPressed(isoFragment, CAMERA_PROPERTY_ISO_SENSITIVITY, R.id.fl_FragCont_ExpApart1);
                        break;
                    case 4:
                        //WbPressed(ft);
                        generalPressed(wbFragment, CAMERA_PROPERTY_WHITE_BALANCE, R.id.fl_FragCont_ExpApart1);
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onSlideValueBar(String value) {
        String propValue = value;
        String property = extractProperty(value);
        fSettings.SetSliderResult(property, propValue);

    }

    //------------------------
    //    Connecting Camera
    //------------------------
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

    private void onConnectedToCamera() {
        Log.d(TAG, "Connected to Cam");
        //restore Cam settings from Shared prefs
        restoreCamSettings();
        //add LiveView to fragment manager if here first time
        if (fm.findFragmentByTag(FRAGMENT_TAG_LIVEVIEW) == null) {
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.fl_FragCont_cameraLiveImageView, fLiveView, FRAGMENT_TAG_LIVEVIEW);
            //Todo: maybe only commit();
            //fragmentTransaction.commitAllowingStateLoss();
            fragmentTransaction.commit();
        }
        try {
            takeModeStrings = camera.getCameraPropertyValueList(CAMERA_PROPERTY_TAKE_MODE);
            createSliderFragments();
            fTrigger.updateAfterCamConnection();
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return;
        }
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

    @Override
    public void onDisconnectedByError(OLYCamera olyCamera, OLYCameraKitException e) {
        Toast.makeText(this, "Connection to Camera Lost, please Reconnect", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    //------------------------
    //     Helpers
    //------------------------
    private void setShootingModeButtons(int mode) {
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
        //remove fragments that are still on
        removeVisibleSliderFragments();
        //refresh view
        //todo: find other way to refresh view
        Fragment frgSettings = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_SETTINGS);
        Fragment frgTrigger = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_TRIGGER);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frgSettings);
        ft.attach(frgSettings);
        ft.detach(frgTrigger);
        ft.attach(frgTrigger);
        ft.commit();
    }

    private void createSliderFragments() {
        //create slider fragments.
        Log.d(TAG, "Creating Slider Fragments: ----------------" + getCamPropertyValues(CAMERA_PROPERTY_APERTURE_VALUE).size());
        apartureFragment = ApertureFragment.newInstance(getCamPropertyValues(CAMERA_PROPERTY_APERTURE_VALUE), getCamPropertyValue(CAMERA_PROPERTY_APERTURE_VALUE));
        apartureFragment.SetOLYCam(camera);

        shutterSpeedFragment = ShutterFragment.newInstance(getCamPropertyValues(CAMERA_PROPERTY_SHUTTER_SPEED), getCamPropertyValue(CAMERA_PROPERTY_SHUTTER_SPEED));
        shutterSpeedFragment.SetOLYCam(camera);

        exposureCorrFragment = ExposureCorrFragment.newInstance(getCamPropertyValues(CAMERA_PROPERTY_EXPOSURE_COMPENSATION), getCamPropertyValue(CAMERA_PROPERTY_EXPOSURE_COMPENSATION));
        exposureCorrFragment.SetOLYCam(camera);

        isoFragment = IsoFragment.newInstance(getCamPropertyValues(CAMERA_PROPERTY_ISO_SENSITIVITY), getCamPropertyValue(CAMERA_PROPERTY_ISO_SENSITIVITY));
        isoFragment.SetOLYCam(camera);

        wbFragment = WbFragment.newInstance(getCamPropertyValues(CAMERA_PROPERTY_WHITE_BALANCE), getCamPropertyValue(CAMERA_PROPERTY_WHITE_BALANCE));
        wbFragment.SetOLYCam(camera);
    }

    private void removeVisibleSliderFragments() {
        //if there is a fragment loaded remove it
        Log.d(TAG, "RemoveVisFragment");
        MasterSlidebarFragment fragment1 = (MasterSlidebarFragment) fm.findFragmentById(R.id.fl_FragCont_ExpApart1);
        if (fragment1 != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(fragment1);
            Log.d(TAG, "Removing expApart1");
            MasterSlidebarFragment fragment2 = (MasterSlidebarFragment) fm.findFragmentById(R.id.fl_FragCont_ExpApart2);
            if (fragment2 != null) {
                ft.remove(fragment2);
                Log.d(TAG, "Removing expApart2");
            }
            ft.commit();
        }
    }

    @Override
    public void onUpdateCameraProperty(OLYCamera olyCamera, String s) {

    }

    private void generalPressed(MasterSlidebarFragment myFragment, final String propertyName, int frameLayoutToAppear) {
        //getting possible values
        List<String> valueList = getCamPropertyValues(propertyName);
        if (valueList == null || valueList.size() == 0) return;
        //Todo: this is ugly find other way
        //set possible values for display
        fSettings.SetExposureCorrValues(valueList);

        //get Value
        String value = getCamPropertyValue(propertyName);
        Log.d(TAG, "Value: " + value);
        if (value == null) return;
        try {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            //Remove Fragment showing
            final MasterSlidebarFragment myFrag = (MasterSlidebarFragment) fm.findFragmentById(frameLayoutToAppear);

            Log.d(TAG, "myFrag: " + myFrag + " myFragment: " + myFragment);
            if (myFrag == myFragment) {
                ft.remove(myFrag);
                ft.commit();
                return;
            } else {
                if (myFrag != null) {
                    Log.d(TAG, "Exists");
                    ft.replace(frameLayoutToAppear, myFragment, propertyName);

                    //set slider to curr value
                    myFrag.SetSliderBarValIdx(value);

                    MasterSlidebarFragment myFrag2 = (MasterSlidebarFragment) fm.findFragmentById(R.id.fl_FragCont_ExpApart2);
                    if (myFrag2 != null)
                        ft.remove(myFrag2);

                } else {
                    Log.d(TAG, "New");
                    myFragment.updateBundle(valueList, value);
                    myFragment.setSliderValueListener(new MasterSlidebarFragment.sliderValue() {
                        @Override
                        public void onSlideValueBar(String value) {
                            fSettings.SetSliderResult(value, propertyName);
                        }
                    });
                    ft.add(frameLayoutToAppear, myFragment, propertyName);
                }
                ft.commit();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String extractProperty(String value) {
        String[] myStringArr = value.split("/");
        String extractedString = myStringArr[0].substring(1);
        return extractedString;
    }

    private List<String> getCamPropertyValues(String propertyName) {
        try {
            return camera.getCameraPropertyValueList(propertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getCamPropertyValue(String propertyName) {
        try {
            return camera.getCameraPropertyValue(propertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return null;
        }
    }

    //------------------------
    //     SaveSettings
    //------------------------
    private void saveCamSettings() {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        Log.d(TAG, "SavingCamSettings");
        SharedPreferences settings = MainActivity.getPreferences();
        SharedPreferences.Editor editor = settings.edit();
        String takeMode = null;
        String driveMode = null;
        String meteringMode = null;

        String aperture = null;
        String shutter = null;
        String exprev = null;
        String iso = null;
        String wb = null;

        try {
            takeMode = camera.getCameraPropertyValue(CAMERA_PROPERTY_TAKE_MODE);
            Log.d(TAG, "Saved: CAMERA_PROPERTY_TAKE_MODE = " + takeMode);

            driveMode = camera.getCameraPropertyValue(CAMERA_PROPERTY_DRIVE_MODE);
            Log.d(TAG, "Saved: CAMERA_PROPERTY_DRIVE_MODE = " + driveMode);

            meteringMode = camera.getCameraPropertyValue(CAMERA_PROPERTY_METERING_MODE);
            Log.d(TAG, "Saved: CAMERA_PROPERTY_METERING_MODE = " +meteringMode);


            aperture = camera.getCameraPropertyValue(CAMERA_PROPERTY_APERTURE_VALUE);
            shutter = camera.getCameraPropertyValue(CAMERA_PROPERTY_SHUTTER_SPEED);
            exprev = camera.getCameraPropertyValue(CAMERA_PROPERTY_EXPOSURE_COMPENSATION);
            iso = camera.getCameraPropertyValue(CAMERA_PROPERTY_ISO_SENSITIVITY);
            wb = camera.getCameraPropertyValue(CAMERA_PROPERTY_WHITE_BALANCE);

        } catch (OLYCameraKitException ex) {
            ex.printStackTrace();
        }
        editor.putString(CAMERA_PROPERTY_TAKE_MODE, takeMode);
        editor.putString(CAMERA_PROPERTY_DRIVE_MODE, driveMode);
        editor.putString(CAMERA_PROPERTY_METERING_MODE, meteringMode);

        editor.putString(CAMERA_PROPERTY_APERTURE_VALUE, aperture);
        editor.putString(CAMERA_PROPERTY_SHUTTER_SPEED, shutter);
        editor.putString(CAMERA_PROPERTY_EXPOSURE_COMPENSATION, exprev);
        editor.putString(CAMERA_PROPERTY_ISO_SENSITIVITY, iso);
        editor.putString(CAMERA_PROPERTY_WHITE_BALANCE, wb);
        // Commit the edits!
        editor.apply();
    }

    private void restoreCamSettings() {
        Log.d(TAG, "Restoring camSettings");
        SharedPreferences settings = MainActivity.getPreferences();
        if (settings != null) {
            String takeMode = settings.getString(CAMERA_PROPERTY_TAKE_MODE, null);
            String driveMode = settings.getString(CAMERA_PROPERTY_DRIVE_MODE, null);
            String meteringMode = settings.getString(CAMERA_PROPERTY_METERING_MODE, null);

            String aperture = settings.getString(CAMERA_PROPERTY_APERTURE_VALUE, null);
            String shutter = settings.getString(CAMERA_PROPERTY_SHUTTER_SPEED, null);
            String exprev = settings.getString(CAMERA_PROPERTY_EXPOSURE_COMPENSATION, null);
            String iso = settings.getString(CAMERA_PROPERTY_ISO_SENSITIVITY, null);
            String wb = settings.getString(CAMERA_PROPERTY_WHITE_BALANCE, null);
            try {
                if (takeMode != null) {
                    camera.setCameraPropertyValue(CAMERA_PROPERTY_TAKE_MODE, takeMode);
                    Log.d(TAG, "Restored: CAMERA_PROPERTY_TAKE_MODE = " + takeMode);

                }
                if (driveMode != null) {
                    camera.setCameraPropertyValue(CAMERA_PROPERTY_DRIVE_MODE, driveMode);
                    Log.d(TAG, "Restored: CAMERA_PROPERTY_DRIVE_MODE =" + driveMode);

                }
                if (meteringMode != null) {
                    camera.setCameraPropertyValue(CAMERA_PROPERTY_METERING_MODE, meteringMode);
                    Log.d(TAG, "Restored: CAMERA_PROPERTY_METERING_MODE = " + meteringMode);

                }
               /* if (aperture != null) {
                    camera.setCameraPropertyValue(CAMERA_PROPERTY_APERTURE_VALUE, aperture);
                    Log.d(TAG, "Restored: CAMERA_PROPERTY_APERTURE_VALUE");

                }
                if (shutter != null) {
                    camera.setCameraPropertyValue(CAMERA_PROPERTY_SHUTTER_SPEED, shutter);
                    Log.d(TAG, "Restored: CAMERA_PROPERTY_SHUTTER_SPEED");

                }
                if (exprev != null) {
                    camera.setCameraPropertyValue(CAMERA_PROPERTY_EXPOSURE_COMPENSATION, exprev);
                    Log.d(TAG, "Restored: CAMERA_PROPERTY_EXPOSURE_COMPENSATION");

                }
                if (iso != null) {
                    camera.setCameraPropertyValue(CAMERA_PROPERTY_ISO_SENSITIVITY, iso);
                    Log.d(TAG, "Restored: CAMERA_PROPERTY_ISO_SENSITIVITY");

                }
                if (wb != null) {
                    camera.setCameraPropertyValue(CAMERA_PROPERTY_WHITE_BALANCE, wb);
                    Log.d(TAG, "Restored: CAMERA_PROPERTY_WHITE_BALANCE");
                }
                fSettings.updateAllValues();*/
                //fLiveView.updateAllViews();

            } catch (OLYCameraKitException ex) {
                ex.printStackTrace();
            }
        }
    }

}



