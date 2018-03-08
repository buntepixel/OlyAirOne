package com.example.mail.OlyAirONE;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail on 13/10/2016.
 */

public class FragmentWbMasterSlidebar extends FragmentMasterSlidebar {
    private static final String TAG = FragmentWbMasterSlidebar.class.getSimpleName();

    public  static FragmentWbMasterSlidebar newInstance(List<String> myString, String value) {
        FragmentWbMasterSlidebar myFragment = new FragmentWbMasterSlidebar();

        Bundle args = new Bundle();
        ArrayList<String> myArrList = new ArrayList();
        myArrList.addAll(myString);
        args.putStringArrayList("myString",myArrList );
        args.putString("value", value);
        myFragment.setArguments(args);

        return myFragment;
    }


}
