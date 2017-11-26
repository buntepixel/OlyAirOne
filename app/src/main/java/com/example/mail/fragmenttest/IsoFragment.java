package com.example.mail.fragmenttest;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mail on 14/06/2017.
 */

public class IsoFragment extends MasterSlidebarFragment {
    private static final String TAG = IsoFragment.class.getSimpleName();

    public  static IsoFragment newInstance(List<String> myString,String value) {
        IsoFragment myFragment = new IsoFragment();

        Bundle args = new Bundle();
        ArrayList<String> myArrList = new ArrayList();
        myArrList.addAll(myString);
        args.putStringArrayList("myString",myArrList );
        args.putString("value", value);
        myFragment.setArguments(args);

        return myFragment;
    }


}
