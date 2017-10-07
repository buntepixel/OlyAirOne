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

    //Boolean isActive = false;
    private Executor connectionExecutor = Executors.newFixedThreadPool(1);
    int currDriveMode = 0;
    String currExpApart1;
    String currExpApart2;
    FragmentManager fm;
    TriggerFragment fTrigger;
    LiveViewFragment fLiveView;
    public static OLYCamera camera = null;

    @Override
    public void onMainSettingsButtonPressed(int currDriveMode) {
        try {
            Log.d(TAG, "Works:  " + currDriveMode);
            this.currDriveMode = currDriveMode;
            SetMainSettingsButtons(currDriveMode);
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);
        }
    }

    @Override
    public void onDisconnectedByError(OLYCamera olyCamera, OLYCameraKitException e) {
        Toast.makeText(this, "Connection to Camera Lost, please Reconnect", Toast.LENGTH_SHORT).show();
        finish();
    }


    private enum OLYRecordModes {
        IAUTO,
        P,
        A,
        S,
        M,
        ART,
        MOVIEP,
        MOVIEA,
        MOVIES,
        MOVIEM
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
        camera = new OLYCamera();
        camera.setContext(getApplicationContext());
        camera.setConnectionListener(this);
        //add Trigger,LiveView Fragment
        fTrigger = new TriggerFragment();
        fLiveView = new LiveViewFragment();
        fm = getSupportFragmentManager();



    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View myView = super.onCreateView(parent, name, context, attrs);
        return myView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startConnectingCamera();
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
            ExposurePressed(ft, R.id.fl_FragCont_ExpApart2, 2);
            AparturePressed(ft);
            //Log.d(TAG, "A_nr1: " + currExpApart1 + " nr2: " + currExpApart2);
            ft.commit();
            //Log.d(TAG, "B_nr1: " + currExpApart1 + " nr2: " + currExpApart2);
        } else {
            switch (settingsType) {
                case 0:
                    currExpApart1 = ExposurePressed(ft, R.id.fl_FragCont_ExpApart1, 1);
                    break;
                case 1:
                    AparturePressed(ft);
                    break;
                case 2:
                    break;
                case 3:
                    IsoPressed(ft);
                    break;
                case 4:
                    WbPressed(ft);
                    break;
            }
            //ft.addToBackStack("back");
            ft.commit();
            //Log.d(TAG, "C_nr1: " + currExpApart1 + " nr2: " + currExpApart2);
        }
    }

    private void startConnectingCamera() {
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CameraActivity.this);

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
                try {
                    camera.changeRunMode(OLYCamera.RunMode.Recording);
                } catch (OLYCameraKitException e) {
                    alertConnectingFailed(e);
                    return;
                }

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
        fragmentTransaction.commit();
           /* LiveViewFragment fragment = new LiveViewFragment();
            fragment.setCamera(camera);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment1, fragment);
            transaction.commitAllowingStateLoss();*/

    }

    void SetMainSettingsButtons(int mode) {
        Log.d(TAG, "Mode: " + mode);
        TriggerFragment fTrigger = (TriggerFragment) getSupportFragmentManager().findFragmentByTag("Trigger");

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

    private String ExposurePressed(FragmentTransaction ft, int FrameLayout, int FrameLayoutId) {
        Fragment myFrag;
        String currTag;
        if (FrameLayoutId == 1)
            currTag = currExpApart1;
        else
            currTag = currExpApart2;
        String myTag = "Expo";
        if (currTag == myTag) {
            //Log.d(TAG, "SameFrag");
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
            if (FrameLayoutId == 1) {
                currExpApart1 = "";
                //Log.d(TAG, "currExpoApart 1");
            } else {
                currExpApart2 = "";
                //Log.d(TAG, "currExpoApart 2:  " + currExpApart2);
            }

        } else {
            if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
                //Log.d(TAG, "Exists");
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                ft.replace(FrameLayout, myFrag, myTag);
                if (FrameLayoutId == 1)
                    currExpApart1 = myTag;
                else
                    currExpApart2 = myTag;
            } else {
                //Log.d(TAG, "New");
                ExposureFragment exposureFragment = new ExposureFragment();
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                exposureFragment.setSliderValueListener(new ApartureFragment.sliderValue() {
                    @Override
                    public void onSlideValueBar(String value) {
                        fTrigger.SetExpTimeValue(value);
                    }
                });
                ft.replace(FrameLayout, exposureFragment, myTag);
                if (FrameLayoutId == 1)
                    currExpApart1 = myTag;
                else
                    currExpApart2 = myTag;
            }
        }
        return myTag;
    }

    private void AparturePressed(FragmentTransaction ft) {
        String myTag;
        Fragment myFrag;
        myTag = "Apart";
        if (currExpApart1 == myTag) {
            ///Log.d(TAG, "SameFrag");
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
            currExpApart1 = "";
        } else {
            if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
                //Log.d(TAG, "Exists");
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                ft.replace(R.id.fl_FragCont_ExpApart1, myFrag, myTag);
                currExpApart1 = myTag;
            } else {
                //Log.d(TAG, "New");
                ApartureFragment apartureFragment = new ApartureFragment();
                apartureFragment.setSliderValueListener(new ApartureFragment.sliderValue() {
                    @Override
                    public void onSlideValueBar(String value) {
                        fTrigger.SetFstopValue(value);
                    }
                });
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                ft.replace(R.id.fl_FragCont_ExpApart1, apartureFragment, myTag);
                currExpApart1 = myTag;
            }
        }
    }

    private void IsoPressed(FragmentTransaction ft) {
        String myTag;
        Fragment myFrag;
        myTag = "Iso";
        if (currExpApart1 == myTag) {
            //Log.d(TAG, "SameFrag");
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
            currExpApart1 = "";
        } else {
            if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
                //Log.d(TAG, "Exists");
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                ft.replace(R.id.fl_FragCont_ExpApart1, myFrag, myTag);
                currExpApart1 = myTag;
            } else {
                //Log.d(TAG, "New");
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                IsoFragment isoFragment = new IsoFragment();
                isoFragment.setSliderValueListener(new MasterSlidebarFragment.sliderValue() {
                    @Override
                    public void onSlideValueBar(String value) {
                        fTrigger.SetIsoValue(value);
                    }
                });
                ft.replace(R.id.fl_FragCont_ExpApart1, isoFragment, myTag);
                currExpApart1 = myTag;
            }
        }
    }

    private void WbPressed(FragmentTransaction ft) {
        String myTag;
        Fragment myFrag;
        myTag = "Wb";
        if (currExpApart1 == myTag) {
            //Log.d(TAG, "SameFrag");
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
            currExpApart1 = "";
        } else {
            if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
                // Log.d(TAG, "Exists");
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                ft.replace(R.id.fl_FragCont_ExpApart1, myFrag, myTag);
                currExpApart1 = myTag;
            } else {
                //Log.d(TAG, "New");
                WbFragment whiteBalanceFragment = new WbFragment();
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                whiteBalanceFragment.setSliderValueListener(new MasterSlidebarFragment.sliderValue() {
                    @Override
                    public void onSlideValueBar(String value) {
                        fTrigger.SetWBValue(value);
                    }
                });
                ft.replace(R.id.fl_FragCont_ExpApart1, whiteBalanceFragment, myTag);
                currExpApart1 = myTag;
            }
        }
    }
}



