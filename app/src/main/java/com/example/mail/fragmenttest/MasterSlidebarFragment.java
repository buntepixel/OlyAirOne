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

import jp.co.olympus.camerakit.OLYCamera;

/**
 * Created by mail on 13/10/2016.
 */

public abstract class MasterSlidebarFragment extends Fragment {
    private static final String TAG = MasterSlidebarFragment.class.getSimpleName();
    OLYCamera camera;
    private String[] myString;
    private sliderValue sliderValueListener;
    private int mySliderValIndex = -1;
    private ScrollingValuePicker mScrollingValuePicker;

    public MasterSlidebarFragment() {
    }


    public interface sliderValue {
        void onSlideValueBar(String value);
    }

    public void setSliderValueListener(sliderValue listener) {
        this.sliderValueListener = listener;
    }
    public ScrollingValuePicker getScrollingValuePicker(){
        return mScrollingValuePicker;
    }

    public void SetOLYCam(OLYCamera camera) {
        this.camera = camera;
    }

    public void setBarStringArr(String[] inStringArr) {
        this.myString = inStringArr;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            //create LinLayout to hold the text view
            LinearLayout mContentLinLayout = new LinearLayout(getContext());

            mContentLinLayout.setId(View.generateViewId());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mContentLinLayout.setLayoutParams(params);
            mContentLinLayout.setOrientation(LinearLayout.HORIZONTAL);


            View rootView = inflater.inflate(R.layout.fragment_observablescrollview, container, false);
            rootView.setId(View.generateViewId());
            // Log.d(TAG, "RootView id: " + rootView.getId());


            mScrollingValuePicker = (ScrollingValuePicker) rootView.findViewById(R.id.svp_neutralScrollingValuePicker);
            mScrollingValuePicker.generateViewId();
            mScrollingValuePicker.addView(mContentLinLayout);
            mScrollingValuePicker.initValuePicker(getContext(), mContentLinLayout, myString);
            mScrollingValuePicker.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if(mySliderValIndex==-1)
                        mySliderValIndex=myString.length/2;
                    mScrollingValuePicker.setBarToValue(mySliderValIndex);
                    mScrollingValuePicker.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
            mScrollingValuePicker.SetScrollingValueInteractionListener(new ScrollingValuePicker.ScrollingValueInteraction() {
                @Override
                public void onScrollEnd(int currIndex) {
                    Log.d(TAG, "onScroll End ");
                    if (sliderValueListener != null) {
                        Log.d(TAG, "CurrSTring: " + myString[currIndex]);
                        sliderValueListener.onSlideValueBar(myString[currIndex]);
                        mySliderValIndex = currIndex;
                        mScrollingValuePicker.snapBarToValue(mySliderValIndex);
                    }
                }
            });

            //
            //mScrollingValuePicker.setOnScrollChangeListener(onScrollChanged(mScrollingValuePicker,0,0););

            //Log.d(TAG, "mScrollingValuePicker id: " + mScrollingValuePicker.getId());
            //mScrollingValuePicker.setupValuePicker(myString);

            return rootView;
        } catch (Exception e) {
            Log.e(TAG, "exception: " + e.getMessage());
            Log.e(TAG, "exception: " + Log.getStackTraceString(e));
        }
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "View Created Slider Value: "+ mySliderValIndex);
        mScrollingValuePicker.setBarToValue(mySliderValIndex);
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


