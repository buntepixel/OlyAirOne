package com.example.mail.fragmenttest;

/**
 * Created by mail on 13/10/2016.
 */

public class ShutterFragment extends MasterSlidebarFragment {
    private static final String TAG = ShutterFragment.class.getSimpleName();



    @Override
    public void SetSliderBarVal(int Index) {
        this.mySliderValIndex = Index;
    }
    @Override
    public void setBarStringArr(String[] inStringArr) {
        this.myString = inStringArr;
    }

}
