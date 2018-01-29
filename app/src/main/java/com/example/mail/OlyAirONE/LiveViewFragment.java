package com.example.mail.OlyAirONE;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
        OLYCameraStatusListener, OLYCameraPropertyListener, OLYCameraRecordingListener, OLYCameraRecordingSupportsListener,
        View.OnClickListener, View.OnTouchListener {
    private static final String TAG = LiveViewFragment.class.getSimpleName();

    int[] takeModeDrawablesArr = new int[]{R.drawable.ic_iautomode, R.drawable.ic_programmmode, R.drawable.ic_aparturemode,
            R.drawable.ic_shuttermode, R.drawable.ic_manualmode, R.drawable.ic_artmode, R.drawable.ic_videomode};


    private ImageView iv_batteryLevelImageView;
    private TextView remainingRecordableImagesTextView;
    private TextView focusModeTextView;
    private ImageButton ib_TakeMode;
    private ImageView iv_AEB;
    private ImageView iv_recording;
    private LinearLayout ll_recording;
    private TextView tv_RecordType;

    private LinearLayout ll_tl_timeLapse;
    private LinearLayout ll_tl_nextCapture;
    private TextView tv_tl_totalImages;
    private TextView tv_tl_doneImages;
    private TextView tv_tl_intervall;
    private TextView tv_tl_nextCapture;
    private int tl_nbImages;
    private int tl_intervall;


    private Boolean AEB = false;
    private Boolean timelapse = false;
    String[] aebSettingsArr;
    int aebCounter = 0;
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
    private int takeModeCounter = 0;
    private OnLiveViewInteractionListener mOnLiveViewInteractionListener;
    Executor connectionExecutor = Executors.newFixedThreadPool(1);


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
    private static final Map<String, Integer> takeModeIconsList = new HashMap<String, Integer>() {
        {
            put("<TAKEMODE/iAuto>", R.drawable.ic_iautomode);
            put("<TAKEMODE/P>", R.drawable.ic_programmmode);
            put("<TAKEMODE/A>", R.drawable.ic_aparturemode);
            put("<TAKEMODE/S>", R.drawable.ic_shuttermode);
            put("<TAKEMODE/M>", R.drawable.ic_manualmode);
            put("<TAKEMODE/ART>", R.drawable.ic_artmode);
            put("<TAKEMODE/movie>", R.drawable.ic_videomode);
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

        void onTakeModeButtonPressed(int currDriveMode);

        void onEnabledFocusLock(Boolean focusLockState);

        void updateDriveModeImage();

        void onRecordVideoPressed(Boolean bool);

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_view, container, false);
        view.setId(View.generateViewId());
        imageView = view.findViewById(R.id.cameraLiveImageView);

        iv_batteryLevelImageView = view.findViewById(R.id.batteryLevelImageView);
        remainingRecordableImagesTextView = view.findViewById(R.id.tv_SdCardSpaceRemain);
        focusModeTextView = view.findViewById(R.id.tv_focusMode);
        ib_TakeMode = view.findViewById(R.id.ib_RecordMode);
        iv_AEB = view.findViewById(R.id.iv_Bracketing);
        tv_RecordType = view.findViewById(R.id.tv_RecordType);

        ll_recording = view.findViewById(R.id.ll_recording);
        iv_recording = view.findViewById(R.id.iv_recording);
        ll_recording.setVisibility(View.INVISIBLE);

        ll_tl_timeLapse = view.findViewById(R.id.ll_timeLapse);
        ll_tl_timeLapse.setVisibility(View.INVISIBLE);
        ll_tl_nextCapture = view.findViewById(R.id.ll_tl_nextCapture);
        ll_tl_nextCapture.setVisibility(View.INVISIBLE);
        tv_tl_totalImages = view.findViewById(R.id.tv_tl_totalImages);
        tv_tl_doneImages = view.findViewById(R.id.tv_tl_doneImages);
        tv_tl_intervall = view.findViewById(R.id.tv_tl_interval);
        tv_tl_nextCapture = view.findViewById(R.id.tv_tl_nextCapture);

        SharedPreferences preferences = getContext().getSharedPreferences(getString(R.string.pref_SharedPrefs), Context.MODE_PRIVATE);
        tl_nbImages = Integer.parseInt(preferences.getString(CamSettingsActivity.TL_NBIMAGES, "35"));
        tl_intervall = Integer.parseInt(preferences.getString(CamSettingsActivity.TL_INTERVALL, "60"));
        tv_tl_intervall.setText(String.valueOf(tl_intervall));
        tv_tl_totalImages.setText(String.valueOf(tl_nbImages));
        if (savedInstanceState != null) {
            Log.d(TAG, "restoredInt: " + savedInstanceState.getInt("CurrTakeMode"));
            takeModeCounter = savedInstanceState.getInt("CurrTakeMode");
            ib_TakeMode.setImageResource(takeModeDrawablesArr[takeModeCounter]);
        }
        focusModeTextView.setOnClickListener(this);
        ib_TakeMode.setOnClickListener(this);
        imageView.setOnTouchListener(this);
        iv_AEB.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        focusModeTextView.setOnClickListener(null);
        ib_TakeMode.setOnClickListener(null);
        imageView.setOnTouchListener(null);
        iv_AEB.setOnClickListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        camera.setLiveViewListener(this);
        camera.setCameraPropertyListener(this);
        camera.setCameraStatusListener(this);
        camera.setRecordingListener(this);
        camera.setRecordingSupportsListener(this);

        //wait for camera to get in to recording mode so we get correct updates
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {// wait for camera to get connected
                while (!camera.isConnected() && !(camera.getRunMode() == OLYCamera.RunMode.Recording)) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                updateImageViews();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        camera.setLiveViewListener(null);
        camera.setCameraPropertyListener(null);
        camera.setCameraStatusListener(null);
        camera.setRecordingListener(null);
        camera.setRecordingSupportsListener(null);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        if (v == focusModeTextView) {
            focusModeTextViewDidTap();
        } else if (v == ib_TakeMode) {
            takeModeDidTap();
        } else if (v == iv_AEB) {
            if (timelapse) {
                Toast.makeText(this.getContext(), "Bracketing not possible in combination with timelapse. Go to your settings and disable timelapse", Toast.LENGTH_LONG).show();
                return;
            }
            AEBracketingDidTap();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch");
        Log.d(TAG, "enabled TouchShutter: " + enabledTouchShutter);

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
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        try {
            focusedSoundPlayer = MediaPlayer.create(getActivity(), R.raw.focusedsound);
            shutterSoundPlayer = MediaPlayer.create(getActivity(), R.raw.shuttersound);
            mOnLiveViewInteractionListener = (OnLiveViewInteractionListener) context;
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
            Log.d(TAG, "SavedInt: " + takeModeCounter);
            outState.putInt("CurrTakeMode", takeModeCounter);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        if (camera == null) {
            camera = CameraActivity.getCamera();
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
                        updateBatteryLevelImageView();
                        break;
                    case CameraActivity.CAMERA_PROPERTY_FOCUS_STILL:
                        //Log.d(TAG, "FOCUS_STILL,onUpdateCameraProperty: " + name);
                        //Log.d(TAG,  "PropVal: "+ property);
                        updateFocusModeTextView(name);
                        break;
                    case CameraActivity.CAMERA_PROPERTY_FOCUS_MOVIE:
                        updateFocusModeTextView(name);
                        break;
                }
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
                    case "MediaError":
                        if (camera.isHighTemperatureWarning())
                            Toast.makeText(getActivity(), "Media Error, something is wromg with your Sd Card",Toast.LENGTH_LONG).show();
                        break;
                    case "HighTemperatureWarning":
                        if (camera.isHighTemperatureWarning())
                            Toast.makeText(getActivity(), "Camera is getting to hot, please turn it off and let it cool down.", Toast.LENGTH_LONG).show();
                        break;
                    case "LevelGauge":
                        //todo: implement level gauge
                        //Log.d(TAG, "LevelGauge: "+name);
                        break;
                }
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

    // Video-------------------

    @Override
    public void onStartRecordingVideo(OLYCamera camera) {
        Log.d("TAG", "onStartRecordingVideo");
        mOnLiveViewInteractionListener.onRecordVideoPressed(true);
    }

    @Override
    public void onStopRecordingVideo(OLYCamera camera) {
        Log.d("TAG", "onStopRecordingVideo");

        mOnLiveViewInteractionListener.onRecordVideoPressed(true);


        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shutterImageView.setSelected(false);
            }
        });*/
    }

    //-------------------------
    //    Interaction
    //------------------------

    public void onShutterTouched(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            shutterImageViewDidTouchDown();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            shutterImageViewDidTouchUp();
        }
    }

    @Override
    public void onReadyToReceiveCapturedImage(OLYCamera camera) {
    }

    @Override
    public void onReceiveCapturedImagePreview(OLYCamera camera, byte[] data, Map<String, Object> metadata) {
        if (timelapse || AEB)
            return;
        if (camera.getActionType() == OLYCamera.ActionType.Single) {
            RecviewFragment fragment = new RecviewFragment();
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
            if (timelapse) {
                startTimelapse();
            }
            // Touch Shutter mode
            else if (actionType == OLYCamera.ActionType.Single) {
                takePictureWithPoint(point);
            } else if (actionType == OLYCamera.ActionType.Sequential) {
                startTakingPictureWithPoint(point);
            } else if (actionType == OLYCamera.ActionType.Movie) {
                if (camera.isRecordingVideo()) {
                    Log.d(TAG, "stopRecording");
                    stopRecordingVideo();
                } else {
                    Log.d(TAG, "startRecording");
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
        if (timelapse) {
            startTimelapse();
            return;
        } else if (AEB) {
            takeAEBSequence();
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
            Log.d(TAG, "returning as is busy");
            return;
        }
        HashMap<String, Object> options = new HashMap<String, Object>();
        Log.d(TAG, "taking pic");

        camera.takePicture(options, new OLYCamera.TakePictureCallback() {
            @Override
            public void onProgress(OLYCamera olyCamera, TakingProgress progress, OLYCameraAutoFocusResult autoFocusResult) {
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
                if (AEB) {
                    takeAEBBracket();
                    return;
                }
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

    //-------------------
    // Timelapse
    //-------------------
    private void startTimelapse() {
        timeLapseDialogue("you're doing " + tl_nbImages + " images with a \ninterval of " + tl_intervall + " seconds", "Start Timelapse", "Cancel");
    }

    private void takeTimelapse() {

        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            Log.d(TAG, "returning as is busy");
            return;
        }
        Log.d(TAG, "takeTimelapse");
        try {
            OLYCamera.ActionType actionType = camera.getActionType();
            Log.d(TAG, "ActionTpe: " + actionType);
            if (actionType != OLYCamera.ActionType.Single) {//if not single dirive set to single
                camera.setCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_DRIVE_MODE, "<TAKE_DRIVE/DRIVE_NORMAL>");
                //Log.d(TAG, "NewActionTpe: " + camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_DRIVE_MODE));
                mOnLiveViewInteractionListener.updateDriveModeImage();
            }
            connectionExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    int counter = 0;
                    nextCaptureVisible(true);
                    while (counter < tl_nbImages) {
                        takePicture();
                        counter++;
                        updateTimelapseStats(String.valueOf(counter));
                        if (counter == tl_nbImages) {//exit if we are done
                            nextCaptureVisible(false);
                            return;
                        }
                        int countdown = tl_intervall;
                        while (countdown > -1) {
                            updateNextCaptureCounter(String.valueOf(countdown));
                            countdown--;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateTimelapseStats(final String donePic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_tl_doneImages.setText(donePic);
            }
        });
    }

    private void updateNextCaptureCounter(final String nextCapture) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_tl_nextCapture.setText(nextCapture);
            }
        });
    }

    private void nextCaptureVisible(final boolean bool) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bool)
                    ll_tl_nextCapture.setVisibility(View.VISIBLE);
                else
                    ll_tl_nextCapture.setVisibility(View.INVISIBLE);


            }
        });
    }

    private void timeLapseDialogue(String text, String btn_Pos, String btn_Neg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage(text);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                btn_Pos,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        takeTimelapse();
                        dialog.dismiss();
                    }
                });

        builder1.setNegativeButton(
                btn_Neg,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    //-------------------
    //  AEB bracketing
    //-------------------
    private void takeAEBSequence() {
        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            Log.d(TAG, "returning as is busy");
            return;
        }
        Log.d(TAG, "Autobracketing");
        try {
            OLYCamera.ActionType actionType = camera.getActionType();
            Log.d(TAG, "ActionTpe: " + actionType);
            if (actionType != OLYCamera.ActionType.Single) {
                String mode = "<TAKE_DRIVE/DRIVE_NORMAL>";
                camera.setCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_DRIVE_MODE, mode);
                //Log.d(TAG, "NewActionTpe: " + camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_DRIVE_MODE));
                mOnLiveViewInteractionListener.updateDriveModeImage();
            }

            //get Settings
            SharedPreferences preferences = getContext().getSharedPreferences(getString(R.string.pref_SharedPrefs), Context.MODE_PRIVATE);
            int images = Integer.parseInt(preferences.getString(CamSettingsActivity.AEB_IMAGETAG, "3"));
            int spread = Integer.parseInt(preferences.getString(CamSettingsActivity.AEB_SPREADTAG, "1"));

            List<String> valuesList = camera.getCameraPropertyValueList(CameraActivity.CAMERA_PROPERTY_SHUTTER_SPEED);
            String shutterSp = camera.getActualShutterSpeed();
            String aparture = camera.getActualApertureValue();
            int listId = valuesList.indexOf(shutterSp);
            int nbOffExp = (images - 1) / 2;

            //set interval
            aebSettingsArr = calcBracketing(shutterSp, spread, images);
            if (aebSettingsArr != null) {
                enabledFocusLock = true;
                takeAEBBracket();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private String[] calcBracketing(String shutterSpeed, int evOffset, int nbImages) {
        //generate arrays
        String[] brackets = new String[nbImages];
        float[] floatBr = new float[nbImages];
        // Log.d(TAG, "_________________shutterSpeed: " + shutterSpeed+"evoffset: "+evOffset+"______________");
        int nbOffExp = (nbImages - 1) / 2;//nb pics neg/pos
        //convert shutter String into millsec float
        floatBr[nbOffExp] = convertShutterStringToMillSec(shutterSpeed);
        for (int i = nbOffExp - 1; i >= 0; i--) {//get neg offset
            floatBr[i] = floatBr[i + 1] / (2 * evOffset);
        }
        for (int i = nbOffExp + 1; i < nbImages; i++) {//get pos offset
            floatBr[i] = floatBr[i - 1] * (2 * evOffset);
        }
        List<String> shutterVals = ShutterFragment.getPossibleShutterValues();
        /*Log.d(TAG, "shutterValsSize: " + shutterVals.size());
        Log.d(TAG, "shutterVals: " + shutterVals.toString());*/
        int k = 0;
        int kk = 0;
        int counter = 0;
        for (int i = 0; i < floatBr.length; i++) {
            float val = floatBr[i];
            float delta = Float.MAX_VALUE;
            float deltaPrev = Float.MAX_VALUE;
            //Log.d(TAG,"________________________________________________");
            for (k = 0; k < shutterVals.size(); k++) {//we dont reinitialize k since we go from the largest val to the
                //Log.d(TAG,"ShutterVal: "+shutterVals.get(k) );
                float shVal = convertShutterStringToMillSec(shutterVals.get(k));
                float deltaNew = Math.abs(shVal - val);
                if (deltaNew < delta) {//if we get closer to correct number
                    deltaPrev = delta;
                    delta = deltaNew;
                    // Log.d(TAG,"k: "+k+" delta: "+delta+" deltaNew: "+deltaNew+" shVal: "+shVal+" val: "+val);
                } else //if we get bigger again go back
                    break;
            }
            if (kk == k) {
                counter++;
                Log.d(TAG, "not enough Space");
            }
            kk = k;
            Log.d(TAG, "k: " + k);
            brackets[i] = shutterVals.get(k - 1);
        }
        for (String value : brackets) {
            Log.d(TAG, "exposure: " + value);
        }
        if (counter > 0) {
            presentMessage("ooops, something ain't right", "you have not enough range for propper under/over exposure.\n" +
                    "You will have " + counter + " pictures with the same exposure.\nchange your Aperture or Iso value");
            return null;
        }
        return brackets;
    }


    private float convertShutterStringToMillSec(String inVal) {
        String valueString = CameraActivity.extractValue(inVal);
        float tv = 0;
        try {
            if (valueString.contains("\""))
                tv = Float.parseFloat(valueString.split("\"")[0]);
            else
                tv = 1 / Float.parseFloat(valueString);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
        }
        return tv;
    }

    private void takeAEBBracket() {
        Log.d(TAG, "camMediaBusy: " + camera.isMediaBusy() + " istakingPicture: " + camera.isTakingPicture() + " is media error: " + camera.isMediaError());
        while (camera.isMediaBusy()) {
            // Log.d(TAG,"medibusy");
        }
        Log.d(TAG, "camMediaBusy: " + camera.isMediaBusy() + " istakingPicture: " + camera.isTakingPicture() + " is media error: " + camera.isMediaError());

        if (aebCounter < aebSettingsArr.length) {
            try {
                Log.d(TAG, "aebCounter: " + aebCounter + "  aebSettingsArr: " + aebSettingsArr.length + " propval: " + aebSettingsArr[aebCounter]);
                camera.setCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_SHUTTER_SPEED, aebSettingsArr[aebCounter]);
                Log.d(TAG, "getActualShutter: " + camera.getActualShutterSpeed());
            } catch (OLYCameraKitException e) {
                e.printStackTrace();
                Log.d(TAG, "Message: " + e.getMessage());
            }
            aebCounter++;
            takePicture();
        } else {
            try {
                camera.setCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_SHUTTER_SPEED, aebSettingsArr[(aebCounter - 1) / 2]);//set Shutter backto where it was
            } catch (OLYCameraKitException e) {
                e.printStackTrace();
                Log.d(TAG, "Message: " + e.getMessage());
            }
            aebCounter = 0;
            //unlockAutoFocus();
            Log.d(TAG, "Reset aebCounter to: " + aebCounter + " AEBBool: " + AEB);
        }
    }

    //-------------------
    //  Single Picture
    //-------------------
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

    //-------------------
    //  shutter control (movie)
    //-------------------

    private void startRecordingVideo() {
        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            return;
        }
        AnimationDrawable blink = (AnimationDrawable) iv_recording.getDrawable();
        blink.start();
        HashMap<String, Object> options = new HashMap<String, Object>();
        camera.startRecordingVideo(options, new OLYCamera.CompletedCallback() {
            @Override
            public void onCompleted() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnLiveViewInteractionListener.onRecordVideoPressed(true);
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
        AnimationDrawable blink = (AnimationDrawable) iv_recording.getDrawable();
        blink.stop();
        camera.stopRecordingVideo(new OLYCamera.CompletedCallback() {
            @Override
            public void onCompleted() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnLiveViewInteractionListener.onRecordVideoPressed(false);
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

    // ------------------
    //  Tap Methods
    // ------------------

    private void focusModeTextViewDidTap() {
        Log.d(TAG, "Click focusmode");
        String propName, propVal, newPropVal = "";
        List<String> propValues;
        if (takeModeCounter == CameraActivity.SHOOTING_MODE_MOVIE)
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

    private void takeModeDidTap() {
        Log.d(TAG, "takeModeDidTap");
        //updateTakeModeImageView();
        takeModeCounter = (takeModeCounter + 1) % takeModeDrawablesArr.length;
        ib_TakeMode.setImageResource(takeModeDrawablesArr[takeModeCounter]);
        updateRecordingLayoutVisibility();
        triggerTakeModeUpdate(takeModeCounter);
    }

    public void triggerTakeModeUpdate(int takeModeCounter) {
        if (takeModeCounter != CameraActivity.SHOOTING_MODE_MOVIE)
            updateFocusModeTextView(CameraActivity.CAMERA_PROPERTY_FOCUS_STILL);

        if (mOnLiveViewInteractionListener != null) {
            mOnLiveViewInteractionListener.onTakeModeButtonPressed(takeModeCounter);
            Log.d(TAG, "mOnliveView... shootingmodeCounter: " + takeModeCounter);
        }
    }

    private void AEBracketingDidTap() {
        if (AEB) {
            AEB = false;
            iv_AEB.setImageResource(R.drawable.ic_no_aeb);
        } else {
            try {
                if (camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_TAKE_MODE).equals("<TAKEMODE/M>")) {
                    AEB = true;
                    iv_AEB.setImageResource(R.drawable.ic_aeb);
                } else {
                    presentMessage("Auto Exposure not Possible", "you need to be in M (ManualMode) to do Exposure Bracketing");
                }
            } catch (OLYCameraKitException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "AEB did Tap: " + AEB);
    }

    // ------------------
    // Updates
    // ------------------
    public void refresh() {
        Log.d(TAG, "refresh");
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void updateImageViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //updateTakeModeImageView();

                updateBatteryLevelImageView();
                updateRemainingRecordableImagesTextView();
                updateRecordTypeText();
                updateRecordingLayoutVisibility();
            }
        });
    }

    public void setEnabledTouchShutter(Boolean bool) {
        Log.d(TAG, "SetEnabledTouchShutter: " + bool);

        enabledTouchShutter = bool;
    }

    public void setEnabledTimeLapse(Boolean bool) {
        timelapse = bool;
        Log.d(TAG, "TimeLapse: " + timelapse);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (timelapse) {
                    ll_tl_timeLapse.setVisibility(View.VISIBLE);
                } else {
                    ll_tl_timeLapse.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    public void updateRecordTypeText() {

        String val="" ;
        try {
            val = camera.getCameraPropertyValue("RAW");
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
        }
        final String finVal=val;
        Log.d(TAG,"RawVal: "+ val);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ("ON".equals(CameraActivity.extractValue(finVal)))
                    tv_RecordType.setText("RAW");
                else
                    tv_RecordType.setText("JPG");
            }
        });

    }

    private void updateTakeModeImageView() {
        Log.d(TAG, "updateTakeModeImageView");
        CameraActivity.updatePropertyImageView(ib_TakeMode, takeModeIconsList, CameraActivity.CAMERA_PROPERTY_TAKE_MODE);
       /* try {
            String takeMode = camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_TAKE_MODE);
            takeModeCounter = CameraActivity.getTakeModeStrings().indexOf(takeMode);
            Log.d(TAG, "updateTakeModeIv shootingModecounter: " + takeModeCounter);
        } catch (OLYCameraKitException ex) {
            ex.printStackTrace();
        }
        ib_TakeMode.setImageResource(takeModeDrawablesArr[takeModeCounter]);*/
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
                iv_batteryLevelImageView.setImageResource(resId);
            } else {
                iv_batteryLevelImageView.setImageDrawable(null);
            }
        } else {
            iv_batteryLevelImageView.setImageDrawable(null);
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


    //---------------------------------------------------------------
    //  update Helpers
    //--------------------------------------------------------------

    private void updateRecordingLayoutVisibility() {
        if (takeModeCounter == 6) {
            //Log.d(TAG, "TRUE Recording Layout: " + takeModeCounter);
            ll_recording.setVisibility(View.VISIBLE);
        } else {
            //Log.d(TAG, "FALSE Recording Layout: " + takeModeCounter);
            ll_recording.setVisibility(View.INVISIBLE);
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
