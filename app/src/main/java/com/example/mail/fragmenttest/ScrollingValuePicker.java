package com.example.mail.fragmenttest;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.mail.fragmenttest.CameraActivity.camera;

/**
 * Created by mail on 13/10/2016.
 */

public class ScrollingValuePicker extends FrameLayout {
    private static final String TAG = ScrollingValuePicker.class.getSimpleName();
    private View mLeftSpacer;
    private View mRightSpacer;
    private View mCenterContainer;
    private int mCenterContainerWidth;
    private int newScrollVal;
    private ObservableHorizontalScrollView obsScrollView;
    private String[] content;

    public int getCurrValueIndex() {

        return 0;
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
            public void onScrollChanged(ObservableHorizontalScrollView view, int l, int t, int scrollBarWidth) {
                int visScrollBarWidth = scrollBarWidth - getWidth();
                float scrollValue = (float) l;// visScrollBarWidth;
                //mScrollingValueListener.onScrollChanged(view, scrollValue, visScrollBarWidth);
            }

            @Override
            public void onTouchUpAction(ObservableHorizontalScrollView view, int scrollValue, int scrollBarWidth) {
                try {

                    Log.d(TAG, "Gettouchup___Value Picker");
                    int barSegment = mCenterContainerWidth / content.length;

                    // scrollvalue from 0-0,99999999
                    float decScrollVal = scrollValue / (mCenterContainerWidth + 1);//add 1 so it never gets 1 and breaks the index
                    int currIndex = (int) Math.floor((decScrollVal * content.length));
                    //make sure index doesn't get out of range on overscroll
                    currIndex = Math.max(0, currIndex);
                    currIndex = Math.min(content.length - 1, currIndex);

                    newScrollVal = Math.round((barSegment * currIndex));//- (barSegment / 2));
                    obsScrollView.scrollTo(newScrollVal, 0);
                    Log.d(TAG, "ScrollValue: " + newScrollVal + " index: " + currIndex + "barSegment: " + barSegment);
                    Log.d(TAG, "index: " + currIndex + " value: " + content[currIndex]);
                } catch (Exception ex) {
                    String stackTrace = Log.getStackTraceString(ex);
                    Log.d(TAG, stackTrace);
                }
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
            mCenterContainerWidth = obsScrollView.getWidth();
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
                    Log.d(TAG, "CurrentValue::::::" + camera.getCameraPropertyValueTitle(i));
                    textView.setText(camera.getCameraPropertyValueTitle(i));
                } else {
                    Log.d(TAG, "Cam Seems to be null");
                    textView.setText(i);
                }

                //textView.setTextSize(40);
                textView.setBackgroundColor(Color.GREEN);
                textView.setPaddingRelative(25, 0, 25, 0);
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
}
