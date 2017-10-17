package com.example.mail.fragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public MasterSlidebarFragment() {
    }

    public interface sliderValue {
        void onSlideValueBar(String value);
    }

    public void SetOLYCam(OLYCamera camera) {
        this.camera = camera;
    }

    public void setSliderValueListener(sliderValue listener) {
        this.sliderValueListener = listener;
    }

    public void setBarStringArr(String[] inStringArr) {
        this.myString = inStringArr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            //create LinLayout to hold the text view
            LinearLayout mContentLinLayout = new LinearLayout(getContext());

            //mContentLinLayout.setId(R.id.mContentLinLayout);
            mContentLinLayout.setId(View.generateViewId());
            //Log.d(TAG, "mContnentLinLayout id: " + mContentLinLayout.getId());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mContentLinLayout.setLayoutParams(params);
            // mContentLinLayout.setBackgroundColor(Color.RED);
            mContentLinLayout.setOrientation(LinearLayout.HORIZONTAL);

            View rootView = inflater.inflate(R.layout.fragment_observablescrollview, container, false);
            rootView.setId(View.generateViewId());
            // Log.d(TAG, "RootView id: " + rootView.getId());


            ScrollingValuePicker mScrollingValuePicker = (ScrollingValuePicker) rootView.findViewById(R.id.svp_neutralScrollingValuePicker);
            mScrollingValuePicker.generateViewId();
            mScrollingValuePicker.SetScrollingValueInteractionListener(new ScrollingValuePicker.ScrollingValueInteraction() {
                @Override
                public void onScrollEnd(int currIndex) {
                    Log.d(TAG, "onScroll End ___ Masterslidebar");
                    if (sliderValueListener != null) {
                        Log.d(TAG, "CurrSTring: " + myString[currIndex]);
                        sliderValueListener.onSlideValueBar(myString[currIndex]);
                    }
                }
            });
//
            //mScrollingValuePicker.setOnScrollChangeListener(onScrollChanged(mScrollingValuePicker,0,0););

            //Log.d(TAG, "mScrollingValuePicker id: " + mScrollingValuePicker.getId());
            //mScrollingValuePicker.setupValuePicker(myString);
            mScrollingValuePicker.intValuePicker(getContext(), mContentLinLayout, myString);

            return rootView;
        } catch (Exception e) {
            Log.e(TAG, "exception: " + e.getMessage());
            Log.e(TAG, "exception: " + Log.getStackTraceString(e));
        }
        return null;
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


