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


public class ObservableHorizontalScrollView extends HorizontalScrollView
{
    private static final String TAG = ObservableHorizontalScrollView.class.getSimpleName();

    int scrollPos;


    /**
     * Interface definition for a callback to be invoked with the scroll
     * position changes.
     */

    public interface OnScrollChangedListener{
        /**
         * Called when the scroll position of <code>view</code> changes.
         *
         * @param view The view whose scroll position changed.
         * @param scrollValue Current horizontal scroll origin.
         * @param t Current vertical scroll origin.
         */
        void onScrollChanged(ObservableHorizontalScrollView view,int scrollValue, int t,int scrollBarWidth);
        void onTouchUpAction(ObservableHorizontalScrollView view,int scrollValue,int scrollBarWidth);
    }
    private OnScrollChangedListener mOnScrollChangedListener;

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

  //todo: remove override
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Boolean myBool= super.onTouchEvent(ev);
        if(ev.getAction()==MotionEvent.ACTION_UP){

            if (mOnScrollChangedListener != null) {
                int scrollBarWidth = this.computeHorizontalScrollRange();

                mOnScrollChangedListener.onTouchUpAction(this,scrollPos,scrollBarWidth);
            }
        }
        return myBool;
    }

    @Override
    protected void onScrollChanged(int scrollValue, int t, int oldl, int oldt) {
        super.onScrollChanged(scrollValue, t, oldl, oldt);


        if(Math.abs(oldl - scrollValue)<=1){
            Log.d(TAG, "scrollVal: "+ scrollValue);
            if (mOnScrollChangedListener != null) {
                int scrollBarWidth = this.computeHorizontalScrollRange();
                mOnScrollChangedListener.onScrollChanged(this, scrollValue, t, scrollBarWidth);
            }
            scrollPos = scrollValue;
        }
    }
}
