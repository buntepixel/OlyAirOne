package com.example.mail.fragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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

    TriggerFragment fTrigger;
    MainSettingsFragment fMainSettings;


    Parcelable stateApa;
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
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }
        fTrigger = new TriggerFragment();

        fMainSettings = new MainSettingsFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fl_FragCont_Trigger, fTrigger, "Trigger");
        fragmentTransaction.add(R.id.fl_FragCont_MainSettings, fMainSettings, "Main");
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
        //Log.d(TAG, "bla " + settingsType);
        FragmentManager fm = getSupportFragmentManager();
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
                    ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
                    currExpApart1 = "";
                } else {
                    if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
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
                    ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
                    currExpApart1 = "";
                } else {
                    if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
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
                Log.d(TAG,"visFrag "+ fm.getFragments().size());
                break;
            case 2:
                break;
            case 3:
                myTag = "Iso";
                if (currExpApart1 == myTag) {
                    Log.d(TAG, "SameFrag");
                    ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                    ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
                    currExpApart1 = "";
                } else {
                    if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
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
                    ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
                    currExpApart1 = "";
                } else {
                    if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
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
}



