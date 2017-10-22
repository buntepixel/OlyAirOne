package com.example.mail.fragmenttest;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraKitException;
import jp.co.olympus.camerakit.OLYCameraLiveViewListener;
import jp.co.olympus.camerakit.OLYCameraPropertyListener;
import jp.co.olympus.camerakit.OLYCameraStatusListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class LiveViewFragment extends Fragment implements OLYCameraLiveViewListener,
        OLYCameraPropertyListener, OLYCameraStatusListener, View.OnClickListener {
    private static final String TAG = LiveViewFragment.class.getSimpleName();

    int[] shootingModeDrawablesArr = new int[]{R.drawable.ic_iautomode, R.drawable.ic_programmmode, R.drawable.ic_aparturemode,
            R.drawable.ic_shuttermode, R.drawable.ic_manualmode, R.drawable.ic_artmode, R.drawable.ic_videomode};
    private static final int SHOOTING_MODE_IAUTO = 0;
    private static final int SHOOTING_MODE_P = 1;
    private static final int SHOOTING_MODE_A = 2;
    private static final int SHOOTING_MODE_S = 3;
    private static final int SHOOTING_MODE_M = 4;
    private static final int SHOOTING_MODE_ART = 5;
    private static final int SHOOTING_MODE_MOVIE = 6;



    private static final String CAMERA_PROPERTY_TAKE_MODE = "TAKEMODE";
    private static final String CAMERA_PROPERTY_DRIVE_MODE = "TAKE_DRIVE";
    private static final String CAMERA_PROPERTY_FOCUS_STILL = "FOCUS_STILL";
    private static final String CAMERA_PROPERTY_FOCUS_MOVIE = "FOCUS_MOVIE";
    private static final String CAMERA_PROPERTY_WHITE_BALANCE = "WB";
    private static final String CAMERA_PROPERTY_BATTERY_LEVEL = "BATTERY_LEVEL";

    private ImageView batteryLevelImageView;
    private TextView remainingRecordableImagesTextView;
    private TextView focusModeTextView;
    private ImageButton ib_shootingMode;
    // private List<String> focusModesList;
    private TextView takemodeTextView;
    private TextView shutterSpeedTextView;
    private TextView apertureValueTextView;
    private TextView exposureCompensationTextView;
    private TextView isoSensitivityTextView;
    private ImageView whiteBalanceImageView;
    private ImageView shutterImageView;
    private ImageView settingImageView;
    private ImageView unlockImageView;
    //	private RectF imageUserInteractionArea = new RectF(0, 0, 1, 1);
    private MediaPlayer focusedSoundPlayer;
    private MediaPlayer shutterSoundPlayer;
    private Boolean enabledTouchShutter;
    private Boolean enabledFocusLock;
    private CameraLiveImageView imageView;
    private OLYCamera camera;
    private int shootingModeCounter = 0;
    private OnLiveViewInteractionListener mOnLiveViewInteractionListener;


    public interface OnLiveViewInteractionListener {
        void onShootingModeButtonPressed(int currDriveMode);
    }

    @SuppressWarnings("serial")
    private static final Map<String, Integer> whiteBalanceIconList = new HashMap<String, Integer>() {
        {
            put("<WB/WB_AUTO>", R.drawable.icn_wb_setting_wbauto);
            put("<WB/MWB_SHADE>", R.drawable.icn_wb_setting_16);
            put("<WB/MWB_CLOUD>", R.drawable.icn_wb_setting_17);
            put("<WB/MWB_FINE>", R.drawable.icn_wb_setting_18);
            put("<WB/MWB_LAMP>", R.drawable.icn_wb_setting_20);
            put("<WB/MWB_FLUORESCENCE1>", R.drawable.icn_wb_setting_35);
            put("<WB/MWB_WATER_1>", R.drawable.icn_wb_setting_64);
            put("<WB/WB_CUSTOM1>", R.drawable.icn_wb_setting_512);
        }
    };

    @SuppressWarnings("serial")
    private static final Map<String, Integer> batteryIconList = new HashMap<String, Integer>() {
        {
            put("<BATTERY_LEVEL/UNKNOWN>", 0);
            put("<BATTERY_LEVEL/CHARGE>", R.drawable.tt_icn_battery_charge);
            put("<BATTERY_LEVEL/EMPTY>", R.drawable.tt_icn_battery_empty);
            put("<BATTERY_LEVEL/WARNING>", R.drawable.tt_icn_battery_half);
            put("<BATTERY_LEVEL/LOW>", R.drawable.tt_icn_battery_middle);
            put("<BATTERY_LEVEL/FULL>", R.drawable.tt_icn_battery_full);
            put("<BATTERY_LEVEL/EMPTY_AC>", R.drawable.tt_icn_battery_supply_empty);
            put("<BATTERY_LEVEL/SUPPLY_WARNING>", R.drawable.tt_icn_battery_supply_half);
            put("<BATTERY_LEVEL/SUPPLY_LOW>", R.drawable.tt_icn_battery_supply_middle);
            put("<BATTERY_LEVEL/SUPPLY_FULL>", R.drawable.tt_icn_battery_supply_full);
        }
    };


    public LiveViewFragment() {
        // Required empty public constructor
    }

    public void SetOLYCam(OLYCamera camera) {
        this.camera = camera;
    }

    //----------------------
    //   Functionality
    //----------------------
    @Override
    public void onUpdateCameraProperty(final OLYCamera camera, final String name) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Log.d(TAG, "updateCameraProp: "+ name);
                switch (name) {
                    case CAMERA_PROPERTY_BATTERY_LEVEL:
                        //todo: check why not working
                        Log.d(TAG, "BATERY LEVEL,onUpdateCameraProperty: " + name);
                        updateBatteryLevelImageView();
                        break;
                    case CAMERA_PROPERTY_FOCUS_STILL:
                        //Log.d(TAG, "FOCUS_STILL,onUpdateCameraProperty: " + name);
                        //Log.d(TAG,  "PropVal: "+ property);
                        updateFocusModeTextView(name);

                    case CAMERA_PROPERTY_FOCUS_MOVIE:
                        updateFocusModeTextView(name);
                        break;
                }

               /* if (name.equals(CAMERA_PROPERTY_TAKE_MODE)) {
                    Log.d(TAG, "::::::::::::::::::CAMERA_PROPERTY_TAKE_MODE updated:::::::::::::::");
                    //updateTakemodeTextView();
                } else if (name.equals(CAMERA_PROPERTY_DRIVE_MODE)) {
                    Log.d(TAG, "::::::::::::::::::CAMERA_PROPERTY_DRIVE_MODE updated:::::::::::::::");
                    //updateDrivemodeImageView();
                } else if (name.equals(CAMERA_PROPERTY_WHITE_BALANCE)) {
                    Log.d(TAG, "::::::::::::::::::CAMERA_PROPERTY_WHITE_BALANCE:::::::::::::::");
                    //updateWhiteBalanceImageView();
                } else if (name.equals(CAMERA_PROPERTY_BATTERY_LEVEL)) {

                }*/
            }
        });
    }

    @Override
    public void onUpdateStatus(OLYCamera olyCamera, final String name) {
        //Log.d(TAG, "onUpdateStatus: " + name);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (name) {
                    case "RemainingImageCapacity":
                        updateRemainingRecordableImagesTextView();
                        break;
                    case "MediaBusy":
                        updateRemainingRecordableImagesTextView();
                        break;
                    case "HighTemperatureWarning":
                        if (camera.isHighTemperatureWarning())
                            Toast.makeText(getActivity(), "Camera is getting to hot, please turn it off and let it cool down.", Toast.LENGTH_LONG).show();
                        break;
                    case "LevelGauge":
                        //Log.d(TAG, "LevelGauge: "+name);
                        break;

                }
               /* if (name.equals("ActualApertureValue")) {
                    updateApertureValueTextView();
                } else if (name.equals("ActualShutterSpeed")) {
                    updateShutterSpeedTextView();
                } else if (name.equals("ActualExposureCompensation")) {
                    updateExposureCompensationTextView();
                } else if (name.equals("ActualIsoSensitivity")) {
                    updateIsoSensitivityTextView();
                } else if (name.equals("RemainingRecordableImages") || name.equals("MediaBusy")) {
                    updateRemainingRecordableImagesTextView();
                }*/
            }
        });
    }

    @Override
    public void onUpdateLiveView(OLYCamera olyCamera, byte[] bytes, Map<String, Object> map) {
        try {
            imageView.setImageData(bytes, map);
        } catch (Error e) {
            Log.e(TAG, "exception: " + e.getMessage());
            Log.e(TAG, "exception: " + Log.getStackTraceString(e));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == focusModeTextView) {
            focusModeTextViewDidTap();

        } else if (v == ib_shootingMode) {
            shootingModeDidTap();
        }
    }

    //----------------------
    //   Creation
    //----------------------
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            return;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_view, container, false);
        view.setId(View.generateViewId());
        imageView = (CameraLiveImageView) view.findViewById(R.id.cameraLiveImageView);

        batteryLevelImageView = (ImageView) view.findViewById(R.id.batteryLevelImageView);
        remainingRecordableImagesTextView = (TextView) view.findViewById(R.id.tv_SdCardSpaceRemain);
        focusModeTextView = (TextView) view.findViewById(R.id.tv_focusMode);
        ib_shootingMode = (ImageButton) view.findViewById(R.id.ib_RecordMode);


        focusModeTextView.setOnClickListener(this);
        ib_shootingMode.setOnClickListener(this);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

      /*  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        enabledTouchShutter = preferences.getBoolean("touch_shutter", false);
        unlockImageView.setVisibility(enabledTouchShutter ? View.INVISIBLE : View.VISIBLE);
*/
        camera.setLiveViewListener(this);
        camera.setCameraPropertyListener(this);
        camera.setCameraStatusListener(this);
        /*camera.setRecordingListener(this);
        camera.setRecordingSupportsListener(this);*/

        // updateDrivemodeImageView();
//        updateTakemodeTextView();
//        updateShutterSpeedTextView();
//        updateApertureValueTextView();
//        updateExposureCompensationTextView();
//        updateIsoSensitivityTextView();
        //updateWhiteBalanceImageView();
        updateBatteryLevelImageView();
        updateRemainingRecordableImagesTextView();

//        try {
//            camera.clearAutoFocusPoint();
//            camera.unlockAutoFocus();
//        } catch (OLYCameraKitException ee) {
//        }
//        enabledFocusLock = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        camera.setLiveViewListener(null);
        camera.setCameraPropertyListener(null);
        camera.setCameraStatusListener(null);
       /* camera.setRecordingListener(null);
        camera.setRecordingSupportsListener(null);*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            focusedSoundPlayer = MediaPlayer.create(getContext(), R.raw.focusedsound);
            shutterSoundPlayer = MediaPlayer.create(getContext(), R.raw.shuttersound);
            camera = camera;

            mOnLiveViewInteractionListener = (OnLiveViewInteractionListener) context;
            Log.d(TAG, "finished onAttatch");
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTriggerFragmInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    // -------------------------------------------------------------------------
    //  Tap Methods
    // -------------------------------------------------------------------------

    private void focusModeTextViewDidTap() {
        Log.d(TAG, "Click focusmode");
        String propName, propVal, newPropVal = "";
        List<String> propValues;
        if (shootingModeCounter == SHOOTING_MODE_MOVIE)
            propName = CAMERA_PROPERTY_FOCUS_MOVIE;
        else
            propName = CAMERA_PROPERTY_FOCUS_STILL;

        try {
            propValues = camera.getCameraPropertyValueList(propName);
            propVal = camera.getCameraPropertyValue(propName);
            for (int i = 0; i < propValues.size(); i++) {
                if (propValues.get(i).equals(propVal)) {
                    //Log.d(TAG,"i: "+ i +" size: "+propValues.size());
                    int tmpIdx = (i+1) % (propValues.size());
                    //Log.d(TAG,"modulo: "+ tmpIdx);
                    newPropVal = propValues.get(tmpIdx);
                    break;
                }
            }
            if (!newPropVal.equals("")) {
                Log.d(TAG,"SetVal: propName: "+propName+ " propVal:"+ propVal);
                camera.setCameraPropertyValue(propName, newPropVal);
            }
        } catch (OLYCameraKitException ex) {
            ex.printStackTrace();
        }
    }

    private void shootingModeDidTap() {
        shootingModeCounter++;
        int counterMode = shootingModeCounter % shootingModeDrawablesArr.length;
        ib_shootingMode.setImageResource(shootingModeDrawablesArr[counterMode]);
        if(shootingModeCounter != SHOOTING_MODE_MOVIE)
            updateFocusModeTextView(CAMERA_PROPERTY_FOCUS_STILL);
        //currDriveMode = counterMod;
        if (mOnLiveViewInteractionListener != null)
            mOnLiveViewInteractionListener.onShootingModeButtonPressed(counterMode);
    }

    // -------------------------------------------------------------------------
    // Updates
    // -------------------------------------------------------------------------
    private void updateBatteryLevelImageView() {
        String value = null;
        try {
            value = camera.getCameraPropertyValue(CAMERA_PROPERTY_BATTERY_LEVEL);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
        }

        if (batteryIconList.containsKey(value)) {
            int resId = batteryIconList.get(value);
            if (resId != 0) {
                batteryLevelImageView.setImageResource(resId);
            } else {
                batteryLevelImageView.setImageDrawable(null);
            }
        } else {
            batteryLevelImageView.setImageDrawable(null);
        }
    }

    private void updateRemainingRecordableImagesTextView() {
        final String text;
        if (camera.isConnected() || camera.getRunMode() == OLYCamera.RunMode.Recording) {
            if (camera.isMediaBusy()) {
                text = "BUSY";
            } else {
                text = String.format(Locale.getDefault(), "%d", camera.getRemainingImageCapacity());
            }
        } else {
            text = "???";
        }
        remainingRecordableImagesTextView.setText(text);
    }

    private void updateFocusModeTextView(String propName) {
        Log.d(TAG, "New FocusMode: " + propName);
        try {
            String txt = camera.getCameraPropertyValueTitle(camera.getCameraPropertyValue(propName));
            Log.d(TAG, "new FocusValues:  " + txt);

            focusModeTextView.setText(txt);
        } catch (OLYCameraKitException ex) {
            ex.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private void runOnUiThread(Runnable action) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        activity.runOnUiThread(action);
    }

}
