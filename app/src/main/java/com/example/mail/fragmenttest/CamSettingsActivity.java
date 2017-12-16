package com.example.mail.fragmenttest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CamSettingsActivity extends AppCompatActivity {
    private static final String TAG = CamSettingsActivity.class.getSimpleName();

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> categoryColl;
    ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camsettings);

        createGroupList();

        createCollection();
        Boolean rawImageSaving;

        expListView = (ExpandableListView) findViewById(R.id.laptop_list);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                this, groupList, categoryColl);
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

    private void createGroupList() {
        groupList = new ArrayList<String>();
        groupList.add("Auto Exposure Bracketing");
        groupList.add("Image Settings");
        groupList.add("Movie Settings");
        groupList.add("Focusing");
        groupList.add("Shooting");

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
