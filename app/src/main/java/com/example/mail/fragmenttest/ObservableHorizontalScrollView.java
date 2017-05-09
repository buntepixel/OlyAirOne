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


public class ObservableHorizontalScrollView extends HorizontalScrollView {

    private OnScrollChangedListener mOnScrollChangedListener;

    public interface OnScrollChangedListener{
        /**
         * Called when the scroll position of <code>view</code> changes.
         *
         * @param view The view whose scroll position changed.
         * @param l Current horizontal scroll origin.
         * @param t Current vertical scroll origin.
         */
        void onScrollChanged(ObservableHorizontalScrollView view,int l, int t);
    }
    public ObservableHorizontalScrollView(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public void setOnScrollChangedListener(OnScrollChangedListener l) {
        mOnScrollChangedListener = l;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t);
        }
    }
}
