package com.example.mail.fragmenttest;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import jp.co.olympus.camerakit.OLYCamera.TakingProgress;
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
         OLYCameraStatusListener,OLYCameraPropertyListener,OLYCameraRecordingListener, OLYCameraRecordingSupportsListener,  View.OnClickListener,View.OnTouchListener {
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
        void onEnabledFocusLock(Boolean focusLockState);
        void onEnabledTouchShutter(Boolean touchShutterState);

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
    public void onChangeAutoFocusResult(OLYCamera camera, OLYCameraAutoFocusResult result) {
    }

    @Override
    public void onStartRecordingVideo(OLYCamera camera) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shutterImageView.setSelected(true);
            }
        });
    }

    @Override
    public void onStopRecordingVideo(OLYCamera camera) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shutterImageView.setSelected(false);
            }
        });
    }

    @Override
    public void onReadyToReceiveCapturedImage(OLYCamera camera) {
    }
    @Override
    public void onReceiveCapturedImagePreview(OLYCamera camera, byte[] data, Map<String, Object> metadata) {
        if (camera.getActionType() == OLYCamera.ActionType.Single) {
            RecviewFragment fragment = new RecviewFragment();
            fragment.setCamera(camera);
            fragment.setImageData(data, metadata);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(getId(), fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onFailToReceiveCapturedImagePreview(OLYCamera camera, Exception e) {
    }

    @Override
    public void onReadyToReceiveCapturedImagePreview(OLYCamera camera) {
    }

    @Override
    public void onReceiveCapturedImage(OLYCamera camera, byte[] data, Map<String, Object> metadata) {
    }

    @Override
    public void onFailToReceiveCapturedImage(OLYCamera camera, Exception e) {
    }

    @Override
    public void onStopDrivingZoomLens(OLYCamera camera) {
    }

    public void onShutterTouched(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            shutterImageViewDidTouchDown();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            shutterImageViewDidTouchUp();
        }
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "touch");
        if (v == imageView) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                imageViewDidTouchDown(event);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                imageViewDidTouchUp();
            }
        }
        return true;
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
        imageView.setOnTouchListener(this);
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
//            camera.UnlockAutoFocus();
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
    // Camera actions
    // -------------------------------------------------------------------------

    //
    // Touch Shutter mode:
    //   - Tap a subject to focus and automatically release the shutter.
    //
    // Touch AF mode:
    //   - Tap to display a focus frame and focus on the subject in the selected area.
    //   - You can use the image view to choose the position of the focus frame.
    //   - Photographs can be taken by tapping the shutter button.
    //

    // UI events

    private void imageViewDidTouchDown(MotionEvent event) {
        OLYCamera.ActionType actionType = camera.getActionType();

        // If the focus point is out of area, ignore the touch.
        PointF point = imageView.getPointWithEvent(event);
        if (!imageView.isContainsPoint(point)) {
            return;
        }

        if (enabledTouchShutter) {
            // Touch Shutter mode
            if (actionType == OLYCamera.ActionType.Single) {
                takePictureWithPoint(point);
            } else if (actionType == OLYCamera.ActionType.Sequential) {
                startTakingPictureWithPoint(point);
            } else if (actionType == OLYCamera.ActionType.Movie) {
                if (camera.isRecordingVideo()) {
                    stopRecordingVideo();
                } else {
                    startRecordingVideo();
                }
            }
        } else {
            // Touch AF mode
            if (actionType == OLYCamera.ActionType.Single ||
                    actionType == OLYCamera.ActionType.Sequential) {
                lockAutoFocus(point);
            }
        }
    }

    private void imageViewDidTouchUp() {
        OLYCamera.ActionType actionType = camera.getActionType();
        if (enabledTouchShutter) {
            // Touch Shutter mode
            if (actionType == OLYCamera.ActionType.Sequential) {
                stopTakingPicture();
            }
        }
    }

    private void shutterImageViewDidTouchDown() {
        OLYCamera.ActionType actionType = camera.getActionType();
        if (actionType == OLYCamera.ActionType.Single) {
            takePicture();
        } else if (actionType == OLYCamera.ActionType.Sequential) {
            startTakingPicture();
        } else if (actionType == OLYCamera.ActionType.Movie) {
            if (camera.isRecordingVideo()) {
                stopRecordingVideo();
            } else {
                startRecordingVideo();
            }
        }
    }

    private void shutterImageViewDidTouchUp() {
        OLYCamera.ActionType actionType = camera.getActionType();
        if (actionType == OLYCamera.ActionType.Sequential) {
            stopTakingPicture();
        }
    }

    private void unlockImageViewDidTap() {
        unlockAutoFocus();
    }

    // focus control

    private void lockAutoFocus(PointF point) {
        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            return;
        }

        // Display a provisional focus frame at the touched point.
        final RectF preFocusFrameRect;
        {
            float focusWidth = 0.125f;  // 0.125 is rough estimate.
            float focusHeight = 0.125f;
            float imageWidth = imageView.getIntrinsicContentSizeWidth();
            float imageHeight = imageView.getIntrinsicContentSizeHeight();
            if (imageWidth > imageHeight) {
                focusHeight *= (imageWidth / imageHeight);
            } else {
                focusHeight *= (imageHeight / imageWidth);
            }
            preFocusFrameRect = new RectF(point.x - focusWidth / 2.0f, point.y - focusHeight / 2.0f,
                    point.x + focusWidth / 2.0f, point.y + focusHeight / 2.0f);
        }
        imageView.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Running);

        // Set auto-focus point.
        try {
            camera.setAutoFocusPoint(point);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            // Lock failed.
            try {
                camera.clearAutoFocusPoint();
                camera.unlockAutoFocus();
            } catch (OLYCameraKitException ee) {
                ee.printStackTrace();
            }
            enabledFocusLock = false;
            mOnLiveViewInteractionListener.onEnabledFocusLock(enabledFocusLock);
            imageView.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Failed, 1.0);
            return;
        }

        // Lock auto-focus.
        camera.lockAutoFocus(new OLYCamera.TakePictureCallback() {
            @Override
            public void onProgress(OLYCamera camera, OLYCamera.TakingProgress progress, OLYCameraAutoFocusResult autoFocusResult) {
                if (progress == OLYCamera.TakingProgress.EndFocusing) {
                    if (autoFocusResult.getResult().equals("ok") && autoFocusResult.getRect() != null) {
                        // Lock succeed.
                        enabledFocusLock = true;
                        mOnLiveViewInteractionListener.onEnabledFocusLock(enabledFocusLock);

                        focusedSoundPlayer.start();
                        RectF postFocusFrameRect = autoFocusResult.getRect();
                        imageView.showFocusFrame(postFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Focused);

                    } else if (autoFocusResult.getResult().equals("none")) {
                        // Could not lock.
                        try {
                            camera.clearAutoFocusPoint();
                            camera.unlockAutoFocus();
                        } catch (OLYCameraKitException ee) {
                            ee.printStackTrace();
                        }
                        enabledFocusLock = false;
                        mOnLiveViewInteractionListener.onEnabledFocusLock(enabledFocusLock);

                        imageView.hideFocusFrame();
                    } else {
                        // Lock failed.
                        try {
                            camera.clearAutoFocusPoint();
                            camera.unlockAutoFocus();
                        } catch (OLYCameraKitException ee) {
                            ee.printStackTrace();
                        }
                        enabledFocusLock = false;
                        mOnLiveViewInteractionListener.onEnabledFocusLock(enabledFocusLock);

                        imageView.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Failed, 1.0);
                    }
                }
            }

            @Override
            public void onCompleted() {
                // No operation.
            }

            @Override
            public void onErrorOccurred(Exception e) {
                // Lock failed.
                try {
                    camera.clearAutoFocusPoint();
                    camera.unlockAutoFocus();
                } catch (OLYCameraKitException ee) {
                    ee.printStackTrace();
                }
                enabledFocusLock = false;
                mOnLiveViewInteractionListener.onEnabledFocusLock(enabledFocusLock);

                imageView.hideFocusFrame();

                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        presentMessage("AF failed", message);
                    }
                });
            }
        });
    }

    private void unlockAutoFocus() {
        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            return;
        }

        // Unlock auto-focus.
        try {
            camera.unlockAutoFocus();
            camera.clearAutoFocusPoint();
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
        }

        enabledFocusLock = false;
        imageView.hideFocusFrame();
    }



    // shutter control (still)

    private void takePicture() {
        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            return;
        }

        HashMap<String, Object> options = new HashMap<String, Object>();
        camera.takePicture(options, new OLYCamera.TakePictureCallback() {
            @Override
            public void onProgress(OLYCamera camera, TakingProgress progress, OLYCameraAutoFocusResult autoFocusResult) {
                if (progress == TakingProgress.EndFocusing) {
                    if (!enabledFocusLock) {
                        if (autoFocusResult.getResult().equals("ok") && autoFocusResult.getRect() != null) {
                            RectF postFocusFrameRect = autoFocusResult.getRect();
                            imageView.showFocusFrame(postFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Focused);
                        } else if (autoFocusResult.getResult().equals("none")) {
                            imageView.hideFocusFrame();
                        } else {
                            imageView.hideFocusFrame();
                        }
                    }
                } else if (progress == TakingProgress.BeginCapturing) {
                    shutterSoundPlayer.start();
                }
            }

            @Override
            public void onCompleted() {
                if (!enabledFocusLock) {
                    try {
                        camera.clearAutoFocusPoint();
                    } catch (OLYCameraKitException ee) {
                        ee.printStackTrace();
                    }
                    imageView.hideFocusFrame();
                }
            }

            @Override
            public void onErrorOccurred(Exception e) {
                if (!enabledFocusLock) {
                    try {
                        camera.clearAutoFocusPoint();
                    } catch (OLYCameraKitException ee) {
                        ee.printStackTrace();
                    }
                    imageView.hideFocusFrame();
                }

                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        presentMessage("Take failed", message);
                    }
                });
            }
        });
    }

    private void takePictureWithPoint(PointF point) {
        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            return;
        }

        // Display a provisional focus frame at the touched point.
        final RectF preFocusFrameRect;
        {
            float focusWidth = 0.125f;  // 0.125 is rough estimate.
            float focusHeight = 0.125f;
            float imageWidth = imageView.getIntrinsicContentSizeWidth();
            float imageHeight = imageView.getIntrinsicContentSizeHeight();
            if (imageWidth > imageHeight) {
                focusHeight *= (imageWidth / imageHeight);
            } else {
                focusHeight *= (imageHeight / imageWidth);
            }
            preFocusFrameRect = new RectF(point.x - focusWidth / 2.0f, point.y - focusHeight / 2.0f,
                    point.x + focusWidth / 2.0f, point.y + focusHeight / 2.0f);
        }
        imageView.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Running);

        // Set auto-focus point.
        try {
            camera.setAutoFocusPoint(point);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            // Lock failed.
            try {
                camera.unlockAutoFocus();
            } catch (OLYCameraKitException ee) {
                ee.printStackTrace();
            }
            enabledFocusLock = false;
            mOnLiveViewInteractionListener.onEnabledFocusLock(enabledFocusLock);

            imageView.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Failed, 1.0);
            return;
        }

        HashMap<String, Object> options = new HashMap<String, Object>();
        camera.takePicture(options, new OLYCamera.TakePictureCallback() {
            @Override
            public void onProgress(OLYCamera camera, TakingProgress progress, OLYCameraAutoFocusResult autoFocusResult) {
                if (progress == TakingProgress.EndFocusing) {
                    if (!enabledFocusLock) {
                        if (autoFocusResult.getResult().equals("ok") && autoFocusResult.getRect() != null) {
                            RectF postFocusFrameRect = autoFocusResult.getRect();
                            imageView.showFocusFrame(postFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Focused);
                        } else if (autoFocusResult.getResult().equals("none")) {
                            imageView.hideFocusFrame();
                        } else {
                            imageView.hideFocusFrame();
                        }
                    }
                } else if (progress == TakingProgress.BeginCapturing) {
                    shutterSoundPlayer.start();
                }
            }

            @Override
            public void onCompleted() {
                if (!enabledFocusLock) {
                    try {
                        camera.clearAutoFocusPoint();
                    } catch (OLYCameraKitException ee) {
                        ee.printStackTrace();
                    }
                    imageView.hideFocusFrame();
                }
            }

            @Override
            public void onErrorOccurred(Exception e) {
                if (!enabledFocusLock) {
                    try {
                        camera.clearAutoFocusPoint();
                    } catch (OLYCameraKitException ee) {
                        ee.printStackTrace();
                    }
                    imageView.hideFocusFrame();
                }

                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        presentMessage("Take failed", message);
                    }
                });
            }
        });
    }

    private void startTakingPicture() {
        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            return;
        }

        camera.startTakingPicture(null, new OLYCamera.TakePictureCallback() {
            @Override
            public void onProgress(OLYCamera camera, TakingProgress progress, OLYCameraAutoFocusResult autoFocusResult) {
                if (progress == TakingProgress.EndFocusing) {
                    if (!enabledFocusLock) {
                        if (autoFocusResult.getResult().equals("ok") && autoFocusResult.getRect() != null) {
                            RectF postFocusFrameRect = autoFocusResult.getRect();
                            imageView.showFocusFrame(postFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Focused);
                        } else if (autoFocusResult.getResult().equals("none")) {
                            imageView.hideFocusFrame();
                        } else {
                            imageView.hideFocusFrame();
                        }
                    }
                } else if (progress == TakingProgress.BeginCapturing) {
                    shutterSoundPlayer.start();
                }
            }

            @Override
            public void onCompleted() {
                // No operation.
            }

            @Override
            public void onErrorOccurred(Exception e) {
                if (!enabledFocusLock) {
                    try {
                        camera.clearAutoFocusPoint();
                    } catch (OLYCameraKitException ee) {
                        ee.printStackTrace();
                    }
                    imageView.hideFocusFrame();
                }

                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        presentMessage("Take failed", message);
                    }
                });
            }
        });
    }

    private void startTakingPictureWithPoint(PointF point) {
        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            return;
        }

        // Display a provisional focus frame at the touched point.
        final RectF preFocusFrameRect;
        {
            float focusWidth = 0.125f;  // 0.125 is rough estimate.
            float focusHeight = 0.125f;
            float imageWidth = imageView.getIntrinsicContentSizeWidth();
            float imageHeight = imageView.getIntrinsicContentSizeHeight();
            if (imageWidth > imageHeight) {
                focusHeight *= (imageWidth / imageHeight);
            } else {
                focusHeight *= (imageHeight / imageWidth);
            }
            preFocusFrameRect = new RectF(point.x - focusWidth / 2.0f, point.y - focusHeight / 2.0f,
                    point.x + focusWidth / 2.0f, point.y + focusHeight / 2.0f);
        }
        imageView.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Running);

        // Set auto-focus point.
        try {
            camera.setAutoFocusPoint(point);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            // Lock failed.
            try {
                camera.unlockAutoFocus();
            } catch (OLYCameraKitException ee) {
                ee.printStackTrace();
            }
            enabledFocusLock = false;
            mOnLiveViewInteractionListener.onEnabledFocusLock(enabledFocusLock);

            imageView.showFocusFrame(preFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Failed, 1.0);
            return;
        }

        camera.startTakingPicture(null, new OLYCamera.TakePictureCallback() {
            @Override
            public void onProgress(OLYCamera camera, TakingProgress progress, OLYCameraAutoFocusResult autoFocusResult) {
                if (progress == TakingProgress.EndFocusing) {
                    if (!enabledFocusLock) {
                        if (autoFocusResult.getResult().equals("ok") && autoFocusResult.getRect() != null) {
                            RectF postFocusFrameRect = autoFocusResult.getRect();
                            imageView.showFocusFrame(postFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Focused);
                        } else if (autoFocusResult.getResult().equals("none")) {
                            imageView.hideFocusFrame();
                        } else {
                            imageView.hideFocusFrame();
                        }
                    }
                } else if (progress == TakingProgress.BeginCapturing) {
                    shutterSoundPlayer.start();
                }
            }

            @Override
            public void onCompleted() {
                // No operation.
            }

            @Override
            public void onErrorOccurred(Exception e) {
                if (!enabledFocusLock) {
                    try {
                        camera.clearAutoFocusPoint();
                    } catch (OLYCameraKitException ee) {
                        ee.printStackTrace();
                    }
                    imageView.hideFocusFrame();
                }

                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        presentMessage("Take failed", message);
                    }
                });
            }
        });
    }

    private void stopTakingPicture() {
        if (!camera.isTakingPicture()) {
            return;
        }

        camera.stopTakingPicture(new OLYCamera.TakePictureCallback() {
            @Override
            public void onProgress(OLYCamera camera, TakingProgress progress, OLYCameraAutoFocusResult autoFocusResult) {
                // No operation.
            }

            @Override
            public void onCompleted() {
                if (!enabledFocusLock) {
                    try {
                        camera.clearAutoFocusPoint();
                    } catch (OLYCameraKitException ee) {
                        ee.printStackTrace();
                    }
                    imageView.hideFocusFrame();
                }
            }

            @Override
            public void onErrorOccurred(Exception e) {
                if (!enabledFocusLock) {
                    try {
                        camera.clearAutoFocusPoint();
                    } catch (OLYCameraKitException ee) {
                        ee.printStackTrace();
                    }
                    imageView.hideFocusFrame();
                }

                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        presentMessage("Take failed", message);
                    }
                });
            }
        });
    }

    // shutter control (movie)

    private void startRecordingVideo() {
        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            return;
        }

        HashMap<String, Object> options = new HashMap<String, Object>();
        camera.startRecordingVideo(options, new OLYCamera.CompletedCallback() {
            @Override
            public void onCompleted() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shutterImageView.setSelected(true);
                    }
                });
            }

            @Override
            public void onErrorOccurred(OLYCameraKitException e) {
                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        presentMessage("Record failed", message);
                    }
                });
            }
        });
    }

    private void stopRecordingVideo() {
        if (!camera.isRecordingVideo()) {
            return;
        }

        camera.stopRecordingVideo(new OLYCamera.CompletedCallback() {
            @Override
            public void onCompleted() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shutterImageView.setSelected(false);
                    }
                });
            }

            @Override
            public void onErrorOccurred(OLYCameraKitException e) {
                shutterImageView.setSelected(false);
                final String message = e.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        presentMessage("Record failed", message);
                    }
                });
            }
        });
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
    private void presentMessage(String title, String message) {
        Context context = getActivity();
        if (context == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message);
        builder.show();
    }

}
