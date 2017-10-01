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
        implements TriggerFragment.OnTriggerFragmInteractionListener {
    private static final String TAG = MainActivity.class.getSimpleName();


    MainSettingsFragment fMainSettings;


    Parcelable stateApa;
    int[] modeArr = new int[]{R.drawable.ic_iautomode, R.drawable.ic_programmmode, R.drawable.ic_aparturemode, R.drawable.ic_shuttermode, R.drawable.ic_manualmode, R.drawable.ic_artmode, R.drawable.ic_videomode};
    int currDriveMode = 0;
    String currExpApart1;
    String currExpApart2;
    TriggerFragment fTrigger;




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
        //add Trigger Fragment
        fTrigger = new TriggerFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fl_FragCont_Trigger, fTrigger, "Trigger");
        fragmentTransaction.commit();
        //recordMode
        final ImageButton ib_RecordMode = (ImageButton) findViewById(R.id.ib_RecordMode);
        ib_RecordMode.setOnClickListener(new View.OnClickListener() {
            int counter = 0;

            @Override
            public void onClick(View v) {
                counter++;
                int counterMod = counter % modeArr.length;
                ib_RecordMode.setImageResource(modeArr[counterMod]);
                currDriveMode = counterMod;
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
        TriggerFragment fTrigger = (TriggerFragment) getSupportFragmentManager().findFragmentByTag("Trigger");

        if (fTrigger != null) {
            switch (mode) {
                case 0://iAuto
                    fTrigger.SetButtonsBool(false, false, false, false, false);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Iauto");
                    break;
                case 1://Programm
                    fTrigger.SetButtonsBool(false, false, true, true, true);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Programm");
                    break;
                case 2://Aparture
                    fTrigger.SetButtonsBool(false, true, true, true, true);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Aparture");
                    break;
                case 3://Speed
                    fTrigger.SetButtonsBool(true, false, true, true, true);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Speed");
                    break;
                case 4://Manual
                    fTrigger.SetButtonsBool(true, true, false, true, true);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Manual");
                    break;
                case 5:
                    break;
                case 6://Movie
                    fTrigger.SetButtonsBool(false, false, true, false, true);
                    fTrigger.SetDriveMode(mode);
                    Log.d(TAG, "Movie");
                    break;
                case 7:
                    break;
                case 8:
                    break;
            }
        } else {
            Log.w(TAG, "couldn't find Fragment with tag: Trigger");
        }

        Fragment frg = getSupportFragmentManager().findFragmentByTag("Trigger");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg).attach(frg).commit();

        //remove fragments that are still on


        ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);

        if (currExpApart1 != null && currExpApart1 != "") {
            ft.remove(getSupportFragmentManager().findFragmentByTag(currExpApart1));
            currExpApart1 = "";
        }
        if (currExpApart2 != null && currExpApart2 != "") {
            ft.remove(getSupportFragmentManager().findFragmentByTag(currExpApart2));
            currExpApart2 = "";
        }
        ft.commit();
    }


    @Override
    public void onTriggerFragmInteraction(int settingsType) {
        // Toast.makeText(getParent(), settingsType, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "settingsType: " + settingsType);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        //Log.d(TAG,"visFrag"+ fm.getFragments().toString());
        int fragLayout;
        Log.d(TAG, "currdriveMode: " + currDriveMode);
        if (currDriveMode == 4 && settingsType <= 1) {
            ExposurePressed(ft, R.id.fl_FragCont_ExpApart2, 2);
            AparturePressed(ft);
            //Log.d(TAG, "A_nr1: " + currExpApart1 + " nr2: " + currExpApart2);
            ft.commit();
            //Log.d(TAG, "B_nr1: " + currExpApart1 + " nr2: " + currExpApart2);
        } else {
            switch (settingsType) {
                case 0:
                    currExpApart1 = ExposurePressed(ft, R.id.fl_FragCont_ExpApart1, 1);
                    break;
                case 1:
                    AparturePressed(ft);
                    break;
                case 2:
                    break;
                case 3:
                    IsoPressed(ft);
                    break;
                case 4:
                    WbPressed(ft);
                    break;
            }
            //ft.addToBackStack("back");
            ft.commit();
            //Log.d(TAG, "C_nr1: " + currExpApart1 + " nr2: " + currExpApart2);
        }
    }

    private String ExposurePressed(FragmentTransaction ft, int FrameLayout, int FrameLayoutId) {
        Fragment myFrag;
        String currTag;
        if (FrameLayoutId == 1)
            currTag = currExpApart1;
        else
            currTag = currExpApart2;
        String myTag = "Expo";
        if (currTag == myTag) {
            //Log.d(TAG, "SameFrag");
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
            if (FrameLayoutId == 1) {
                currExpApart1 = "";
                //Log.d(TAG, "currExpoApart 1");
            } else {
                currExpApart2 = "";
                //Log.d(TAG, "currExpoApart 2:  " + currExpApart2);
            }

        } else {
            if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
                //Log.d(TAG, "Exists");
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                ft.replace(FrameLayout, myFrag, myTag);
                if (FrameLayoutId == 1)
                    currExpApart1 = myTag;
                else
                    currExpApart2 = myTag;
            } else {
                //Log.d(TAG, "New");
                ExposureFragment exposureFragment = new ExposureFragment();
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                exposureFragment.setSliderValueListener(new ApartureFragment.sliderValue() {
                    @Override
                    public void onSlideValueBar(String value) {
                        fTrigger.SetExpTimeValue(value);
                    }
                });
                ft.replace(FrameLayout, exposureFragment, myTag);
                if (FrameLayoutId == 1)
                    currExpApart1 = myTag;
                else
                    currExpApart2 = myTag;
            }
        }
        return myTag;
    }

    private void AparturePressed(FragmentTransaction ft) {
        String myTag;
        Fragment myFrag;
        myTag = "Apart";
        if (currExpApart1 == myTag) {
            ///Log.d(TAG, "SameFrag");
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
            currExpApart1 = "";
        } else {
            if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
                //Log.d(TAG, "Exists");
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                ft.replace(R.id.fl_FragCont_ExpApart1, myFrag, myTag);
                currExpApart1 = myTag;
            } else {
                //Log.d(TAG, "New");
                ApartureFragment apartureFragment = new ApartureFragment();
                apartureFragment.setSliderValueListener(new ApartureFragment.sliderValue() {
                    @Override
                    public void onSlideValueBar(String value) {
                        fTrigger.SetFstopValue(value);
                    }
                });
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                ft.replace(R.id.fl_FragCont_ExpApart1, apartureFragment, myTag);
                currExpApart1 = myTag;
            }
        }
    }

    private void IsoPressed(FragmentTransaction ft) {
        String myTag;
        Fragment myFrag;
        myTag = "Iso";
        if (currExpApart1 == myTag) {
            //Log.d(TAG, "SameFrag");
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
            currExpApart1 = "";
        } else {
            if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
                //Log.d(TAG, "Exists");
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                ft.replace(R.id.fl_FragCont_ExpApart1, myFrag, myTag);
                currExpApart1 = myTag;
            } else {
                //Log.d(TAG, "New");
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                IsoFragment isoFragment = new IsoFragment();
                isoFragment.setSliderValueListener(new parentSlidebarFragment.sliderValue() {
                    @Override
                    public void onSlideValueBar(String value) {
                        fTrigger.SetIsoValue(value);
                    }
                });
                ft.replace(R.id.fl_FragCont_ExpApart1, isoFragment, myTag);
                currExpApart1 = myTag;
            }
        }
    }

    private void WbPressed(FragmentTransaction ft) {
        String myTag;
        Fragment myFrag;
        myTag = "Wb";
        if (currExpApart1 == myTag) {
            //Log.d(TAG, "SameFrag");
            ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
            ft.remove(getSupportFragmentManager().findFragmentByTag(myTag));
            currExpApart1 = "";
        } else {
            if ((myFrag = getSupportFragmentManager().findFragmentByTag(myTag)) != null) {
                // Log.d(TAG, "Exists");
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                ft.replace(R.id.fl_FragCont_ExpApart1, myFrag, myTag);
                currExpApart1 = myTag;
            } else {
                //Log.d(TAG, "New");
                WbFragment whiteBalanceFragment = new WbFragment();
                ft.setCustomAnimations(R.anim.slidedown, R.anim.slideup);
                whiteBalanceFragment.setSliderValueListener(new parentSlidebarFragment.sliderValue() {
                    @Override
                    public void onSlideValueBar(String value) {
                        fTrigger.SetWBValue(value);
                    }
                });
                ft.replace(R.id.fl_FragCont_ExpApart1, whiteBalanceFragment, myTag);
                currExpApart1 = myTag;
            }
        }
    }
}



