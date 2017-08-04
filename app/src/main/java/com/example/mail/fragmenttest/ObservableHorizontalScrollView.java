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
    private int stateToSave;

    private int stuff; // stuff
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

    //// TODO: 01/08/2017 mabe remove
//    @Override
//    public Parcelable onSaveInstanceState() {
//        //begin boilerplate code that allows parent classes to save state
//        Parcelable superState = super.onSaveInstanceState();
//
//        SavedState ss = new SavedState(superState);
//        //end
//
//        ss.stateToSave = this.stateToSave;
//
//        return ss;
//    }

//    @Override
//    public void onRestoreInstanceState(Parcelable state) {
//        //begin boilerplate code so parent classes can restore state
//        if(!(state instanceof SavedState)) {
//            super.onRestoreInstanceState(state);
//            return;
//        }
//
//        SavedState ss = (SavedState)state;
//        super.onRestoreInstanceState(ss.getSuperState());
//        //end
//
//        this.stateToSave = ss.stateToSave;
//    }

//    static class SavedState extends BaseSavedState {
//        int stateToSave;
//
//        SavedState(Parcelable superState) {
//            super(superState);
//        }
//
//        private SavedState(Parcel in) {
//            super(in);
//            this.stateToSave = in.readInt();
//        }
//
//        @Override
//        public void writeToParcel(Parcel out, int flags) {
//            super.writeToParcel(out, flags);
//            out.writeInt(this.stateToSave);
//        }
//
//        //required field that makes Parcelables from a Parcel
//        public static final Parcelable.Creator<SavedState> CREATOR =
//                new Parcelable.Creator<SavedState>() {
//                    public SavedState createFromParcel(Parcel in) {
//                        return new SavedState(in);
//                    }
//                    public SavedState[] newArray(int size) {
//                        return new SavedState[size];
//                    }
//                };
//    }

}
