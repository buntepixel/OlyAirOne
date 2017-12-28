package com.example.mail.OlyAirONE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraConnectionListener;
import jp.co.olympus.camerakit.OLYCameraKitException;

import static com.example.mail.OlyAirONE.MainActivity.PREFS_NAME;

public class CamSettingsActivity extends AppCompatActivity implements OLYCameraConnectionListener,ExpandableListAdapter.CallParentActivtiy {
    private static final String TAG = CamSettingsActivity.class.getSimpleName();

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> categoryColl;
    ExpandableListView expListView;
    static OLYCamera  camera;
    Executor connectionExecutor = Executors.newFixedThreadPool(1);

    private final Map<String, String> aspectRatio = new HashMap<String, String>() {{
        put("4:3 ", "<ASPECT_RATIO/04_03>");
        put("3:2", "<ASPECT_RATIO/03_02>");
        put("16:9", "<ASPECT_RATIO/16_09>");
        put("3:4", "<ASPECT_RATIO/03_04>");
        put("1:1", "<ASPECT_RATIO/06_06>");
    }};
    private final Map<String, String> jpgCompression = new HashMap<String, String>() {
        {
            put("Super Fine", "<COMPRESSIBILITY_RATIO/CMP_2_7>>");
            put("Fine", "<COMPRESSIBILITY_RATIO/CMP_4>");
            put("Normal", "<COMPRESSIBILITY_RATIO/CMP_8>");
            put("Basic", "<COMPRESSIBILITY_RATIO/CMP_12>");
        }
    };
    private final Map<String, String> imageSize = new HashMap<String, String>() {
        {
            put("4608x3456", "<IMAGESIZE/4608x3456>");
            put("2560x1920", "<IMAGESIZE/2560x1920>");
            put("1920x1440", "<IMAGESIZE/1920x1440>");
            put("1600x1200", "<IMAGESIZE/1600x1200>");
            put("1280x960", "<IMAGESIZE/1280x960>");
            put("1024x768", "<IMAGESIZE/1024x768>");
            put("640x480", "<IMAGESIZE/640x480>");
        }
    };
    private final Map<String, String> imageSaveDestination = new HashMap<String, String>() {{
        put("store on Camera", "<DESTINATION_FILE/DESTINATION_FILE_MEDIA>");
        put("Store on Mobile", "<DESTINATION_FILE/DESTINATION_FILE_WIFI>");
    }};


    private final Map<String, String> movieQuality = new HashMap<String, String>() {{
        put("Full HD (Fine Quality)", "<QUALITY_MOVIE/QUALITY_MOVIE_FULL_HD_FINE>");
        put("Full HD (Normal Quality)", "<QUALITY_MOVIE/QUALITY_MOVIE_FULL_HD_NORMAL>");
        put("HD (Fine Quality)", "<QUALITY_MOVIE/QUALITY_MOVIE_HD_FINE>");
        put("HD (Normal Quality)", "<QUALITY_MOVIE/QUALITY_MOVIE_HD_NORMAL>");
        put("Clip Full HD (1920x1080)", "<QUALITY_MOVIE/QUALITY_MOVIE_SHORT_MOVIE>");
    }};
    private final Map<String, String> clipRecordTime = new HashMap<String, String>() {{
        put("1 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/1>");
        put("2 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/2>");
        put("3 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/3>");
        put("4 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/4>");
        put("5 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/5>");
        put("6 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/6>");
        put("7 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/7>");
        put("8 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/8>");
    }};

    private final Map<String, String> continousShootingSpeed = new HashMap<String, String>() {{
        put("1fps", "<CONTINUOUS_SHOOTING_VELOCITY/1>");
        put("2fps", "<CONTINUOUS_SHOOTING_VELOCITY/2>");
        put("3fps", "<CONTINUOUS_SHOOTING_VELOCITY/3>");
        put("4fps", "<CONTINUOUS_SHOOTING_VELOCITY/4>");
        put("5fps", "<CONTINUOUS_SHOOTING_VELOCITY/5>");
        put("6fps", "<CONTINUOUS_SHOOTING_VELOCITY/6>");
        put("7fps", "<CONTINUOUS_SHOOTING_VELOCITY/7>");
        put("8fps", "<CONTINUOUS_SHOOTING_VELOCITY/8>");
        put("9fps", "<CONTINUOUS_SHOOTING_VELOCITY/9>");
        put("10fps", "<CONTINUOUS_SHOOTING_VELOCITY/10>");
    }};
    private final Map<String, String> empty = new HashMap<String, String>() {{
        put("empty", "empty");
    }};

    //------------------------
    //    Getters
    //------------------------
    public static OLYCamera getCamera() {
        return camera;
    }

    public Map<String,String> getAspectRatioMap(){return aspectRatio;}
    public Map<String,String> getJpgCompressionMap(){return jpgCompression;}
    public Map<String,String> getImageSizeMap(){return imageSize;}
    public Map<String,String> getImageSaveDestinationMap(){return imageSaveDestination;}
    public Map<String,String> getMovieQualityMap(){return movieQuality;}
    public Map<String,String> getClipRecordTimeMap(){return clipRecordTime;}
    public Map<String,String> getContinousShootingSpeedMap(){return continousShootingSpeed;}
    public Map<String,String> getEmptyMap(){return empty;}

    @Override
    public void saveSetting(String property, String value) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(property, value);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camsettings);

        createGroupList();
        createCollection();

        Boolean rawImageSaving;

        camera = new OLYCamera();
        camera.setContext(this);
        camera.setConnectionListener(this);

        expListView = (ExpandableListView) findViewById(R.id.laptop_list);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                this, groupList, categoryColl,this);
        expListView.setAdapter(expListAdapter);
        Log.d(TAG, "What is it: " + expListAdapter.getChild(1, 2));


        //setGroupIndicatorToRight();

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final String selected = (String) expListAdapter.getChild(groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "START Resume");
        if (!camera.isConnected()) {
            Log.d(TAG, "Cam Is NOT connected");
            startConnectingCamera();
        } else {
            Log.d(TAG, "Cam Is connected");
            onConnectedToCamera();
        }
        Log.d(TAG, "END Resume");
    }


    @Override
    public void onStop() {
        super.onStop();
        try {
            camera.disconnectWithPowerOff(false);
        } catch (OLYCameraKitException e) {
            Log.w(this.toString(), "To disconnect from the camera is failed.");
        }
    }

    //------------------------
    //    Connecting Camera
    //------------------------
    private void startConnectingCamera() {
        Log.d(TAG, "startConnectingCamera__" + "Adding trigger fragment to View");
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {

                Boolean canConnect = false;
                while (!canConnect) {
                    canConnect = camera.canConnect(OLYCamera.ConnectionType.WiFi, 0);
                }
                Log.d(TAG, "OLYCamera.ConnectionType.WiFi");
                try {
                    camera.connect(OLYCamera.ConnectionType.WiFi);
                } catch (OLYCameraKitException e) {
                    alertConnectingFailed(e);
                    return;
                }

                Log.d(TAG, "startConnectingCamera__" + "OLYCamera.RunMode.Recording");
                try {
                    camera.changeRunMode(OLYCamera.RunMode.Recording);
                } catch (OLYCameraKitException e) {
                    alertConnectingFailed(e);
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onConnectedToCamera();
                    }
                });
            }
        });
    }

    @Override
    public void onDisconnectedByError(OLYCamera olyCamera, OLYCameraKitException e) {
        Log.d(TAG, "LostConnection");
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getBaseContext(), "Connection to Camera Lost, please Reconnect", Toast.LENGTH_LONG).show();
            }
        });
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void alertConnectingFailed(Exception e) {
        final Intent myIntent = new Intent(this, ConnectToCamActivity.class);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Connect failed")
                .setMessage(e.getMessage() != null ? e.getMessage() : "Unknown error")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //startScanningCamera();
                        startActivity(myIntent);
                    }
                });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.show();
            }
        });
    }

    private void onConnectedToCamera() {
        try {
            Log.d(TAG, "Connected to Cam");
            //restore Cam settings from Shared prefs
            //restoreCamSettings();
            //add LiveView to fragment manager if here first time
            Log.d(TAG, "::::::::::::Cam is Connected: " + camera.isConnected());
           /* try {

            } catch (OLYCameraKitException e) {
                e.printStackTrace();
                return;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //------------------------
    //    listView
    //------------------------
    private void createGroupList() {
        groupList = new ArrayList<String>();
        groupList.add("Auto Exposure Bracketing");
        groupList.add("Image Settings");
        groupList.add("Movie Settings");
        groupList.add("Focusing");
        groupList.add("Shooting");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, MainActivity.class));
    }

    private void createCollection() {
        // preparing laptops collection(child)
        String[] AEB = {"setup"};
        String[] image = {"AspectRatio", "ImageSize", "jpgComression", "ImageDestination", "RawImageSaving"};
        String[] movie = {"Quality", "ClipRecordTime"};
        String[] focusing = {"Touch shutter"};
        String[] shooting = {"contShootinvVel", "Self Timer", ""};


        categoryColl = new LinkedHashMap<String, List<String>>();

        for (String group : groupList) {
            if (group.equals("Auto Exposure Bracketing"))
                loadChild(AEB);
            else if (group.equals("Image Settings"))
                loadChild(image);
            else if (group.equals("Movie Settings"))
                loadChild(movie);
            else if (group.equals("Focusing"))
                loadChild(focusing);
            else if (group.equals("Shooting"))
                loadChild(shooting);
            categoryColl.put(group, childList);
        }
    }

    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels) {

            childList.add(model);
        }
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }




   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menue.activity_camsettings, menu);
        return true;
    }*/
}
