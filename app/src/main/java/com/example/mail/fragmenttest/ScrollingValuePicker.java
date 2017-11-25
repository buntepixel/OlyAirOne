package com.example.mail.fragmenttest;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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

public class ScrollingValuePicker extends FrameLayout  {
    private static final String TAG = ScrollingValuePicker.class.getSimpleName();
    private View mLeftSpacer;
    private View mRightSpacer;
    private LinearLayout mCenterContainer;
    private int mCenterContainerWidth;
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

    public ScrollingValuePicker(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // Create our internal scroll view
        obsScrollView = new ObservableHorizontalScrollView(context, attrs);
        obsScrollView.setId(View.generateViewId());
        obsScrollView.setHorizontalScrollBarEnabled(false);
        //addView(obsScrollView);
        obsScrollView.setOnScrollChangedListener(new ObservableHorizontalScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ObservableHorizontalScrollView view, int scrollValue, int scrollBarWidth) {
                try {
                    Log.d(TAG, "::::::::::onScrollChanged:::::::::::::::::" + scrollValue);
                    currContentIndex = getCurrIndex(scrollValue);
                    Log.d(TAG, "index" + currContentIndex);
                    mValueInteractionListener.onScrollEnd(currContentIndex);

                } catch (Exception ex) {
                    String stackTrace = Log.getStackTraceString(ex);
                    Log.d(TAG, stackTrace);
                }
            }

            @Override
            public void onTouchUpAction(ObservableHorizontalScrollView view, int scrollValue, int scrollBarWidth) {
                try {
                    //onScrollChanged(view, scrollValue, scrollBarWidth);

                } catch (Exception ex) {
                    String stackTrace = Log.getStackTraceString(ex);
                    Log.d(TAG, stackTrace);
                }
            }
        });
    }

    private int getCurrIndex(int scrollValue) {
        // scrollvalue from 0-0,99999999
        float decScrollVal = (float) scrollValue / (mCenterContainerWidth + 1);//add 1 so it never gets 1 and breaks the index
        int currIndex = Math.round((decScrollVal * content.length));
        //Log.d(TAG, "containerWidth: " + mCenterContainerWidth + "  ScrollValue" + scrollValue + " decScrollValue: " + decScrollVal);
        //make sure index doesn't get out of range on overscroll
        currIndex = Math.max(0, currIndex);
        currIndex = Math.min(content.length - 1, currIndex);
        return currIndex;
    }

    private int getScrollPos(int index) {
        int sum = 0;
        //add all width of subViews together till we are at the current index
        for (int i = 0; i < contentWidthList.size(); i++) {
            //Log.d(TAG, "index: "+ index +" sum: "+ sum+ " i: "+i + " currWidth: "+ contentWidthList.get(i));
            sum = sum + contentWidthList.get(i);
            if (i == index) {
                sum -= contentWidthList.get(i) / 2;
                break;
            }
        }
        return sum;
    }

    public void initValuePicker(Context context, LinearLayout ll_wraperLayout, String[] myStringBarValues) {
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
            //if icons
            if (whiteBalanceIconList.containsKey(myStringBarValues[0])) {
                for (String i : myStringBarValues)
                    Log.d(TAG, "WbValues: " + i);
                AddImageViewContent(context, myStringBarValues, ll_contentContainer);

            } else
                AddTextViewContent(context, myStringBarValues, ll_contentContainer);
            ll_wraperLayout.addView(obsScrollView); //ll_contentContainer.addView(linearLayout);

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

    public void snapBarToValue(int index) {
        int tmp = getScrollPos(index);
        //Log.d(TAG,"snapBArToValue: "+ tmp);
        obsScrollView.smoothScrollTo(tmp, 0);
        setSelScrollBarValSelected(index);
    }
    public void setBarToValue(int index){
        int tmp = getScrollPos(index);
        obsScrollView.setScrollX(tmp);
        setSelScrollBarValSelected(index);
    }


    private void setSelScrollBarValSelected(int index) {
        //index+1 since we have the spacers
        int childCount = mCenterContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (i > 0 && i < childCount - 1) {
                if (i == index + 1) {
                    mCenterContainer.getChildAt(i).setSelected(true);
                } else
                    mCenterContainer.getChildAt(i).setSelected(false);
            }
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

            final int childCount = mCenterContainer.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View v = mCenterContainer.getChildAt(i);
                int myWidth = v.getWidth();
                if (i > 0 && i < childCount - 1) {
                    contentWidthList.add(myWidth);
                    //Log.d(TAG, "Array: " + v.getWidth());
                }
            }
        }
    }

    // do stuff with the scroll listener we created early to make our values usable.
    private void AddTextViewContent(Context context, String[] stringArr, LinearLayout linearLayout) {
        //Adding Textview
        try {
            for (int i = 0; i < stringArr.length; i++) {
                TextView textView = new TextView(context);
                textView.setId(View.generateViewId());
                //Log.d(TAG, "textView " + i + " id: " + textView.getId());
                //Log.d(TAG, "mystring:  " + i);
                if (camera != null) {
                    //Log.d(TAG, "CurrentValue::::::" + camera.getCameraPropertyValueTitle(i));
                    textView.setText(camera.getCameraPropertyValueTitle(stringArr[i]));
                } else {
                    Log.d(TAG, "Cam Seems to be null");
                    textView.setText(i);
                }
                //textView.setTextSize(40);
                textView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.SrlBar_Bg));

                textView.setTextColor(getResources().getColorStateList(R.color.button_text_states));
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
            Log.d(TAG, "StringArrLength: " + stringArr.length + "Iconlist" + myValues.size());

            for (int i = 0; i < stringArr.length; i++) {
                ImageView imageView = new ImageView(getContext());
                int viewId = whiteBalanceIconList.get(stringArr[i]);
                imageView.setImageResource(viewId);

                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                imageView.setPaddingRelative(txtPadding / 2, 0, txtPadding / 2, 0);

                linearLayout.addView(imageView);
                Log.d(TAG, "ChildCount Nr:  " + i + " count:" + linearLayout.getChildCount());
            }
        } catch (Error e) {
            String stackTrace = Log.getStackTraceString(e);
            System.err.println(TAG + e.getMessage());
            Log.d(TAG, stackTrace);
        }
    }
}
