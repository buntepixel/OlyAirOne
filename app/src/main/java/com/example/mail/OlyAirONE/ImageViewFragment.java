/*
 * Copyright (c) Olympus Imaging Corporation. All rights reserved.
 * Olympus Imaging Corp. licenses this software to you under EULA_OlympusCameraKit_ForDevelopers.pdf.
 */

package com.example.mail.OlyAirONE;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCamera.ProgressEvent;
import jp.co.olympus.camerakit.OLYCameraFileInfo;

public class ImageViewFragment extends android.support.v4.app.Fragment {
	
	private ImageView imageView;

	private OLYCamera camera;
	private List<OLYCameraFileInfo> contentList;
	private int contentIndex;

	public void setCamera(OLYCamera camera) {
		this.camera = camera;
	}
	
	public void setContentList(List<OLYCameraFileInfo> contentList) {
		this.contentList = contentList;
	}
	
	public  void setContentIndex(int contentIndex) {
		this.contentIndex = contentIndex;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_image_view, container, false);
		
		imageView = (ImageView)view.findViewById(R.id.imageView1);
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		OLYCameraFileInfo file = contentList.get(contentIndex);
		String path = file.getDirectoryPath() + "/" + file.getFilename();
		ActionBar bar = getActivity().getActionBar();
		if (bar != null) {
			bar.setTitle(path);
		}

		//inflater.inflate(R.menu.image_view, menu);
		MenuItem downloadMenuItem = menu.findItem(R.id.action_download);
       	
		String lowerCasePath = path.toLowerCase();
       	if (lowerCasePath.endsWith(".jpg")) {
			downloadMenuItem.setEnabled(true);
       	} else {
			downloadMenuItem.setEnabled(false);
       	}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean doDownload = false;
		float downloadSize = 0;

		if (item.getItemId() == R.id.action_download_original_size) {
			downloadSize = OLYCamera.IMAGE_RESIZE_NONE;
			doDownload = true;
		} else if (item.getItemId() == R.id.action_download_2048x1536) {
			downloadSize = OLYCamera.IMAGE_RESIZE_2048;
			doDownload = true;
		} else if (item.getItemId() == R.id.action_download_1920x1440) {
			downloadSize = OLYCamera.IMAGE_RESIZE_1920;
			doDownload = true;
		} else if (item.getItemId() == R.id.action_download_1600x1200) {
			downloadSize = OLYCamera.IMAGE_RESIZE_1600;
			doDownload = true;
		} else if (item.getItemId() == R.id.action_download_1024x768) {
			downloadSize = OLYCamera.IMAGE_RESIZE_1024;
			doDownload = true;
		}
		
		if (doDownload) {
			Calendar calendar = Calendar.getInstance();
			String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(calendar.getTime()) + ".jpg";
			
			/*Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment1);
			if (ImageViewFragment.class.isAssignableFrom(fragment.getClass())) {
				ImageViewFragment imageViewFragment = (ImageViewFragment)fragment;
				imageViewFragment.saveImage(filename, downloadSize);
				return true;
			}*/
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// Download the image.
		OLYCameraFileInfo file = contentList.get(contentIndex);
		String path = file.getDirectoryPath() + "/" + file.getFilename();
		camera.downloadContentScreennail(path, new OLYCamera.DownloadImageCallback() {
			@Override
			public void onProgress(ProgressEvent e) {
			}
			
			@Override
			public void onCompleted(final byte[] data, final Map<String, Object> metadata) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						imageView.setImageBitmap(createRotatedBitmap(data, metadata));
					}
				});
			}
			
			@Override
			public void onErrorOccurred(Exception e) {
				final String message = e.getMessage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						presentMessage("Load failed", message);
					}
				});
			}
		});

	}
	
	private void saveImage(final String filename, float downloadSize) {
		// Download the image.
		OLYCameraFileInfo file = contentList.get(contentIndex);
		String path = file.getDirectoryPath() + "/" + file.getFilename();
		camera.downloadImage(path, downloadSize, new OLYCamera.DownloadImageCallback() {
			@Override
			public void onProgress(ProgressEvent e) {
			}
			
			@Override
			public void onCompleted(final byte[] data, Map<String, Object> metadata) {
				final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/ImageViewerSample/";
				String filepath = new File(directoryPath, filename).getPath();
				
				// Saves the image.
				try {
					final File directory = new File(directoryPath);
					if (!directory.exists()) {
						// noinspection ResultOfMethodCallIgnored
						directory.mkdirs();
					}
					
					FileOutputStream outputStream = new FileOutputStream(filepath);
					outputStream.write(data);
					outputStream.close();
				} catch (IOException e) {
					final String message = e.getMessage();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							presentMessage("Save failed", message);
						}
					});
					return;
				}
				
				// Updates the gallery.
				try {
					long now = System.currentTimeMillis();
					ContentValues values = new ContentValues();
					ContentResolver resolver = getActivity().getContentResolver();
					values.put(Images.Media.MIME_TYPE, "image/jpeg");
					values.put(Images.Media.DATA, filepath);
					values.put(Images.Media.DATE_ADDED, now);
					values.put(Images.Media.DATE_TAKEN, now);
					values.put(Images.Media.DATE_MODIFIED, now);
					values.put(Images.Media.ORIENTATION, getRotationDegrees(data, metadata));
					resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getActivity(), "Saved " + filename, Toast.LENGTH_SHORT).show();
						}
					});
				} catch (Exception e) {
					final String message = e.getMessage();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							presentMessage("Save failed", message);
						}
					});
				}
			}
			
			@Override
			public void onErrorOccurred(Exception e) {
				final String message = e.getMessage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						presentMessage("Download failed", message);
					}
				});
			}
		});
		
	}
	
	
	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------
	
	private void presentMessage(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title).setMessage(message);
		builder.show();
	}
	
	private void runOnUiThread(Runnable action) {
		if (getActivity() == null) {
			return;
		}
		
		getActivity().runOnUiThread(action);
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
}
