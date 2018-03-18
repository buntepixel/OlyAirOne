package com.example.mail.OlyAirONE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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
        implements FragmentLiveView.OnLiveViewInteractionListener, FragmentTrigger.OnTriggerFragmInteractionListener,
        FragmentSlidebarMaster.sliderValue, FragmentSettings.OnSettingsFragmInteractionListener,
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

    private static final String CAMERA_PROPERTY_IMAGE_PREVIEW = "RECVIEW";
    private static final String CAMERA_LIVEVIEWSIZE = "LIVEVIESIZE";


    private static List<String> takeModeStrings;

    private Executor connectionExecutor = Executors.newFixedThreadPool(1);
    static int currTakeMode = 0;

    private FragmentManager fm;
    private FragmentTrigger fTrigger;
    private FragmentLiveView fLiveView;
    private FragmentSettings fSettings;
    private FragmentSlidebarMasterAperture apartureFragment;
    private FragmentSlidebarMasterIso mFragmentIso;
    private FragmentSlidebarMasterWb wbFragment;
    private FragmentSlidebarMasterShutter shutterSpeedFragment;
    private FragmentSlidebarMasterExposureCorr exposureCorrFragment;
    static OLYCamera camera = null;
    private SharedPreferences preferences;
    public static List<String> possibleExpCorrValues;


    public static List<String> getTakeModeStrings() {
        return takeModeStrings;
    }

    public static OLYCamera getCamera() {
        return camera;
    }

    //-----------------
    //   Setup
    //-----------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        preferences = getSharedPreferences(getResources().getString(R.string.pref_SharedPrefs), MODE_PRIVATE);

        //--------------------------
        //setContentView(R.layout.activity_camera);
        fm = getSupportFragmentManager();
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        //Log.d(TAG, "ONCreate");
        if (savedInstanceState != null) {
            Log.d(TAG, ":::::::::::::::::::setsavedInstances::::::::::::::::::::::");
            fSettings = (FragmentSettings) fm.getFragment(savedInstanceState, FRAGMENT_TAG_SETTINGS);
            fTrigger = (FragmentTrigger) fm.getFragment(savedInstanceState, FRAGMENT_TAG_TRIGGER);
            fLiveView = (FragmentLiveView) fm.getFragment(savedInstanceState, FRAGMENT_TAG_LIVEVIEW);
            return;
        }
        Log.d(TAG, "onCreate__" + "Creating Camera Object");
        camera = new OLYCamera();
        camera.setContext(this);
        camera.setConnectionListener(this);

        //add Trigger,LiveView Fragment
        //Log.d(TAG, "onCreate__" + "Creating Fragments,setting olycam to fragments");
        fTrigger = new FragmentTrigger();
        fSettings = new FragmentSettings();
        fLiveView = new FragmentLiveView();

        Log.d(TAG, "onResume__" + "bevoreCommit");
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fl_FragCont_Trigger, fTrigger, FRAGMENT_TAG_TRIGGER);
        fragmentTransaction.add(R.id.fl_FragCont_Settings, fSettings, FRAGMENT_TAG_SETTINGS);
        fragmentTransaction.add(R.id.fl_FragCont_cameraLiveImageView, fLiveView, FRAGMENT_TAG_LIVEVIEW);
        fragmentTransaction.commit();
        Log.d(TAG, "onResume__" + "AfterCommit");

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getInt("currTakeMode");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "START Resume");
        if (!camera.isConnected()) {
            Log.d(TAG, "Cam Is NOT connected");
            startConnectingCamera();
        } else {
            Log.d(TAG, "Cam Is connected");
            onConnectedToCamera();
        }
        Log.d(TAG, "END Resume");
    }

    //-----------------
    //   close
    //-----------------
    @Override
    public void onPause() {
        super.onPause();
        removeVisibleSliderFragments();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, ":::::::::::::::::::::::::::SaveInstanceState:::::::::::::::::::::::");
        if (fm.findFragmentByTag(FRAGMENT_TAG_LIVEVIEW) != null)
            fm.putFragment(outState, FRAGMENT_TAG_LIVEVIEW, fLiveView);
        if (fm.findFragmentByTag(FRAGMENT_TAG_SETTINGS) != null)
            fm.putFragment(outState, FRAGMENT_TAG_SETTINGS, fSettings);
        if (fm.findFragmentByTag(FRAGMENT_TAG_TRIGGER) != null)
            fm.putFragment(outState, FRAGMENT_TAG_TRIGGER, fTrigger);
        outState.putInt("currTakeMode", currTakeMode);
    }

    @Override
    public void onStop() {
        super.onStop();
        saveCamSettings();
        try {
            camera.disconnectWithPowerOff(false);
        } catch (OLYCameraKitException e) {
            Log.w(this.toString(), "To disconnect from the camera is failed.");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        if (camera.isConnected())
            intent.putExtra("correctNetwork", false);
        else
            intent.putExtra("correctNetwork", true);
        startActivity(intent);
    }

    //-----------------
    //   Interaction
    //-----------------

    //-----------------
    //  Interaction
    //-----------------
    @Override
    public void onDriveModeButtonPressed(int currDriveMode) {
        try {
            try {
                camera.setCameraPropertyValue(CAMERA_PROPERTY_TAKE_MODE, takeModeStrings.get(currDriveMode));
            } catch (Exception e) {
                e.printStackTrace();
            }
            possibleExpCorrValues = getCamPropertyValues(CAMERA_PROPERTY_EXPOSURE_COMPENSATION);
            Log.d(TAG, "possibelvals: " + possibleExpCorrValues.toString());

            currTakeMode = currDriveMode;
            setTakeModeInFragments(currDriveMode);
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);
        }
    }

    @Override
    public void onShutterTouched(MotionEvent event) {
        fLiveView.onShutterTouched(event);
    }

    @Override
    public void onRecordVideoPressed(Boolean bool) {
        fTrigger.setTriggerButtonSelected(bool);
    }

    //..............


    @Override
    public void onButtonsInteraction(int settingsType) {
        try {
            //if Manual mode we have 2 fragment sliders
            if (currTakeMode == 4 && settingsType <= 1) {
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
                        generalPressed(mFragmentIso, CAMERA_PROPERTY_ISO_SENSITIVITY, R.id.fl_FragCont_ExpApart1);
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
        String property = extractProperty(value);
        fSettings.SetSliderResult(property, value);

    }

    //------------------------
    //    Updates
    //------------------------
    @Override
    public void updateDriveModeImage() {
        fTrigger.updateDrivemodeImageView();
    }

    //------------------------
    //    Connecting Camera
    //------------------------
    private void startConnectingCamera() {
        Log.d(TAG, "startConnectingCamera__" + "Adding trigger fragment to View");
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
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
                try {
                    camera.changeLiveViewSize(toLiveViewSize(preferences.getString("live_view_quality", "QVGA")));
                } catch (OLYCameraKitException e) {
                    Log.w(TAG, "You had better uninstall this application and install it again.");
                    alertConnectingFailed(e);
                    return;
                }
                Log.d(TAG, "startConnectingCamera__" + "OLYCamera.RunMode.Recording");
                try {
                    camera.changeRunMode(OLYCamera.RunMode.Recording);
                } catch (OLYCameraKitException e) {
                    alertConnectingFailed(e);
                    return;
                }
                Log.d(TAG, "startConnectingCamera__" + "Restores my settings");
                // Restores my settings.
                restoreCamSettings(preferences);
                fLiveView.updateImageViews();//update Views
                Log.d(TAG, "::::::::::::::::::::::::::::-----Restored Settings----:::::::::::::::::::::::::::::::::: ");
                if (!camera.isAutoStartLiveView()) { // Please refer a document about OLYCamera.autoStartLiveView.
                    // Start the live-view.
                    // If you forget calling this method, live view will not be displayed on the screen.
                    try {
                        camera.startLiveView();
                        Log.d(TAG, "StartedLiveView");
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
        try {
            Log.d(TAG, "Connected to Cam");
            fTrigger.setCamera(camera);
            try {
                //get Takemode strings for static variable
                takeModeStrings = camera.getCameraPropertyValueList(CAMERA_PROPERTY_TAKE_MODE);
                currTakeMode = takeModeStrings.indexOf(camera.getCameraPropertyValue(CAMERA_PROPERTY_TAKE_MODE));
                possibleExpCorrValues = camera.getCameraPropertyValueList(CAMERA_PROPERTY_EXPOSURE_COMPENSATION);
            } catch (OLYCameraKitException e) {
                e.printStackTrace();
            }
            setTakeModeInFragments(currTakeMode);
            createSliderFragments();

        } catch (Exception e) {
            e.printStackTrace();
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
        startActivity(new Intent(this, MainActivity.class));
    }

    //------------------------
    //     Helpers
    //------------------------
    private void setTakeModeInFragments(int mode) {
        Log.d(TAG, "Mode: " + mode);
        // fSettings = (FragmentSettings) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_SETTINGS);
        if (fSettings != null) {
            fSettings.UpdateSliderButtons();
            fTrigger.setTakeMode(mode);
        } else {
            Log.w(TAG, "couldn't find Fragment with tag: Trigger");
        }
        //remove fragments that are still on
        removeVisibleSliderFragments();
    }

    private void createSliderFragments() {
        //create slider fragments.
        Log.d(TAG, "Creating Slider Fragments");
        apartureFragment = FragmentSlidebarMasterAperture.newInstance(getCamPropertyValues(CAMERA_PROPERTY_APERTURE_VALUE), getCamPropertyValue(CAMERA_PROPERTY_APERTURE_VALUE));
        shutterSpeedFragment = FragmentSlidebarMasterShutter.newInstance(getCamPropertyValues(CAMERA_PROPERTY_SHUTTER_SPEED), getCamPropertyValue(CAMERA_PROPERTY_SHUTTER_SPEED));
        exposureCorrFragment = FragmentSlidebarMasterExposureCorr.newInstance(possibleExpCorrValues, getCamPropertyValue(CAMERA_PROPERTY_EXPOSURE_COMPENSATION));
        mFragmentIso = FragmentSlidebarMasterIso.newInstance(getCamPropertyValues(CAMERA_PROPERTY_ISO_SENSITIVITY), getCamPropertyValue(CAMERA_PROPERTY_ISO_SENSITIVITY));
        wbFragment = FragmentSlidebarMasterWb.newInstance(getCamPropertyValues(CAMERA_PROPERTY_WHITE_BALANCE), getCamPropertyValue(CAMERA_PROPERTY_WHITE_BALANCE));
    }

    private void removeVisibleSliderFragments() {
        //if there is a fragment loaded remove it
        Log.d(TAG, "RemoveVisFragment");
        FragmentSlidebarMaster fragment1 = (FragmentSlidebarMaster) fm.findFragmentById(R.id.fl_FragCont_ExpApart1);
        if (fragment1 != null) {
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(fragment1);
            Log.d(TAG, "Removing expApart1");
            FragmentSlidebarMaster fragment2 = (FragmentSlidebarMaster) fm.findFragmentById(R.id.fl_FragCont_ExpApart2);
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

    private void generalPressed(FragmentSlidebarMaster myFragment, final String propertyName, int frameLayout) {
        //getting possible values
        List<String> valueList = getCamPropertyValues(propertyName);
        if (valueList == null || valueList.size() == 0) return;
        //get Value
        String value = getCamPropertyValue(propertyName);
        Log.d(TAG, "Value: " + value);
        if (value == null) return;
        try {
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            //Remove Fragment showing
            final FragmentSlidebarMaster myFrag = (FragmentSlidebarMaster) fm.findFragmentById(frameLayout);

            Log.d(TAG, "myFrag: " + myFrag + " myFragment: " + myFragment);
            if (myFrag == myFragment) {
                ft.remove(myFrag);
                ft.commit();
            } else {
                if (myFrag != null) {
                    Log.d(TAG, "Exists, Value: " + value);
                    myFragment.updateBundle(valueList, value);
                    ft.replace(frameLayout, myFragment, propertyName);
                    //set slider to curr value
                    myFrag.SetSliderBarValIdx(value);

                    //if we had 2 sliders remove the 2nd
                    FragmentSlidebarMaster myFrag2 = (FragmentSlidebarMaster) fm.findFragmentById(R.id.fl_FragCont_ExpApart2);
                    if (myFrag2 != null)
                        ft.remove(myFrag2);
                } else {
                    Log.d(TAG, "New");
                    myFragment.updateBundle(valueList, value);
                    myFragment.setSliderValueListener(new FragmentSlidebarMaster.sliderValue() {
                        @Override
                        public void onSlideValueBar(String value) {
                            fSettings.SetSliderResult(value, propertyName);
                        }
                    });
                    ft.add(frameLayout, myFragment, propertyName);
                }
                ft.commit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String extractProperty(String value) {
        try {
            //Log.d(TAG, "prop: " + value);
            String[] myStringArr = value.split("/");
            return myStringArr[0].substring(1);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String extractValue(String value) {
        try {
            Log.d(TAG, "val: " + value);
            String[] myStringArr = value.split("/");
            return myStringArr[1].substring(0, myStringArr[1].length() - 1);
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return "";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
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

    private OLYCamera.LiveViewSize toLiveViewSize(String quality) {
        if (quality.equalsIgnoreCase("QUAD_VGA"))
            return OLYCamera.LiveViewSize.QUAD_VGA;
        else if (quality.equalsIgnoreCase("QVGA"))
            return OLYCamera.LiveViewSize.QVGA;
        else if (quality.equalsIgnoreCase("VGA"))
            return OLYCamera.LiveViewSize.VGA;
        else if (quality.equalsIgnoreCase("SVGA"))
            return OLYCamera.LiveViewSize.SVGA;
        else if (quality.equalsIgnoreCase("XGA"))
            return OLYCamera.LiveViewSize.XGA;

        return OLYCamera.LiveViewSize.QVGA;
    }

    public static String setCameraProperty(View inView, String inPropertyName) {
        final List<String> valueList;
        String retVal = "";
        try {//get List of values
            valueList = camera.getCameraPropertyValueList(inPropertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return "";
        }
        if (valueList == null || valueList.size() == 0) return retVal;

        String value;
        try {//get actual Value
            value = camera.getCameraPropertyValue(inPropertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return retVal;
        }
        if (value == null) return retVal;
        // inView.setSelected(true);//todo: can be deleted?????
        try {
            int index = valueList.indexOf(value) + 1;//get Index of current value and increment
            //Log.d(TAG, "Index: " + index);
            int listSize = valueList.size();
            //Log.d(TAG, "listSize: " + listSize);
            int moduloIndex = index % listSize;//wrap around if index out of range
            Log.d(TAG, "Property: " + inPropertyName + " Value: " + valueList.get(moduloIndex));
            retVal = valueList.get(moduloIndex);//get correct value of List of possibel values and set in camera
            camera.setCameraPropertyValue(inPropertyName, retVal);
            return retVal;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public static void updatePropertyImageView(ImageView imageView, Map<String, Integer> iconList, String propertyName) {
        try {
            imageView.setEnabled(camera.canSetCameraProperty(propertyName));
            Log.d(TAG, "Update: " + propertyName);
            String propValue;
            try {
                propValue = camera.getCameraPropertyValue(propertyName);
                Log.d(TAG, "PropVal: " + propValue);

            } catch (OLYCameraKitException e) {
                e.printStackTrace();
                return;
            }

            if (propValue == null) {
                return;
            }
            CameraActivity.updateImageView(imageView, iconList, propValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void updateImageView(ImageView imageView, Map<String, Integer> iconList, String propValue) {
        Log.d(TAG, "update imageView: " + propValue);
        try {
            if (iconList.containsKey(propValue)) {
                int resId = iconList.get(propValue);
                imageView.setImageResource(resId);
            } else {
                imageView.setImageDrawable(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //------------------------
    //     SaveSettings
    //------------------------
    private void saveCamSettings() {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        Log.d(TAG, "SavingCamSettings");
        // SharedPreferences settings = MainActivity.getPreferences();
        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.pref_SharedPrefs), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        String value;
        OLYCamera.LiveViewSize live_view_quality;
        try {
            for (String name : Arrays.asList(
                    CAMERA_PROPERTY_TAKE_MODE,
                    CAMERA_PROPERTY_DRIVE_MODE,
                    CAMERA_PROPERTY_METERING_MODE,
                    CAMERA_PROPERTY_APERTURE_VALUE,
                    CAMERA_PROPERTY_SHUTTER_SPEED,
                    CAMERA_PROPERTY_EXPOSURE_COMPENSATION,
                    CAMERA_PROPERTY_ISO_SENSITIVITY,
                    CAMERA_PROPERTY_WHITE_BALANCE,
                    CAMERA_PROPERTY_IMAGE_PREVIEW
            )) {
                value = camera.getCameraPropertyValue(name);
                editor.putString(name, value);
                Log.d(TAG, "Saved: " + name + "  =  " + value);
            }
            live_view_quality = camera.getLiveViewSize();
            if (live_view_quality == OLYCamera.LiveViewSize.QUAD_VGA)
                editor.putString(CAMERA_LIVEVIEWSIZE, "QUAD_VGA");
            else if (live_view_quality == OLYCamera.LiveViewSize.QVGA)
                editor.putString(CAMERA_LIVEVIEWSIZE, "QUAD");
            else if (live_view_quality == OLYCamera.LiveViewSize.SVGA)
                editor.putString(CAMERA_LIVEVIEWSIZE, "SVGA");
            else if (live_view_quality == OLYCamera.LiveViewSize.VGA)
                editor.putString(CAMERA_LIVEVIEWSIZE, "VGA");
            else if (live_view_quality == OLYCamera.LiveViewSize.XGA)
                editor.putString(CAMERA_LIVEVIEWSIZE, "XGA");

            Log.d(TAG, "Saved: live_view_quality = " + settings.getString(CAMERA_LIVEVIEWSIZE, "noValue"));
            //General Info for settings page
            editor.putString("KitVersion", OLYCamera.getVersion());
            editor.putString("KitBuildNumber", OLYCamera.getBuildNumber());

            Map<String, Object> hardwareInformation = camera.inquireHardwareInformation();
            editor.putString("CameraVersion", (String) hardwareInformation.get(OLYCamera.HARDWARE_INFORMATION_CAMERA_FIRMWARE_VERSION_KEY));
            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                editor.putString("AppVersion", version);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            editor.putInt("takeMode", currTakeMode);

        } catch (OLYCameraKitException ex) {
            ex.printStackTrace();
            editor.putString("CameraVersion", "Unknown");
            editor.putString("LensVersion", "Unknown");
        }
        // Commit the edits!
        editor.apply();
    }


    private void restoreCamSettings(SharedPreferences preferences) {
        if (preferences.getBoolean("firstTimeGetPref", true)) {
            SetInitSettings();
        }
        if (camera.isConnected()) {
            Map<String, String> values = new HashMap<>();
            for (String name : Arrays.asList(
                    CAMERA_PROPERTY_TAKE_MODE,
                    CAMERA_PROPERTY_DRIVE_MODE,
                    CAMERA_PROPERTY_METERING_MODE,
                    CAMERA_PROPERTY_APERTURE_VALUE,
                    CAMERA_PROPERTY_SHUTTER_SPEED,
                    CAMERA_PROPERTY_EXPOSURE_COMPENSATION,
                    CAMERA_PROPERTY_ISO_SENSITIVITY,
                    CAMERA_PROPERTY_WHITE_BALANCE,
                    CAMERA_PROPERTY_IMAGE_PREVIEW,
                    "ASPECT_RATIO",
                    "COMPRESSIBILITY_RATIO",
                    "IMAGESIZE",
                    "DESTINATION_FILE",
                    "QUALITY_MOVIE",
                    "QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME",
                    "CONTINUOUS_SHOOTING_VELOCITY",
                    "FACE_SCAN",
                    "RAW",
                    "RECVIEW"
            )) {
                String value = preferences.getString(name, null);
                if (value != null) {
                    Log.d(TAG, "Name: " + name + "  Value: " + value);
                    if (name.equals("FACE_SCAN")) {
                        if (value.equals("<FACE_SCAN/FACE_SCAN_OFF>"))
                            fLiveView.setEnabledFaceScan(false);
                        else
                            fLiveView.setEnabledFaceScan(true);
                    }
                    values.put(name, value);
                }
            }
            Log.d(TAG, "camvalues to set: " + values.size());
            if (values.size() > 0) {
                try {
                    camera.setCameraPropertyValues(values);
                } catch (OLYCameraKitException e) {
                    Log.w(TAG, "To change the camera properties has failed: " + e.getMessage());
                }
            }
            currTakeMode = preferences.getInt("takeMode", 0);

            //setTouchShutter
            Boolean touchShutterEnabled = "ON".equals(CameraActivity.extractValue(preferences.getString("TOUCHSHUTTER", "<TOUCHSHUTTER/ON>")));
            fLiveView.setEnabledTouchShutter(touchShutterEnabled);
            Boolean timeLapseEnabled = "ON".equals(CameraActivity.extractValue(preferences.getString("TIMELAPSE", "<TIMELAPSE/ON>")));
            fLiveView.setEnabledTimeLapse(timeLapseEnabled);
            Boolean rawEnabled = "ON".equals(CameraActivity.extractValue(preferences.getString("RAW", "<RAW/ON>")));
            fLiveView.updateRecordTypeText();
        }
    }

    private void SetInitSettings() {
        SharedPreferences.Editor editor = preferences.edit();
      /*  editor.putString(CamSettingsActivity.AEB_SPREADTAG, "<SPREAD/1>");
        editor.putString(CamSettingsActivity.AEB_IMAGETAG, "<NBPIC/3>");*/

        /*editor.putString(CamSettingsActivity.TL_INTERVALL, "<NBPIC/3>");
        editor.putString(CamSettingsActivity.TL_NBIMAGES, "<NBPIC/3>");*/
        editor.putString("ASPECT_RATIO", "<ASPECT_RATIO/06_06>");
        editor.putString("COMPRESSIBILITY_RATIO", "<COMPRESSIBILITY_RATIO/CMP_4>");
        editor.putString("IMAGESIZE", "<IMAGESIZE/4608x3456>");
        editor.putString("DESTINATION_FILE", "<DESTINATION_FILE/DESTINATION_FILE_MEDIA>");
        editor.putString("QUALITY_MOVIE", "<QUALITY_MOVIE/QUALITY_MOVIE_FULL_HD_NORMAL>");
        editor.putString("QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/5>");
        editor.putString("CONTINUOUS_SHOOTING_VELOCITY", "<CONTINUOUS_SHOOTING_VELOCITY/6>");
        editor.putString("FACE_SCAN", "<FACE_SCAN/FACE_SCAN_ON>");
        editor.putString("RAW", "<RAW/ON>");
        editor.putString("RECVIEW", "<RECVIEW/ON>");
        editor.putString("TIMELAPSE", "<TIMELAPSE/OFF>");
        editor.putString("TOUCHSHUTTER", "<TOUCHSHUTTER/ON>");
        editor.putBoolean("firstTimeGetPref", false);


        editor.apply();

    }

}



