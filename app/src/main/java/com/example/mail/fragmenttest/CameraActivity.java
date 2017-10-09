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


public class CameraActivity extends FragmentActivity
        implements TriggerFragment.OnTriggerFragmInteractionListener, LiveViewFragment.OnLiveViewInteractionListener,
        OLYCameraConnectionListener {
    private static final String TAG = CameraActivity.class.getSimpleName();


    private static final String CAMERA_PROPERTY_TAKE_MODE = "TAKEMODE";
    private static final String CAMERA_PROPERTY_DRIVE_MODE = "TAKE_DRIVE";
    private static final String CAMERA_PROPERTY_APERTURE_VALUE = "APERTURE";
    private static final String CAMERA_PROPERTY_SHUTTER_SPEED = "SHUTTER";
    private static final String CAMERA_PROPERTY_EXPOSURE_COMPENSATION = "EXPREV";
    private static final String CAMERA_PROPERTY_ISO_SENSITIVITY = "ISO";
    private static final String CAMERA_PROPERTY_WHITE_BALANCE = "WB";
    private static final String CAMERA_PROPERTY_BATTERY_LEVEL = "BATTERY_LEVEL";

    List<String> takeModeStrings;

    //Boolean isActive = false;
    Executor connectionExecutor = Executors.newFixedThreadPool(1);
    int currDriveMode = 0;
    String currExpApart1;
    String currExpApart2;
    FragmentManager fm;
    TriggerFragment fTrigger;
    LiveViewFragment fLiveView;
    ApartureFragment apartureFragment;
    IsoFragment isoFragment;
    WbFragment wbFragment;
    ExposureFragment exposureFragment;
    public static OLYCamera camera = null;





    @Override
    public void onTakeModeButtonPressed(int currDriveMode) {
        try {
            try {
                camera.setCameraPropertyValue(CAMERA_PROPERTY_TAKE_MODE, takeModeStrings.get(currDriveMode));
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.currDriveMode = currDriveMode;
            SetMainSettingsButtons(currDriveMode);
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //Log.d(TAG, "start");
        //check for Trigger container
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }
        Log.d(TAG, "onCreate__" + "Creating Camera Object");
        camera = new OLYCamera();
        camera.setContext(getApplicationContext());
        camera.setConnectionListener(this);
        //add Trigger,LiveView Fragment
        Log.d(TAG, "onCreate__" + "Creating Fragments,setting olycam to fragments");
        fTrigger = new TriggerFragment();
        fTrigger.SetOLYCam(camera);
        fLiveView = new LiveViewFragment();
        apartureFragment = new ApartureFragment();
        //Log.d(TAG, "Setting OlyCAMERA");
        apartureFragment.SetOLYCam(camera);
        exposureFragment = new ExposureFragment();
        exposureFragment.SetOLYCam(camera);
        isoFragment = new IsoFragment();
        isoFragment.SetOLYCam(camera);
        wbFragment = new WbFragment();
        wbFragment.SetOLYCam(camera);
        fm = getSupportFragmentManager();
    }


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(parent, name, context, attrs);



        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume__" + "starting to connect to Camera");
        startConnectingCamera();

        Log.d(TAG, "onResume__" + "Adding trigger fragment to View");
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fl_FragCont_Trigger, fTrigger, "Trigger");
        fragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }



    @Override
    public void onTriggerFragmInteraction(int settingsType) {
        // Toast.makeText(getParent(), settingsType, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "settingsType: " + settingsType);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        //Log.d(TAG,"visFrag"+ fm.getFragments().toString());
        int fragLayout;
        Log.d(TAG, "currdriveMode: " + currDriveMode);
        if (currDriveMode == 4 && settingsType <= 1) {
            //ExposurePressed(ft, R.id.fl_FragCont_ExpApart2, 2);
            generalPressed(exposureFragment, CAMERA_PROPERTY_SHUTTER_SPEED, R.id.fl_FragCont_ExpApart2, ft, 1);
            generalPressed(apartureFragment, CAMERA_PROPERTY_APERTURE_VALUE, R.id.fl_FragCont_ExpApart1, ft, 0);
            //Log.d(TAG, "A_nr1: " + currExpApart1 + " nr2: " + currExpApart2);
            ft.commit();
            //Log.d(TAG, "B_nr1: " + currExpApart1 + " nr2: " + currExpApart2);

        } else {
            switch (settingsType) {
                case 0:
                    //currExpApart1 = ExposurePressed(ft, R.id.fl_FragCont_ExpApart1, 1);
                    currExpApart1 = generalPressed(exposureFragment, CAMERA_PROPERTY_SHUTTER_SPEED, R.id.fl_FragCont_ExpApart1, ft, 0);
                    break;
                case 1:
                    //AparturePressed(ft);
                    generalPressed(apartureFragment, CAMERA_PROPERTY_APERTURE_VALUE, R.id.fl_FragCont_ExpApart1, ft, 0);
                    break;
                case 2:
                    break;
                case 3:
                    //IsoPressed(ft);
                    generalPressed(isoFragment, CAMERA_PROPERTY_ISO_SENSITIVITY, R.id.fl_FragCont_ExpApart1, ft, 0);
                    break;
                case 4:
                    //WbPressed(ft);
                    generalPressed(wbFragment, CAMERA_PROPERTY_WHITE_BALANCE, R.id.fl_FragCont_ExpApart1, ft, 0);
                    break;
            }

            if (currExpApart2 != null && !currExpApart2.equals("")) {
                ft.remove(getSupportFragmentManager().findFragmentByTag(currExpApart2));
                currExpApart2 = "";
            }
            ft.commit();
        }
    }

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
              /*  try {
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
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fl_FragCont_cameraLiveImageView, fLiveView, "LiveView");
        //Todo: maybe only commit();
        fragmentTransaction.commitAllowingStateLoss();
           /* LiveViewFragment fragment = new LiveViewFragment();
            fragment.setCamera(camera);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment1, fragment);
            transaction.commitAllowingStateLoss();*/

        try {
            takeModeStrings = camera.getCameraPropertyValueList(CAMERA_PROPERTY_TAKE_MODE);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return;
        }

    }

    @Override
    public void onDisconnectedByError(OLYCamera olyCamera, OLYCameraKitException e) {
        Toast.makeText(this, "Connection to Camera Lost, please Reconnect", Toast.LENGTH_SHORT).show();
        finish();
    }

    void SetMainSettingsButtons(int mode) {
        Log.d(TAG, "Mode: " + mode);
        fTrigger = (TriggerFragment) getSupportFragmentManager().findFragmentByTag("Trigger");

        if (fTrigger != null) {
            switch (mode) {
                case 0://iAuto
                    fTrigger.SetButtonsBool(false, false, false, false, false);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Iauto");
                    break;
                case 1://Programm
                    fTrigger.SetButtonsBool(false, false, true, true, true);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Programm");
                    break;
                case 2://Aparture
                    fTrigger.SetButtonsBool(false, true, true, true, true);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Aparture");
                    break;
                case 3://Speed
                    fTrigger.SetButtonsBool(true, false, true, true, true);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Speed");
                    break;
                case 4://Manual
                    fTrigger.SetButtonsBool(true, true, false, true, true);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Manual");
                    break;
                case 5:
                    break;
                case 6://Movie
                    fTrigger.SetButtonsBool(false, false, true, false, true);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Movie");
                    break;
                case 7:
                    break;
                case 8:
                    break;
            }
        } else {
            Log.w(TAG, "couldn't find Fragment with tag: Trigger");
        }

        Fragment frg = getSupportFragmentManager().findFragmentByTag("Trigger");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg).attach(frg).commit();

        //remove fragments that are still on


        ft = getSupportFragmentManager().beginTransaction();
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

    private String generalPressed(MasterSlidebarFragment myFragment, final String propertyName, int frameLayoutToAppear, FragmentTransaction ft, int frameLayoutId) {

        //getting possible values
        List<String> valueList;
        try {
            valueList = camera.getCameraPropertyValueList(propertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return null;
        }
        if (valueList == null || valueList.size() == 0) return null;

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
        //::::::::::::::
        //todo:find better way of doing
        String currFlName;
        if (frameLayoutId == 1)
            currFlName = currExpApart2;
        else
            currFlName = currExpApart1;

        if (currFlName == propertyName) {
            //Log.d(TAG, "SameFrag");
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(getSupportFragmentManager().findFragmentByTag(propertyName));
            currFlName = "";
        } else {
            if ((getSupportFragmentManager().findFragmentByTag(propertyName)) != null) {
                //Log.d(TAG, "Exists");
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                ft.replace(frameLayoutToAppear, myFragment, propertyName);
                currFlName = propertyName;
            } else {
                //Log.d(TAG, "New");

                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                myFragment.setSliderValueListener(new MasterSlidebarFragment.sliderValue() {
                    @Override
                    public void onSlideValueBar(String value) {
                        fTrigger.SetSliderResult(value, propertyName);
                    }
                });
                ft.replace(frameLayoutToAppear, myFragment, propertyName);
                currFlName = propertyName;
            }
        }
        //todo:find better way of doing
        if (frameLayoutId == 1)
            currExpApart2 = currFlName;
        else
            currExpApart1 = currFlName;
        return propertyName;
    }



}



