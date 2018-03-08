package com.example.mail.OlyAirONE;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail on 13/10/2016.
 */

public class FragmentShutterMasterSlidebar extends FragmentMasterSlidebar {
    private static final String TAG = FragmentShutterMasterSlidebar.class.getSimpleName();
    private static List<String> expValues;

    public static FragmentShutterMasterSlidebar newInstance(List<String> myString, String value) {
        FragmentShutterMasterSlidebar myFragment = new FragmentShutterMasterSlidebar();

        Bundle args = new Bundle();
        ArrayList<String> myArrList = new ArrayList();
        myArrList.addAll(myString);
        args.putStringArrayList("myString", myArrList);
        args.putString("value", value);
        myFragment.setArguments(args);
        expValues = myString;
        return myFragment;
    }
    public static List<String> getPossibleShutterValues(){
        return expValues;
    }


}
