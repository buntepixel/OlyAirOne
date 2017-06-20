package com.example.mail.fragmenttest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Log.d(TAG, "start");
        //check for Trigger container

        Fragment fTrigger = new TriggerFragment();
        Fragment fExposure = new ExposureFragment();
        Fragment fAparture = new ApartureFragment();


        fTrigger.setArguments(getIntent().getExtras());
        fAparture.setArguments(getIntent().getExtras());
        fExposure.setArguments(getIntent().getExtras());
        fragmentTransaction.add(R.id.fl_FragCont_ExpApart1, fExposure);
        fragmentTransaction.add(R.id.fl_FragCont_ExpApart2, fAparture);
        fragmentTransaction.add(R.id.fl_FragCont_Trigger, fTrigger);

        fragmentTransaction.commit();
        //check for Exposure container
       /* //check if fragment container exists
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.ll_settings);
        if (relativeLayout != null) {
            ExposureCorrection myExposure = new ExposureCorrection(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            myExposure.setLayoutParams(params);
            //Todo: Check for possibility to do compensation visualisation

            //relativeLayout.addView(myExposure);

        }
        if (findViewById(R.id.fl_FragCont_Trigger) != null) {
            //check if we return from a previous state if yes do nothing if no
            //create new fragment.
            if (savedInstanceState != null) {
                return;
            }
            Log.d(TAG, "start01");
            ExposureFragment exposureFragment = new ExposureFragment();
            exposureFragment.setArguments(getIntent().getExtras());
            *//*ApartureFragment apartureFragment = new ApartureFragment();
            apartureFragment.setArguments(getIntent().getExtras());*//*

            getSupportFragmentManager().beginTransaction().add(R.id.fl_FragCont_Trigger, exposureFragment).commit();
        }*/


    }
}
