package com.example.mail.fragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by mail on 14/06/2017.
 */

public class IsoFragment extends Fragment {
    private static final String TAG = ExposureFragment.class.getSimpleName();
    OnShutterReleasePressed mCallback;
    OnDrivemodePressed mPressed;

    public interface OnShutterReleasePressed {
        void onShutterReleasedPressed(int pos);

    }
    public  interface OnDrivemodePressed{
        void onDrivemodePressed();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnShutterReleasePressed) context;
            mPressed = (OnDrivemodePressed) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnShutterReleasePressed");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trigger, container, false);
        //shutter release pressed
        ImageButton ib_shutterRelease = (ImageButton) view.findViewById(R.id.ib_shutterrelease);
        ib_shutterRelease.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                //Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
                mCallback.onShutterReleasedPressed(10);

            }
        });

        ImageButton ib_driveMode = (ImageButton) view.findViewById(R.id.ib_drivemode);
        ib_driveMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPressed.onDrivemodePressed();
            }
        });
        return view;
    }


}
