package com.example.mail.fragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by mail on 14/06/2017.
 */

public class TriggerFragment extends Fragment {
    private static final String TAG = TriggerFragment.class.getSimpleName();

    String currExpApart1;
    String currExpApart2;
    private boolean time, aparture, exposureAdj, iso, wb;
    private final String[] settingsArr = new String[]{"4", "F5.6", "0.0", "ISO\n250", "WB\nAuto"};
    private int driveMode;
    private OnTriggerFragmInteractionListener mListener;


    //    OnShutterReleasePressed mCallback;
//    OnDrivemodePressed mPressed;
//
//    public interface OnShutterReleasePressed {
//        void onShutterReleasedPressed(int pos);
//
//    }
//    public  interface OnDrivemodePressed{
//        void onDrivemodePressed();
//    }

//    public TriggerFragment(){
//        this.mListener = null;
//    }
//    public void setTriggerFragmListener(OnTriggerFragmInteractionListener listener){
//        this.mListener = listener;
//    }

    public interface OnTriggerFragmInteractionListener {
        void onTriggerFragmInteraction(int settingsType);
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
        Log.d(TAG, "notdead A");
        View view = inflater.inflate(R.layout.fragment_trigger, container, false);
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

//        ImageButton ib_driveMode = (ImageButton) view.findViewById(R.id.ib_drivemode);
//        ib_driveMode.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                mPressed.onDrivemodePressed();
//            }
//        });
        return view;
    }

    private RelativeLayout CreateSettings(String[] inputStringArr, View rootView) {
        RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.rl_settings);
        Log.d(TAG, "notDead B");

        //linearLayout.setBackgroundColor(Color.YELLOW);
        SetupButtons(relativeLayout);
        return relativeLayout;
    }

    public void SetButtonsBool(boolean time, boolean aparture, boolean exposureAdj, boolean iso, boolean wb) {
        this.time = time;
        this.aparture = aparture;
        this.exposureAdj = exposureAdj;
        this.iso = iso;
        this.wb = wb;
    }

    public void SetDriveMode(int driveMode) {
        this.driveMode = driveMode;
    }


    private void SetupButtons(RelativeLayout relativeLayout) {
        //LinearLayout linearLayout = ll_main;
        int padding = 40;
        int cTxtDis = ContextCompat.getColor(getContext(), R.color.ColorBarTextDisabled);
        int cTxtEn = ContextCompat.getColor(getContext(), R.color.ColorBarTextEnabled);
        //ExposureCorr
        Log.d(TAG, "notDead C");
        LinearLayout center_linearLayout = CreateExposureCorr(cTxtEn, cTxtDis, padding);
        LinearLayout left_LinearLayout = CreateExpTFstop(cTxtEn, cTxtDis, padding, center_linearLayout);
        LinearLayout right_LinearLayout = CreateIsoWBBtn(cTxtEn, cTxtDis, padding, center_linearLayout);

        relativeLayout.addView(left_LinearLayout);
        relativeLayout.addView(center_linearLayout);
        relativeLayout.addView(right_LinearLayout);
    }

    private LinearLayout CreateExpTFstop(int colEnable, int colDisable, int padding, LinearLayout alignLayout) {
        LinearLayout root_linearLayout = new LinearLayout(getContext());
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        root_linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        relParams.addRule(RelativeLayout.START_OF, alignLayout.getId());
        relParams.addRule(RelativeLayout.CENTER_VERTICAL);
        //root_linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        root_linearLayout.setLayoutParams(relParams);

        Log.d(TAG, time + " " + aparture + " " + exposureAdj + " " + iso + " " + wb);

        // exposure Time
        TextView tv_expTime = new TextView(getActivity());
        tv_expTime.setText(settingsArr[0]);
        tv_expTime.setPaddingRelative(padding, 0, padding, 0);
        if (time) {
            tv_expTime.setTextColor(colEnable);
            tv_expTime.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[0], Toast.LENGTH_SHORT).show();
                    mListener.onTriggerFragmInteraction(0);
                }
            });
        } else
            tv_expTime.setTextColor(colDisable);
        root_linearLayout.addView(tv_expTime);

        //Fstop
        TextView tv_fStop = new TextView(getActivity());
        tv_fStop.setText(settingsArr[1]);
        tv_fStop.setPaddingRelative(padding, 0, padding, 0);
        if (aparture) {
            tv_fStop.setTextColor(colEnable);
            tv_fStop.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), settingsArr[1], Toast.LENGTH_SHORT).show();
                    mListener.onTriggerFragmInteraction(1);
                }
            });
        } else {
            tv_fStop.setTextColor(colDisable);
        }
        root_linearLayout.addView(tv_fStop);
        return root_linearLayout;
    }

    private LinearLayout CreateExposureCorr(int colEnable, int colDisable, int padding) {
        LinearLayout rootLinearLayout = new LinearLayout(getContext());
        rootLinearLayout.setId(View.generateViewId());
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //relParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rootLinearLayout.setMinimumWidth(130);
        rootLinearLayout.setOrientation(LinearLayout.VERTICAL);
        rootLinearLayout.setLayoutParams(relParams);

        String expOffsetTxt = "+ 0.3 ";

        TextView tv_expOffset = new TextView(getActivity());
        tv_expOffset.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_expOffset.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv_expOffset.setText(expOffsetTxt);
        if (exposureAdj) {
            tv_expOffset.setTextColor(colEnable);
            tv_expOffset.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[2], Toast.LENGTH_SHORT).show();
                    mListener.onTriggerFragmInteraction(2);
                }
            });
        } else
            tv_expOffset.setTextColor(colDisable);

        rootLinearLayout.addView(tv_expOffset);
        //Expcorr Layout
        if (driveMode == 4) {
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
        LinearLayout root_linerarLayout = new LinearLayout(getContext());
        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //relParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        relParams.addRule(RelativeLayout.END_OF, alignLayout.getId());
        root_linerarLayout.setOrientation(LinearLayout.HORIZONTAL);
        root_linerarLayout.setLayoutParams(relParams);


        //iso
        TextView tv_iso = new TextView(getActivity());
        tv_iso.setText(settingsArr[3]);
        tv_iso.setGravity(Gravity.CENTER);
        tv_iso.setPaddingRelative(padding, 0, padding, 0);
        if (iso) {
            tv_iso.setTextColor(colEnable);
            tv_iso.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[3], Toast.LENGTH_SHORT).show();
                    mListener.onTriggerFragmInteraction(3);
                }
            });
        } else
            tv_iso.setTextColor(colDisable);
        root_linerarLayout.addView(tv_iso);

        //WhiteBalance
        TextView tv_wb = new TextView(getActivity());
        tv_wb.setText(settingsArr[4]);
        tv_wb.setGravity(Gravity.CENTER);
        tv_wb.setPaddingRelative(padding, 0, padding, 0);
        if (wb) {
            tv_wb.setTextColor(colEnable);
            tv_wb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), settingsArr[4], Toast.LENGTH_SHORT).show();
                    mListener.onTriggerFragmInteraction(4);
                }
            });
        } else
            tv_wb.setTextColor(colDisable);
        root_linerarLayout.addView(tv_wb);
        return root_linerarLayout;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnTriggerFragmInteractionListener) context;
           /* mCallback = (OnShutterReleasePressed) context;
            mPressed = (OnDrivemodePressed) context;*/
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTriggerFragmInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}


