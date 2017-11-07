package com.example.mail.fragmenttest;

/**
 * Created by mail on 13/10/2016.
 */

public class ShutterFragment extends MasterSlidebarFragment {
    private static final String TAG = ShutterFragment.class.getSimpleName();
    private static final String[] myString = {"Exp", "3.2", "3.5", "4", "4.5", "5", "A", "P", "Bulb"};

    public ShutterFragment() {
        this.setBarStringArr(myString);
    }

}
