package com.example.mail.fragmenttest;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraAutoFocusResult;
import jp.co.olympus.camerakit.OLYCameraKitException;
import jp.co.olympus.camerakit.OLYCameraLiveViewListener;
import jp.co.olympus.camerakit.OLYCameraPropertyListener;
import jp.co.olympus.camerakit.OLYCameraRecordingListener;
import jp.co.olympus.camerakit.OLYCameraRecordingSupportsListener;
import jp.co.olympus.camerakit.OLYCameraStatusListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class LiveViewFragment extends Fragment implements OLYCameraLiveViewListener,
        OLYCameraStatusListener, OLYCameraPropertyListener, OLYCameraRecordingListener, OLYCameraRecordingSupportsListener,
        View.OnClickListener, View.OnTouchListener {
    private static final String TAG = ConnectToCamActivity.class.getSimpleName();
    int[] modeArr = new int[]{R.drawable.ic_iautomode, R.drawable.ic_programmmode, R.drawable.ic_aparturemode, R.drawable.ic_shuttermode, R.drawable.ic_manualmode, R.drawable.ic_artmode, R.drawable.ic_videomode};

    private static final String CAMERA_PROPERTY_TAKE_MODE = "TAKEMODE";
    private static final String CAMERA_PROPERTY_DRIVE_MODE = "TAKE_DRIVE";
    private static final String CAMERA_PROPERTY_APERTURE_VALUE = "APERTURE";
    private static final String CAMERA_PROPERTY_SHUTTER_SPEED = "SHUTTER";
    private static final String CAMERA_PROPERTY_EXPOSURE_COMPENSATION = "EXPREV";
    private static final String CAMERA_PROPERTY_ISO_SENSITIVITY = "ISO";
    private static final String CAMERA_PROPERTY_WHITE_BALANCE = "WB";
    private static final String CAMERA_PROPERTY_BATTERY_LEVEL = "BATTERY_LEVEL";

    private ImageView batteryLevelImageView;
    private TextView remainingRecordableImagesTextView;
    private ImageView drivemodeImageView;
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

    private OnLiveViewInteractionListener mListener;


    public interface OnLiveViewInteractionListener {
        void onMainSettingsButtonPressed(int currDriveMode);
    }

    public LiveViewFragment() {
        // Required empty public constructor
    }

    private OLYCamera camera;

    public void setCamera(OLYCamera camera) {
        this.camera = camera;
    }

    @SuppressWarnings("serial")
    private static final Map<String, Integer> drivemodeIconList = new HashMap<String, Integer>() {
        {
            put("<TAKE_DRIVE/DRIVE_NORMAL>", R.drawable.icn_drive_setting_single);
            put("<TAKE_DRIVE/DRIVE_CONTINUE>", R.drawable.icn_drive_setting_seq_l);
        }
    };

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

        //recordMode
        final ImageButton ib_RecordMode = (ImageButton) view.findViewById(R.id.ib_RecordMode);
        ib_RecordMode.setOnClickListener(new View.OnClickListener() {
            int counter = 0;

            @Override
            public void onClick(View v) {
                counter++;
                int counterMod = counter % modeArr.length;
                ib_RecordMode.setImageResource(modeArr[counterMod]);
                //currDriveMode = counterMod;
                if (mListener != null)
                    mListener.onMainSettingsButtonPressed(counter % (modeArr.length));
                //SetMainSettingsButtons(counter % (modeArr.length));
                //Log.d(TAG, "start"+ counter);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        enabledTouchShutter = preferences.getBoolean("touch_shutter", false);
        unlockImageView.setVisibility(enabledTouchShutter ? View.INVISIBLE : View.VISIBLE);

        camera.setLiveViewListener(this);
        camera.setCameraPropertyListener(this);
        camera.setCameraStatusListener(this);
        camera.setRecordingListener(this);
        camera.setRecordingSupportsListener(this);

/*        updateDrivemodeImageView();
        updateTakemodeTextView();
        updateShutterSpeedTextView();
        updateApertureValueTextView();
        updateExposureCompensationTextView();
        updateIsoSensitivityTextView();
        updateWhiteBalanceImageView();
        updateBatteryLevelImageView();
        updateRemainingRecordableImagesTextView();*/

        try {
            camera.clearAutoFocusPoint();
            camera.unlockAutoFocus();
        } catch (OLYCameraKitException ee) {
        }
        enabledFocusLock = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            focusedSoundPlayer = MediaPlayer.create(getContext(), R.raw.focusedsound);
            shutterSoundPlayer = MediaPlayer.create(getContext(), R.raw.shuttersound);
            camera = CameraActivity.camera;

            mListener = (OnLiveViewInteractionListener) context;
            Log.d(TAG,"finished onAttatch");
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTriggerFragmInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onUpdateLiveView(OLYCamera olyCamera, byte[] bytes, Map<String, Object> map) {
        //imageView.setImageData(bytes, map);
        Log.d(TAG,"finished updateLiveView");
    }

    @Override
    public void onUpdateCameraProperty(OLYCamera olyCamera, String s) {
        Log.d(TAG, "onUpdateCameraProperty" );
    }

    @Override
    public void onStartRecordingVideo(OLYCamera olyCamera) {
        Log.d(TAG, "onStartRecordingVideo" );
    }

    @Override
    public void onStopRecordingVideo(OLYCamera olyCamera) {
        Log.d(TAG, "onStopRecordingVideo" );
    }

    @Override
    public void onChangeAutoFocusResult(OLYCamera olyCamera, OLYCameraAutoFocusResult olyCameraAutoFocusResult) {
        Log.d(TAG, "nChangeAutoFocusResult" );
    }

    @Override
    public void onReadyToReceiveCapturedImagePreview(OLYCamera olyCamera) {
        Log.d(TAG, "onReadyToReceiveCapturedImagePreview" );
    }

    @Override
    public void onReceiveCapturedImagePreview(OLYCamera olyCamera, byte[] bytes, Map<String, Object> map) {
        Log.d(TAG, "onReceiveCapturedImagePreview" );
    }

    @Override
    public void onFailToReceiveCapturedImagePreview(OLYCamera olyCamera, Exception e) {
        Log.d(TAG, "onFailToReceiveCapturedImagePreview" );
    }

    @Override
    public void onReadyToReceiveCapturedImage(OLYCamera olyCamera) {
        Log.d(TAG, "onReadyToReceiveCapturedImage" );
    }

    @Override
    public void onReceiveCapturedImage(OLYCamera olyCamera, byte[] bytes, Map<String, Object> map) {
        Log.d(TAG, "onReceiveCapturedImage" );
    }

    @Override
    public void onFailToReceiveCapturedImage(OLYCamera olyCamera, Exception e) {
        Log.d(TAG, "onFailToReceiveCapturedImage" );
    }

    @Override
    public void onStopDrivingZoomLens(OLYCamera olyCamera) {
        Log.d(TAG, "onStopDrivingZoomLens" );
    }

    @Override
    public void onUpdateStatus(OLYCamera olyCamera, String s) {
        Log.d(TAG, "onUpdateStatus" );
    }
}
