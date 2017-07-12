package com.example.mail.fragmenttest;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainSettingsFragment extends Fragment {
    private static final String TAG = MainSettingsFragment.class.getSimpleName();
    ;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //bools for SetupButtons
    private boolean time= true, aparture = true, exposureAdj, iso, wb;

    private final String[] settingsArr = new String[]{"4", "F5.6", "0.0", "ISO\n250", "WB\nAuto"};

    private OnFragmentInteractionListener mListener;

    public MainSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainSettingsFragment newInstance(String param1, String param2) {
        MainSettingsFragment fragment = new MainSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fragment_mainsettings, container, false);
        CreateSettings(settingsArr, rootView);
        return rootView;
    }

/*    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }

    private LinearLayout CreateSettings(String[] inputStringArr, View rootView) {
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.ll_mainSettings);
        linearLayout.setBackgroundColor(Color.YELLOW);
        SetupButtons(linearLayout);
        return linearLayout;
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


        // exposure Time
        TextView tv_expTime = new TextView(getActivity());
        tv_expTime.setText(settingsArr[0]);
        tv_expTime.setPaddingRelative(padding, 0, padding, 0);
        if (time) {
            Log.d(TAG, "huuuuu");
            tv_expTime.setTextColor(cTxtEn);
            tv_expTime.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getActivity(), settingsArr[0], Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), settingsArr[1], Toast.LENGTH_SHORT).show();
                }
            });
        } else
            Log.d(TAG, "fuck");
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
                    Toast.makeText(getActivity(), settingsArr[2], Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), settingsArr[3], Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), settingsArr[4], Toast.LENGTH_SHORT).show();
                }
            });
        } else
            tv_wb.setTextColor(cTxtDis);

        linearLayout.addView(tv_wb);

    }
    /*    private LinearLayout Create2LineTextView(String prefix, String text) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }*/

}
