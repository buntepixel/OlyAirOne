package com.example.mail.fragmenttest;

/**
 * Created by mail on 14/06/2017.
 */

public class IsoFragment extends MasterSlidebarFragment {
    private static final String TAG = IsoFragment.class.getSimpleName();

    @Override
    public void SetSliderBarVal(int Index) {
        this.mySliderValIndex = Index;
    }
    @Override
    public void setBarStringArr(String[] inStringArr) {
        this.myString = inStringArr;
    }
}
