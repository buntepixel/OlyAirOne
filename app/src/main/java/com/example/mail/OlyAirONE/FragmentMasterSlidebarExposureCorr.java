package com.example.mail.OlyAirONE;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail on 13/10/2016.
 */

public class FragmentMasterSlidebarExposureCorr extends FragmentMasterSlidebar {
    private static final String TAG = FragmentMasterSlidebarExposureCorr.class.getSimpleName();

    public  static FragmentMasterSlidebarExposureCorr newInstance(List<String> myString, String value) {
        FragmentMasterSlidebarExposureCorr myFragment = new FragmentMasterSlidebarExposureCorr();

        Bundle args = new Bundle();
        ArrayList<String> myArrList = new ArrayList();
        myArrList.addAll(myString);
        args.putStringArrayList("myString",myArrList );
        args.putString("value", value);
        myFragment.setArguments(args);

        return myFragment;
    }



}
