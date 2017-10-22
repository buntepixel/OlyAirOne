package com.example.mail.fragmenttest;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
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

public class TriggerFragment extends Fragment {
    private static final String TAG = TriggerFragment.class.getSimpleName();

    private boolean time, aparture, exposureAdj, iso, wb;
    private final String[] settingsArr = new String[]{"4", "5.6", "0.0", "250", "Auto"};
    private int takeMode;
    OLYCamera camera;

    private static final String CAMERA_PROPERTY_DRIVE_MODE = "TAKE_DRIVE";
    private static final String CAMERA_PROPERTY_METERING_MODE = "AE";
    private static final String CAMERA_PROPERTY_EXPOSURE_COMPENSATION = "EXPREV";


    private TextView tv_expTime;
    private TextView tv_fStop;
    private TextView tv_iso;
    private ImageView iv_Wb;
    private TextView tv_expOffset;

    private ExposureCorrection expCorr;
    private List<String> possibleExpCorrValues;
    private ImageView drivemodeImageView;
    private ImageView meteringImageView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        //Log.d(TAG, "notdead A");
        View view = inflater.inflate(R.layout.fragment_trigger, container, false);
        view.setId(View.generateViewId());

        drivemodeImageView = (ImageView) view.findViewById(R.id.ib_drivemode);
        meteringImageView = (ImageView) view.findViewById(R.id.ib_metering);


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
        drivemodeImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drivemodeImageViewDidTap();
            }
        });
        meteringImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meteringImageViewDidTap();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (takeMode < 1 || takeMode > 5)
            meteringImageView.setVisibility(View.INVISIBLE);
        else
            updateMeteringImageView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private RelativeLayout CreateSettings(String[] inputStringArr, View rootView) {
        RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.rl_settings);
        //Log.d(TAG, "notDead B");

        //linearLayout.setBackgroundColor(Color.YELLOW);
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
    }

    public void SetExposureCorrValues(List<String> values) {
        possibleExpCorrValues = values;
    }


    private void updateDrivemodeImageView() {
        updatePropertyImageView(drivemodeImageView, drivemodeIconList, CAMERA_PROPERTY_DRIVE_MODE);
    }

    private void drivemodeImageViewDidTap() {
        final View view = drivemodeImageView;
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
        updatePropertyImageView(meteringImageView, meteringIconList, CAMERA_PROPERTY_METERING_MODE);
    }

    private void meteringImageViewDidTap() {
        //Log.d(TAG, "Click");
        final View view = meteringImageView;
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


    private LinearLayout CreateExpTFstop(ColorStateList colorStateList, int padding, LinearLayout alignLayout) {
        LinearLayout root_linearLayout = new LinearLayout(getContext());
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        root_linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        relParams.addRule(RelativeLayout.START_OF, alignLayout.getId());
        relParams.addRule(RelativeLayout.CENTER_VERTICAL);
        root_linearLayout.setLayoutParams(relParams);

        // exposure Time
        LinearLayout ll_expTime = new LinearLayout(getContext());
        ll_expTime.setOrientation(LinearLayout.VERTICAL);
        ll_expTime.setId(View.generateViewId());

        TextView tv_expTimeText = new TextView(getContext());
        tv_expTimeText.setText("EXP");
        tv_expTimeText.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_expTime = new TextView(getActivity());
        tv_expTime.setText(settingsArr[0]);
        tv_expTime.setPaddingRelative(padding, 0, padding, 0);
        tv_expTime.setTextColor(colorStateList);
        tv_expTimeText.setTextColor(colorStateList);
        if (time) {
            tv_expTimeText.setEnabled(true);
            tv_expTime.setEnabled(true);
            ll_expTime.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[0], Toast.LENGTH_SHORT).show();
                    triggerFragmListener.onShootingModeInteraction(0);
                }
            });
        } else {
            tv_expTimeText.setEnabled(false);
            tv_expTime.setEnabled(false);
        }
        ll_expTime.addView(tv_expTimeText);
        ll_expTime.addView(tv_expTime);
        root_linearLayout.addView(ll_expTime);

        //Fstop
        LinearLayout ll_fStop = new LinearLayout(getContext());
        ll_fStop.setOrientation(LinearLayout.VERTICAL);
        ll_fStop.setId(View.generateViewId());

        TextView tv_fStopText = new TextView(getContext());
        tv_fStopText.setText("F");
        tv_fStopText.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_fStop = new TextView(getActivity());
        tv_fStop.setText(settingsArr[1]);
        tv_fStop.setPaddingRelative(padding, 0, padding, 0);
        tv_fStop.setTextColor(colorStateList);
        tv_fStopText.setTextColor(colorStateList);
        if (aparture) {
            tv_fStopText.setEnabled(true);
            tv_fStop.setEnabled(true);
            ll_fStop.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), settingsArr[1], Toast.LENGTH_SHORT).show();
                    triggerFragmListener.onShootingModeInteraction(1);
                }
            });
        } else {
            tv_fStopText.setEnabled(false);
            tv_fStop.setEnabled(false);
        }
        ll_fStop.addView(tv_fStopText);
        ll_fStop.addView(tv_fStop);
        root_linearLayout.addView(ll_fStop);
        return root_linearLayout;
    }

    private LinearLayout CreateExposureCorr(ColorStateList colorStateList, int padding) {
        LinearLayout rootLinearLayout = new LinearLayout(getContext());
        rootLinearLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rootLinearLayout.setMinimumWidth(130);

        rootLinearLayout.setOrientation(LinearLayout.VERTICAL);
        rootLinearLayout.setLayoutParams(relParams);

        String expOffsetTxt = " 0.0 ";

        tv_expOffset = new TextView(getActivity());
        tv_expOffset.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_expOffset.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv_expOffset.setText(expOffsetTxt);
        tv_expOffset.setTextColor(colorStateList);
        if (exposureAdj) {
            tv_expOffset.setEnabled(true);
            rootLinearLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[2], Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "exposureCorr clicked");
                    triggerFragmListener.onShootingModeInteraction(2);
                }
            });
        } else
            tv_expOffset.setEnabled(false);

        rootLinearLayout.addView(tv_expOffset);
        //Expcorr Layout only if manual Mode
        if (takeMode > 0 && takeMode < 4) {
            LinearLayout containerLLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams linParams = (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            containerLLayout.setLayoutParams(linParams);
            containerLLayout.setGravity(Gravity.CENTER_VERTICAL);
            containerLLayout.setOrientation(LinearLayout.HORIZONTAL);
            containerLLayout.setWeightSum(8);
            rootLinearLayout.addView(containerLLayout);

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
        return rootLinearLayout;
    }

    private LinearLayout CreateIsoWBBtn(ColorStateList colorStateList, int padding, LinearLayout alignLayout) {
        LinearLayout root_linearLayout = new LinearLayout(getContext());
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //relParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        relParams.addRule(RelativeLayout.END_OF, alignLayout.getId());
        root_linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        root_linearLayout.setLayoutParams(relParams);


        //iso
        LinearLayout ll_iso = new LinearLayout(getContext());
        ll_iso.setOrientation(LinearLayout.VERTICAL);
        ll_iso.setId(View.generateViewId());

        TextView tv_isoText = new TextView(getContext());
        tv_isoText.setText("ISO");
        tv_isoText.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_iso = new TextView(getActivity());
        tv_iso.setText(settingsArr[3]);
        tv_iso.setGravity(Gravity.CENTER);
        tv_iso.setPaddingRelative(padding, 0, padding, 0);
        tv_iso.setTextColor(colorStateList);
        tv_isoText.setTextColor(colorStateList);
        if (iso) {
            tv_isoText.setEnabled(true);
            tv_iso.setEnabled(true);
            ll_iso.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[3], Toast.LENGTH_SHORT).show();
                    triggerFragmListener.onShootingModeInteraction(3);
                }
            });
        } else {
            tv_isoText.setEnabled(false);
            tv_iso.setEnabled(false);
        }
        ll_iso.addView(tv_isoText);
        ll_iso.addView(tv_iso);
        root_linearLayout.addView(ll_iso);

        //WhiteBalance
        LinearLayout ll_linLayoutWb = new LinearLayout(getContext());
        ll_linLayoutWb.setOrientation(LinearLayout.VERTICAL);
        ll_linLayoutWb.setId(View.generateViewId());


        iv_Wb = new ImageView(getActivity());
        //todo: make setter for prefs
        iv_Wb.setImageResource(R.drawable.icn_wb_setting_16);
        iv_Wb.setPaddingRelative(padding, 0, padding, 0);
        if (wb) {
            iv_Wb.setEnabled(true);
            ll_linLayoutWb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), settingsArr[4], Toast.LENGTH_SHORT).show();
                    triggerFragmListener.onShootingModeInteraction(4);
                }
            });
        } else {
            iv_Wb.setEnabled(false);
        }
        ll_linLayoutWb.addView(iv_Wb);
        root_linearLayout.addView(ll_linLayoutWb);
        return root_linearLayout;
    }

    public void SetOLYCam(OLYCamera camera) {
        this.camera = camera;

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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            triggerFragmListener = (OnTriggerFragmInteractionListener) context;
           /* mCallback = (OnShutterReleasePressed) context;
            mPressed = (OnDrivemodePressed) context;*/
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


