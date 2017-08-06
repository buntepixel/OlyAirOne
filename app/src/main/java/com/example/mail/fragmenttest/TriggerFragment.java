package com.example.mail.fragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by mail on 14/06/2017.
 */

public class TriggerFragment extends Fragment
        implements MainSettingsFragment.OnMainSettingsFragmInteractionListener {
    private static final String TAG = TriggerFragment.class.getSimpleName();

    MainSettingsFragment fMainSettings;
    String currExpApart1;
    String currExpApart2;
    private boolean time, aparture, exposureAdj, iso, wb;

    private final String[] settingsArr = new String[]{"4", "F5.6", "0.0", "ISO\n250", "WB\nAuto"};
    private OnMainSettingsFragmInteractionListener mListener;

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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trigger, container, false);
        view.setId(View.generateViewId());
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

    public void SetButtonsBool(boolean time,boolean aparture,boolean exposureAdj,boolean iso, boolean wb){
        this.time = time;
        this.aparture = aparture;
        this.exposureAdj = exposureAdj;
        this.iso = iso;
        this.wb = wb;
    }

    private void SetupButtons(LinearLayout linearLayout) {
        int padding = 45;
        //LinearLayout linearLayout = ll_main;
        int cTxtDis = ContextCompat.getColor(getContext(), R.color.ColorBarTextDisabled);
        int cTxtEn = ContextCompat.getColor(getContext(), R.color.ColorBarTextEnabled);

        Log.d(TAG, time+" "+aparture+" "+exposureAdj+" "+iso+" "+wb);
        // exposure Time
        TextView tv_expTime = new TextView(getActivity());
        tv_expTime.setText(settingsArr[0]);
        tv_expTime.setPaddingRelative(padding, 0, padding, 0);
        if (time) {
            //Log.d(TAG, "huuuuu");
            tv_expTime.setTextColor(cTxtEn);
            tv_expTime.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[0], Toast.LENGTH_SHORT).show();
                    mListener.onMainSettinsInteraction(0);
                }
            });
        } else
            tv_expTime.setTextColor(cTxtDis);
        linearLayout.addView(tv_expTime);

        //Fstop
        TextView tv_fStop = new TextView(getActivity());
        tv_fStop.setText(settingsArr[1]);
        tv_fStop.setPaddingRelative(padding, 0, padding, 0);
        if (aparture) {
            tv_fStop.setTextColor(cTxtEn);
            tv_fStop.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), settingsArr[1], Toast.LENGTH_SHORT).show();
                    mListener.onMainSettinsInteraction(1);
                }
            });
        } else
            // Log.d(TAG, "fuck");
            tv_fStop.setTextColor(cTxtDis);
        linearLayout.addView(tv_fStop);

        //ExposureCorr
        TextView tv_expCorr = new TextView(getActivity());
        tv_expCorr.setText(settingsArr[2]);
        tv_expCorr.setPaddingRelative(padding, 0, padding, 0);
        if (exposureAdj) {
            tv_expCorr.setTextColor(cTxtEn);
            tv_expCorr.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[2], Toast.LENGTH_SHORT).show();
                    mListener.onMainSettinsInteraction(2);
                }
            });
        } else
            tv_expCorr.setTextColor(cTxtDis);

        linearLayout.addView(tv_expCorr);

        //iso
        TextView tv_iso = new TextView(getActivity());
        tv_iso.setText(settingsArr[3]);
        tv_iso.setGravity(Gravity.CENTER);
        tv_iso.setPaddingRelative(padding, 0, padding, 0);
        if (iso) {
            tv_iso.setTextColor(cTxtEn);
            tv_iso.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Toast.makeText(getActivity(), settingsArr[3], Toast.LENGTH_SHORT).show();
                    mListener.onMainSettinsInteraction(3);
                }
            });
        } else
            tv_iso.setTextColor(cTxtDis);

        linearLayout.addView(tv_iso);

        //WhiteBalance
        TextView tv_wb = new TextView(getActivity());
        tv_wb.setText(settingsArr[4]);
        tv_wb.setGravity(Gravity.CENTER);
        tv_wb.setPaddingRelative(padding, 0, padding, 0);
        if (wb) {
            tv_wb.setTextColor(cTxtEn);
            tv_wb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), settingsArr[4], Toast.LENGTH_SHORT).show();
                    mListener.onMainSettinsInteraction(4);
                }
            });
        } else
            tv_wb.setTextColor(cTxtDis);

        linearLayout.addView(tv_wb);

    }

   public void SetMainSettingsButtons(int mode) {
        Log.d(TAG, "Mode: " + mode);
        switch (mode) {
            case 0://iAuto
                fMainSettings.SetButtonsBool(false, false, false, false, false);
                Log.d(TAG, "Iauto");
                break;
            case 1://Programm
                fMainSettings.SetButtonsBool(false, false, true, true, true);
                Log.d(TAG, "Programm");
                break;
            case 2://Aparture
                fMainSettings.SetButtonsBool(false, true, true, true, true);
                Log.d(TAG, "Aparture");
                break;
            case 3://Speed
                fMainSettings.SetButtonsBool(true, false, true, true, true);
                Log.d(TAG, "Speed");
                break;
            case 4://Manual
                fMainSettings.SetButtonsBool(true, true, false, true, true);
                Log.d(TAG, "Manual");
                break;
            case 5:
                break;
            case 6://Movie
                fMainSettings.SetButtonsBool(false, false, true, false, true);
                Log.d(TAG, "Movie");
                break;
            case 7:
                break;
            case 8:
                break;
        }
       /* FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fl_FragCont_MainSettings, fMainSettings, "Expo");
        fragmentTransaction.commit();*/
        Fragment frg = getFragmentManager().findFragmentByTag("Main");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg).attach(frg).commit();
    }


    @Override
    public void onMainSettinsInteraction(int settingsType) {
        // Toast.makeText(getParent(), settingsType, Toast.LENGTH_SHORT).show();
        //Log.d(TAG, "bla " + settingsType);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment myFrag;
        String myTag;
        //Log.d(TAG,"visFrag"+ fm.getFragments().toString());
        switch (settingsType) {
            case 0:
                myTag = "Expo";
                if (currExpApart1 == myTag) {
                    Log.d(TAG, "SameFrag");
                    ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                    ft.remove(getFragmentManager().findFragmentByTag(myTag));
                    currExpApart1 = "";
                } else {
                    if ((myFrag = getFragmentManager().findFragmentByTag(myTag)) != null) {
                        Log.d(TAG, "Exists");
                        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                        ft.replace(R.id.fl_FragCont_ExpApart1, myFrag, myTag);
                        currExpApart1 = myTag;
                    } else {
                        Log.d(TAG, "New");
                        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                        ft.replace(R.id.fl_FragCont_ExpApart1, new ExposureFragment(), myTag);
                        currExpApart1 = myTag;
                    }
                }
                ft.addToBackStack("back");
                ft.commit();
                break;
            case 1:
                myTag = "Apart";

                if (currExpApart1 == myTag) {
                    Log.d(TAG, "SameFrag");
                    ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                    ft.remove(getFragmentManager().findFragmentByTag(myTag));
                    currExpApart1 = "";
                } else {
                    if ((myFrag = getFragmentManager().findFragmentByTag(myTag)) != null) {
                        Log.d(TAG, "Exists");
                        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                        ft.replace(R.id.fl_FragCont_ExpApart1, myFrag, myTag);
                        currExpApart1 = myTag;
                    } else {
                        Log.d(TAG, "New");
                        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                        ft.replace(R.id.fl_FragCont_ExpApart1, new ApartureFragment(), myTag);
                        currExpApart1 = myTag;
                    }
                }
                ft.addToBackStack("back");
                ft.commit();
                Log.d(TAG, "visFrag " + fm.getFragments().size());
                break;
            case 2:
                break;
            case 3:
                myTag = "Iso";
                if (currExpApart1 == myTag) {
                    Log.d(TAG, "SameFrag");
                    ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                    ft.remove(getFragmentManager().findFragmentByTag(myTag));
                    currExpApart1 = "";
                } else {
                    if ((myFrag = getFragmentManager().findFragmentByTag(myTag)) != null) {
                        Log.d(TAG, "Exists");
                        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                        ft.replace(R.id.fl_FragCont_ExpApart1, myFrag, myTag);
                        currExpApart1 = myTag;
                    } else {
                        Log.d(TAG, "New");
                        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                        ft.replace(R.id.fl_FragCont_ExpApart1, new IsoFragment(), myTag);
                        currExpApart1 = myTag;
                    }
                }
                ft.addToBackStack("back");
                ft.commit();
                break;
            case 4:
                myTag = "Wb";
                if (currExpApart1 == myTag) {
                    Log.d(TAG, "SameFrag");
                    ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                    ft.remove(getFragmentManager().findFragmentByTag(myTag));
                    currExpApart1 = "";
                } else {
                    if ((myFrag = getFragmentManager().findFragmentByTag(myTag)) != null) {
                        Log.d(TAG, "Exists");
                        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                        ft.replace(R.id.fl_FragCont_ExpApart1, myFrag, myTag);
                        currExpApart1 = myTag;
                    } else {
                        Log.d(TAG, "New");
                        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                        ft.replace(R.id.fl_FragCont_ExpApart1, new WbFragment(), myTag);
                        currExpApart1 = myTag;
                    }
                }
                ft.addToBackStack("back");
                ft.commit();
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
           /* mCallback = (OnShutterReleasePressed) context;
            mPressed = (OnDrivemodePressed) context;*/
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnShutterReleasePressed");
        }
    }
}


