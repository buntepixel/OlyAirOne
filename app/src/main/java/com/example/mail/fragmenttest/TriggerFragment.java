package com.example.mail.fragmenttest;

import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCamera.TakingProgress;
import jp.co.olympus.camerakit.OLYCamera.TakePictureCallback;

import jp.co.olympus.camerakit.OLYCameraAutoFocusResult;
import jp.co.olympus.camerakit.OLYCameraKitException;

/**
 * Created by mail on 14/06/2017.
 */

public class TriggerFragment extends Fragment
        implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = TriggerFragment.class.getSimpleName();

    private boolean time, aparture, exposureAdj, iso, wb;
    private final String[] settingsArr = new String[]{"4", "5.6", "0.0", "250", "Auto"};
    private int takeMode;
    OLYCamera camera;

    private static final String CAMERA_PROPERTY_DRIVE_MODE = "TAKE_DRIVE";
    private static final String CAMERA_PROPERTY_METERING_MODE = "AE";


    private ExposureCorrection expCorr;
    private List<String> possibleExpCorrValues;
    private ImageView iv_driveMode;
    private ImageView iv_meteringMode;
    private ImageView iv_shutter;
    private LiveViewFragment fLiveView;

    private OnTriggerFragmInteractionListener triggerFragmListener;

    @SuppressWarnings("serial")
    private static final Map<String, Integer> drivemodeIconList = new HashMap<String, Integer>() {
        {
            put("<TAKE_DRIVE/DRIVE_NORMAL>", R.drawable.icn_drive_setting_single);
            put("<TAKE_DRIVE/DRIVE_CONTINUE>", R.drawable.icn_drive_setting_seq_l);
        }
    };

    @SuppressWarnings("serial")
    private static final Map<String, Integer> meteringIconList = new HashMap<String, Integer>() {
        {
            put("<AE/AE_ESP>", R.drawable.icn_metering_esp);
            put("<AE/AE_CENTER>", R.drawable.icn_metering_center);
            put("<AE/AE_PINPOINT>", R.drawable.icn_metereing_pinpoint);
        }
    };


    public interface OnTriggerFragmInteractionListener {
        void onShootingModeInteraction(int settingsType);

        void onDriveModeChange(String propValue);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            return;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Log.d(TAG, "notdead A");
        View view = inflater.inflate(R.layout.fragment_trigger, container, false);
        view.setId(View.generateViewId());

        iv_driveMode = (ImageView) view.findViewById(R.id.ib_drivemode);
        iv_meteringMode = (ImageView) view.findViewById(R.id.ib_metering);
        iv_shutter = (ImageView) view.findViewById(R.id.ib_shutterrelease);

        iv_driveMode.setOnClickListener(this);
        iv_meteringMode.setOnClickListener(this);
        iv_shutter.setOnTouchListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == iv_driveMode)
            drivemodeImageViewDidTap();
        else if (v == iv_meteringMode)
            meteringImageViewDidTap();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == iv_shutter) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                fLiveView.ShutterImageViewDidTouchDown();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                fLiveView.ShutterImageViewDidTouchUp();
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (takeMode < 1 || takeMode > 5)
            iv_meteringMode.setVisibility(View.INVISIBLE);
        else
            updateMeteringImageView();
    }

    public void SetLiveViewFragment(LiveViewFragment liveViewFragment){
        if(liveViewFragment!=null){
            fLiveView=liveViewFragment;
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


    private void unlockImageViewDidTap() {
        fLiveView.UnlockAutoFocus();
    }

    // shutter control (still)

    private void takePicture() {
        if (camera.isTakingPicture() || camera.isRecordingVideo()) {
            return;
        }

        HashMap<String, Object> options = new HashMap<String, Object>();
        camera.takePicture(options, new TakePictureCallback() {
            @Override
            public void onProgress(OLYCamera camera, TakingProgress progress, OLYCameraAutoFocusResult autoFocusResult) {
                if (progress == TakingProgress.EndFocusing) {
                    if (!fLiveView.GetEnabledFocusLock()) {
                        if (autoFocusResult.getResult().equals("ok") && autoFocusResult.getRect() != null) {
                            RectF postFocusFrameRect = autoFocusResult.getRect();
                            fLiveView.GetLiveImageView().showFocusFrame(postFocusFrameRect, CameraLiveImageView.FocusFrameStatus.Focused);
                        } else if (autoFocusResult.getResult().equals("none")) {
                            fLiveView.GetLiveImageView().hideFocusFrame();
                        } else {
                            fLiveView.GetLiveImageView().hideFocusFrame();
                        }
                    }
                } else if (progress == TakingProgress.BeginCapturing) {
                   fLiveView.GetShutterSoundPlayer().start();
                }
            }

            @Override
            public void onCompleted() {
                if (!fLiveView.GetEnabledFocusLock()) {
                    try {
                        camera.clearAutoFocusPoint();
                    } catch (OLYCameraKitException ee) {
                        ee.printStackTrace();
                    }
                    fLiveView.GetLiveImageView().hideFocusFrame();
                }
            }

            @Override
            public void onErrorOccurred(Exception e) {
                if (!fLiveView.GetEnabledFocusLock()) {
                    try {
                        camera.clearAutoFocusPoint();
                    } catch (OLYCameraKitException ee) {
                        ee.printStackTrace();
                    }
                    fLiveView.GetLiveImageView().hideFocusFrame();
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

    //----------------

    public void SetTakeMode(int takeMode) {
        this.takeMode = takeMode;
    }

    private void updateDrivemodeImageView() {
        updatePropertyImageView(iv_driveMode, drivemodeIconList, CAMERA_PROPERTY_DRIVE_MODE);
    }

    private void drivemodeImageViewDidTap() {
        final View view = iv_driveMode;
        final String propertyName = CAMERA_PROPERTY_DRIVE_MODE;
        cameraPropertyDidTab(view, propertyName);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateDrivemodeImageView();
            }
        });
    }

    private void updateMeteringImageView() {
        updatePropertyImageView(iv_meteringMode, meteringIconList, CAMERA_PROPERTY_METERING_MODE);
    }

    private void meteringImageViewDidTap() {
        //Log.d(TAG, "Click");
        final View view = iv_meteringMode;
        final String propertyName = CAMERA_PROPERTY_METERING_MODE;

        if (takeMode < 1 || takeMode > 5)
            view.setVisibility(View.INVISIBLE);
        else
            cameraPropertyDidTab(view, propertyName);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateMeteringImageView();
            }
        });
    }

    private void updatePropertyImageView(ImageView imageView, Map<String, Integer> iconList, String propertyName) {
        imageView.setEnabled(camera.canSetCameraProperty(propertyName));
        Log.d(TAG, "Update: " + propertyName);
        String propValue;
        try {
            propValue = camera.getCameraPropertyValue(propertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return;
        }

        if (propValue == null) {
            return;
        }
        if (iconList.containsKey(propValue)) {
            int resId = iconList.get(propValue);
            imageView.setImageResource(resId);
            triggerFragmListener.onDriveModeChange(propValue);
        } else {

            imageView.setImageDrawable(null);
        }
    }

    private void cameraPropertyDidTab(View inView, String inPropertyName) {
        final List<String> valueList;
        try {
            valueList = camera.getCameraPropertyValueList(inPropertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return;
        }
        if (valueList == null || valueList.size() == 0) return;

        String value;
        try {
            value = camera.getCameraPropertyValue(inPropertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return;
        }
        if (value == null) return;
        inView.setSelected(true);

        try {
            int index = valueList.indexOf(value) + 1;
            //Log.d(TAG, "Index: " + index);
            int listSize = valueList.size();
            //Log.d(TAG, "listSize: " + listSize);
            int moduloIndex = index % listSize;
            //Log.d(TAG, "ModuloIndex: " + moduloIndex);
            camera.setCameraPropertyValue(inPropertyName, valueList.get(moduloIndex));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SetOLYCam(OLYCamera camera) {
        this.camera = camera;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            triggerFragmListener = (OnTriggerFragmInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTriggerFragmInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (triggerFragmListener != null)
            triggerFragmListener = null;
    }
}


