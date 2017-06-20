package com.example.mail.fragmenttest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by mail on 14/06/2017.
 */

public class TriggerFragment extends Fragment {
    private static final String TAG = ExposureFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trigger, container, false);

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.ib_shutterrelease);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();


            }
        });
        return  view;
    }
}
