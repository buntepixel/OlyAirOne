package com.example.mail.fragmenttest;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail on 13/10/2016.
 */

public class ApartureFragment extends MasterSlidebarFragment {
    private static final String TAG = ApartureFragment.class.getSimpleName();

    public  static ApartureFragment newInstance(List<String> myString,String value) {
        ApartureFragment myFragment = new ApartureFragment();

        Bundle args = new Bundle();
        ArrayList<String> myArrList = new ArrayList();
        myArrList.addAll(myString);
        args.putStringArrayList("myString",myArrList );
        args.putString("value", value);
        myFragment.setArguments(args);

        return myFragment;
    }

}


