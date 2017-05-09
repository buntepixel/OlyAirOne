package com.example.mail.fragmenttest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by mail on 13/10/2016.
 */

public class ExposureFragment extends Fragment {
    private static final String TAG = ExposureFragment.class.getSimpleName();

    public ExposureFragment() {
    }

    private LinearLayout mLinearLayout;
    private LinearLayout mContentLinLayout;
    private ScrollingValuePicker mScrollingValuePicker;

    private static final String[] myString = {"2.8", "3.2", "3.5", "4", "4.5", "5", "A", "P", "M", "S", "A", "P", "M", "S", "A", "P"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            mContentLinLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
            mContentLinLayout.setLayoutParams(params);
            mContentLinLayout.setBackgroundColor(Color.GREEN);
            mContentLinLayout.setOrientation(LinearLayout.HORIZONTAL);

            //this.CreateTextViewContent(myString);
            this.CreateImageViewContent(15);

            Log.d(TAG, "ExposureFrag" + mContentLinLayout.toString());
            View rootView = inflater.inflate(R.layout.fragment_exposure, container, false);
            mScrollingValuePicker = (ScrollingValuePicker) rootView.findViewById(R.id.svp_expScrollingValuePicker);

            mScrollingValuePicker.execute(getContext(), mContentLinLayout);
            return rootView;
        } catch (Exception e) {
            Log.e(TAG, "exception: " + e.getMessage());
            Log.e(TAG, "exception: " + Log.getStackTraceString(e));
        }
        return null;
    }

    private void CreateTextViewContent(String[] stringArr) {
        //Adding Textview
        for (String i : stringArr) {
            TextView textView = new TextView(getActivity());
            Log.d(TAG, "mystring:  " + i);
            textView.setText(i);
            //textView.setTextSize(40);
            textView.setBackgroundColor(Color.BLUE);
            textView.setPaddingRelative(30, 0, 30, 0);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            //mContentLinLayout = (LinearLayout) rootView.findViewById(R.id.svp_expScrollingValuePicker);
            mContentLinLayout.addView(textView);
        }
    }

    private void CreateImageViewContent(int itemCount) {
        //Adding ImageView
        for (int i = 0; i <= itemCount; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.ic_rasterstrip);
            //imageView.setScaleX( (float) 0.5);
            //imageView.setScaleType(ImageView.ScaleType.FIT_END);
            imageView.setBackgroundColor(Color.BLUE);
            if((i+1) % 5 !=0){
                imageView.setScaleY( (float) 0.5);
                imageView.setScaleX( (float) 0.5);
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
                //imageView.setAdjustViewBounds(true);
            }
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            mContentLinLayout.addView(imageView);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
