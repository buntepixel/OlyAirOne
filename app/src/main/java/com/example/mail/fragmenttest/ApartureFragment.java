package com.example.mail.fragmenttest;

/**
 * Created by mail on 13/10/2016.
 */

public class ApartureFragment extends MasterSlidebarFragment {
    private static final String TAG = ApartureFragment.class.getSimpleName();

    private static String[] myString = {"app", "3.2", "3.5", "4", "4.5", "5", "6", "7", "8.0"};


    public ApartureFragment() {
    }

    public void SetContentString(String[] myString) {
        this.setBarStringArr(myString);
    }



}


