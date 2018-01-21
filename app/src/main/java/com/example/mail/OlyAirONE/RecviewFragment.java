/*
 * Copyright (c) Olympus Imaging Corporation. All rights reserved.
 * Olympus Imaging Corp. licenses this software to you under EULA_OlympusCameraKit_ForDevelopers.pdf.
 */

package com.example.mail.OlyAirONE;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraRecordingSupportsListener;

public class RecviewFragment extends android.support.v4.app.Fragment implements OLYCameraRecordingSupportsListener, View.OnTouchListener {
	
	private ImageView imageView;
	
	private OLYCamera camera;
	private byte[] data;
	private Map<String, Object> metadata;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recview, container, false);
		
		imageView = view.findViewById(R.id.iv_capturedImage);
		
		view.findViewById(R.id.ib_backToCam).setOnTouchListener(this);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		camera.setRecordingSupportsListener(this);
		imageView.setImageBitmap(createRotatedBitmap(data, metadata));
	}

	@Override
	public void onPause() {
		super.onPause();
		camera.setRecordingSupportsListener(null);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			getFragmentManager().popBackStack();
			return true;
		}
		return false;
	}
	
	public void setCamera(OLYCamera camera) {
		this.camera = camera;
	}
	
	public void setImageData(byte[] data, Map<String, Object> metadata) {
		this.data = data;
		this.metadata = metadata;
	}
	
	private Bitmap createRotatedBitmap(byte[] data, Map<String, Object> metadata) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		if (bitmap == null) {
			return null;
		}
		
		int degrees = getRotationDegrees(data, metadata);
		if (degrees != 0) {
			Matrix m = new Matrix();
			m.postRotate(degrees);
			try {
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		
		return bitmap;
	}
	
	private int getRotationDegrees(byte[] data, Map<String, Object> metadata) {
		int degrees = 0;
		int orientation = ExifInterface.ORIENTATION_UNDEFINED;
		
		if (metadata != null && metadata.containsKey("Orientation")) {
			orientation = Integer.parseInt((String)metadata.get("Orientation"));
		} else {
			// Gets image orientation to display a picture.
			try {
				File tempFile = File.createTempFile("temp", null);
				{
					FileOutputStream outStream = new FileOutputStream(tempFile.getAbsolutePath());
					outStream.write(data);
					outStream.close();
				}
				
				ExifInterface exifInterface = new ExifInterface(tempFile.getAbsolutePath());
				orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

				// noinspection ResultOfMethodCallIgnored
				tempFile.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		switch (orientation) {
		case ExifInterface.ORIENTATION_NORMAL:
			degrees = 0;
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			degrees = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			degrees = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			degrees = 270;
			break;
		default:
			break;
		}
		
		return degrees;
	}

	@Override
	public void onReadyToReceiveCapturedImagePreview(OLYCamera camera) {
	}

	@Override
	public void onReceiveCapturedImagePreview(OLYCamera camera, byte[] data, Map<String, Object> metadata) {
		this.data = data;
		this.metadata = metadata;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				imageView.setImageBitmap(createRotatedBitmap(RecviewFragment.this.data, RecviewFragment.this.metadata));
			}
		});
	}

	@Override
	public void onFailToReceiveCapturedImage(OLYCamera camera, Exception e) {
	}

	@Override
	public void onReadyToReceiveCapturedImage(OLYCamera camera) {
	}

	@Override
	public void onReceiveCapturedImage(OLYCamera camera, byte[] data, Map<String, Object> metadata) {
	}

	@Override
	public void onFailToReceiveCapturedImagePreview(OLYCamera camera, Exception e) {
	}

	@Override
	public void onStopDrivingZoomLens(OLYCamera camera) {
	}

	private void runOnUiThread(Runnable action) {
		Activity activity = getActivity();
		if (activity == null) {
			return;
		}
		
		activity.runOnUiThread(action);
	}
}
