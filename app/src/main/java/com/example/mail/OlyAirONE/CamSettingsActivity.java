package com.example.mail.OlyAirONE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CamSettingsActivity extends AppCompatActivity implements ExpandableListAdapter.CallParentActivtiy {
    private static final String TAG = CamSettingsActivity.class.getSimpleName();

    private List<String> groupList;
    private List<String> childList;
    private Map<String, List<String>> categoryColl;
    private ExpandableListView expListView;
    private SharedPreferences preferences;
    public static final String AEB_IMAGETAG = "aebimage";
    public static final String AEB_SPREADTAG = "aebspread";
    public static final String TL_INTERVALL = "intervall";
    public static final String TL_NBIMAGES = "nbImages";


    private final Map<String, String> aspectRatio = new LinkedHashMap<String, String>() {{
        put("4:3 ", "<ASPECT_RATIO/04_03>");
        put("3:2", "<ASPECT_RATIO/03_02>");
        put("16:9", "<ASPECT_RATIO/16_09>");
        put("3:4", "<ASPECT_RATIO/03_04>");
        put("1:1", "<ASPECT_RATIO/06_06>");
    }};
    private final Map<String, String> jpgCompression = new LinkedHashMap<String, String>() {
        {
            put("Super Fine", "<COMPRESSIBILITY_RATIO/CMP_2_7>>");
            put("Fine", "<COMPRESSIBILITY_RATIO/CMP_4>");
            put("Normal", "<COMPRESSIBILITY_RATIO/CMP_8>");
            put("Basic", "<COMPRESSIBILITY_RATIO/CMP_12>");
        }
    };
    private final Map<String, String> imageSize = new LinkedHashMap<String, String>() {
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


    private final Map<String, String> movieQuality = new LinkedHashMap<String, String>() {{
        put("Full HD (Fine Quality)", "<QUALITY_MOVIE/QUALITY_MOVIE_FULL_HD_FINE>");
        put("Full HD (Normal Quality)", "<QUALITY_MOVIE/QUALITY_MOVIE_FULL_HD_NORMAL>");
        put("HD (Fine Quality)", "<QUALITY_MOVIE/QUALITY_MOVIE_HD_FINE>");
        put("HD (Normal Quality)", "<QUALITY_MOVIE/QUALITY_MOVIE_HD_NORMAL>");
        put("Clip Full HD (1920x1080)", "<QUALITY_MOVIE/QUALITY_MOVIE_SHORT_MOVIE>");
    }};
    private final Map<String, String> clipRecordTime = new LinkedHashMap<String, String>() {{
        put("1 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/1>");
        put("2 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/2>");
        put("3 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/3>");
        put("4 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/4>");
        put("5 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/5>");
        put("6 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/6>");
        put("7 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/7>");
        put("8 sec", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/8>");
    }};

    private final Map<String, String> continousShootingSpeed = new LinkedHashMap<String, String>() {{
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
    private final Map<String, String> selfTimer = new LinkedHashMap<String, String>() {{
        put("3sec", "<SELF_TIMER/3>");
        put("7sec", "<SELF_TIMER/7>");
        put("10sec", "<SELF_TIMER/10>");
        put("15sec", "<SELF_TIMER/15>");
    }};
    private final Map<String, String> faceDetection = new LinkedHashMap<String, String>() {{
        put("Face Priority Off", "<FACE_SCAN/FACE_SCAN_OFF>");
        put("Face Priority On", "<FACE_SCAN/FACE_SCAN_ON>");
        put("Closest Eye Priority", "<FACE_SCAN/FACE_SCAN_NEAR>");
    }};
    private final Map<String, String> empty = new LinkedHashMap<String, String>() {{
        put("empty", "empty");
    }};

    //------------------------
    //    Getters
    //------------------------

//Todo: implement sound off and volume
    public Map<String,String> getAspectRatioMap(){return aspectRatio;}
    public Map<String,String> getJpgCompressionMap(){return jpgCompression;}
    public Map<String,String> getImageSizeMap(){return imageSize;}
    public Map<String,String> getImageSaveDestinationMap(){return imageSaveDestination;}
    public Map<String,String> getMovieQualityMap(){return movieQuality;}

    public Map<String,String> getClipRecordTimeMap(){return clipRecordTime;}
    public Map<String,String> getFaceDetectionMap(){return faceDetection;}


    public Map<String,String> getContinousShootingSpeedMap(){return continousShootingSpeed;}
    public Map<String,String> getSelfTimerMap(){return selfTimer;}

    public Map<String,String> getEmptyMap(){return empty;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camsettings);
        createGroupList();
        createCollection();
        preferences = getSharedPreferences(getResources().getString(R.string.pref_SharedPrefs), MODE_PRIVATE);

        expListView = findViewById(R.id.elv_settings_list);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                this, groupList, categoryColl,this);
        expListView.setAdapter(expListAdapter);
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
    public void saveSetting(String property, String value) {
        Log.wtf(TAG, "save: prop: "+property+"  val: "+value);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(property, value);
        editor.apply();
    }

    @Override
    public String getSetting(String property, String defvalue) {
        try {
            String value = preferences.getString(property, defvalue ) ;
            Log.d(TAG, "getSetting: prop: "+property+"  val: "+value+"  getting value: "+value);
            return  value;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return "couldn't get Value";
    }


    //------------------------
    //    listView
    //------------------------
    private void createGroupList() {
        groupList = new ArrayList<>();
        groupList.add("Auto Exposure Bracketing");
        groupList.add("Time Lapse");
        groupList.add("Image Settings");
        groupList.add("Movie Settings");
        groupList.add("Focusing");
        groupList.add("Shooting");
        groupList.add("Network");
        groupList.add("Info");
        groupList.add("Impressum");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void createCollection() {
        // preparing laptops collection(child)
        String[] AEB = {"setup"};
        String[] TL = {"Enable","setup"};
        String[] image = {"aspect ratio", "image size", "jpg compression",  "save raw image","generate preview image"};
        String[] movie = {"quality", "clip record time"};
        String[] focusing = { "face detection"};
        String[] shooting = {"touch shutter","continous shooting vel"};
        String[] network = {"SSID(wifi name)"};
        String[] info = {"Camera Version","CameraKit Version","CameraKit BuildNumber","App Version"};
        String[] impressum = {"Impressum"};




        categoryColl = new LinkedHashMap<>();

        for (String group : groupList) {
            switch (group) {
                case "Auto Exposure Bracketing":
                    loadChild(AEB);
                    break;
                case "Time Lapse":
                    loadChild(TL);
                    break;
                case "Image Settings":
                    loadChild(image);
                    break;
                case "Movie Settings":
                    loadChild(movie);
                    break;
                case "Focusing":
                    loadChild(focusing);
                    break;
                case "Shooting":
                    loadChild(shooting);
                    break;
                case "Network":
                    loadChild(network);
                    break;
                case "Info":
                    loadChild(info);
                    break;
                case "Impressum":
                    loadChild(impressum);
                    break;
            }
            categoryColl.put(group, childList);
        }
    }

    private void loadChild(String[] childArr) {
        childList = new ArrayList<>();
        Collections.addAll(childList, childArr);
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

}
