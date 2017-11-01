package com.example.mail.fragmenttest;

/**
 * Created by mail on 14/06/2017.
 */

public class IsoFragment extends MasterSlidebarFragment {
    private static final String TAG = IsoFragment.class.getSimpleName();
    private  String[] myString = {"iso", "3.2", "3.5", "4", "4.5", "5", "A", "P", "M", "S", "A", "P", "M", "S", "A", "P"};

    public IsoFragment() {
        this.setBarStringArr(myString);
    }
    public void SetContentString(String[] myString) {
        this.setBarStringArr(myString);
    }


}
