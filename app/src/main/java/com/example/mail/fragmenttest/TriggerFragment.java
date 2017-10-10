package com.example.mail.fragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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


    private TextView tv_expTime;
    private TextView tv_fStop;
    private TextView tv_iso;
    private TextView tv_wb;
    private TextView tv_expOffset;
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
            put("<AE/AE_CENTER>", R.drawable.icn_metering_center);
            put("<AE/AE_AE_ESP>", R.drawable.icn_metering_esp);
            put("<AE/AE_PINPOINT>", R.drawable.icn_metereing_pinpoint);
        }
    };


    public interface OnTriggerFragmInteractionListener {
        void onShootingModeInteraction(int settingsType);
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

        ImageButton ib_driveMode = (ImageButton) view.findViewById(R.id.ib_drivemode);
        ib_driveMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drivemodeImageViewDidTap();
                //updateDrivemodeImageView();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
        int cTxtDis = ContextCompat.getColor(getContext(), R.color.ColorBarTextDisabled);
        int cTxtEn = ContextCompat.getColor(getContext(), R.color.ColorBarTextEnabled);
        //ExposureCorr
        //Log.d(TAG, "notDead C");
        LinearLayout center_linearLayout = CreateExposureCorr(cTxtEn, cTxtDis, padding);
        LinearLayout left_LinearLayout = CreateExpTFstop(cTxtEn, cTxtDis, padding, center_linearLayout);
        LinearLayout right_LinearLayout = CreateIsoWBBtn(cTxtEn, cTxtDis, padding, center_linearLayout);

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

    public void SetDriveMode(String driveMode) {

    }

    private void updateDrivemodeImageView() {
        drivemodeImageView.setEnabled(camera.canSetCameraProperty(CAMERA_PROPERTY_DRIVE_MODE));

        String drivemode;
        try {
            drivemode = camera.getCameraPropertyValue(CAMERA_PROPERTY_DRIVE_MODE);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return;
        }

        if (drivemode == null) {
            return;
        }

        if (drivemodeIconList.containsKey(drivemode)) {
            int resId = drivemodeIconList.get(drivemode);
            drivemodeImageView.setImageResource(resId);
        } else {
            drivemodeImageView.setImageDrawable(null);
        }
    }

    private void drivemodeImageViewDidTap() {
        final View view = drivemodeImageView;
        final String propertyName = CAMERA_PROPERTY_DRIVE_MODE;

        final List<String> valueList;
        try {
            valueList = camera.getCameraPropertyValueList(propertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return;
        }
        if (valueList == null || valueList.size() == 0) return;

        String value;
        try {
            value = camera.getCameraPropertyValue(propertyName);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
            return;
        }
        if (value == null) return;
        view.setSelected(true);

        try {
            if (valueList.get(0) == value)
                camera.setCameraPropertyValue(propertyName, valueList.get(1));
            else
                camera.setCameraPropertyValue(propertyName, valueList.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateDrivemodeImageView();
            }
        });

    }

    private  void updateMeteringImageView(){

    }
    private void meteringImageViewDidTap(){

    }


    private LinearLayout CreateExpTFstop(int colEnable, int colDisable, int padding, LinearLayout alignLayout) {
        LinearLayout root_linearLayout = new LinearLayout(getContext());
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        root_linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        relParams.addRule(RelativeLayout.START_OF, alignLayout.getId());
        relParams.addRule(RelativeLayout.CENTER_VERTICAL);
        root_linearLayout.setLayoutParams(relParams);
        //Log.d(TAG, time + " " + aparture + " " + exposureAdj + " " + iso + " " + wb);

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
        if (time) {
            tv_expTime.setTextColor(colEnable);
            tv_expTimeText.setTextColor(colEnable);
            ll_expTime.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[0], Toast.LENGTH_SHORT).show();
                    triggerFragmListener.onShootingModeInteraction(0);
                }
            });
        } else {
            tv_expTime.setTextColor(colDisable);
            tv_expTimeText.setTextColor(colDisable);
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
        if (aparture) {
            tv_fStop.setTextColor(colEnable);
            tv_fStopText.setTextColor(colEnable);
            ll_fStop.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), settingsArr[1], Toast.LENGTH_SHORT).show();
                    triggerFragmListener.onShootingModeInteraction(1);
                }
            });
        } else {
            tv_fStop.setTextColor(colDisable);
            tv_fStopText.setTextColor(colDisable);
        }
        ll_fStop.addView(tv_fStopText);
        ll_fStop.addView(tv_fStop);
        root_linearLayout.addView(ll_fStop);
        return root_linearLayout;
    }

    private LinearLayout CreateExposureCorr(int colEnable, int colDisable, int padding) {
        LinearLayout rootLinearLayout = new LinearLayout(getContext());
        rootLinearLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rootLinearLayout.setMinimumWidth(130);
        rootLinearLayout.setOrientation(LinearLayout.VERTICAL);
        rootLinearLayout.setLayoutParams(relParams);

        String expOffsetTxt = "+ 0.3 ";

        tv_expOffset = new TextView(getActivity());
        tv_expOffset.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_expOffset.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv_expOffset.setText(expOffsetTxt);
        if (exposureAdj) {
            tv_expOffset.setTextColor(colEnable);
            tv_expOffset.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[2], Toast.LENGTH_SHORT).show();
                    triggerFragmListener.onShootingModeInteraction(2);
                }
            });
        } else
            tv_expOffset.setTextColor(colDisable);

        rootLinearLayout.addView(tv_expOffset);
        //Expcorr Layout only if manual Mode
        if (takeMode == 4) {
            LinearLayout containerLLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams linParams = (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            containerLLayout.setLayoutParams(linParams);
            containerLLayout.setGravity(Gravity.CENTER_VERTICAL);
            containerLLayout.setOrientation(LinearLayout.HORIZONTAL);
            containerLLayout.setWeightSum(8);
            rootLinearLayout.addView(containerLLayout);

            TextView leftText = new TextView(getContext());
            leftText.setGravity(Gravity.CENTER_HORIZONTAL);
            leftText.setMinWidth(10);

            leftText.setText("-  ");
            leftText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
            leftText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            containerLLayout.addView(leftText);


            ExposureCorrection expCorr = new ExposureCorrection(getContext());
            linParams = (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            expCorr.setLayoutParams(linParams);

            containerLLayout.addView(expCorr);

            TextView rightText = new TextView(getContext());
            rightText.setGravity(Gravity.CENTER_VERTICAL);
            rightText.setMinWidth(10);
            rightText.setText(" +");
            rightText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
            rightText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            containerLLayout.addView(rightText);
        }
        return rootLinearLayout;
    }

    private LinearLayout CreateIsoWBBtn(int colEnable, int colDisable, int padding, LinearLayout alignLayout) {
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
        if (iso) {
            tv_iso.setTextColor(colEnable);
            tv_isoText.setTextColor(colEnable);
            ll_iso.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[3], Toast.LENGTH_SHORT).show();
                    triggerFragmListener.onShootingModeInteraction(3);
                }
            });
        } else {
            tv_iso.setTextColor(colDisable);
            tv_isoText.setTextColor(colDisable);
        }
        ll_iso.addView(tv_isoText);
        ll_iso.addView(tv_iso);
        root_linearLayout.addView(ll_iso);

        //WhiteBalance
        LinearLayout ll_linLayoutWb = new LinearLayout(getContext());
        ll_linLayoutWb.setOrientation(LinearLayout.VERTICAL);
        ll_linLayoutWb.setId(View.generateViewId());

        TextView tv_wbText = new TextView(getContext());
        tv_wbText.setText("WB");
        tv_wbText.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_wb = new TextView(getActivity());
        tv_wb.setText(settingsArr[4]);
        tv_wb.setGravity(Gravity.CENTER);
        tv_wb.setPaddingRelative(padding, 0, padding, 0);
        if (wb) {
            tv_wb.setTextColor(colEnable);
            tv_wbText.setTextColor(colEnable);
            ll_linLayoutWb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), settingsArr[4], Toast.LENGTH_SHORT).show();
                    triggerFragmListener.onShootingModeInteraction(4);
                }
            });
        } else {
            tv_wb.setTextColor(colDisable);
            tv_wbText.setTextColor(colDisable);
        }
        ll_linLayoutWb.addView(tv_wbText);
        ll_linLayoutWb.addView(tv_wb);
        root_linearLayout.addView(ll_linLayoutWb);
        return root_linearLayout;
    }

    public void SetOLYCam(OLYCamera camera) {
        this.camera = camera;
    }

    public void SetSliderResult(String value, String property) {
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
                tv_wb.setText(camera.getCameraPropertyValueTitle(value));
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


