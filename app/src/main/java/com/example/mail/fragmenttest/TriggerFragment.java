package com.example.mail.fragmenttest;

import android.content.Context;
import android.content.SharedPreferences;
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
    private String val_driveMode;
    private ImageView iv_meteringMode;
    private String val_meteringMode;
    private ImageView iv_shutter;

    private Boolean enabledFocusLock;

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
        void onShutterTouched(MotionEvent event);

        //void updateDrivemodeImage();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
                triggerFragmListener.onShutterTouched(event);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                triggerFragmListener.onShutterTouched(event);
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Todo: find way to do it after reconnecting to the cam
        //restoreCamSettings();
        if (takeMode < 1 || takeMode > 5)
            iv_meteringMode.setVisibility(View.INVISIBLE);
        else
            updateMeteringImageView();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveCamSettings();
    }

    public void updateAfterCamConnection() {
        updateDrivemodeImageView();
    }
//----------------

    public void SetTakeMode(int takeMode) {
        this.takeMode = takeMode;
    }

    public void updateDrivemodeImageView() {
        updatePropertyImageView(iv_driveMode, drivemodeIconList, CAMERA_PROPERTY_DRIVE_MODE);
    }
    public void updateDrivemodeImageView(String value){
        updateImageView( iv_driveMode,drivemodeIconList,value);
    }

    private void drivemodeImageViewDidTap() {
        final View view = iv_driveMode;
        cameraPropertyDidTab(view, CAMERA_PROPERTY_DRIVE_MODE);

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
        if (takeMode < 1 || takeMode > 5)
            view.setVisibility(View.INVISIBLE);
        else
            cameraPropertyDidTab(view, CAMERA_PROPERTY_METERING_MODE);

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
            Log.d(TAG, "PropVal: " + propValue);

        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return;
        }

        if (propValue == null) {
            return;
        }
        updateImageView(imageView, iconList, propValue);

    }

    public void updateImageView(ImageView imageView, Map<String, Integer> iconList, String propValue) {
        if (iconList.containsKey(propValue)) {
            int resId = iconList.get(propValue);
            imageView.setImageResource(resId);
            //triggerFragmListener.updateDrivemodeImage();
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
            Log.d(TAG, "Property: " + inPropertyName + " Value: " + valueList.get(moduloIndex));
            camera.setCameraPropertyValue(inPropertyName, valueList.get(moduloIndex));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SetOLYCam(OLYCamera camera) {
        this.camera = camera;

    }

    private void restoreCamSettings() {
        SharedPreferences settings = getActivity().getSharedPreferences(CameraActivity.CAMERA_SETTINGS, 0);
        String driveMode = settings.getString(CAMERA_PROPERTY_DRIVE_MODE, null);
        String meteringMode = settings.getString(CAMERA_PROPERTY_METERING_MODE, null);
        try {
            if (driveMode != null) {
                camera.setCameraPropertyValue(CAMERA_PROPERTY_DRIVE_MODE, driveMode);
                updatePropertyImageView(iv_driveMode, drivemodeIconList, driveMode);
            }
            if (meteringMode != null) {
                camera.setCameraPropertyValue(CAMERA_PROPERTY_METERING_MODE, meteringMode);
                updatePropertyImageView(iv_meteringMode, meteringIconList, meteringMode);
            }

        } catch (OLYCameraKitException ex) {
            ex.printStackTrace();
        }

    }

    private void saveCamSettings() {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context

        SharedPreferences settings = getActivity().getSharedPreferences(CameraActivity.CAMERA_SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
        String driveMode = null;
        String meteringMode = null;
        try {
            driveMode = camera.getCameraPropertyValue(CAMERA_PROPERTY_DRIVE_MODE);
            meteringMode = camera.getCameraPropertyValue(CAMERA_PROPERTY_METERING_MODE);
        } catch (OLYCameraKitException ex) {
            ex.printStackTrace();
        }
        editor.putString(CAMERA_PROPERTY_DRIVE_MODE, driveMode);
        editor.putString(CAMERA_PROPERTY_METERING_MODE, meteringMode);

        // Commit the edits!
        editor.apply();

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


