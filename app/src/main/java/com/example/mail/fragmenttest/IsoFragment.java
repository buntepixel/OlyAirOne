package com.example.mail.fragmenttest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by mail on 14/06/2017.
 */

public class IsoFragment extends Fragment {
    private static final String TAG = IsoFragment.class.getSimpleName();
    private static final String[] myString = {"app", "3.2", "3.5", "4", "4.5", "5", "A", "P", "M", "S", "A", "P", "M", "S", "A", "P"};

    public IsoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            //create LinLayout to hold the text view
            LinearLayout mContentLinLayout = new LinearLayout(getContext());

            //mContentLinLayout.setId(R.id.mContentLinLayout);
            mContentLinLayout.setId(View.generateViewId());
            //Log.d(TAG, "mContnentLinLayou id: " + mContentLinLayout.getId());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mContentLinLayout.setLayoutParams(params);
            // mContentLinLayout.setBackgroundColor(Color.RED);
            mContentLinLayout.setOrientation(LinearLayout.HORIZONTAL);
            //create text View for LinLayout
            this.AddTextViewContent(myString, mContentLinLayout);

            //Log.d(TAG, "Iso Fragment:: " + mContentLinLayout.toString());

            View rootView = inflater.inflate(R.layout.fragment_observablescrollview, container, false);
            rootView.setId(View.generateViewId());
            //Log.d(TAG, "RootView id: " + rootView.getId());


            ScrollingValuePicker mScrollingValuePicker = (ScrollingValuePicker) rootView.findViewById(R.id.svp_neutralScrollingValuePicker);
            mScrollingValuePicker.generateViewId();
            //Log.d(TAG, "mScrollingValuePicker id: " + mScrollingValuePicker.getId());

            mScrollingValuePicker.execute(getContext(), mContentLinLayout);
            return rootView;
        } catch (Exception e) {
            Log.e(TAG, "exception: " + e.getMessage());
            Log.e(TAG, "exception: " + Log.getStackTraceString(e));
        }
        return null;
    }


    private void AddTextViewContent(String[] stringArr, LinearLayout linearLayout) {
        //Adding Textview
        for (String i : stringArr) {
            TextView textView = new TextView(getActivity());
            textView.setId(View.generateViewId());
            //Log.d(TAG, "textView "+i+" id: " + textView.getId());


            //Log.d(TAG, "mystring:  " + i);
            textView.setText(i);
            //textView.setTextSize(40);
            textView.setBackgroundColor(Color.DKGRAY);
            textView.setPaddingRelative(25, 0, 25, 0);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(textView);
        }
    }

//    private void CreateImageViewContent(int itemCount) {
//        //Adding ImageView
//        for (int i = 0; i <= itemCount; i++) {
//            ImageView imageView = new ImageView(getActivity());
//            imageView.setImageResource(R.drawable.ic_rasterstrip);
//            //imageView.setScaleX( (float) 0.5);
//            //imageView.setScaleType(ImageView.ScaleType.FIT_END);
//            if ((i + 1) % 5 != 0) {
//                imageView.setScaleY((float) 0.5);
//                imageView.setScaleX((float) 0.5);
//                imageView.setScaleType(ImageView.ScaleType.FIT_START);
//                //imageView.setAdjustViewBounds(true);
//            }
//            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT));
//
//            mContentLinLayout.addView(imageView);
//        }
//    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
