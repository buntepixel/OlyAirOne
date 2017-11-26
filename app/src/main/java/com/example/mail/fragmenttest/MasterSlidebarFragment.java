package com.example.mail.fragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.util.List;

import jp.co.olympus.camerakit.OLYCamera;

/**
 * Created by mail on 13/10/2016.
 */

public abstract class MasterSlidebarFragment extends Fragment implements ViewTreeObserver.OnPreDrawListener, ScrollingValuePicker.ScrollingValueInteraction {
    private static final String TAG = MasterSlidebarFragment.class.getSimpleName();
    OLYCamera camera;
    protected List<String> myString;
    private sliderValue sliderValueListener;
    protected int mySliderValIndex = -1;
    private ScrollingValuePicker mScrollingValuePicker;

    public MasterSlidebarFragment() {

    }

    public interface sliderValue {
        void onSlideValueBar(String value);
    }

    public void setSliderValueListener(sliderValue listener) {
        this.sliderValueListener = listener;
    }

    public ScrollingValuePicker getScrollingValuePicker() {
        return mScrollingValuePicker;
    }

    public void SetOLYCam(OLYCamera camera) {
        this.camera = camera;
    }


    public void SetSliderBarValues(List<String> inStringArr) {
        myString = inStringArr;
    }

    public boolean SetSliderBarValIdx(String value) {
        if (myString != null && myString.size() > 0) {
            SetSliderBarValIdx(myString.indexOf(value));
            return true;
        } else {
            return false;
        }
    }

    public boolean SetSliderBarValIdx(int index) {
        Log.d(TAG,"setting SlideBar to: "+ index);

        mySliderValIndex = index;
        return true;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            Log.d(TAG, "OnCreateView");

            //create LinLayout to hold the text view
            LinearLayout mContentLinLayout = new LinearLayout(getContext());

            mContentLinLayout.setId(View.generateViewId());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mContentLinLayout.setLayoutParams(params);
            mContentLinLayout.setOrientation(LinearLayout.HORIZONTAL);


            View rootView = inflater.inflate(R.layout.fragment_observablescrollview, container, false);
            rootView.setId(View.generateViewId());

            myString = getArguments().getStringArrayList("myString");
            mySliderValIndex = myString.indexOf(getArguments().getString("value"));
            Log.d(TAG, "mySliderValIdx: " + mySliderValIndex);


            mScrollingValuePicker = (ScrollingValuePicker) rootView.findViewById(R.id.svp_neutralScrollingValuePicker);
            mScrollingValuePicker.generateViewId();
            mScrollingValuePicker.addView(mContentLinLayout);
            mScrollingValuePicker.initValuePicker(getContext(), mContentLinLayout, myString);
            mScrollingValuePicker.getViewTreeObserver().addOnPreDrawListener(this);
            mScrollingValuePicker.SetScrollingValueInteractionListener(this);
            return rootView;
        } catch (Exception e) {
            Log.e(TAG, "exception: " + e.getMessage());
            Log.e(TAG, "exception: " + Log.getStackTraceString(e));
        }
        return null;
    }

    @Override
    public boolean onPreDraw() {
        Log.d(TAG, "OnPreeDraw");
        Log.d(TAG, "mySliderValIdx: " + mySliderValIndex);
        if (mySliderValIndex == -1)
            mySliderValIndex = myString.size() / 2;
        Log.d(TAG, "mySliderValIdx: " + mySliderValIndex);

        mScrollingValuePicker.setBarToValue(mySliderValIndex);
        mScrollingValuePicker.getViewTreeObserver().removeOnPreDrawListener(this);
        return true;
    }

    @Override
    public void onScrollEnd(int currIndex) {
        Log.d(TAG, "onScroll End ");
        if (sliderValueListener != null) {
            sliderValueListener.onSlideValueBar(myString.get(currIndex));
            mySliderValIndex = currIndex;
            Log.d(TAG, "CurrSTring: " + myString.get(currIndex) + "mysliderValIdx: " + mySliderValIndex);
            mScrollingValuePicker.snapBarToValue(mySliderValIndex);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("mySliderValIndex", mySliderValIndex);
        Log.d(TAG, "Save Instance State" + mySliderValIndex);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "View Created");
        if (savedInstanceState != null) {
            mySliderValIndex = savedInstanceState.getInt("mySliderValIndex", -5);
            mScrollingValuePicker.setBarToValue(mySliderValIndex);
            Log.d(TAG, "mySliderValIndex: " + mySliderValIndex);
        }

    }

/*    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "ACTCREA mySliderValIndex: " + mySliderValIndex);

        mySliderValIndex = savedInstanceState.getInt("mySliderValIndex", -5);
        mScrollingValuePicker.setBarToValue(mySliderValIndex);
        Log.d(TAG, "ACTCREA mySliderValIndex: " + mySliderValIndex);

    }*/
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
        try {
            sliderValueListener = (sliderValue) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement sliderValueListener ");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (sliderValueListener != null) {
            sliderValueListener = null;
        }
    }
}


