/*
 * Copyright (c) Olympus Imaging Corporation. All rights reserved.
 * Olympus Imaging Corp. licenses this software to you under EULA_OlympusCameraKit_ForDevelopers.pdf.
 */

package com.example.mail.fragmenttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView.ScaleType;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.olympus.camerakit.OLYCamera;

public class CameraLiveImageView extends View {
	public static enum FocusFrameStatus {
		Running,
		Focused,
		Failed,
	}

	private ScaleType imageScaleType;
	private Bitmap imageBitmap;
	private int imageRotationDegrees;
	
	private boolean showingFocusFrame;
	private FocusFrameStatus focusFrameStatus;
	private RectF focusFrameRect;
	private Timer focusFrameHideTimer;

	public CameraLiveImageView(Context context) {
		super(context);
		initComponent();
	}

	public CameraLiveImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initComponent();
	}

	public CameraLiveImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initComponent();
	}
	
	private void initComponent() {
		imageScaleType = ScaleType.FIT_CENTER;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		imageBitmap = null;
		if (focusFrameHideTimer != null) {
			focusFrameHideTimer.cancel();
			focusFrameHideTimer = null;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawCanvas(canvas);
	}
	
	public float getIntrinsicContentSizeWidth() {
		if (imageBitmap == null) {
			return 0;
		}
		return imageBitmap.getWidth();
	}

	public float getIntrinsicContentSizeHeight() {
		if (imageBitmap == null) {
			return 0;
		}
		return imageBitmap.getHeight();
	}
	
	/**
	 * Sets a image to view.
	 * 
	 * @param data A image of live-view.
	 * @param metadata A metadata of the image.
	 */
	public void setImageData(byte[] data, Map<String, Object> metadata) {
		Bitmap bitmap = null;
		int rotationDegrees = 0;
		
		if (data != null && metadata != null) {
			// Create a bitmap.
			try {
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return;
			}
			// Acquire a rotation degree of image.
			int orientation = ExifInterface.ORIENTATION_UNDEFINED;
			if (metadata != null && metadata.containsKey("Orientation")) {
				orientation = Integer.parseInt((String)metadata.get("Orientation"));
			}
			switch (orientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				rotationDegrees = 0;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotationDegrees = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotationDegrees = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotationDegrees = 270;
				break;
			default:
				rotationDegrees = 0;
				break;
			}
			imageBitmap = bitmap;
			imageRotationDegrees = rotationDegrees;
		}

		refreshCanvas();
	}

	/**
	 * Returns a point which is detected by a motion event.
	 * 
	 * @param event A motion event.
	 * @return A point in the view finder. if a point is equal to null, the point is out of the view finder.
	 */
	public PointF getPointWithEvent(MotionEvent event) {
		if (event == null || imageBitmap == null) {
			return null;
		}
		
		PointF pointOnView = new PointF(event.getX(), event.getY());
		PointF pointOnImage = convertPointFromViewArea(pointOnView);
		float imageWidth;
		float imageHeight;
		if (imageRotationDegrees == 0 || imageRotationDegrees == 180) {
			imageWidth = imageBitmap.getWidth();
			imageHeight = imageBitmap.getHeight();
		} else {
			imageWidth = imageBitmap.getHeight();
			imageHeight = imageBitmap.getWidth();
		}
		PointF pointOnViewfinder = OLYCamera.convertPointOnLiveImageIntoViewfinder(pointOnImage, imageWidth, imageHeight, imageRotationDegrees);
		if (pointOnViewfinder == null) {
			return null;
		}
		
		return pointOnViewfinder;
	}
	
	/**
	 * Returns whether a image area contains a specified point.
	 *
	 * @param point The point to examine.
	 * @return true if the image is not null or empty and the point is located within the rectangle; otherwise, false.
	 */
	public boolean isContainsPoint(PointF point) {
		if (point == null) {
			return false;
		}
		return (new RectF(0, 0, 1, 1)).contains(point.x, point.y);
	}
	
	/**
	 * Hides the forcus frame.
	 */
	public void hideFocusFrame() {
		if (focusFrameHideTimer != null) {
			focusFrameHideTimer.cancel();
			focusFrameHideTimer = null;
		}
		
		showingFocusFrame = false;

		refreshCanvas();
	}
	
	/**
	 * Shows the focus frame.
	 *
	 * @param rect A rectangle of the focus frame on view area.
	 * @param status A status of the focus frame.
	 */
	public void showFocusFrame(RectF rect, FocusFrameStatus status) {
		showFocusFrame(rect, status, 0);
	}

	/**
	 * Shows the focus frame.
	 *
	 * @param rect A rectangle of the focus frame on view area.
	 * @param status A status of the focus frame.
	 * @param duration A duration of the focus frame showing.
	 */
	public void showFocusFrame(RectF rect, FocusFrameStatus status, double duration) {
		if (focusFrameHideTimer != null) {
			focusFrameHideTimer.cancel();
			focusFrameHideTimer = null;
		}
		
		showingFocusFrame = true;
		focusFrameStatus = status;
		focusFrameRect = rect;

		refreshCanvas();

		if (duration > 0) {
			focusFrameHideTimer = new Timer();
			focusFrameHideTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					hideFocusFrame();
				}
			}, (long)(duration * 1000));
		}
	}

	private void refreshCanvas() {
		if(Looper.getMainLooper().getThread() == Thread.currentThread()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}
	
	private void drawCanvas(Canvas canvas) {
		// Clears the canvas.
		canvas.drawARGB(255, 0, 0, 0);
	
		if (imageBitmap != null) {
			// Rotates the image.
			int centerX = canvas.getWidth() / 2;
			int centerY = canvas.getHeight() / 2;
			canvas.rotate(imageRotationDegrees, centerX, centerY);
			
			//  Calculate the viewport of bitmap.
			if (imageScaleType == ScaleType.FIT_CENTER) {
				RectF viewRect = null;
				final int srcWidth;
				final int srcHeight;
				if ((imageRotationDegrees == 0) || (imageRotationDegrees == 180)) {
					srcWidth = (int)imageBitmap.getWidth();
					srcHeight = (int)imageBitmap.getHeight();
				} else {
					// Replaces width and height.
					srcWidth = (int)imageBitmap.getHeight();
					srcHeight = (int)imageBitmap.getWidth();
				}
				final int dstWidth;
				final int dstHeight;
				{	
					int maxWidth = canvas.getWidth();
					int maxHeight = canvas.getHeight();
					float widthRatio = maxWidth / (float)srcWidth;
					float heightRatio = maxHeight / (float)srcHeight;
					float smallRatio = Math.min(widthRatio, heightRatio);
					
					if (widthRatio < heightRatio) {
						// Fits to maxWidth with keeping aspect ratio.
						dstWidth = maxWidth;
						dstHeight = (int)(smallRatio * srcHeight);
					} else {
						// Fits to maxHeight with keeping aspect ratio.
						dstHeight = maxHeight;
						dstWidth = (int)(smallRatio * srcWidth); 
					}
				}
				final float halfWidth = dstWidth * 0.5f;
				final float halfHeight = dstHeight * 0.5f;
				if ((imageRotationDegrees == 0) || (imageRotationDegrees == 180)) {
					viewRect = new RectF(
							centerX - halfWidth,
							centerY - halfHeight,
							centerX - halfWidth + dstWidth,
							centerY - halfHeight + dstHeight);
				} else {
					// Replaces the width and height.
					viewRect = new RectF(
							centerX - halfHeight,
							centerY - halfWidth,
							centerX - halfHeight + dstHeight,
							centerY - halfWidth + dstWidth);
				}
				
				// Draws the bitmap.
				Rect imageRect = new Rect(0, 0, (int)imageBitmap.getWidth(), (int)imageBitmap.getHeight());
				canvas.drawBitmap(imageBitmap, imageRect, viewRect, null);

			} else {
				// Sorry, other scale types are not supported.
			}

			// Cancels rotation of the canvas.
			canvas.rotate(-imageRotationDegrees, centerX, centerY);
		}

		if (imageBitmap != null && focusFrameRect != null && showingFocusFrame) {
			//  Calculate the rectangle of focus.
			float imageWidth;
			float imageHeight;
			if (imageRotationDegrees == 0 || imageRotationDegrees == 180) {
				imageWidth = imageBitmap.getWidth();
				imageHeight = imageBitmap.getHeight();
			} else {
				imageWidth = imageBitmap.getHeight();
				imageHeight = imageBitmap.getWidth();
			}
			RectF focusRectOnImage = OLYCamera.convertRectOnViewfinderIntoLiveImage(focusFrameRect, imageWidth, imageHeight, imageRotationDegrees);
			RectF focusRectOnView = convertRectFromImageArea(focusRectOnImage);

			// Draw a rectangle to the canvas.
			Paint focusFramePaint = new Paint();
			focusFramePaint.setStyle(Paint.Style.STROKE);
			switch (focusFrameStatus) {
			case Running:
				focusFramePaint.setColor(Color.WHITE);
				break;
			case Focused:
				focusFramePaint.setColor(Color.GREEN);
				break;
			case Failed:
				focusFramePaint.setColor(Color.RED);
				break;
			}
			float focusFrameStrokeWidth = 2.0f;
	        DisplayMetrics dm = getResources().getDisplayMetrics() ;
	        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, focusFrameStrokeWidth, dm);
			focusFramePaint.setStrokeWidth(strokeWidth);
			canvas.drawRect(focusRectOnView, focusFramePaint);
		}
	}

	/**
	 * Converts a point on image area to a point on view area.
	 *
	 * @param point A point on image area. (e.g. a live preview image)
	 * @return A point on view area. (e.g. a touch panel view)
	 */
	private PointF convertPointFromImageArea(PointF point) {
		if (imageBitmap == null) {
			return new PointF();
		}

		float viewPointX = point.x;
		float viewPointY = point.y;
		float imageSizeWidth;
		float imageSizeHeight;
		if (imageRotationDegrees == 0 || imageRotationDegrees == 180) {
			imageSizeWidth = imageBitmap.getWidth();
			imageSizeHeight = imageBitmap.getHeight();
		} else {
			imageSizeWidth = imageBitmap.getHeight();
			imageSizeHeight = imageBitmap.getWidth();
		}
		float viewSizeWidth = this.getWidth();
		float viewSizeHeight = this.getHeight(); 
		float ratioX = viewSizeWidth / imageSizeWidth;
		float ratioY = viewSizeHeight / imageSizeHeight;
		float scale = 0.0f;

		switch (imageScaleType) {
			case FIT_XY:
				viewPointX *= ratioX;
				viewPointY *= ratioY;
				break;
			case FIT_CENTER:	// go to next label.
			case CENTER_INSIDE:
				scale = Math.min(ratioX, ratioY);
				viewPointX *= scale;
				viewPointY *= scale;
				viewPointX += (viewSizeWidth  - imageSizeWidth  * scale) / 2.0f;
				viewPointY += (viewSizeHeight - imageSizeHeight * scale) / 2.0f;
				break;
			case CENTER_CROP:
				scale = Math.max(ratioX, ratioY);
				viewPointX *= scale;
				viewPointY *= scale;
				viewPointX += (viewSizeWidth  - imageSizeWidth  * scale) / 2.0f;
				viewPointY += (viewSizeHeight - imageSizeHeight * scale) / 2.0f;
				break;
			case CENTER:
				viewPointX += viewSizeWidth / 2.0  - imageSizeWidth  / 2.0f;
				viewPointY += viewSizeHeight / 2.0 - imageSizeHeight / 2.0f;
				break;
			default:
				break;
		}
		
	    return new PointF(viewPointX, viewPointY);
	}

	/**
	 * Converts a point on view area to a point on image area.
	 *
	 * @param point A point on view area. (e.g. a touch panel view)
	 * @param imageAreaRect A rectangle of image displayed on the view.
	 * @return A point on image area. (e.g. a live preview image)
	 */
	private PointF convertPointFromViewArea(PointF point) {
		if (imageBitmap == null) {
			return new PointF();
		}
		
		float imagePointX = point.x;
		float imagePointY = point.y;
		float imageSizeWidth;
		float imageSizeHeight;
		if (imageRotationDegrees == 0 || imageRotationDegrees == 180) {
			imageSizeWidth = imageBitmap.getWidth();
			imageSizeHeight = imageBitmap.getHeight();
		} else {
			imageSizeWidth = imageBitmap.getHeight();
			imageSizeHeight = imageBitmap.getWidth();
		}
		float viewSizeWidth = this.getWidth();
		float viewSizeHeight = this.getHeight();
		float ratioX = viewSizeWidth / imageSizeWidth;
		float ratioY = viewSizeHeight / imageSizeHeight;
		float scale = 0.0f;
	    
	    switch (imageScaleType) {
	        case FIT_XY:
	            imagePointX /= ratioX;
	            imagePointY /= ratioY;
	            break;
			case FIT_CENTER:	// go to next label.
			case CENTER_INSIDE:
				scale = Math.min(ratioX, ratioY);
	            imagePointX -= (viewSizeWidth  - imageSizeWidth  * scale) / 2.0f;
	            imagePointY -= (viewSizeHeight - imageSizeHeight * scale) / 2.0f;
	            imagePointX /= scale;
	            imagePointY /= scale;
	            break;
	        case CENTER_CROP:
				scale = Math.max(ratioX, ratioY);
	            imagePointX -= (viewSizeWidth  - imageSizeWidth  * scale) / 2.0f;
	            imagePointY -= (viewSizeHeight - imageSizeHeight * scale) / 2.0f;
	            imagePointX /= scale;
	            imagePointY /= scale;
	            break;
	        case CENTER:
	            imagePointX -= (viewSizeWidth - imageSizeWidth)  / 2.0f;
	            imagePointY -= (viewSizeHeight - imageSizeHeight) / 2.0f;
	            break;
	        default:
	            break;
	    }
	    
	    return new PointF(imagePointX, imagePointY);
	}

	/**
	 * Converts a rectangle on image area to a rectangle on view area.
	 *
	 * @param rect A rectangle on image area. (e.g. a live preview image)
	 * @param imageAreaRect A rectangle of image displayed on the view.
	 * @return A rectangle on view area. (e.g. a touch panel view)
	 */
	private RectF convertRectFromImageArea(RectF rect) {
		if (imageBitmap == null) {
			return new RectF();
		}
		
		PointF imageTopLeft =  new PointF(rect.left, rect.top);
		PointF imageBottomRight = new PointF(rect.right, rect.bottom);

		PointF viewTopLeft = convertPointFromImageArea(imageTopLeft);
		PointF viewBottomRight = convertPointFromImageArea(imageBottomRight);

		RectF viewRect = new RectF(viewTopLeft.x, viewTopLeft.y, viewBottomRight.x, viewBottomRight.y);
	    
		return viewRect;
	}

	/**
	 * Converts a rectangle on view area to a rectangle on image area.
	 *
	 * @param rect A rectangle on view area. (e.g. a touch panel view)
	 * @param imageAreaRect A rectangle of image displayed on the view.
	 * @return A rectangle on image area. (e.g. a live preview image)
	 */
	@SuppressWarnings("unused")
	private RectF convertRectFromViewArea(RectF rect) {
		if (imageBitmap == null) {
			return new RectF();
		}
		
		PointF viewTopLeft = new PointF(rect.left, rect.top);
		PointF viewBottomRight = new PointF(rect.right, rect.bottom);

		PointF imageTopLeft = convertPointFromViewArea(viewTopLeft);
		PointF imageBottomRight = convertPointFromViewArea(viewBottomRight);

		RectF imageRect = new RectF(imageTopLeft.x, imageTopLeft.y, imageBottomRight.x, imageBottomRight.y);
	    
	    return imageRect;
	}
}
