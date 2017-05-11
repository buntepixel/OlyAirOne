package com.example.mail.fragmenttest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by mail on 21/10/2016.
 */

public class ExposureCorrection extends View {
    private int mNbStrokes = 19;
    private int mStrokeGap = 15;

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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Animation called when attaching to the window, i.e to your screen
        //startAnimation();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = 20;
        //calculate the view width
        int calculatedWidth = (mNbStrokes * mStrokeGap);

        int width = calculatedWidth;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        //set the color for the dot that you want to draw
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mStrokeGap / 4);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeJoin(Paint.Join.ROUND);


        //function to create dot
        createLine(canvas, paint);
        //createExposureContrVis(canvas, paint, 8);
    }

    private void createLine(Canvas canvas, Paint paint) {
        int width = getWidth();
        int height = getHeight();
        int  startLine, endLine;
        //strokes next to each other
        for (int i = 0; i < mNbStrokes; i++) {
            startLine = height / 3;
            endLine = startLine*2;
            paint.setARGB(255, 90, 90, 90);
            if (i == mNbStrokes / 2) {
                paint.setARGB(255, 70, 255, 50);
                startLine = height ;
                endLine = 0;
                Log.d(TAG, String.format("i: %d myNbstrokes: %d", i, mNbStrokes));
            } else if (i % 3 == 0) {
                paint.setARGB(255, 200, 200, 200);
                startLine = height /4;
                endLine = startLine*3;

            }


            //Log.d(TAG, String.format("width: %d height: %d i: %d ", width, height, i));
            //canvas.drawLine((i * (width / mNbStrokes)), 0, (i * (width / mNbStrokes)), height , paint);
            canvas.drawLine((i * (mStrokeGap)), startLine, (i * (mStrokeGap)), endLine , paint);


        }
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
