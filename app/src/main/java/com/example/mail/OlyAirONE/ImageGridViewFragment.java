/*
 * Copyright (c) Olympus Imaging Corporation. All rights reserved.
 * Olympus Imaging Corp. licenses this software to you under EULA_OlympusCameraKit_ForDevelopers.pdf.
 */

package com.example.mail.OlyAirONE;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCamera.ProgressEvent;
import jp.co.olympus.camerakit.OLYCameraFileInfo;
import jp.co.olympus.camerakit.OLYCameraKitException;

public class ImageGridViewFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = ImageGridViewFragment.class.getSimpleName();

    private GridView gridView;
    private RelativeLayout infoLayout;
    private TextView info_task;
    private TextView info_currNb;
    private TextView info_totalNb;
    private TextView info_FileName;
    private boolean gridViewIsScrolling;
    private boolean downloading;

    private Menu optionsMenue;
    MenuItem dropdown;
    CountDownTimer timer;
    Boolean selectionChbx = false;
    private ArrayList<OLYCameraFileInfo> selectionList;
    private List<OLYCameraFileInfo> contentList;
    private int contentIndex;

    private ExecutorService executor;
    Executor connectionExecutor = Executors.newFixedThreadPool(1);

    private LruCache<String, Bitmap> imageCache;
    OLYCamera camera;

    private ImagerGridViewInteractionListener listener;

    public interface ImagerGridViewInteractionListener {
        void OnFragmentChange(String fragmentName);
    }

    public void setImageGridViewInteractionListener(ImagerGridViewInteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executor = Executors.newFixedThreadPool(1);
        imageCache = new LruCache<String, Bitmap>(100);
        camera = ImageViewActivity.camera;
        selectionList = new ArrayList<OLYCameraFileInfo>();
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_grid_view, container, false);
        gridView = (GridView) view.findViewById(R.id.gv_imagegridview);
        GridViewAdapter gridViewAdapter = new GridViewAdapter(inflater);
        gridView.setAdapter(gridViewAdapter);
        gridView.setTag(gridViewAdapter);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        gridView.setOnScrollListener(new GridViewOnScrollListener());
        infoLayout = (RelativeLayout) view.findViewById(R.id.rl_ifo_totalLayout);
        info_task = view.findViewById((R.id.tv_ifo_task));
        info_FileName = view.findViewById(R.id.tv_ifo_filename);
        info_currNb = (TextView) view.findViewById(R.id.tv_ifo_itemNr);
        info_totalNb = (TextView) view.findViewById(R.id.tv_ifo_totalNr);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        optionsMenue = menu;
        inflater.inflate(R.menu.image_grid_view, menu);
        dropdown = menu.findItem(R.id.action_download);
        //dropdown.setOnMenuItemClickListener()

        ActionBar bar = getActivity().getActionBar();
        if (bar != null) {
            bar.setTitle(getString(R.string.app_name));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "nb to download: " + selectionList.size());
        boolean doDownload = false;
        float downloadSize = 0;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_info:
                String cameraVersion;
                try {
                    Map<String, Object> hardwareInformation = ImageViewActivity.camera.inquireHardwareInformation();
                    cameraVersion = (String) hardwareInformation.get(OLYCamera.HARDWARE_INFORMATION_CAMERA_FIRMWARE_VERSION_KEY);
                } catch (OLYCameraKitException e) {
                    cameraVersion = "Unknown";
                }
                Toast.makeText(getActivity(), "Camera " + cameraVersion + " / " + "CameraKit " + OLYCamera.getVersion(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_delete:
                boolean deleting = true;
                Log.d(TAG, "nb to delete: " + selectionList.size());
                try {
                    if (camera.getRunMode() != OLYCamera.RunMode.Playmaintenance) {
                        camera.changeRunMode(OLYCamera.RunMode.Playmaintenance);
                    }
                } catch (OLYCameraKitException ex) {
                    ex.printStackTrace();
                }
                deleteContentFromCam();
                return true;

            case R.id.action_download_original_size:
                downloadSize = OLYCamera.IMAGE_RESIZE_NONE;
                doDownload = true;
                break;
            case R.id.action_download_2048x1536:
                downloadSize = OLYCamera.IMAGE_RESIZE_NONE;
                doDownload = true;
                break;
            case R.id.action_download_1920x1440:
                downloadSize = OLYCamera.IMAGE_RESIZE_2048;
                doDownload = true;
                break;
            case R.id.action_download_1600x1200:
                downloadSize = OLYCamera.IMAGE_RESIZE_1920;
                doDownload = true;
                break;
            case R.id.action_download_1024x768:
                downloadSize = OLYCamera.IMAGE_RESIZE_1024;
                doDownload = true;
                break;
            case R.id.action_EndAction:
                SetOptionsLongClick(false);
                Log.d(TAG, "canceled Action " + selectionList.size());
                return true;
        }
        Log.d(TAG, "downloadsize:  " + downloadSize);
        if (selectionList.size() > 0 && doDownload) {
            downLoadContentFromCam(downloadSize);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void downLoadContentFromCam(float downloadSize) {
        Log.d(TAG, "downloading Images");
        final float myDownloadsize = downloadSize;
        Calendar calendar = Calendar.getInstance();
        String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(calendar.getTime()) + ".jpg";
        infoLayout.setVisibility(View.VISIBLE);
        info_task.setText(R.string.gv_ifo_downloading);
        info_totalNb.setText(String.valueOf(selectionList.size()));
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {

                int count = 1;
                for (OLYCameraFileInfo fileInfo : selectionList) {
                    final String filename = fileInfo.getFilename();
                    final int fincount = count;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            info_FileName.setText(filename);
                            info_currNb.setText(String.valueOf(fincount));
                        }
                    });
                    Log.d(TAG, "downloading Image: " + fileInfo.getFilename());
                    downloading = true;
                    camera.downloadImage(fileInfo.getDirectoryPath() + "/" + fileInfo.getFilename(), myDownloadsize, new OLYCamera.DownloadImageCallback() {
                        @Override
                        public void onProgress(ProgressEvent e) {
                            if (e.getProgress() < 1)
                                downloading = true;
                            else
                                downloading = false;
                            // Log.d(TAG, "progress: " + e.getProgress() + " downloading: " + downloading);
                        }

                        @Override
                        public void onCompleted(byte[] data, Map<String, Object> metadata) {
                            final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/OLYAirONE/";
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
                                Log.d(TAG, "Error: " + message);
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
                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                values.put(MediaStore.Images.Media.DATA, filepath);
                                values.put(MediaStore.Images.Media.DATE_ADDED, now);
                                values.put(MediaStore.Images.Media.DATE_TAKEN, now);
                                values.put(MediaStore.Images.Media.DATE_MODIFIED, now);
                                values.put(MediaStore.Images.Media.ORIENTATION, getRotationDegrees(data, metadata));
                                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

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
                            Log.d(TAG, message);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    presentMessage("Download failed", message);
                                }
                            });
                        }
                    });
                    Log.d(TAG, "downloading?: " + downloading);
                    while (downloading) {
                        try {
                            //Log.d(TAG,"sleeping");
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    count++;
                }
                // refresh();


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoLayout.setVisibility(View.GONE);
                        SetOptionsLongClick(false);
                        refresh();
                    }
                });

            }
        });
    }


    private void deleteContentFromCam() {
        infoLayout.setVisibility(View.VISIBLE);
        info_totalNb.setText(String.valueOf(selectionList.size()));
        info_task.setText(R.string.gv_ifo_deleting);

        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int count = 1;
                    for (OLYCameraFileInfo fileInfo : selectionList) {
                        Log.d(TAG, "fullfilePath: " + fileInfo.getDirectoryPath() + "/" + fileInfo.getFilename());
                        final String filename = fileInfo.getFilename();
                        final int fincount = count;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                info_FileName.setText(filename);
                                info_currNb.setText(String.valueOf(fincount));
                            }
                        });
                        camera.eraseContent(fileInfo.getDirectoryPath() + "/" + fileInfo.getFilename());
                        count++;
                    }
                    camera.changeRunMode(OLYCamera.RunMode.Playback);
                    // refresh();

                } catch (OLYCameraKitException ex) {
                    ex.printStackTrace();
                    final String error = ex.toString();
                    Log.d(TAG, "Error: " + ex);
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoLayout.setVisibility(View.GONE);
                        SetOptionsLongClick(false);
                        refresh();
                    }
                });

            }
        });
    }

    @Override
    public void onResume() {
        selectionList.clear();
        if (camera.getRunMode() != OLYCamera.RunMode.Playback) {
            try {
                camera.changeRunMode(OLYCamera.RunMode.Playback);
            } catch (OLYCameraKitException ex) {
                ex.printStackTrace();
            }
        }
        super.onResume();
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (!camera.isConnected()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });
    }

    @Override
    public void onPause() {
        if (!executor.isShutdown()) {
            executor.shutdownNow();
        }
        super.onPause();
    }


    private void refresh() {
        contentList = null;
        Log.d(TAG, "refreshing list");
        camera.downloadContentList(new OLYCamera.DownloadContentListCallback() {
            @Override
            public void onCompleted(List<OLYCameraFileInfo> list) {
                // Sort contents in chronological order (or alphabetical order).
                Collections.sort(list, new Comparator<OLYCameraFileInfo>() {
                    @Override
                    public int compare(OLYCameraFileInfo lhs, OLYCameraFileInfo rhs) {
                        long diff = rhs.getDatetime().getTime() - lhs.getDatetime().getTime();
                        if (diff == 0) {
                            diff = rhs.getFilename().compareTo(lhs.getFilename());
                        }

                        return (int) Math.min(Math.max(-1, diff), 1);
                    }
                });

                List<OLYCameraFileInfo> shouldBeRemovedItems = new ArrayList<OLYCameraFileInfo>();
                for (OLYCameraFileInfo item : list) {
                    String path = item.getFilename().toLowerCase(Locale.getDefault());
                    if (!path.endsWith(".jpg") && !path.endsWith(".mov")) {
                        shouldBeRemovedItems.add(item);
                    }
                }
                list.removeAll(shouldBeRemovedItems);

                contentList = list;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gridView.invalidateViews();
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

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "Long click: " + selectionChbx);
        selectionChbx = true;
        SetOptionsLongClick(true);
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        OLYCameraFileInfo selItem;
        try {
            Log.d(TAG, "click pos: " + position);
            GridViewAdapter adapter = (GridViewAdapter) adapterView.getAdapter();
            GridCellViewHolder holder = (GridCellViewHolder) view.getTag();
            selItem = (OLYCameraFileInfo) adapterView.getAdapter().getItem(position);
            if (selectionChbx) {
                if (!holder.chbxView.isChecked()) {
                    holder.chbxView.setChecked(true);
                    if (!selectionList.contains(selItem)) {
                        selectionList.add(selItem);
                        Log.d(TAG, "Added selItem: " + selItem.getFilename());
                    }

                } else {
                    holder.chbxView.setChecked(false);
                    if (selectionList.contains(selItem)) {
                        selectionList.remove(selItem);
                        Log.d(TAG, "Removed selItem: " + selItem.getFilename());
                    }
                }
            } else {
                Log.d(TAG, "Go to big view: ");
                ImagePagerViewFragment fImgPagerView = new ImagePagerViewFragment();    // Use an advanced viewer.
                fImgPagerView.setContentList(contentList);
                fImgPagerView.setContentIndex(position);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(getId(), fImgPagerView, ImageViewActivity.FRAGMENT_TAG_IMGPAGEVIEWE);
                transaction.addToBackStack(ImageViewActivity.FRAGMENT_TAG_IMGGRIDVIEW);
                if (listener != null)
                    listener.OnFragmentChange(ImageViewActivity.FRAGMENT_TAG_IMGPAGEVIEWE);
                transaction.commit();


            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
            Log.d(TAG, "error: " + ex.toString());

        }
    }


    private void SetOptionsLongClick(Boolean visible) {/*
        if (visible) {
            gridView.setOnItemClickListener(this);
            gridView.setOnTouchListener(null);
        } else {
            gridView.setOnItemClickListener(null);
            gridView.setOnTouchListener(this);
        }*/
        selectionChbx = visible;
        selectionList.clear();
        optionsMenue.findItem(R.id.action_delete).setVisible(visible);
        optionsMenue.findItem(R.id.action_download).setVisible(visible);
        optionsMenue.findItem(R.id.action_EndAction).setVisible(visible);
        gridView.setAdapter(gridView.getAdapter());//refresh and hide checkboxes
    }


    private static class GridCellViewHolder {
        private final String TAG = GridCellViewHolder.class.getSimpleName();
        public ImageView imageView;
        public ImageView iconView;
        public CheckBox chbxView;
    }

    private class GridViewAdapter extends BaseAdapter {
        private final String TAG = GridViewAdapter.class.getSimpleName();

        private final LayoutInflater inflater;
        GridCellViewHolder viewHolder;


        public GridViewAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        private List<?> getItemList() {
            return contentList;
        }

        @Override
        public int getCount() {
            if (getItemList() == null) {
                return 0;
            }
            return getItemList().size();
        }

        @Override
        public Object getItem(int position) {
            if (getItemList() == null) {
                return null;
            }
            return getItemList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.view_grid_cell, parent, false);

                viewHolder = new GridCellViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_gv_preview);
                viewHolder.iconView = (ImageView) convertView.findViewById(R.id.iv_gv_icon);
                viewHolder.chbxView = (CheckBox) convertView.findViewById(R.id.chbx_gv_delete);

               /* viewHolder.chbxView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OLYCameraFileInfo selItem;
                        try {
                            Log.d(TAG, "click pos: " + tmpPos);
                            selItem = (OLYCameraFileInfo) getItem(tmpPos);
                            if (((CheckBox) view).isChecked() && !selectionList.contains(selItem)) {
                                selectionList.add(selItem);
                                selectionPosArr[tmpPos] = true;
                            } else if (selectionList.contains(selItem)) {
                                selectionList.remove(selItem);
                                selectionPosArr[tmpPos] = false;
                                Log.d(TAG, "Removed selItem: " + selItem.getFilename());
                            } else {
                                Log.d(TAG, "NotingHappend in click");
                            }
                        } catch (ClassCastException ex) {
                            ex.printStackTrace();
                            Log.d(TAG, "error: " + ex.toString());

                        }
                    }
                });*/
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GridCellViewHolder) convertView.getTag();
            }
            OLYCameraFileInfo item = (OLYCameraFileInfo) getItem(position);

            if (selectionChbx) {//set checked if previously checked
                Log.d(TAG, "pos: " + position);

                viewHolder.chbxView.setVisibility(View.VISIBLE);
                //viewHolder.chbxView.setChecked(selectionPosArr[position]);
            }
            if (item == null) {
                viewHolder.imageView.setImageDrawable(null);
                viewHolder.iconView.setImageDrawable(null);
                return convertView;
            }
            String path = new File(item.getDirectoryPath(), item.getFilename()).getPath();
            Bitmap thumbnail = imageCache.get(path);

            if (thumbnail == null) {
                viewHolder.imageView.setImageDrawable(null);
                viewHolder.iconView.setImageDrawable(null);
                if (!gridViewIsScrolling) {
                    if (executor.isShutdown()) {
                        executor = Executors.newFixedThreadPool(1);
                    }
                    executor.execute(new ThumbnailLoader(viewHolder, path));
                }
            } else {
                viewHolder.imageView.setImageBitmap(thumbnail);
                if (path.toLowerCase().endsWith(".mov")) {
                    viewHolder.iconView.setImageResource(R.drawable.icn_movie);
                } else {
                    viewHolder.iconView.setImageDrawable(null);
                }
            }

            return convertView;
        }


    }

    private class GridViewOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }

    private class GridViewOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            // No operation.
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                gridViewIsScrolling = false;
                gridView.invalidateViews();
            } else if ((scrollState == SCROLL_STATE_FLING) || (scrollState == SCROLL_STATE_TOUCH_SCROLL)) {
                gridViewIsScrolling = true;
                if (!executor.isShutdown()) {
                    executor.shutdownNow();
                }
            }
        }
    }

    private class ThumbnailLoader implements Runnable {
        private final GridCellViewHolder viewHolder;
        private final String path;

        public ThumbnailLoader(GridCellViewHolder viewHolder, String path) {
            this.viewHolder = viewHolder;
            this.path = path;
        }

        @Override
        public void run() {
            class Box {
                boolean isDownloading = true;
            }
            final Box box = new Box();

            ImageViewActivity.camera.downloadContentThumbnail(path, new OLYCamera.DownloadImageCallback() {
                @Override
                public void onProgress(ProgressEvent e) {
                }

                @Override
                public void onCompleted(byte[] data, Map<String, Object> metadata) {
                    final Bitmap thumbnail = createRotatedBitmap(data, metadata);
                    imageCache.put(path, thumbnail);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewHolder.imageView.setImageBitmap(thumbnail);
                            if (path.toLowerCase().endsWith(".mov")) {
                                viewHolder.iconView.setImageResource(R.drawable.icn_movie);
                            } else {
                                viewHolder.iconView.setImageDrawable(null);
                            }
                        }
                    });
                    box.isDownloading = false;
                }

                @Override
                public void onErrorOccurred(Exception e) {
                    box.isDownloading = false;
                }
            });

            // Waits to realize the serial download.
            while (box.isDownloading) {
                Thread.yield();
            }
        }
    }


    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    @SuppressWarnings("SameParameterValue")
    private void presentMessage(String title, String message) {
        Context context = getActivity();
        if (context == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message);
        builder.show();
    }

    private void runOnUiThread(Runnable action) {
        Activity activity = getActivity();
        if (activity == null) return;

        activity.runOnUiThread(action);
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
