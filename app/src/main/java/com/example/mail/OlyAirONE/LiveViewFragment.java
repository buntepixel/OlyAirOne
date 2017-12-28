package com.example.mail.OlyAirONE;


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
        OLYCameraStatusListener, OLYCameraPropertyListener, OLYCameraRecordingListener, OLYCameraRecordingSupportsListener, View.OnClickListener, View.OnTouchListener {
    private static final String TAG = LiveViewFragment.class.getSimpleName();

    int[] shootingModeDrawablesArr = new int[]{R.drawable.ic_iautomode, R.drawable.ic_programmmode, R.drawable.ic_aparturemode,
            R.drawable.ic_shuttermode, R.drawable.ic_manualmode, R.drawable.ic_artmode, R.drawable.ic_videomode};


    private ImageView batteryLevelImageView;
    private TextView remainingRecordableImagesTextView;
    private TextView focusModeTextView;
    private ImageButton ib_shootingMode;
    private TextView tv_AEB;
    private Boolean AEB = true;
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
    private Boolean enabledTouchShutter = false;
    private Boolean enabledFocusLock = false;
    private CameraLiveImageView imageView;
    private OLYCamera camera;
    private int shootingModeCounter = 0;
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

    public interface OnLiveViewInteractionListener {

        void onShootingModeButtonPressed(int currDriveMode);

        void onEnabledFocusLock(Boolean focusLockState);

        void updateDriveModeImage(String propValue);

        void onEnabledTouchShutter(Boolean touchShutterState);
    }

    //----------------------
    //   Creation
    //----------------------
    public LiveViewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
       /* if (savedInstanceState != null)
            return;*/
        camera = CameraActivity.getCamera();
        Log.d(TAG, " camera Conntected: camera set:" + camera.isConnected());

        //setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_view, container, false);
        view.setId(View.generateViewId());
        imageView = view.findViewById(R.id.cameraLiveImageView);

        batteryLevelImageView = view.findViewById(R.id.batteryLevelImageView);
        remainingRecordableImagesTextView = view.findViewById(R.id.tv_SdCardSpaceRemain);
        focusModeTextView = view.findViewById(R.id.tv_focusMode);
        ib_shootingMode = view.findViewById(R.id.ib_RecordMode);
        tv_AEB = view.findViewById(R.id.tv_Bracketing);


        focusModeTextView.setOnClickListener(this);
        ib_shootingMode.setOnClickListener(this);
        imageView.setOnTouchListener(this);
        tv_AEB.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
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
        if (camera.isConnected()) {
            updateBatteryLevelImageView();
            updateRemainingRecordableImagesTextView();
            updateTakeModeImageView();
            updateAEBTextView();
        }
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
        Log.d(TAG, "onAttach");

        super.onAttach(context);
        try {
            focusedSoundPlayer = MediaPlayer.create(getActivity(), R.raw.focusedsound);
            shutterSoundPlayer = MediaPlayer.create(getActivity(), R.raw.shuttersound);
            mOnLiveViewInteractionListener = (OnLiveViewInteractionListener) context;

            Log.d(TAG, "finished onAttatch");
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTriggerFragmInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mOnLiveViewInteractionListener != null)
            mOnLiveViewInteractionListener = null;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        {
            Log.d(TAG, "SavedInt: " + shootingModeCounter);
            outState.putInt("SliderValIndex", shootingModeCounter);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (camera == null)
            camera = CameraActivity.getCamera();
        Log.d(TAG, "onActivityCreated");
        Log.d(TAG, "ActivtiyCreated ISCONNECTED " + camera.isConnected());
        if (savedInstanceState != null) {
            Log.d(TAG, "restoredInt: " + savedInstanceState.getInt("SliderValIndex"));
            shootingModeCounter = savedInstanceState.getInt("SliderValIndex");
        }
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
                    case CameraActivity.CAMERA_PROPERTY_BATTERY_LEVEL:
                        //todo: check why not working
                        Log.d(TAG, "BATERY LEVEL,onUpdateCameraProperty: " + name);
                        updateBatteryLevelImageView();
                        break;
                    case CameraActivity.CAMERA_PROPERTY_FOCUS_STILL:
                        //Log.d(TAG, "FOCUS_STILL,onUpdateCameraProperty: " + name);
                        //Log.d(TAG,  "PropVal: "+ property);
                        updateFocusModeTextView(name);

                    case CameraActivity.CAMERA_PROPERTY_FOCUS_MOVIE:
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch");
        if (v == imageView) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                imageViewDidTouchDown(event);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                imageViewDidTouchUp();
            }
        }
        return true;
    }

    public void onShutterTouched(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            shutterImageViewDidTouchDown();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            shutterImageViewDidTouchUp();
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        if (v == focusModeTextView) {
            focusModeTextViewDidTap();
        } else if (v == ib_shootingMode) {
            shootingModeDidTap();
        } else if (v == tv_AEB) {
            autoExposureBracketingDidTap();
        }
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
        //if Autobracketing is active
        if (AEB) {
            takeAEBPicture();
            return;
        }
        //ifNot
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

    private void takeAEBPicture() {
        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            return;
        }
        Log.d(TAG, "Autobracketing");
        try {
            OLYCamera.ActionType actionType = camera.getActionType();
            Log.d(TAG, "ActionTpe: " + actionType);
            if (actionType != OLYCamera.ActionType.Single) {
                String mode = "<TAKE_DRIVE/DRIVE_NORMAL>";
                camera.setCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_DRIVE_MODE, mode);
                Log.d(TAG, "NewActionTpe: " + camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_DRIVE_MODE));
                mOnLiveViewInteractionListener.updateDriveModeImage(mode);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
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
        if (shootingModeCounter == CameraActivity.SHOOTING_MODE_MOVIE)
            propName = CameraActivity.CAMERA_PROPERTY_FOCUS_MOVIE;
        else
            propName = CameraActivity.CAMERA_PROPERTY_FOCUS_STILL;

        try {
            propValues = camera.getCameraPropertyValueList(propName);
            propVal = camera.getCameraPropertyValue(propName);
            for (int i = 0; i < propValues.size(); i++) {
                if (propValues.get(i).equals(propVal)) {
                    //Log.d(TAG,"i: "+ i +" size: "+propValues.size());
                    int tmpIdx = (i + 1) % (propValues.size());
                    //Log.d(TAG,"modulo: "+ tmpIdx);
                    newPropVal = propValues.get(tmpIdx);
                    break;
                }
            }
            if (!newPropVal.equals("")) {
                Log.d(TAG, "SetVal: propName: " + propName + " propVal:" + propVal);
                camera.setCameraPropertyValue(propName, newPropVal);
            }
        } catch (OLYCameraKitException ex) {
            ex.printStackTrace();
        }
    }

    private void shootingModeDidTap() {
        Log.d(TAG, "shootingModeDidTap");
        updateTakeModeImageView();
        Log.d(TAG, "ISCONNECTED " + camera.isConnected());
        Log.d(TAG, "shootingmodeCounter: " + shootingModeCounter);
        shootingModeCounter = (shootingModeCounter + 1) % shootingModeDrawablesArr.length;
        Log.d(TAG, "shootingmodeCounter: " + shootingModeCounter);

        ib_shootingMode.setImageResource(shootingModeDrawablesArr[shootingModeCounter]);
        triggerTakeModeUpdate(shootingModeCounter);

    }

    public void triggerTakeModeUpdate(int shootingModeCounter) {
        if (shootingModeCounter != CameraActivity.SHOOTING_MODE_MOVIE)
            updateFocusModeTextView(CameraActivity.CAMERA_PROPERTY_FOCUS_STILL);
        if (mOnLiveViewInteractionListener != null) {
            mOnLiveViewInteractionListener.onShootingModeButtonPressed(shootingModeCounter);
            Log.d(TAG, "mOnliveView... shootingmodeCounter: " + shootingModeCounter);
        }
    }

    private void autoExposureBracketingDidTap() {
        if (AEB) {
            AEB = false;
            tv_AEB.setTextColor(getActivity().getResources().getColor(R.color.LiveView_Text_deactivated));
        } else {
            AEB = true;
            tv_AEB.setTextColor(getActivity().getResources().getColor(R.color.button_text_states));
        }
        Log.d(TAG, "AEB did Tap: " + AEB);
    }

    // -------------------------------------------------------------------------
    // Updates
    // -------------------------------------------------------------------------
    public void refresh(){
        Log.d(TAG,"refresh");
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }


    private void updateTakeModeImageView() {
        Log.d(TAG, "updateTakeModeImageView");
        try {
            String takeMode = camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_TAKE_MODE);
            shootingModeCounter = CameraActivity.getTakeModeStrings().indexOf(takeMode);
            Log.d(TAG, "updateTakeModeIv shootingModecounter: " + shootingModeCounter);
        } catch (OLYCameraKitException ex) {
            ex.printStackTrace();
        }
        ib_shootingMode.setImageResource(shootingModeDrawablesArr[shootingModeCounter]);
    }

    private void updateBatteryLevelImageView() {
        Log.d(TAG, "updateBatteryLevelImageView");

        String value = null;
        try {
            value = camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_BATTERY_LEVEL);
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
        Log.d(TAG, "updateRemainingRecordableImagesTextView");

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
        Log.d(TAG, "updateFocusModeTextView");

        Log.d(TAG, "New FocusMode: " + propName);
        try {
            String txt = camera.getCameraPropertyValueTitle(camera.getCameraPropertyValue(propName));
            Log.d(TAG, "new FocusValues:  " + txt);

            focusModeTextView.setText(txt);
        } catch (OLYCameraKitException ex) {
            ex.printStackTrace();
        }
    }

    private void updateAEBTextView() {
        Log.d(TAG, "updateAEBTextView");
        tv_AEB.setEnabled(AEB);
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
