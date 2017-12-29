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
    CallParentActivtiy listener;


    public ExpandableListAdapter(Activity context, List<String> parent, Map<String, List<String>> childs, CallParentActivtiy listener) {
        this.context = context;
        this.myChilds = childs;
        this.myParents = parent;
        this.listener = listener;
    }

    public interface CallParentActivtiy {
        public Map<String, String> getAspectRatioMap();

        public Map<String, String> getJpgCompressionMap();

        public Map<String, String> getImageSizeMap();

        public Map<String, String> getImageSaveDestinationMap();

        public Map<String, String> getMovieQualityMap();

        public Map<String, String> getClipRecordTimeMap();

        public Map<String, String> getContinousShootingSpeedMap();
        public Map<String, String> getSelfTimerMap();


        public Map<String, String> getEmptyMap();

        public void saveSetting(String property, String value);

        public String getSetting(String property, String defvalue);

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
                int spinnerPosition = -1;
                //if Image Settings
                if (groupPosition == 1) {
                    if (childPosition == 0) {
                        spinnerPosition = setAdapterValues(adapter, listener.getAspectRatioMap(), "ASPECT_RATIO", "<ASPECT_RATIO/04_03>");
                    } else if (childPosition == 1) {
                        spinnerPosition = setAdapterValues(adapter, listener.getImageSizeMap(), "IMAGESIZE", "<IMAGESIZE/4608x3456>");
                    } else if (childPosition == 2) {
                        spinnerPosition = setAdapterValues(adapter, listener.getJpgCompressionMap(), "COMPRESSIBILITY_RATIO", "<COMPRESSIBILITY_RATIO/CMP_4>");
                    } else if (childPosition == 3) {
                        spinnerPosition = setAdapterValues(adapter, listener.getImageSaveDestinationMap(), "DESTINATION_FILE", "<DESTINATION_FILE/DESTINATION_FILE_MEDIA>");
                    } else {
                        adapter.addAll(listener.getEmptyMap().keySet().toArray(new CharSequence[0]));
                    }

                } //if movie settings
                else if (groupPosition == 2) {
                    if (childPosition == 0) {
                        spinnerPosition = setAdapterValues(adapter, listener.getMovieQualityMap(), "QUALITY_MOVIE", "<QUALITY_MOVIE/QUALITY_MOVIE_FULL_HD_NORMAL>");
                    } else if (childPosition == 1) {
                        spinnerPosition = setAdapterValues(adapter, listener.getClipRecordTimeMap(), "QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/5>");
                    } else {
                        adapter.addAll(listener.getEmptyMap().keySet().toArray(new CharSequence[0]));
                    }
                } else if (groupPosition == 4) {
                    if (childPosition == 0) {
                        spinnerPosition = setAdapterValues(adapter, listener.getContinousShootingSpeedMap(), "CONTINUOUS_SHOOTING_VELOCITY", "<CONTINUOUS_SHOOTING_VELOCITY/5>");
                    } else if (childPosition == 1) {
                        spinnerPosition = setAdapterValues(adapter, listener.getSelfTimerMap(), "SELF_TIMER", "<SELF_TIMER/10>");
                    } else {
                        adapter.addAll(listener.getEmptyMap().keySet().toArray(new CharSequence[0]));
                    }

                }
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                if (spinnerPosition != -1)
                    spinner.setSelection(spinnerPosition);

                //Define how to render the data on the CHILD_TYPE_3 layout
                break;
            case CHILD_TYPE_UNDEFINED:
                //Define how to render the data on the CHILD_TYPE_UNDEFINED layout
                break;
        }
        return convertView;
    }

    private int setAdapterValues(ArrayAdapter<CharSequence> adapter, Map<String, String> dropdownVals, String camProperty, String defValue) {
        int spinnerPosition;
        adapter.addAll(dropdownVals.keySet().toArray(new CharSequence[0]));
        this.dropdownVals.putAll(dropdownVals);
        Log.d(TAG,"dropdownVals: "+dropdownVals.toString() );
        String settingVal = listener.getSetting(camProperty, defValue);
        String compareValue =(String) getKeyFromValue(dropdownVals,settingVal);
        Log.d(TAG,"compareVal: "+compareValue +" settingVal: "+settingVal+"  what:"+adapter.getCount());
        spinnerPosition = adapter.getPosition(compareValue);
        Log.d(TAG,"spinnerPos: "+spinnerPosition);
        return spinnerPosition;
    }
    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
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
                switch (childPosition) {//AutoExposureBracketing
                    case 0:
                        return CHILD_TYPE_1;
                }
            case 1:
                switch (childPosition) {//ImageSettings
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
                    default:
                        return CHILD_TYPE_UNDEFINED;
                }
            case 2:
                switch (childPosition) {//MovieSettings
                    case 0:
                        return CHILD_TYPE_3;//movieQuality
                    case 1:
                        return CHILD_TYPE_3;//ClipRecTime
                    default:
                        return CHILD_TYPE_UNDEFINED;
                }
            case 3:
                switch (childPosition) {//Focusing
                    case 0:
                        return CHILD_TYPE_2;//touchshutter
                    default:
                        return CHILD_TYPE_UNDEFINED;
                }
            case 4:
                switch (childPosition) {//Shooting
                    case 0:
                        return CHILD_TYPE_3;//continousShootingVel
                    case 1:
                        return CHILD_TYPE_3;//selfTimer
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
        String value = dropdownVals.get(parent.getItemAtPosition(position));
        String prop = CameraActivity.extractProperty(value);
        Log.d(TAG, "saved: " + value + "  " + prop);
        listener.saveSetting(prop, value);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
