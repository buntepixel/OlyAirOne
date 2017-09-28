package com.example.mail.fragmenttest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by mail on 13/10/2016.
 */

public class ApartureFragment extends Fragment {
    private static final String TAG = ApartureFragment.class.getSimpleName();

    public ApartureFragment() {
    }

    //private LinearLayout mLinearLayout;
    //private LinearLayout mContentLinLayout;
    //private ScrollingValuePicker mScrollingValuePicker;

    private static final String[] myString = {"app", "3.2", "3.5", "4", "4.5", "5", "6", "7", "8.0"};


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

            //Log.d(TAG, "Aparture Fragment:: " + mContentLinLayout.toString());

            View rootView = inflater.inflate(R.layout.fragment_observablescrollview, container, false);
            rootView.setId(View.generateViewId());
            // Log.d(TAG, "RootView id: " + rootView.getId());


            final ScrollingValuePicker mScrollingValuePicker = (ScrollingValuePicker) rootView.findViewById(R.id.svp_neutralScrollingValuePicker);
            mScrollingValuePicker.generateViewId();
            mScrollingValuePicker.setScrollingValueListener(new ScrollingValuePicker.ScrollingValueListener() {
                @Override
                public void onScrollChanged(ObservableHorizontalScrollView view, float scrollValue, int visibleScrollBarVal) {
                    Log.d(TAG, "scrollVal: " + scrollValue + " visScrollVal: " + visibleScrollBarVal);
                    int barSegment = visibleScrollBarVal / myString.length;
                    // scrollvalue from 0-0,99999999
                    float decScrollVal = scrollValue / (visibleScrollBarVal + 1);//add 1 so it never gets 1 and breaks the index
                    int currIndex = (int) Math.floor((decScrollVal * myString.length));
                    final int newScrollVal = (int) Math.round((barSegment * currIndex) - (barSegment / 2));
                    // view.scrollTo(newScrollVal,0);
                    Log.d(TAG, "ScrollValue: " + newScrollVal + " index: " + currIndex);
                    view.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                                Log.d("TouchTest", "Touch down");
                            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                                Log.d("TouchTest", "Touch up"+ newScrollVal);
                                v.scrollTo(newScrollVal, 0);
                            }
                            return true;
                        }
                    });
                    //Log.d(TAG,"index: "+currIndex+" value: "+myString[currIndex]);
                }
            });

            //mScrollingValuePicker.setOnScrollChangeListener(onScrollChanged(mScrollingValuePicker,0,0););

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
            //Log.d(TAG, "textView " + i + " id: " + textView.getId());


            //Log.d(TAG, "mystring:  " + i);
            textView.setText(i);
            //textView.setTextSize(40);
            textView.setBackgroundColor(Color.GREEN);
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


