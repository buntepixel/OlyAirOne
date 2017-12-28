package com.example.mail.OlyAirONE;

import android.content.Context;
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
        camera = CameraActivity.getCamera();
        Log.d(TAG, " camera Conntected: camera set:" + camera.isConnected());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trigger, container, false);
        view.setId(View.generateViewId());

        iv_driveMode = view.findViewById(R.id.ib_drivemode);
        iv_meteringMode = view.findViewById(R.id.ib_metering);
        iv_shutter = view.findViewById(R.id.ib_shutterrelease);

        iv_shutter.setOnTouchListener(this);
        iv_driveMode.setOnClickListener(this);
        iv_meteringMode.setOnClickListener(this);

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
        Log.d(TAG, "takemode on resume: " + takeMode);
        if (takeMode < 1 || takeMode > 5)
            iv_meteringMode.setVisibility(View.INVISIBLE);
        else
            iv_meteringMode.setVisibility(View.VISIBLE);
        // updateMeteringImageView();
    }

    public void refresh() {
        Log.d(TAG,"refresh");
        updateDrivemodeImageView();
        updateMeteringImageView();
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
 /*   public void updateAfterCamConnection() {
        updateDrivemodeImageView();
    }*/
//----------------

    public void SetTakeMode(int takeMode) {
        this.takeMode = takeMode;
    }

    public void updateDrivemodeImageView(String value) {
        updateImageView(iv_driveMode, drivemodeIconList, value);
    }

    private void drivemodeImageViewDidTap() {

        updateImageView(iv_driveMode, drivemodeIconList, cameraPropertyDidTab(iv_driveMode, CameraActivity.CAMERA_PROPERTY_DRIVE_MODE) );
       /* getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });*/
    }

    private void updateDrivemodeImageView() {
        updatePropertyImageView(iv_driveMode, drivemodeIconList, CameraActivity.CAMERA_PROPERTY_DRIVE_MODE);
    }

    private void updateMeteringImageView() {
        updatePropertyImageView(iv_meteringMode, meteringIconList, CameraActivity.CAMERA_PROPERTY_METERING_MODE);
    }

    private void meteringImageViewDidTap() {
        final View view = iv_meteringMode;
        if (takeMode < 1 || takeMode > 5)
            view.setVisibility(View.INVISIBLE);
        else
            cameraPropertyDidTab(view, CameraActivity.CAMERA_PROPERTY_METERING_MODE);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateMeteringImageView();
            }
        });
    }

    private void updatePropertyImageView(ImageView imageView, Map<String, Integer> iconList, String propertyName) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateImageView(ImageView imageView, Map<String, Integer> iconList, String propValue) {
        try {
            if (iconList.containsKey(propValue)) {
                int resId = iconList.get(propValue);
                imageView.setImageResource(resId);
            } else {
                imageView.setImageDrawable(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String cameraPropertyDidTab(View inView, String inPropertyName) {
        final List<String> valueList;
        String retVal = "";
        try {
            valueList = camera.getCameraPropertyValueList(inPropertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return "";
        }
        if (valueList == null || valueList.size() == 0) return retVal;

        String value;
        try {
            value = camera.getCameraPropertyValue(inPropertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return retVal;
        }
        if (value == null) return retVal;
        inView.setSelected(true);

        try {
            int index = valueList.indexOf(value) + 1;
            //Log.d(TAG, "Index: " + index);
            int listSize = valueList.size();
            //Log.d(TAG, "listSize: " + listSize);
            int moduloIndex = index % listSize;
            Log.d(TAG, "Property: " + inPropertyName + " Value: " + valueList.get(moduloIndex));
            retVal = valueList.get(moduloIndex);
            camera.setCameraPropertyValue(inPropertyName,retVal );
            return retVal;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
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


