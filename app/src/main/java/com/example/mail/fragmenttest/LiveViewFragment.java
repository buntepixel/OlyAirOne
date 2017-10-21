package com.example.mail.fragmenttest;


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
        OLYCameraStatusListener, OLYCameraPropertyListener {
    private static final String TAG = LiveViewFragment.class.getSimpleName();

    int[] takeModeDrawablesArr = new int[]{R.drawable.ic_iautomode, R.drawable.ic_programmmode, R.drawable.ic_aparturemode,
            R.drawable.ic_shuttermode, R.drawable.ic_manualmode, R.drawable.ic_artmode, R.drawable.ic_videomode};


    private static final String CAMERA_PROPERTY_TAKE_MODE = "TAKEMODE";
    private static final String CAMERA_PROPERTY_DRIVE_MODE = "TAKE_DRIVE";
    private static final String CAMERA_PROPERTY_FOCUS_STILL = "FOCUS_STILL";

    private static final String CAMERA_PROPERTY_WHITE_BALANCE = "WB";
    private static final String CAMERA_PROPERTY_BATTERY_LEVEL = "BATTERY_LEVEL";

    private ImageView batteryLevelImageView;
    private TextView remainingRecordableImagesTextView;
    private TextView focusModeTextView;
    private List<String> focusModesList;
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

    private OnLiveViewInteractionListener mOnLiveViewInteractionListener;

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

    @Override
    public void onUpdateCameraProperty(OLYCamera camera, final String name) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (name) {
                    case CAMERA_PROPERTY_BATTERY_LEVEL:
                        updateBatteryLevelImageView();
                        break;
                    case CAMERA_PROPERTY_FOCUS_STILL:
                        updateFocusMode(name);
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
    public void onUpdateStatus(OLYCamera olyCamera, String s) {
        switch (s) {
            case "RemainingRecordableImages":
                updateRemainingRecordableImagesTextView();
                break;
        }
    }

    public interface OnLiveViewInteractionListener {
        void onTakeModeButtonPressed(int currDriveMode);

    }


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
        focusModeTextView = (TextView) view.findViewById(R.id.tv_driveMode);
//        takemodeTextView = (TextView)view.findViewById(R.id.takemodeTextView);
//        shutterSpeedTextView = (TextView)view.findViewById(R.id.shutterSpeedTextView);
//        apertureValueTextView = (TextView)view.findViewById(R.id.apertureValueTextView);
//        exposureCompensationTextView = (TextView)view.findViewById(R.id.exposureCompensationTextView);
//        isoSensitivityTextView = (TextView)view.findViewById(R.id.isoSensitivityTextView);
//        whiteBalanceImageView = (ImageView)view.findViewById(R.id.whiteBalaneImageView);
//        shutterImageView = (ImageView)view.findViewById(R.id.shutterImageView);
//        settingImageView = (ImageView)view.findViewById(R.id.settingImageView);
//        unlockImageView = (ImageView)view.findViewById(R.id.unlockImageView);
        //get necessary values
        try {
            focusModesList = camera.getCameraPropertyValueList("FOCUS_STILL");

        } catch (OLYCameraKitException e) {
            e.printStackTrace();
        }
        setupClickables();

        //recordMode
        final ImageButton ib_RecordMode = (ImageButton) view.findViewById(R.id.ib_RecordMode);
        ib_RecordMode.setOnClickListener(new View.OnClickListener() {
            int counter = 0;

            @Override
            public void onClick(View v) {
                counter++;
                int counterMod = counter % takeModeDrawablesArr.length;
                ib_RecordMode.setImageResource(takeModeDrawablesArr[counterMod]);
                //currDriveMode = counterMod;
                if (mOnLiveViewInteractionListener != null)
                    mOnLiveViewInteractionListener.onTakeModeButtonPressed(counter % (takeModeDrawablesArr.length));
                //SetMainSettingsButtons(counter % (takeModeDrawablesArr.length));
                //Log.d(TAG, "start"+ counter);
            }
        });
        return view;
    }

    private void setupClickables() {
        focusModeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String currVal = camera.getCameraPropertyValue(CAMERA_PROPERTY_FOCUS_STILL);
                    if (currVal == focusModesList.get(0))
                        updateFocusMode(focusModesList.get(1));
                    else
                        updateFocusMode(focusModesList.get(0));

                } catch (OLYCameraKitException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

      /*  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        enabledTouchShutter = preferences.getBoolean("touch_shutter", false);
        unlockImageView.setVisibility(enabledTouchShutter ? View.INVISIBLE : View.VISIBLE);
*/
        camera.setLiveViewListener(this);
        /*camera.setCameraPropertyListener(this);
        camera.setCameraStatusListener(this);
        camera.setRecordingListener(this);
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

    @Override
    public void onUpdateLiveView(OLYCamera olyCamera, byte[] bytes, Map<String, Object> map) {
        try {
            imageView.setImageData(bytes, map);
        } catch (Error e) {
            Log.e(TAG, "exception: " + e.getMessage());
            Log.e(TAG, "exception: " + Log.getStackTraceString(e));
        }
    }


    //update Methods
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


    public void updateFocusMode(String propValue) {
        Log.d(TAG, "PropVal: " + propValue);
        try {
            String val = camera.getCameraPropertyValueTitle(propValue);
            if (val == "FOCUS_MF")
                focusModeTextView.setText("MF");
            else if (val.equals("FOCUS_SAF"))
                focusModeTextView.setText("S-AF");
            camera.setCameraPropertyValue(CAMERA_PROPERTY_FOCUS_STILL, propValue);
        } catch (OLYCameraKitException e) {

        }

    }

}
