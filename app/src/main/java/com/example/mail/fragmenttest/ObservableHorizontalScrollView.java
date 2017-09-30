package com.example.mail.fragmenttest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by mail on 13/10/2016.
 */
/**
 * A {@link HorizontalScrollView} with an {@link OnScrollChangedListener} interface
 * to notify listeners of scroll position changes.
 */


public class ObservableHorizontalScrollView extends HorizontalScrollView
{
    private static final String TAG = ObservableHorizontalScrollView.class.getSimpleName();


    /**
     * Interface definition for a callback to be invoked with the scroll
     * position changes.
     */

    public interface OnScrollChangedListener{
        /**
         * Called when the scroll position of <code>view</code> changes.
         *
         * @param view The view whose scroll position changed.
         * @param l Current horizontal scroll origin.
         * @param t Current vertical scroll origin.
         */
        void onScrollChanged(ObservableHorizontalScrollView view,int l, int t,int scrollBarWidth);
    }
    private OnScrollChangedListener mOnScrollChangedListener;

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

 
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            int scrollBarWidth = this.computeHorizontalScrollRange();
            mOnScrollChangedListener.onScrollChanged(this, l, t, scrollBarWidth);
        }
    }
}
