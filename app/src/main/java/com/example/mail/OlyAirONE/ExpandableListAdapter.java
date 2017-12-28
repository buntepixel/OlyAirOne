package com.example.mail.OlyAirONE;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCameraKitException;

/**
 * Created by mail on 30/11/2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter implements AdapterView.OnItemSelectedListener {
    private static final String TAG = ExpandableListAdapter.class.getSimpleName();

    // 4 Child types
    private static final int CHILD_TYPE_1 = 0;
    private static final int CHILD_TYPE_2 = 1;
    private static final int CHILD_TYPE_3 = 2;
    private static final int CHILD_TYPE_UNDEFINED = 3;


    private Activity context;
    private Map<String, List<String>> myChilds;
    private List<String> myParents;
    private final Map<String, String> dropdownVals = new HashMap<String, String>();

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


    public ExpandableListAdapter(Activity context, List<String> parent, Map<String, List<String>> childs) {
        this.context = context;
        this.myChilds = childs;
        this.myParents = parent;
    }

    //children----------------
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return myChilds.get(myParents.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String child = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        final int childType = getChildType(groupPosition, childPosition);

        if (convertView == null) {
        }
        // We need to create a new "cell container"
        if (convertView == null || !convertView.getTag().equals(childType)) {
            switch (childType) {
                case CHILD_TYPE_1:
                    Log.d(TAG, "ChildType 1");
                    convertView = inflater.inflate(R.layout.childitem_aeb_camsettingsactivity, parent, false);
                    convertView.setTag(childType);
                    break;
                case CHILD_TYPE_2:
                    Log.d(TAG, "ChildType 2");
                    convertView = inflater.inflate(R.layout.childitem_bool_camsettingsactivity, parent, false);
                    convertView.setTag(childType);
                    break;
                case CHILD_TYPE_3:
                    Log.d(TAG, "ChildType 3");

                    convertView = inflater.inflate(R.layout.childitem_dropdownchooser_camsettingsactivity, parent, false);
                    convertView.setTag(childType);
                    break;
                case CHILD_TYPE_UNDEFINED:
                    Log.d(TAG, "ChildType 4");
                    convertView = inflater.inflate(R.layout.childitem_bool_camsettingsactivity, parent, false);
                    convertView.setTag(childType);
                    break;
                default:
                    // Maybe we should implement a default behaviour but it should be ok we know there are 4 child types right?
                    break;
            }
        }
        // We'll reuse the existing one
        else {
            // There is nothing to do here really we just need to set the content of view which we do in both cases
        }
        TextView txt;
        switch (childType) {
            case CHILD_TYPE_1:
                /*TextView description_child = (TextView) convertView.findViewById(R.id.description_of_ads_expandable_list_child_text_view);
                description_child.setText(incoming_text);*/
                setup_AEB(convertView);
                break;
            case CHILD_TYPE_2:
                //
                txt = (TextView) convertView.findViewById(R.id.tv_chbx_discription);
                txt.setText(child);
                //Define how to render the data on the CHILD_TYPE_2 layout
                break;
            case CHILD_TYPE_3:
                Spinner spinner = (Spinner) convertView.findViewById(R.id.sp_ddch_spinner);
                spinner.setOnItemSelectedListener(this);
                txt = (TextView) convertView.findViewById(R.id.tv_ddch_discription);
                txt.setText(child);
                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item);

                //if Image Settings
                if (groupPosition == 1) {
                    if (childPosition == 0) {
                        adapter.addAll(aspectRatio.keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(aspectRatio);
                    } else if (childPosition == 1) {
                        adapter.addAll(imageSize.keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(imageSize);
                    } else if (childPosition == 2) {
                        //CharSequence[] player_names = players.keySet().toArray(new CharSequence[0]);
                        adapter.addAll(jpgCompression.keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(jpgCompression);
                    } else if (childPosition == 3) {
                        adapter.addAll(imageSaveDestination.keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(imageSaveDestination);
                    } else {
                        adapter.addAll(empty.keySet().toArray(new CharSequence[0]));
                    }

                } //if movie settings
                else if (groupPosition == 2) {
                    if (childPosition == 0) {
                        adapter.addAll(movieQuality.keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(movieQuality);
                    } else if (childPosition == 1) {
                        adapter.addAll(clipRecordTime.keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(clipRecordTime);
                    }
                }

                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                //Define how to render the data on the CHILD_TYPE_3 layout
                break;
            case CHILD_TYPE_UNDEFINED:
                //Define how to render the data on the CHILD_TYPE_UNDEFINED layout
                break;
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return myChilds.get(myParents.get(groupPosition)).size();
    }


    public int getChildTypeCount() {
        return 4; // I defined 4 child types (CHILD_TYPE_1, CHILD_TYPE_2, CHILD_TYPE_3, CHILD_TYPE_UNDEFINED)
    }

    public int getChildType(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case 0:
                switch (childPosition) {
                    case 0:
                        return CHILD_TYPE_1;
                }
                break;
            case 1:
                switch (childPosition) {
                    case 0:
                        return CHILD_TYPE_3;//AspectRatio
                    case 1:
                        return CHILD_TYPE_3;//ImageSize
                    case 2:
                        return CHILD_TYPE_3;//jpgCompression
                    case 3:
                        return CHILD_TYPE_3;//ImageDestination
                    case 4:
                        return CHILD_TYPE_2;//RawImageSaving
                }
                break;
            case 2:
                switch (childPosition) {
                    case 0:
                        return CHILD_TYPE_3;//AspectRatio
                    case 1:
                        return CHILD_TYPE_3;//ImageSize
                    default:
                        return CHILD_TYPE_UNDEFINED;
                }
        }
        return CHILD_TYPE_UNDEFINED;

    }

    //group-----------------

    @Override
    public Object getGroup(int groupPosition) {
        return myParents.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return myParents.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View
            convertView, ViewGroup parent) {
        String itemName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.groupitem_camsettingsactivtiy, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.tv_parent);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(itemName);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void setup_AEB(View convertView) {
        NumberPicker np_nbImagesVal = (NumberPicker) convertView.findViewById(R.id.np_nbImagesVal);
        final int[] values = {3, 5, 7, 9};
        final String[] strVal = {"3", "5", "7", "9", "11"};
        setupNumberPicker(np_nbImagesVal, strVal);

        NumberPicker np_exposureSpreadVal = (NumberPicker) convertView.findViewById(R.id.np_exposureSpreadVal);
        final String[] expSprVal = {"1", "2", "3"};
        setupNumberPicker(np_exposureSpreadVal, expSprVal);
    }


    private void setupNumberPicker(NumberPicker np, String[] strVal) {
        np.setMinValue(0); //from array first value
        np.setMaxValue(strVal.length - 1); //to array last value

        //Specify the NumberPicker data source as array elements
        np.setDisplayedValues(strVal);
        //np.setWrapSelectorWheel(true); //wrap.

        //Set a value change listener for NumberPicker
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected value from picker
                Log.d(TAG, "New NuberPicerVal: " + newVal);
            }
        });
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "selected: " + parent.getItemAtPosition(position));
        String value = dropdownVals.get(parent.getItemAtPosition(position));
        String prop = CameraActivity.extractProperty(value);
        Log.d(TAG, "selected: " + value +"  "+ CameraActivity.extractProperty(value));
        try {
            CameraActivity.getCamera().setCameraPropertyValue(prop,value);
        } catch (OLYCameraKitException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
