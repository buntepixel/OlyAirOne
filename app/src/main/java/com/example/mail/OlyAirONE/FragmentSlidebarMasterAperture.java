package com.example.mail.OlyAirONE;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail on 13/10/2016.
 */

public class FragmentSlidebarMasterAperture extends FragmentSlidebarMaster {
    private static final String TAG = FragmentSlidebarMasterAperture.class.getSimpleName();

    public  static FragmentSlidebarMasterAperture newInstance(List<String> myString, String value) {
        FragmentSlidebarMasterAperture myFragment = new FragmentSlidebarMasterAperture();

        Bundle args = new Bundle();
        ArrayList<String> myArrList = new ArrayList(myString);
        args.putStringArrayList("myString",myArrList );
        args.putString("value", value);
        myFragment.setArguments(args);

        return myFragment;
    }

}


