/*
 * Copyright (c) Olympus Imaging Corporation. All rights reserved.
 * Olympus Imaging Corp. licenses this software to you under EULA_OlympusCameraKit_ForDevelopers.pdf.
 */

package com.example.mail.OlyAirONE;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCamera.ProgressEvent;
import jp.co.olympus.camerakit.OLYCameraFileInfo;

public class ImagePagerViewFragment extends Fragment {
    private static final String TAG = ImagePagerViewFragment.class.getSimpleName();

    private OLYCamera camera;
    private List<OLYCameraFileInfo> contentList;
    private int contentIndex;

    private LayoutInflater layoutInflater;
    private ViewPager viewPager;
    private RelativeLayout infoLayout;

    private TextView info_FileName;
    FragmentManager fm;

    private LruCache<String, Bitmap> imageCache;
    Executor connectionExecutor = Executors.newFixedThreadPool(1);


    public void setContentList(List<OLYCameraFileInfo> contentList) {
        this.contentList = contentList;
    }

    public void setContentIndex(int contentIndex) {
        this.contentIndex = contentIndex;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageCache = new LruCache<String, Bitmap>(5);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        fm = getFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layoutInflater = inflater;
        View view = layoutInflater.inflate(R.layout.fragment_image_pager_view, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager1);
        ImagePagerAdapter pagerAdaptor = new ImagePagerAdapter();
        viewPager.setAdapter(pagerAdaptor);
        ImagePageChangeListener pageChangeListener = new ImagePageChangeListener();
        viewPager.addOnPageChangeListener(pageChangeListener);
        infoLayout = (RelativeLayout) view.findViewById(R.id.rl_ifo_totalLayout);
        info_FileName = (TextView) view.findViewById(R.id.tv_ifo_filename);

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

        inflater.inflate(R.menu.image_view, menu);
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
        Log.d(TAG, "itemId:  " + item.getItemId() + "needed: " + R.id.action_download_1024x768);
        if (item.getItemId() == android.R.id.home) {
         fm.popBackStackImmediate(ImageViewActivity.FRAGMENT_TAG_IMGGRIDVIEW,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (item.getItemId() == R.id.action_download_original_size) {
            Log.d(TAG, "Ori:  " + item.getItemId());
            downloadSize = OLYCamera.IMAGE_RESIZE_NONE;
            doDownload = true;
        } else if (item.getItemId() == R.id.action_download_2048x1536) {
            Log.d(TAG, "2048:  " + item.getItemId());
            downloadSize = OLYCamera.IMAGE_RESIZE_2048;
            doDownload = true;
        } else if (item.getItemId() == R.id.action_download_1920x1440) {
            Log.d(TAG, "1920:  " + item.getItemId());
            downloadSize = OLYCamera.IMAGE_RESIZE_1920;
            doDownload = true;
        } else if (item.getItemId() == R.id.action_download_1600x1200) {
            Log.d(TAG, "1600:  " + item.getItemId());
            downloadSize = OLYCamera.IMAGE_RESIZE_1600;
            doDownload = true;
        } else if (item.getItemId() == R.id.action_download_1024x768) {
            Log.d(TAG, "1024:  " + item.getItemId());
            downloadSize = OLYCamera.IMAGE_RESIZE_1024;
            doDownload = true;
        }

        if (doDownload) {
            Calendar calendar = Calendar.getInstance();
            String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(calendar.getTime()) + ".jpg";
            saveImageToPhone(filename, downloadSize);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        camera = ImageViewActivity.camera;

        viewPager.setCurrentItem(contentIndex);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return contentList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = (ImageView) layoutInflater.inflate(R.layout.view_image_page, container, false);
            container.addView(view);
            downloadImage(position, view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }

    }

    private class ImagePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            contentIndex = position;

            OLYCameraFileInfo file = contentList.get(contentIndex);
            String path = file.getDirectoryPath() + "/" + file.getFilename();
            ActionBar bar = getActivity().getActionBar();
            if (bar != null) {
                bar.setTitle(path);
            }
            getActivity().invalidateOptionsMenu();
        }

    }

    private void downloadImage(int position, final ImageView view) {
        OLYCameraFileInfo file = contentList.get(position);
        final String path = file.getDirectoryPath() + "/" + file.getFilename();

        // Get the cached image.
        Bitmap bitmap = imageCache.get(path);
        if (bitmap != null) {
            if (view != null && viewPager.indexOfChild(view) > -1) {
                view.setImageBitmap(bitmap);
            }
            return;
        }

        // Download the image.
        camera.downloadContentScreennail(path, new OLYCamera.DownloadImageCallback() {
            @Override
            public void onProgress(ProgressEvent e) {
                // MARK: Do not use to cancel a downloading by progress handler.
                //       A communication error may occur by the downloading of the next image when
                //       you cancel the downloading of the image by a progress handler in
                //       the current version.
            }

            @Override
            public void onCompleted(final byte[] data, final Map<String, Object> metadata) {
                // Cache the downloaded image.
                final Bitmap bitmap = createRotatedBitmap(data, metadata);
                imageCache.put(path, bitmap);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (view != null && viewPager.indexOfChild(view) > -1) {
                            view.setImageBitmap(bitmap);
                        }
                    }
                });
            }

            @Override
            public void onErrorOccurred(Exception e) {
                final String message = e.getMessage();
                Log.d(TAG, message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        presentMessage("Load failed", message);
                    }
                });
            }
        });

    }

    private void saveImageToPhone(final String filename, float downloadSize) {
        // Download the image.
        OLYCameraFileInfo file = contentList.get(contentIndex);
        String path = file.getDirectoryPath() + "/" + file.getFilename();
        infoLayout.setVisibility(View.VISIBLE);
        info_FileName.setText(file.getFilename());
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
                            infoLayout.setVisibility(View.GONE);
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
            orientation = Integer.parseInt((String) metadata.get("Orientation"));
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
