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


    int scrollBarWidth;
    boolean setScroll = false;

    private OnScrollChangedListener mOnScrollChangedListener;

    public interface OnScrollChangedListener {
        /**
         * Called when the scroll position of <code>view</code> changes.
         *
         * @param view           The view whose scroll position changed.
         * @param scrollValue    Current horizontal scroll origin.
         * @param scrollBarWidth width of scroll bar.
         */
        void onScrollChanged(ObservableHorizontalScrollView view, int scrollValue, int scrollBarWidth);

    }

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }


    @Override
    protected void onScrollChanged(int scrollValue, int t, int oldl, int oldt) {
        Log.d(TAG, "ScrollChanged: " + scrollValue + "  oldVal: " + oldl + " diff: " + Math.abs(oldl - scrollValue)+ " setscroll: "+setScroll);

        if (!setScroll&& Math.abs(oldl - scrollValue) <= 1) {// && !touch
            if (mOnScrollChangedListener != null) {
                setScroll = true;//set it true so we don't get a scroll when snapping to a value;
                mOnScrollChangedListener.onScrollChanged(this, scrollValue, scrollBarWidth);
                Log.d(TAG, "ScrollChanged: " + scrollValue );
            }
        }
        super.onScrollChanged(scrollValue, t, oldl, oldt);
    }

  /*  @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            setScroll= false;//set back to false on next scroll
        }
        return super.onTouchEvent(ev);
    }
*/
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        scrollBarWidth = this.computeHorizontalScrollRange();
    }
}
