package com.example.mail.OlyAirONE;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;



public class ExposureCorrection extends View {
    private int mNbStrokes = 31;
    private int mStrokeGap = 10;
    private int mhighlightIdx = 15;
    Paint paint = new Paint();

    private static final String TAG = ExposureCorrection.class.getSimpleName();

    public ExposureCorrection(Context context) {
        super(context);
    }

    public ExposureCorrection(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExposureCorrection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = 40;
        //calculate the view width
        setMeasuredDimension((mNbStrokes * mStrokeGap), height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //set the color for the dot that you want to draw
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mStrokeGap / 3);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeJoin(Paint.Join.ROUND);


        //function to create dot
        createLine(canvas, paint);
        //createExposureContrVis(canvas, paint, 8);
    }

    private void createLine(Canvas canvas, Paint paint) {
        int width = getWidth();
        int height = getHeight();
        int startLine, endLine;
        int mLineCenter = 15;

        //strokes next to each other
        for (int i = 0; i < mNbStrokes; i++) {
            startLine = height / 3;
            endLine = startLine * 2;
            if ((i < mLineCenter && i >= mhighlightIdx) || (i > mLineCenter && i <= mhighlightIdx))
                paint.setColor(getResources().getColor(R.color.ExpCorr_HighliteLine));
            else
                paint.setColor(getResources().getColor(R.color.ExpCorr_subLine));
            if (i == mNbStrokes / 2) {
                paint.setColor(getResources().getColor(R.color.ExpCorr_HighliteLine));
                startLine = height;
                endLine = 0;
                //Log.d(TAG, String.format("i: %d myNbstrokes: %d", i, mNbStrokes));
            } else if (i % 3 == 0) {
                if ((i < mLineCenter && i >= mhighlightIdx) || (i > mLineCenter && i <= mhighlightIdx))
                    paint.setColor(getResources().getColor(R.color.ExpCorr_HighliteLine));
                else
                    paint.setColor(getResources().getColor(R.color.ExpCorr_Line));
                startLine = height / 4;
                endLine = startLine * 3;
            }


            //Log.d(TAG, String.format("width: %d height: %d i: %d ", width, height, i));
            //canvas.drawLine((i * (width / mNbStrokes)), 0, (i * (width / mNbStrokes)), height , paint);
            canvas.drawLine((i * (mStrokeGap)), startLine, (i * (mStrokeGap)), endLine, paint);


        }
    }

    public void SetLineParams(int endIndex) {
        Log.d(TAG, "seting Param to: " + endIndex);
        mhighlightIdx = endIndex;
        invalidate();
    }

    private void createExposureContrVis(Canvas canvas, Paint paint, Integer inputVal) {
        int width = getWidth();
        int height = getHeight();
        int loop = inputVal;
        if (inputVal < 0) {
            loop = inputVal * (-1);
        }
        for (int i = 0; i < loop; i++) {
            paint.setARGB(255, 70, 70, 70);
            if (i % 3 == 0) {
                paint.setARGB(255, 200, 200, 200);
            }
            if (inputVal < 0) {
                canvas.drawLine(width / 2 - (i * (width / mNbStrokes)), 0, width / 2 - (i * (width / mNbStrokes)), height / 2, paint);
            } else {
                canvas.drawLine(width / 2 + (i * (width / mNbStrokes)), 0, width / 2 + (i * (width / mNbStrokes)), height / 2, paint);
            }

        }
    }

}
