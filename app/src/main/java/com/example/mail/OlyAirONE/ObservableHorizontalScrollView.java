package com.example.mail.OlyAirONE;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;

/**
 * Created by mail on 13/10/2016.
 */

/**
 * A {@link HorizontalScrollView} with an {@link OnScrollChangedListener} interface
 * to notify listeners of scroll position changes.
 */


public class ObservableHorizontalScrollView extends HorizontalScrollView {
    private static final String TAG = ObservableHorizontalScrollView.class.getSimpleName();

    int scrollPos;
    int scrollBarWidth;
    boolean touch = false;


    /**
     * Interface definition for a callback to be invoked with the scroll
     * position changes.
     */

    public interface OnScrollChangedListener {
        /**
         * Called when the scroll position of <code>view</code> changes.
         *
         * @param view           The view whose scroll position changed.
         * @param scrollValue    Current horizontal scroll origin.
         * @param scrollBarWidth width of scroll bar.
         */
        void onScrollChanged(ObservableHorizontalScrollView view, int scrollValue,  int scrollBarWidth);

        void onTouchUpAction(ObservableHorizontalScrollView view, int scrollValue, int scrollBarWidth);
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }


    @Override
    protected void onScrollChanged(int scrollValue, int t, int oldl, int oldt) {
        super.onScrollChanged(scrollValue, t, oldl, oldt);
        Log.d(TAG, "ScrollChanged: " + scrollValue);
        scrollPos = scrollValue;
        if (Math.abs(oldl - scrollValue) <= 1 ) {// && !touch
            //  Log.d(TAG, "ScrollChanged = scrollVal: " + scrollValue);
            if (mOnScrollChangedListener != null) {
                Log.d(TAG, "onScrollChanged: " + scrollValue);
                mOnScrollChangedListener.onScrollChanged(this, scrollValue,  scrollBarWidth);
            }
            scrollPos = scrollValue;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        scrollBarWidth = this.computeHorizontalScrollRange();
    }
}
