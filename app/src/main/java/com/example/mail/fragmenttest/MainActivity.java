package com.example.mail.fragmenttest;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
        implements TriggerFragment.OnShutterReleasePressed, MainSettingsFragment.OnFragmentInteractionListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    ExposureFragment fExposure;
    ApartureFragment fAparture;
    TriggerFragment fTrigger;
    MainSettingsFragment fMainSettings;
    FragmentManager fm = getSupportFragmentManager();
    int[] modeArr = new int[]{R.drawable.ic_aparturemode, R.drawable.ic_shuttermode, R.drawable.ic_manualmode, R.drawable.ic_videomode};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton ib_RecordMode = (ImageButton) findViewById(R.id.ib_RecordMode);
        ib_RecordMode.setOnClickListener(new View.OnClickListener() {
            int modeCounter = 0;

            @Override
            public void onClick(View v) {

                modeCounter++;
                Toast.makeText(MainActivity.this, "Hello" + modeCounter, Toast.LENGTH_SHORT).show();
                ib_RecordMode.setImageResource(modeArr[modeCounter % (modeArr.length - 1)]);

            }
        });

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Log.d(TAG, "start");
        //check for Trigger container

        fTrigger = new TriggerFragment();
        fExposure = new ExposureFragment();
        fAparture = new ApartureFragment();
        fMainSettings = new MainSettingsFragment();
        fragmentTransaction.add(R.id.fl_FragCont_ExpApart1, fExposure, "Expo");
        //fragmentTransaction.add(R.id.fl_FragCont_ExpApart2, fAparture, "Apart");
        fragmentTransaction.add(R.id.fl_FragCont_Trigger, fTrigger);
        fragmentTransaction.add(R.id.fl_FragCont_MainSettings, fMainSettings);

        fragmentTransaction.commit();
    }


    @Override
    public void onShutterReleasedPressed(int pos) {

        Toast.makeText(this, "Click!" + pos, Toast.LENGTH_SHORT).show();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
        ft.replace(R.id.fl_FragCont_ExpApart1, fExposure);
        ft.addToBackStack(fExposure.toString());
        ft.commit();
    }

    @Override
    public void onDrivemodePressed() {
        Toast.makeText(this, "drivemode!", Toast.LENGTH_SHORT).show();


        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
        ft.replace(R.id.fl_FragCont_ExpApart1, fAparture, "Apart");
        ft.addToBackStack(fAparture.toString());
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

