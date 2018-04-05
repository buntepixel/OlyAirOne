package com.example.mail.OlyAirONE;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail on 13/10/2016.
 */

public class FragmentSlidebarMasterWb extends FragmentSlidebarMaster {
    private static final String TAG = FragmentSlidebarMasterWb.class.getSimpleName();

    public  static FragmentSlidebarMasterWb newInstance(List<String> myString, String value) {
        FragmentSlidebarMasterWb myFragment = new FragmentSlidebarMasterWb();

        Bundle args = new Bundle();
        ArrayList<String> myArrList = new ArrayList(myString);
        args.putStringArrayList("myString",myArrList );
        args.putString("value", value);
        myFragment.setArguments(args);

        return myFragment;
    }


}
