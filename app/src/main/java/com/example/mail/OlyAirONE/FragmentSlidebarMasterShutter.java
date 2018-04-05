package com.example.mail.OlyAirONE;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail on 13/10/2016.
 */

public class FragmentSlidebarMasterShutter extends FragmentSlidebarMaster {
    private static final String TAG = FragmentSlidebarMasterShutter.class.getSimpleName();
    private static List<String> expValues;

    public static FragmentSlidebarMasterShutter newInstance(List<String> myString, String value) {
        FragmentSlidebarMasterShutter myFragment = new FragmentSlidebarMasterShutter();

        Bundle args = new Bundle();
        ArrayList<String> myArrList = new ArrayList(myString);
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
