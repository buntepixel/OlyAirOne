package com.example.mail.fragmenttest;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail on 13/10/2016.
 */

public class ExposureCorrFragment extends MasterSlidebarFragment {
    private static final String TAG = ExposureCorrFragment.class.getSimpleName();

    public  static ExposureCorrFragment newInstance(List<String> myString,String value) {
        ExposureCorrFragment myFragment = new ExposureCorrFragment();

        Bundle args = new Bundle();
        ArrayList<String> myArrList = new ArrayList();
        myArrList.addAll(myString);
        args.putStringArrayList("myString",myArrList );
        args.putString("value", value);
        myFragment.setArguments(args);

        return myFragment;
    }



}
