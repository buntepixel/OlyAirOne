package com.example.mail.fragmenttest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //check if fragment container exists
        Log.d(TAG, "start");
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_exposurecomp);
        if (linearLayout != null) {
            ExposureCorrection myExposure = new ExposureCorrection(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
            myExposure.setLayoutParams(params);
            //Todo: Check for possibility to do compensation visualisation

            linearLayout.addView(myExposure);

        }
        if (findViewById(R.id.fl_fragment_container) != null) {
            //check if we return from a previous state if yes do nothing if no
            //create new fragment.
            if (savedInstanceState != null) {
                return;
            }
            Log.d(TAG, "start01");
            ExposureFragment exposureFragment = new ExposureFragment();
            exposureFragment.setArguments(getIntent().getExtras());
            ApartureFragment apartureFragment = new ApartureFragment();
            apartureFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_fragment_container, exposureFragment)

                    .commit();
        }


    }
}
