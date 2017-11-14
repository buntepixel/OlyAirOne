package com.example.mail.fragmenttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraKitException;

/**
 * Created by mail on 14/06/2017.
 */

public class SettingsFragment extends Fragment
        implements View.OnClickListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private boolean time, aparture, exposureAdj, iso, wb;
    private final String[] settingsArr = new String[]{"4", "5.6", "0.0", "250", "Auto"};
    private int takeMode;
    OLYCamera camera;

    private static final String CAMERA_PROPERTY_DRIVE_MODE = "TAKE_DRIVE";
    private static final String CAMERA_PROPERTY_METERING_MODE = "AE";

    private TextView tv_expTime;
    private TextView tv_expTimeText;
    private LinearLayout ll_expTime;
    private TextView tv_fStop;
    private TextView tv_fStopText;
    private LinearLayout ll_fStop;
    private TextView tv_iso;
    private TextView tv_isoText;
    private LinearLayout ll_iso;
    private ImageView iv_Wb;
    private LinearLayout ll_Wb;
    private TextView tv_expOffset;
    private LinearLayout ll_expOffset;


    private ExposureCorrection expCorr;
    private List<String> possibleExpCorrValues;


    private OnSettingsFragmInteractionListener settingsFragmListener;

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


    public interface OnSettingsFragmInteractionListener {
        void onShootingModeInteraction(int settingsType);

        void onDriveModeChange(String propValue);
    }


    public void SetOLYCam(OLYCamera camera) {
        this.camera = camera;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        view.setId(View.generateViewId());


        CreateSettings(settingsArr, view);
//        //shutter release pressed
//        ImageButton ib_shutterRelease = (ImageButton) view.findViewById(R.id.ib_shutterrelease);
//        ib_shutterRelease.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Do something in response to button click
//                //Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
//                mCallback.onShutterReleasedPressed(10);
//
//            }
//        });

        //ImageButton ib_driveMode = (ImageButton) view.findViewById(R.id.ib_drivemode);
   /*     ll_expTime.setOnClickListener(this);
        ll_fStop.setOnClickListener(this);
        ll_expOffset.setOnClickListener(this);
        ll_iso.setOnClickListener(this);
        ll_Wb.setOnClickListener(this);*/
        return view;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Click: " + v);
        if (v == ll_expTime) {
            settingsFragmListener.onShootingModeInteraction(0);
        } else if (v == ll_fStop) {
            settingsFragmListener.onShootingModeInteraction(1);
        } else if (v == ll_expOffset) {
            settingsFragmListener.onShootingModeInteraction(2);
        } else if (v == ll_iso) {
            settingsFragmListener.onShootingModeInteraction(3);
        } else if (v == ll_Wb) {
            Log.d(TAG, "ImageView click:");

            settingsFragmListener.onShootingModeInteraction(4);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("takeMode", takeMode);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            takeMode = savedInstanceState.getInt("takeMode", 0);
        }
        //update values and set onclickListeners
        updateValues();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "tageMode: " + takeMode);
        // restoreCamSettings();
        //UpdateValues();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveCamSettings();

    }

    private RelativeLayout CreateSettings(String[] inputStringArr, View rootView) {
        RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.rl_settings);

        SetupButtons(relativeLayout);
        return relativeLayout;
    }

    private void SetupButtons(RelativeLayout relativeLayout) {
        //LinearLayout linearLayout = ll_main;
        int padding = 40;

        //ExposureCorr
        LinearLayout center_linearLayout = CreateExposureCorr(getResources().getColorStateList(R.color.button_text_states), padding);
        LinearLayout left_LinearLayout = CreateExpTFstop(getResources().getColorStateList(R.color.button_text_states), padding, center_linearLayout);
        LinearLayout right_LinearLayout = CreateIsoWBBtn(getResources().getColorStateList(R.color.button_text_states), padding, center_linearLayout);

        relativeLayout.addView(left_LinearLayout);
        relativeLayout.addView(center_linearLayout);
        relativeLayout.addView(right_LinearLayout);
    }

    public void SetButtonsBool(boolean time, boolean aparture, boolean exposureAdj, boolean iso, boolean wb) {
        this.time = time;
        this.aparture = aparture;
        this.exposureAdj = exposureAdj;
        this.iso = iso;
        this.wb = wb;
    }

    public void SetTakeMode(int takeMode) {
        this.takeMode = takeMode;
        switch (takeMode) {
            case 0://iAuto
                SetButtonsBool(false, false, false, false, false);
                Log.d(TAG, "Iauto");
                break;
            case 1://Programm
                SetButtonsBool(false, false, true, true, true);
                Log.d(TAG, "Programm");
                break;
            case 2://Aparture
                SetButtonsBool(false, true, true, true, true);
                Log.d(TAG, "Aparture");
                break;
            case 3://Speed
                SetButtonsBool(true, false, true, true, true);
                Log.d(TAG, "Speed");
                break;
            case 4://Manual
                SetButtonsBool(true, true, false, true, true);
                Log.d(TAG, "Manual");
                break;
            case 5://Art
                SetButtonsBool(false, false, true, true, true);
                Log.d(TAG, "Art");
                break;
            case 6://Movie
                SetButtonsBool(false, true, true, false, true);
                Log.d(TAG, "Movie");
                break;
        }
        updateValues();
    }

    public void SetExposureCorrValues(List<String> values) {
        possibleExpCorrValues = values;
    }

    public void SetSliderResult(String property, String value) {
        try {
            Log.d(TAG, "onSlideValueBar_TriggerFragment: " + property + " value " + value);
            camera.setCameraPropertyValue(property, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Set appropriate text to value
        switch (property) {
            case "SHUTTER":
                tv_expTime.setText(camera.getCameraPropertyValueTitle(value));
                break;
            case "APERTURE":
                tv_fStop.setText(camera.getCameraPropertyValueTitle(value));
                break;
            case "ISO":
                tv_iso.setText(camera.getCameraPropertyValueTitle(value));
                break;
            case "WB":
                Log.d(TAG, "CameraValue: " + camera.getCameraPropertyValueTitle(value));
                iv_Wb.setImageResource(whiteBalanceIconList.get(value));
                break;
            case "EXPREV":
                String myVal = camera.getCameraPropertyValueTitle(value);
                tv_expOffset.setText(myVal);
                int myIndex = possibleExpCorrValues.indexOf(value);
                expCorr.SetLineParams(myIndex);
                break;
        }
    }

    //UPDATES
    private void updateValues() {
        updateWbImageView();
        updateIsoTxtView();
        updateApartureTextView();
        updateShutterSpTextView();
        updateExposureCompTxtView();
    }

    private void updateWbImageView() {
        Log.d(TAG, "updating Wb");
        updatePropertyImageView(iv_Wb, whiteBalanceIconList, CameraActivity.CAMERA_PROPERTY_WHITE_BALANCE);
        if (wb) {
            iv_Wb.setEnabled(true);
            ll_Wb.setOnClickListener(this);
        } else {
            iv_Wb.setEnabled(false);
            ll_Wb.setOnClickListener(null);
        }
    }

    private void updateIsoTxtView() {
        Log.d(TAG, "updating ISO");
        updatePropertyTxtView(tv_iso, CameraActivity.CAMERA_PROPERTY_ISO_SENSITIVITY);
        if (iso) {
            tv_isoText.setEnabled(true);
            tv_iso.setEnabled(true);
            ll_iso.setOnClickListener(this);
        } else {
            tv_isoText.setEnabled(false);
            tv_iso.setEnabled(false);
            ll_iso.setOnClickListener(null);
        }
    }

    private void updateApartureTextView() {
        Log.d(TAG, "updating Aparture");
        updatePropertyTxtView(tv_fStop, CameraActivity.CAMERA_PROPERTY_APERTURE_VALUE);
        if (aparture) {
            tv_fStopText.setEnabled(true);
            tv_fStop.setEnabled(true);
            ll_fStop.setOnClickListener(this);
        } else {
            tv_fStopText.setEnabled(false);
            tv_fStop.setEnabled(false);
            ll_fStop.setOnClickListener(null);
        }
    }

    private void updateShutterSpTextView() {
        Log.d(TAG, "updating ShutterSpeed");
        updatePropertyTxtView(tv_expTime, CameraActivity.CAMERA_PROPERTY_SHUTTER_SPEED);
        if (time) {
            tv_expTimeText.setEnabled(true);
            tv_expTime.setEnabled(true);
            ll_expTime.setOnClickListener(this);
        } else {
            tv_expTimeText.setEnabled(false);
            tv_expTime.setEnabled(false);
            ll_expTime.setOnClickListener(null);
        }
    }

    private void updateExposureCompTxtView() {
        Log.d(TAG, "updating ExposureCompensation");
        updatePropertyTxtView(tv_expOffset, CameraActivity.CAMERA_PROPERTY_EXPOSURE_COMPENSATION);
        if (exposureAdj) {
            tv_expOffset.setEnabled(true);
            ll_expOffset.setOnClickListener(this);
        } else {
            tv_expOffset.setEnabled(false);
            tv_expOffset.setText("0.0");
            ll_expOffset.setOnClickListener(null);
        }


    }

    private void updatePropertyImageView(ImageView imageView, Map<String, Integer> iconList, String propertyName) {
        imageView.setEnabled(camera.canSetCameraProperty(propertyName));
        Log.d(TAG, "UpdateImageView: " + propertyName);
        String propValue;
        Log.d(TAG, "PropName: " + propertyName + "Cameactive" + camera.isConnected());

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
        if (iconList.containsKey(propValue)) {
            int resId = iconList.get(propValue);
            imageView.setImageResource(resId);
            //settingsFragmListener.onDriveModeChange(propValue);
        } else {
            imageView.setImageDrawable(null);
        }
    }

    private void updatePropertyTxtView(TextView textView, String propertyName) {
        textView.setEnabled(camera.canSetCameraProperty(propertyName));
        Log.d(TAG, "UpdateTextView: " + propertyName);
        String propValue;
        try {
            propValue = camera.getCameraPropertyValue(propertyName);
            Log.d(TAG, "PropName: " + propValue);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return;
        }


        if (propValue == null) {
            return;
        } else {
            textView.setText(camera.getCameraPropertyValueTitle(propValue));
        }
    }

    //RESTORE

    private void restoreCamSettings() {
        SharedPreferences settings = getActivity().getSharedPreferences(CameraActivity.CAMERA_SETTINGS, 0);
        String driveMode = settings.getString(CAMERA_PROPERTY_DRIVE_MODE, null);
        String meteringMode = settings.getString(CAMERA_PROPERTY_METERING_MODE, null);
        try {
            if (driveMode != null) {
                camera.setCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_SHUTTER_SPEED, driveMode);
                updateShutterSpTextView();
            }
            if (meteringMode != null) {
                camera.setCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_ISO_SENSITIVITY, meteringMode);
                updateIsoTxtView();
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
        String shutter = null;
        String iso = null;
        String wb = null;
        String aparture = null;
        String expComp = null;
        try {
            shutter = camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_SHUTTER_SPEED);
            iso = camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_ISO_SENSITIVITY);
            wb = camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_WHITE_BALANCE);
            aparture = camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_APERTURE_VALUE);
            expComp = camera.getCameraPropertyValue(CameraActivity.CAMERA_PROPERTY_EXPOSURE_COMPENSATION);

        } catch (OLYCameraKitException ex) {
            ex.printStackTrace();
        }
        editor.putString(CameraActivity.CAMERA_PROPERTY_SHUTTER_SPEED, shutter);
        editor.putString(CameraActivity.CAMERA_PROPERTY_ISO_SENSITIVITY, iso);
        editor.putString(CameraActivity.CAMERA_PROPERTY_WHITE_BALANCE, wb);
        editor.putString(CameraActivity.CAMERA_PROPERTY_APERTURE_VALUE, aparture);
        editor.putString(CameraActivity.CAMERA_PROPERTY_EXPOSURE_COMPENSATION, expComp);

        // Commit the edits!
        editor.commit();

    }

    private LinearLayout CreateExpTFstop(ColorStateList colorStateList, int padding, LinearLayout alignLayout) {
        LinearLayout root_linearLayout = new LinearLayout(getContext());
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        root_linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        relParams.addRule(RelativeLayout.START_OF, alignLayout.getId());
        relParams.addRule(RelativeLayout.CENTER_VERTICAL);
        root_linearLayout.setLayoutParams(relParams);

        // exposure Time
        ll_expTime = new LinearLayout(getContext());
        ll_expTime.setOrientation(LinearLayout.VERTICAL);
        ll_expTime.setId(View.generateViewId());

        tv_expTimeText = new TextView(getContext());
        tv_expTimeText.setText("EXP");
        tv_expTimeText.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_expTime = new TextView(getActivity());
        tv_expTime.setText(settingsArr[0]);
        tv_expTime.setPaddingRelative(padding, 0, padding, 0);
        tv_expTime.setTextColor(colorStateList);
        tv_expTimeText.setTextColor(colorStateList);

        ll_expTime.addView(tv_expTimeText);
        ll_expTime.addView(tv_expTime);
        root_linearLayout.addView(ll_expTime);

        //Fstop
        ll_fStop = new LinearLayout(getContext());
        ll_fStop.setOrientation(LinearLayout.VERTICAL);
        ll_fStop.setId(View.generateViewId());

        tv_fStopText = new TextView(getContext());
        tv_fStopText.setText("F");
        tv_fStopText.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_fStop = new TextView(getActivity());
        tv_fStop.setText(settingsArr[1]);
        tv_fStop.setPaddingRelative(padding, 0, padding, 0);
        tv_fStop.setTextColor(colorStateList);
        tv_fStopText.setTextColor(colorStateList);

        ll_fStop.addView(tv_fStopText);
        ll_fStop.addView(tv_fStop);
        root_linearLayout.addView(ll_fStop);
        return root_linearLayout;
    }

    private LinearLayout CreateExposureCorr(ColorStateList colorStateList, int padding) {
        ll_expOffset = new LinearLayout(getContext());
        ll_expOffset.setId(View.generateViewId());
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        ll_expOffset.setMinimumWidth(130);

        ll_expOffset.setOrientation(LinearLayout.VERTICAL);
        ll_expOffset.setLayoutParams(relParams);

        String expOffsetTxt = " 0.0 ";

        tv_expOffset = new TextView(getActivity());
        tv_expOffset.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_expOffset.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv_expOffset.setText(expOffsetTxt);
        tv_expOffset.setTextColor(colorStateList);


        ll_expOffset.addView(tv_expOffset);
        //Expcorr Layout only if manual Mode
        if (takeMode > 0 && takeMode < 4) {
            LinearLayout containerLLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams linParams = (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            containerLLayout.setLayoutParams(linParams);
            containerLLayout.setGravity(Gravity.CENTER_VERTICAL);
            containerLLayout.setOrientation(LinearLayout.HORIZONTAL);
            containerLLayout.setWeightSum(8);
            ll_expOffset.addView(containerLLayout);

            TextView leftText = new TextView(getContext());
            leftText.setGravity(Gravity.CENTER);
            leftText.setMinWidth(10);

            leftText.setText("-");
            leftText.setWidth(30);
            leftText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextWhite));
            leftText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            containerLLayout.addView(leftText);


            expCorr = new ExposureCorrection(getContext());
            linParams = (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            expCorr.setLayoutParams(linParams);

            containerLLayout.addView(expCorr);

            TextView rightText = new TextView(getContext());
            rightText.setGravity(Gravity.CENTER);
            rightText.setWidth(30);
            rightText.setText("+");
            rightText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextWhite));
            rightText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            containerLLayout.addView(rightText);
        }
        return ll_expOffset;
    }

    private LinearLayout CreateIsoWBBtn(ColorStateList colorStateList, int padding, LinearLayout alignLayout) {
        LinearLayout root_linearLayout = new LinearLayout(getContext());
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //relParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        relParams.addRule(RelativeLayout.END_OF, alignLayout.getId());
        root_linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        root_linearLayout.setLayoutParams(relParams);

        //iso
        ll_iso = new LinearLayout(getContext());
        ll_iso.setOrientation(LinearLayout.VERTICAL);
        ll_iso.setId(View.generateViewId());

        tv_isoText = new TextView(getContext());
        tv_isoText.setText("ISO");
        tv_isoText.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_iso = new TextView(getActivity());
        tv_iso.setText(settingsArr[3]);
        tv_iso.setGravity(Gravity.CENTER);
        tv_iso.setPaddingRelative(padding, 0, padding, 0);
        tv_iso.setTextColor(colorStateList);
        tv_isoText.setTextColor(colorStateList);

        ll_iso.addView(tv_isoText);
        ll_iso.addView(tv_iso);
        root_linearLayout.addView(ll_iso);

        //WhiteBalance
        ll_Wb = new LinearLayout(getContext());
        ll_Wb.setOrientation(LinearLayout.VERTICAL);
        ll_Wb.setId(View.generateViewId());


        iv_Wb = new ImageView(getActivity());
        //todo: make setter for prefs
        iv_Wb.setImageResource(R.drawable.icn_wb_setting_16);
        iv_Wb.setPaddingRelative(padding, 0, padding, 0);

        ll_Wb.addView(iv_Wb);
        root_linearLayout.addView(ll_Wb);
        return root_linearLayout;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            settingsFragmListener = (OnSettingsFragmInteractionListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnSettingsFragmInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (settingsFragmListener != null)
            settingsFragmListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tv_expTime = null; tv_expTimeText =null; ll_expTime=null; tv_fStop=null; tv_fStopText=null; ll_fStop=null;
        tv_iso=null; tv_isoText=null; ll_iso=null;iv_Wb=null; ll_Wb=null; tv_expOffset=null;  ll_expOffset =null;
    }
}


