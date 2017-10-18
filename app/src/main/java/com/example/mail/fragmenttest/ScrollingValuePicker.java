package com.example.mail.fragmenttest;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mail.fragmenttest.CameraActivity.camera;

/**
 * Created by mail on 13/10/2016.
 */

public class ScrollingValuePicker extends FrameLayout {
    private static final String TAG = ScrollingValuePicker.class.getSimpleName();
    private View mLeftSpacer;
    private View mRightSpacer;
    private LinearLayout mCenterContainer;
    private int mCenterContainerWidth;
    private int newScrollVal;
    private int currContentIndex;
    private ObservableHorizontalScrollView obsScrollView;
    private String[] content;
    private int txtPadding = 25;
    private List<Integer> contentWidthList = new ArrayList<Integer>();

    @SuppressWarnings("serial")
    private static final Map<String, Integer> whiteBalanceIconList = new HashMap<String, Integer>() {
        {
            put("<WB/WB_AUTO>", R.drawable.icn_wb_setting_wbauto);
            put("<WB/MWB_SHADE>", R.drawable.icn_wb_setting_16);
            put("<WB/MWB_CLOUD>", R.drawable.icn_wb_setting_17);
            put("<WB/MWB_FINE>", R.drawable.icn_wb_setting_18);
            put("<WB/MWB_LAMP>", R.drawable.icn_wb_setting_20);
            put("<WB/MWB_FLUORESCENCE1>", R.drawable.icn_wb_setting_35);
            put("<WB/MWB_WATER_1>", R.drawable.icn_wb_setting_64);
            put("<WB/WB_CUSTOM1>", R.drawable.icn_wb_setting_512);
        }
    };

    public int getCurrValueIndex() {
        return currContentIndex;
    }

    public ScrollingValueInteraction mValueInteractionListener;

    public interface ScrollingValueInteraction {
        void onScrollEnd(int currentIndex);
    }

    public void SetScrollingValueInteractionListener(ScrollingValueInteraction listener) {
        mValueInteractionListener = listener;
    }

    private int getScrollPos(int index) {
        List<Integer> subList = contentWidthList.subList(0, index);
        int sum = 0;
        for (int i = 0; i < subList.size(); i++) {
            sum = sum + subList.get(i);
            if(i ==(subList.size()-1)){
                sum = sum +(subList.get(i)/2);
            }
        }
        return sum;
    }

    public ScrollingValuePicker(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // Create our internal scroll view
        obsScrollView = new ObservableHorizontalScrollView(context, attrs);
        obsScrollView.setId(View.generateViewId());
        obsScrollView.setHorizontalScrollBarEnabled(false);
        addView(obsScrollView);
        obsScrollView.setOnScrollChangedListener(new ObservableHorizontalScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ObservableHorizontalScrollView view, int scrollValue, int t, int scrollBarWidth) {
                try {

                    Log.d(TAG, "::::::::::onScrollChanged:::::::::::::::::" + content.length);
                    int barSegment = mCenterContainerWidth / (content.length);

                    // scrollvalue from 0-0,99999999
                    float decScrollVal = (float) scrollValue / (mCenterContainerWidth + 1);//add 1 so it never gets 1 and breaks the index
                    int currIndex = (int) Math.floor((decScrollVal * content.length));
                    Log.d(TAG, "containerWidth: " + mCenterContainerWidth + "  ScrollValue" + scrollValue + " decScrollValue: " + decScrollVal);
                    //make sure index doesn't get out of range on overscroll
                    currIndex = Math.max(0, currIndex);
                    currIndex = Math.min(content.length - 1, currIndex);
                    currContentIndex = currIndex;


                    newScrollVal = getScrollPos(currIndex);
                    mValueInteractionListener.onScrollEnd(currIndex);

                    obsScrollView.smoothScrollTo(newScrollVal , 0);

                    Log.d(TAG, "ScrollValue: " + newScrollVal + " index: " + currIndex + "  barSegment: " + barSegment);
                    Log.d(TAG, "index: " + currIndex + " value: " + content[currIndex]);
                } catch (Exception ex) {
                    String stackTrace = Log.getStackTraceString(ex);
                    Log.d(TAG, stackTrace);
                }
            }

            @Override
            public void onTouchUpAction(ObservableHorizontalScrollView view, int scrollValue, int scrollBarWidth) {
               /* try {

                    Log.d(TAG, "Gettouchup___Value Picker");
                    int barSegment = mCenterContainerWidth / content.length;

                    // scrollvalue from 0-0,99999999
                    float decScrollVal = (float) scrollValue / (mCenterContainerWidth + 1);//add 1 so it never gets 1 and breaks the index
                    int currIndex = (int) Math.floor((decScrollVal * content.length));
                    Log.d(TAG, "containerWidth: " + mCenterContainerWidth + "  ScrollValue" + scrollValue + " decScrollValue: " + decScrollVal);
                    //make sure index doesn't get out of range on overscroll
                    currIndex = Math.max(0, currIndex);
                    currIndex = Math.min(content.length - 1, currIndex);
                    currContentIndex = currIndex;
                    newScrollVal = Math.round((barSegment * currIndex) + (barSegment / 2));

                    mValueInteractionListener.onScrollEnd(currIndex);

                    //obsScrollView.scrollTo(newScrollVal, 0);

                    Log.d(TAG, "ScrollValue: " + newScrollVal + " index: " + currIndex + "  barSegment: " + barSegment);
                    Log.d(TAG, "index: " + currIndex + " value: " + content[currIndex]);
                } catch (Exception ex) {
                    String stackTrace = Log.getStackTraceString(ex);
                    Log.d(TAG, stackTrace);
                }*/
            }
        });
    }

    public void intValuePicker(Context context, LinearLayout linearLayout, String[] myStringBarValues) {
        try {
            // Create a horizontal (by default) LinearLayout as our child container
            content = myStringBarValues;
            final LinearLayout ll_contentContainer = new LinearLayout(context);
            ll_contentContainer.setId(View.generateViewId());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            obsScrollView.addView(ll_contentContainer);
            mCenterContainer = ll_contentContainer;

            //the actual context gets Injected
            Log.d(TAG, "myStringBarValues: " + myStringBarValues[0]);
            if (whiteBalanceIconList.containsKey(myStringBarValues[0])) {
                for (String i : myStringBarValues)
                    Log.d(TAG, "WbValues: " + i);
                AddImageViewContent(context, myStringBarValues, ll_contentContainer);

            } else
                AddTextViewContent(context, myStringBarValues, ll_contentContainer);

            ll_contentContainer.addView(linearLayout);

            // Create the left and right spacers, don't worry about their dimensions, yet
            mLeftSpacer = new View(context);
            ll_contentContainer.addView(mLeftSpacer, 0);
            mRightSpacer = new View(context);
            ll_contentContainer.addView(mRightSpacer);

        } catch (Exception e) {
            Log.e(TAG, "exception: " + e.getMessage());
            Log.e(TAG, "exception: " + Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            // Layout the spacers now that we are measured
            final int width = getWidth();

            final ViewGroup.LayoutParams leftParams = mLeftSpacer.getLayoutParams();
            leftParams.width = width / 2;
            mLeftSpacer.setLayoutParams(leftParams);

            final ViewGroup.LayoutParams rightParams = mRightSpacer.getLayoutParams();
            rightParams.width = width / 2;
            mRightSpacer.setLayoutParams(rightParams);
            mCenterContainerWidth = mCenterContainer.getWidth();
            LinearLayout ll = mCenterContainer;
            final int childCount = ll.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View v = ll.getChildAt(i);
                int myWidth = v.getWidth();
                if (myWidth != 0)
                    contentWidthList.add(myWidth);

                Log.d(TAG, "Array: " + v.getWidth());
                // Do something with v.
                // …
            }
        }
    }
//Scrolling listener

    // do stuff with the scroll listener we created early to make our values usable.
    private void AddTextViewContent(Context context, String[] stringArr, LinearLayout linearLayout) {
        //Adding Textview
        try {
            for (String i : stringArr) {
                TextView textView = new TextView(context);
                textView.setId(View.generateViewId());
                //Log.d(TAG, "textView " + i + " id: " + textView.getId());
                //Log.d(TAG, "mystring:  " + i);
                if (camera != null) {
                    //Log.d(TAG, "CurrentValue::::::" + camera.getCameraPropertyValueTitle(i));
                    textView.setText(camera.getCameraPropertyValueTitle(i));
                } else {
                    Log.d(TAG, "Cam Seems to be null");
                    textView.setText(i);
                }

                //textView.setTextSize(40);
                textView.setBackgroundColor(Color.GREEN);
                textView.setPaddingRelative(txtPadding, 0, txtPadding, 0);
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(textView);
            }
        } catch (Error e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);
        }
    }

    private void AddImageViewContent(Context context, String[] stringArr, LinearLayout linearLayout) {
        try {
            //Adding ImageView
            Collection<Integer> myValues = whiteBalanceIconList.values();
            Log.d(TAG, "StringArrLength: " + stringArr.length + " ");

            for (int i = 0; i < stringArr.length; i++) {
                ImageView imageView = new ImageView(getContext());
                int viewId = whiteBalanceIconList.get(stringArr[i]);
                imageView.setImageResource(viewId);

                //imageView.setScaleX( (float) 0.5);
                //imageView.setScaleType(ImageView.ScaleType.FIT_END);
      /*      if ((i + 1) % 5 != 0) {
                imageView.setScaleY((float) 0.5);
                imageView.setScaleX((float) 0.5);
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
                //imageView.setAdjustViewBounds(true);
            }*/
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                linearLayout.addView(imageView);
            }
        } catch (Error e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);
        }
    }
}
