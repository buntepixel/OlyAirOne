package com.example.mail.fragmenttest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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
    float scrollPosf;
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
         * @param scrollValueOld Previous horizontal scroll origin.
         * @param scrollBarWidth width of scroll bar.
         */
        void onScrollChanged(ObservableHorizontalScrollView view, int scrollValue, int scrollValueOld, int scrollBarWidth);

        void onTouchUpAction(ObservableHorizontalScrollView view, float scrollValue, int scrollBarWidth);
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    //todo: remove override
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Boolean myBool = super.onTouchEvent(ev);
        Log.d(TAG, "MotionEvent: " + ev.getAction());

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            touch = true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (mOnScrollChangedListener != null) {
                scrollPosf = ev.getX();
                Log.d(TAG, "ScrollPos: " + scrollPosf);
                int scrollBarWidth = this.computeHorizontalScrollRange();
                mOnScrollChangedListener.onTouchUpAction(this, scrollPosf, scrollBarWidth);
                touch = false;
            }
        }
        return myBool;
    }

    @Override
    protected void onScrollChanged(int scrollValue, int t, int oldl, int oldt) {
        super.onScrollChanged(scrollValue, t, oldl, oldt);
        if (Math.abs(oldl - scrollValue) <= 1 && !touch) {
            //Log.d(TAG, "scrollVal: " + scrollValue);
            if (mOnScrollChangedListener != null) {
                int scrollBarWidth = this.computeHorizontalScrollRange();
                mOnScrollChangedListener.onScrollChanged(this, scrollValue, oldl, scrollBarWidth);
            }
            scrollPos = scrollValue;
        }
    }
}
