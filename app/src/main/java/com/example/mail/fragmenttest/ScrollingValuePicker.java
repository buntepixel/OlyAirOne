package com.example.mail.fragmenttest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by mail on 13/10/2016.
 */

public class ScrollingValuePicker extends FrameLayout {
    private static final String TAG= ScrollingValuePicker.class.getSimpleName();
    private View mLeftSpacer;
    private View mRightSpacer;
    private ObservableHorizontalScrollView mScrollView;

    private LinearLayout mLinearLayout;
    public LinearLayout getLinearLayout() {
        return mLinearLayout;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        mLinearLayout = linearLayout;
        Log.d(TAG, "ScrollingValuePicker"+ mLinearLayout.toString());
    }

    public ScrollingValuePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScrollView = new ObservableHorizontalScrollView(context, attrs);
        mScrollView.setId(View.generateViewId());
    }
    public void execute(Context context, LinearLayout linearLayout){
        try{
            // Create our internal scroll view
            mScrollView.setHorizontalScrollBarEnabled(false);
            addView(mScrollView);
            // Create a horizontal (by default) LinearLayout as our child container
            final LinearLayout ll_container = new LinearLayout(context);
            ll_container.setId(View.generateViewId());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mScrollView.addView(ll_container);

            //the actual context gets Injected
            ll_container.addView(linearLayout);

            // Create the left and right spacers, don't worry about their dimensions, yet
            mLeftSpacer = new View(context);
            ll_container.addView(mLeftSpacer,0);
            mRightSpacer = new View(context);
            ll_container.addView(mRightSpacer);
        }catch(Exception e){
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
            Log.d(TAG, "Width"+ width);

            final ViewGroup.LayoutParams leftParams = mLeftSpacer.getLayoutParams();
            leftParams.width = width / 2;
            //leftParams.width = 0;
            mLeftSpacer.setLayoutParams(leftParams);

            final ViewGroup.LayoutParams rightParams = mRightSpacer.getLayoutParams();
            rightParams.width = width / 2;
            //rightParams.width = 0;
            mRightSpacer.setLayoutParams(rightParams);

        }
    }

    // do stuff with the scroll listener we created early to make our values usable.
}
