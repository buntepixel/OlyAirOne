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

                //if Image Settings
                if (groupPosition == 1) {
                    if (childPosition == 0) {
                        adapter.addAll(listener.getAspectRatioMap().keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(listener.getAspectRatioMap());
                    } else if (childPosition == 1) {
                        adapter.addAll(listener.getImageSizeMap().keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(listener.getImageSizeMap());
                        String compareValue = listener.getSetting("IMAGESIZE", "1024x768");
                        Log.d(TAG,"compareVal: "+compareValue);
                        if (!compareValue.equals(null)) {
                            int spinnerPosition = adapter.getPosition(compareValue);
                            Log.d(TAG,"spinnerPos: "+spinnerPosition);
                            spinner.setSelection(spinnerPosition);
                        }
                    } else if (childPosition == 2) {
                        //CharSequence[] player_names = players.keySet().toArray(new CharSequence[0]);
                        adapter.addAll(listener.getJpgCompressionMap().keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(listener.getJpgCompressionMap());
                    } else if (childPosition == 3) {
                        adapter.addAll(listener.getImageSaveDestinationMap().keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(listener.getImageSaveDestinationMap());
                    } else {
                        adapter.addAll(listener.getEmptyMap().keySet().toArray(new CharSequence[0]));
                    }

                } //if movie settings
                else if (groupPosition == 2) {
                    if (childPosition == 0) {
                        adapter.addAll(listener.getMovieQualityMap().keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(listener.getMovieQualityMap());
                    } else if (childPosition == 1) {
                        adapter.addAll(listener.getClipRecordTimeMap().keySet().toArray(new CharSequence[0]));
                        dropdownVals.putAll(listener.getClipRecordTimeMap());
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
        Log.d(TAG, "selected: " + value + "  " + prop);
        listener.saveSetting(prop, value);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
