package com.example.mail.fragmenttest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CamSettingsActivity extends AppCompatActivity {
    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> laptopCollection;
    ExpandableListView expListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camsettings);

        createGroupList();

        createCollection();
        ArrayList<String> CONTINUOUS_SHOOTING_VELOCITY= new ArrayList<String>(Arrays.asList("1fps","2fps","3fps","4fps","5fps","6fps","7fps","8fps","9fps","10fps"));
        ArrayList<String> IMAGESIZE= new ArrayList<String>(Arrays.asList("4608x3456","3200x2400","2560x1920","1920x1440","1600x1200","1280x960","1024x768","640x480"));
        Boolean rawImageSaving;
        ArrayList<String> jpecCompressionRatio= new ArrayList<String>(Arrays.asList("Super Fine","Fine","Normal","Basic"));
        ArrayList<String> movieQuality= new ArrayList<String>(Arrays.asList("Full HD (MOV, 1920x1080, Fine Quality)","Full HD (MOV, 1920x1080, Normal Quality)"
                ,"HD (MOV, 1280x720, Fine Quality)","HD (MOV, 1280x720, Normal Quality)"," Clip Full HD (1920x1080)"));
        ArrayList<String> clipRecordTime = new ArrayList<String>(Arrays.asList("1sec","2sec","3sec","4sec","5sec","6sec","7sec","8sec"));

        ArrayList<String> imageSavingDest= new ArrayList<String>(Arrays.asList("DESTINATION_FILE_MEDIA","DESTINATION_FILE_WIFI"));





        expListView = (ExpandableListView) findViewById(R.id.laptop_list);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                this, groupList, laptopCollection);
        expListView.setAdapter(expListAdapter);

        //setGroupIndicatorToRight();

     /*   expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final String selected = (String) expListAdapter.getChild(groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG).show();
                return true;
            }
        });*/
    }
    private void createGroupList() {
        groupList = new ArrayList<String>();
        groupList.add("Auto Exposure Bracketing");
        groupList.add("Image Settings");
        groupList.add("Focusing");
        groupList.add("Shooting");

    }
    private void createCollection() {
        // preparing laptops collection(child)
        String[] AEB = { "setup" };
        String[] hclModels = { "HCL S2101", "HCL L2102", "HCL V2002" };
        String[] lenovoModels = { "IdeaPad Z Series", "Essential G Series",
                "ThinkPad X Series", "Ideapad Z Series" };
        String[] sonyModels = { "VAIO E Series", "VAIO Z Series",
                "VAIO S Series", "VAIO YB Series" };
        String[] dellModels = { "Inspiron", "Vostro", "XPS" };
        String[] samsungModels = { "NP Series", "Series 5", "SF Series" };

        laptopCollection = new LinkedHashMap<String, List<String>>();

        for (String laptop : groupList) {
            if (laptop.equals("Auto Exposure Bracketing")) {
                loadChild(AEB);
            } else if (laptop.equals("Image Settings"))
                loadChild(dellModels);
            else if (laptop.equals("Focusing"))
                loadChild(sonyModels);
            else if (laptop.equals("Shooting"))
                loadChild(hclModels);
            laptopCollection.put(laptop, childList);
        }
    }
    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels)
            childList.add(model);
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
