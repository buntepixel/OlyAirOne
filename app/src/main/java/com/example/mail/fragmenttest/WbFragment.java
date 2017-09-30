package com.example.mail.fragmenttest;

/**
 * Created by mail on 13/10/2016.
 */

public class WbFragment extends parentSlidebarFragment {
    private static final String TAG = WbFragment.class.getSimpleName();
    private static final String[] myString = {"Auto", "Fine", "Shade", "Cloud", "Lamp", "Fluoresc", "Water", "Custom"};

    public WbFragment() {
        this.setBarStringArr(myString);
    }
}
