package com.example.mail.fragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends FragmentActivity
        implements MainSettingsFragment.OnMainSettingsFragmInteractionListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    ExposureFragment fExposure;
    ApartureFragment fAparture;
    TriggerFragment fTrigger;
    IsoFragment fIso;
    WbFragment fWb;
    MainSettingsFragment fMainSettings;

    int[] modeArr = new int[]{R.drawable.ic_iautomode, R.drawable.ic_programmmode, R.drawable.ic_aparturemode, R.drawable.ic_shuttermode, R.drawable.ic_manualmode, R.drawable.ic_artmode, R.drawable.ic_videomode};
    int currDriveMode = 0;
    String currExpApart1;
    String currExpApart2;


    private enum OLYRecordModes {
        IAUTO,
        P,
        A,
        S,
        M,
        ART,
        MOVIEP,
        MOVIEA,
        MOVIES,
        MOVIEM
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Log.d(TAG, "start");
        //check for Trigger container

        fTrigger = new TriggerFragment();
        fExposure = new ExposureFragment();
        fAparture = new ApartureFragment();
        fMainSettings = new MainSettingsFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//        fragmentTransaction.add(R.id.fl_FragCont_ExpApart1, fExposure, "Expo");
        fragmentTransaction.add(R.id.fl_FragCont_Trigger, fTrigger, "Trigger");
        fragmentTransaction.add(R.id.fl_FragCont_MainSettings, fMainSettings, "Main");
        //fragmentTransaction.add(R.id.fl_FragCont_ExpApart1, fAparture, "Apart");
        fragmentTransaction.commit();
        //recordMode
        final ImageButton ib_RecordMode = (ImageButton) findViewById(R.id.ib_RecordMode);
        ib_RecordMode.setOnClickListener(new View.OnClickListener() {
            int counter = 0;

            @Override
            public void onClick(View v) {
                counter++;
                ib_RecordMode.setImageResource(modeArr[counter % (modeArr.length)]);
                currDriveMode = counter;
                SetMainSettingsButtons(counter % (modeArr.length));
                //Log.d(TAG, "start"+ counter);

            }
        });
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View myView = super.onCreateView(parent, name, context, attrs);

        return myView;
    }


    void SetMainSettingsButtons(int mode) {
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
        Fragment frg = getSupportFragmentManager().findFragmentByTag("Main");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg).attach(frg).commit();
    }


    @Override
    public void onMainSettinsInteraction(int settingsType) {
        // Toast.makeText(getParent(), settingsType, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "bla " + settingsType);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment myFrag;
        switch (settingsType) {
            case 0:

                ft.setCustomAnimations(R.anim.slidedown ,R.anim.slideup);
                if ((myFrag = getSupportFragmentManager().findFragmentByTag("Expo")) != null){
                    Log.d(TAG,"foundFrag Expo  "+myFrag.getId());
                    ft.replace(R.id.fl_FragCont_ExpApart1, myFrag,"Expo");
                }
                else{
                    Log.d(TAG,"Not foundFrag Expo  "+fExposure.getId());
                    ft.replace(R.id.fl_FragCont_ExpApart1, fExposure, "Expo");
                }
                ft.addToBackStack("back");
                ft.commit();
                break;
            case 1:
            /*    if (currExpApart1 == "" && currDriveMode != 4) {
                    ft.add(R.id.fl_FragCont_ExpApart1, fAparture, "Apart");
                }
                else {
                    ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                    ft.replace(R.id.fl_FragCont_ExpApart1, fAparture, "Expo");
                }*/
                ft.setCustomAnimations(R.anim.slidedown ,R.anim.slideup);
                ft.replace(R.id.fl_FragCont_ExpApart1, fAparture, "Apart");
                ft.commit();

                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }
    //    @Override
//    public void onShutterReleasedPressed(int pos) {
//
//        Toast.makeText(this, "Click!" + pos, Toast.LENGTH_SHORT).show();
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
//        ft.replace(R.id.fl_FragCont_ExpApart1, fExposure);
//        ft.addToBackStack(fExposure.toString());
//        ft.commit();
//    }

//    @Override
//    public void onDrivemodePressed() {
//        Toast.makeText(this, "drivemode!", Toast.LENGTH_SHORT).show();
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
//        ft.replace(R.id.fl_FragCont_ExpApart1, fAparture, "Apart");
//        ft.addToBackStack(fAparture.toString());
//        ft.commit();
//    }
}



