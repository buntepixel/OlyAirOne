package com.example.mail.fragmenttest;

/**
 * Created by mail on 13/10/2016.
 */

public class ApartureFragment extends MasterSlidebarFragment {
    private static final String TAG = ApartureFragment.class.getSimpleName();


    @Override
    public void SetSliderBarVal(int Index) {
        this.mySliderValIndex = Index;
    }

    @Override
    public void setBarStringArr(String[] inStringArr) {
       this.myString = new String[]{"app", "3.2", "3.5", "4", "4.5", "5", "6", "7", "8.0"};

    }


}


