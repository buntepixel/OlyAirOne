package com.example.mail.OlyAirONE;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExpandableListAdapter extends BaseExpandableListAdapter implements AdapterView.OnItemSelectedListener, View.OnClickListener,
        View.OnFocusChangeListener, NumberPicker.OnValueChangeListener {
    private static final String TAG = ExpandableListAdapter.class.getSimpleName();

    // 4 Child types
    private static final int NUMBERPICKER = 0;
    private static final int CHECKBOX = 1;
    private static final int SPINNER = 2;
    private static final int TEXTFIELD = 3;
    private static final int CHILD_TYPE_UNDEFINED = 4;

    final String[] strVal = {"3", "5", "7", "9", "11"};
    final String[] expSprVal = {"1", "2", "3"};

    private Activity context;
    private Map<String, List<String>> myChilds;
    private List<String> myParents;
    private final Map<String, String> dropdownVals = new HashMap<>();
    private CallParentActivtiy listener;


    public ExpandableListAdapter(Activity context, List<String> parent, Map<String, List<String>> childs, CallParentActivtiy listener) {
        this.context = context;
        this.myChilds = childs;
        this.myParents = parent;
        this.listener = listener;
    }


    public interface CallParentActivtiy {
        Map<String, String> getAspectRatioMap();

        Map<String, String> getJpgCompressionMap();

        Map<String, String> getImageSizeMap();

        Map<String, String> getImageSaveDestinationMap();

        Map<String, String> getMovieQualityMap();

        Map<String, String> getClipRecordTimeMap();

        Map<String, String> getContinousShootingSpeedMap();

        Map<String, String> getSelfTimerMap();

        Map<String, String> getFaceDetectionMap();

        Map<String, String> getEmptyMap();

        void saveSetting(String property, String value);

        String getSetting(String property, String defvalue);
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
        Log.d(TAG, "childType childtype: " + childType);

        // We need to create a new "cell container"
        if (convertView == null || !convertView.getTag().equals(childType)) {
            switch (childType) {
                case NUMBERPICKER:
                    Log.d(TAG, "NUMBERPICKER");
                    if (groupPosition == 0) {
                        convertView = inflater.inflate(R.layout.childitem_aeb_camsettingsactivity, parent, false);
                    } else if (groupPosition == 1) {
                        convertView = inflater.inflate(R.layout.childitem_tl_camsettingsactivity, parent, false);
                    }
                    convertView.setTag(childType);
                    break;
                case CHECKBOX:
                    Log.d(TAG, "CHECKBOX");
                    convertView = inflater.inflate(R.layout.childitem_bool_camsettingsactivity, parent, false);
                    convertView.setTag(childType);
                    break;
                case SPINNER:
                    Log.d(TAG, "SPINNER");
                    convertView = inflater.inflate(R.layout.childitem_dropdownchooser_camsettingsactivity, parent, false);
                    convertView.setTag(childType);
                    break;
                case TEXTFIELD:
                    Log.d(TAG, "TEXTFIELD");
                    convertView = inflater.inflate(R.layout.childiten_textfield_camsettingsactivity, parent, false);
                    convertView.setTag(childType);
                    break;
                case CHILD_TYPE_UNDEFINED:
                    Log.d(TAG, "ChildType 4");
                    convertView = inflater.inflate(R.layout.childitem_bool_camsettingsactivity, parent, false);
                    convertView.setTag(childType);
                    break;
                default:
                    // Maybe we should implement a default behaviour but it should be ok we know there are 5 child types right?
                    break;
            }
        }
        // We'll reuse the existing one
        else {
            // There is nothing to do here really we just need to set the content of view which we do in both cases
        }
        TextView txt;
        TextView txtcontent;
        switch (childType) {
            case NUMBERPICKER:
                /*TextView description_child = (TextView) convertView.findViewById(R.id.description_of_ads_expandable_list_child_text_view);
                description_child.setText(incoming_text);*/
                if (groupPosition == 0) {
                    setup_AEB(convertView);
                } else if (groupPosition == 1) {
                    setup_TL(convertView);
                }
                break;
            case CHECKBOX:
                //
                txt = convertView.findViewById(R.id.tv_chbx_discription);
                txt.setText(child);
                CheckBox cbx = convertView.findViewById(R.id.chbx_chbx);
                if (cbx != null) {
                    String setting = "";
                    if (groupPosition == 1) {
                        if (childPosition == 0) {
                            setting = listener.getSetting("TIMELAPSE", "<TIMELAPSE/OFF>");
                            cbx.setTag("TIMELAPSE");//tag used in on click listener
                        }
                    } else if (groupPosition == 2) {
                        if (childPosition == 4) {
                            setting = listener.getSetting("RAW", "<RAW/ON>");
                            cbx.setTag("RAW");//tag used in on click listener
                        } else if (childPosition == 5) {
                            setting = listener.getSetting("RECVIEW", "<RECVIEW/ON>");
                            cbx.setTag("RECVIEW");//tag used in on click listener
                        }
                    } else if (groupPosition == 4) {
                        if (childPosition == 0) {
                            setting = listener.getSetting("TOUCHSHUTTER", "<TOUCHSHUTTER/ON>");
                            cbx.setTag("TOUCHSHUTTER");//tag used in on click listener
                        }
                    }
                    cbx.setOnClickListener(this);
                    Boolean cbxEnabled = setting.equals("") && "ON".equals(CameraActivity.extractValue(setting));
                    if (!cbxEnabled) {
                        cbx.setChecked(true);
                    } else
                        cbx.setChecked(false);
                }

                //Define how to render the data on the CHECKBOX layout
                break;
            case SPINNER:
                Spinner spinner = convertView.findViewById(R.id.sp_ddch_spinner);
                Log.d(TAG, "spinner is null: " + (spinner == null));
                if (spinner != null)
                    spinner.setOnItemSelectedListener(this);
                txt = convertView.findViewById(R.id.tv_ddch_discription);
                txt.setText(child);
                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
                int spinnerPosition = -1;
                if (groupPosition == 2) {//if Image Settings
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

                } else if (groupPosition == 3) { //if movie settings
                    if (childPosition == 0) {
                        spinnerPosition = setAdapterValues(adapter, listener.getMovieQualityMap(), "QUALITY_MOVIE", "<QUALITY_MOVIE/QUALITY_MOVIE_FULL_HD_NORMAL>");
                    } else if (childPosition == 1) {
                        spinnerPosition = setAdapterValues(adapter, listener.getClipRecordTimeMap(), "QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME", "<QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME/5>");
                    } else {
                        adapter.addAll(listener.getEmptyMap().keySet().toArray(new CharSequence[0]));
                    }

                } else if (groupPosition == 4) { //if focus settings
                    if (childPosition == 1) {
                        spinnerPosition = setAdapterValues(adapter, listener.getFaceDetectionMap(), "FACE_SCAN", "<FACE_SCAN/FACE_SCAN_ON>");
                    } else {
                        adapter.addAll(listener.getEmptyMap().keySet().toArray(new CharSequence[0]));
                    }
                } else if (groupPosition == 5) { //if movie settings
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
                if (spinner != null)
                    spinner.setAdapter(adapter);
                if (spinnerPosition != -1)
                    spinner.setSelection(spinnerPosition);

                //Define how to render the data on the SPINNER layout
                break;
            case TEXTFIELD:
                txt = convertView.findViewById(R.id.tv_tv_discription);
                txt.setText(child);
                txtcontent = convertView.findViewById(R.id.tv_tv_content);
                txtcontent.setOnFocusChangeListener(this);
                Log.d(TAG, "networkName: " + listener.getSetting(context.getResources().getString(R.string.pref_ssid), "No saved Network"));
                txtcontent.setText(listener.getSetting(context.getResources().getString(R.string.pref_ssid), "No saved Network"));
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
        return 5; // I defined 4 child types (NUMBERPICKER, CHECKBOX, SPINNER,TEXTFIELD CHILD_TYPE_UNDEFINED)
    }

    public int getChildType(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case 0:
                switch (childPosition) {//AutoExposureBracketing
                    case 0:
                        return NUMBERPICKER;
                }
            case 1:
                switch (childPosition) {//Timelapse
                    case 0:
                        return CHECKBOX;
                    case 1:
                        return NUMBERPICKER;
                }
            case 2:
                switch (childPosition) {//ImageSettings
                    case 0:
                        return SPINNER;//AspectRatio
                    case 1:
                        return SPINNER;//ImageSize
                    case 2:
                        return SPINNER;//jpgCompression
                    case 3:
                        return SPINNER;//ImageDestination
                    case 4:
                        return CHECKBOX;//RawImageSaving
                    case 5:
                        return CHECKBOX;//Create preview image
                    default:
                        return CHILD_TYPE_UNDEFINED;
                }
            case 3:
                switch (childPosition) {//MovieSettings
                    case 0:
                        return SPINNER;//movieQuality
                    case 1:
                        return SPINNER;//ClipRecTime
                    default:
                        return CHILD_TYPE_UNDEFINED;
                }
            case 4:
                switch (childPosition) {//Focusing
                    case 0:
                        return CHECKBOX;//touchshutter

                    case 1:
                        return SPINNER;//faceDetection
                    default:
                        return CHILD_TYPE_UNDEFINED;
                }
            case 5:
                switch (childPosition) {//Shooting
                    case 0:
                        return SPINNER;//continousShootingVel
                    case 1:
                        return SPINNER;//selfTimer
                    default:
                        return CHILD_TYPE_UNDEFINED;
                }
            case 6:
                switch (childPosition) {//Network
                    case 0:
                        return TEXTFIELD;
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
        TextView item = convertView.findViewById(R.id.tv_parent);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(itemName);
        return convertView;
    }

    //------------------------
    //    Helpers
    //------------------------
    private int setAdapterValues(ArrayAdapter<CharSequence> adapter, Map<String, String> dropdownVals, String camProperty, String defValue) {
        int spinnerPosition;
        adapter.addAll(dropdownVals.keySet().toArray(new CharSequence[0]));
        this.dropdownVals.putAll(dropdownVals);
        Log.d(TAG, "dropdownVals: " + dropdownVals.toString());
        String settingVal = listener.getSetting(camProperty, defValue);
        String compareValue = (String) getKeyFromValue(dropdownVals, settingVal);
        Log.d(TAG, "compareVal: " + compareValue + " settingVal: " + settingVal + "  what:" + adapter.getCount());
        spinnerPosition = adapter.getPosition(compareValue);
        Log.d(TAG, "spinnerPos: " + spinnerPosition);
        return spinnerPosition;
    }

    private static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    //------------------------
    //    Setups
    //------------------------


    private void setup_AEB(View convertView) {

        NumberPicker np_nbImagesVal = convertView.findViewById(R.id.np_nbImagesVal);
        setupNumberPicker(np_nbImagesVal, strVal,false, CamSettingsActivity.AEB_IMAGETAG);
        Log.d(TAG, "savedVal: " + listener.getSetting(CamSettingsActivity.AEB_IMAGETAG, "default"));
        int tmp = getArrIdFromValue(strVal, listener.getSetting(CamSettingsActivity.AEB_IMAGETAG, strVal[0]));
        Log.d(TAG, "savedArrVal: " + tmp);
        np_nbImagesVal.setValue(getArrIdFromValue(strVal, listener.getSetting(CamSettingsActivity.AEB_IMAGETAG, strVal[0])));

        NumberPicker np_exposureSpreadVal = convertView.findViewById(R.id.np_exposureSpreadVal);
        setupNumberPicker(np_exposureSpreadVal, expSprVal,false, CamSettingsActivity.AEB_SPREADTAG);
        np_exposureSpreadVal.setValue(getArrIdFromValue(expSprVal, listener.getSetting(CamSettingsActivity.AEB_SPREADTAG, strVal[0])));

        //np_exposureSpreadVal.setValue(Integer.parseInt(listener.getSetting(AEB_SPREADTAG, expSprVal[0])));
    }

    private void setup_TL(View convertView) {
        NumberPicker np_total_100 = convertView.findViewById(R.id.np_nbTotalImg100);
        setupNumberPicker(np_total_100, 9, 0, 0, true, "np_total_100");
        NumberPicker np_total_10 = convertView.findViewById(R.id.np_nbTotalImg10);
        setupNumberPicker(np_total_10, 9, 0, 3, true, "np_total_10");
        NumberPicker np_total_1 = convertView.findViewById(R.id.np_nbTotalImg1);
        setupNumberPicker(np_total_1, 9, 0, 5, true, "np_total_1");

        NumberPicker np_intervall_hrs = convertView.findViewById(R.id.np_intervallTime_Hrs);
        setupNumberPicker(np_intervall_hrs, 23, 0, 0, true, "np_intervall_hrs");
        NumberPicker np_intervall_min = convertView.findViewById(R.id.np_intervallTime_Min);
        setupNumberPicker(np_intervall_min, 59, 0, 1, true, "np_intervall_min");
        NumberPicker np_intervall_sec = convertView.findViewById(R.id.np_intervallTime_Sec);
        setupNumberPicker(np_intervall_sec, 59, 0, 0, true, "np_intervall_sec");
    }

    private int getArrIdFromValue(String[] arr, String val) {
        int counter = 0;
        for (String item : arr) {
            if (item.equals(val)) {
                return counter;
            }
            counter++;
        }
        return -1;
    }
    private void setupNumberPicker(NumberPicker np,  Integer minVal,Integer maxVal, Integer startVal, Boolean wrap, String tag) {
        np.setMinValue(minVal); //from array first value
        np.setMaxValue(maxVal); //to array last value
        np.setTag(tag);
        np.setWrapSelectorWheel(wrap); //wrap.
        np.setOnValueChangedListener(this);
    }

    private void setupNumberPicker(NumberPicker np, String[] strVal,Boolean wrap, String tag) {
        setupNumberPicker(np,0,strVal.length - 1,0,wrap,tag);
        np.setDisplayedValues(strVal);
    }



    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    //------------------------
    //    Interaction
    //------------------------
    @Override
    public void onFocusChange(View view, boolean b) {
        //if(view.getTag()== )
        if (!b) {//if lost focus save textfield
            Log.d(TAG, "focus true");
            listener.saveSetting(context.getResources().getString(R.string.pref_ssid), ((EditText) view).getText().toString());
        }
        //Todo: also implement hide keyboard
    }

    @Override
    public void onValueChange(final NumberPicker numberPicker, final int oldVal, final int newVal) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (newVal == numberPicker.getValue()) {//make sure picker scroll stopped
                    if (numberPicker.getTag() == CamSettingsActivity.AEB_IMAGETAG) {
                        listener.saveSetting(CamSettingsActivity.AEB_IMAGETAG, strVal[numberPicker.getValue()]);
                        Log.d(TAG, "numberpicker changed to val: " + strVal[numberPicker.getValue()]);

                    } else if (numberPicker.getTag() == CamSettingsActivity.AEB_SPREADTAG) {
                        listener.saveSetting(CamSettingsActivity.AEB_SPREADTAG, expSprVal[numberPicker.getValue()]);
                        Log.d(TAG, "numberpicker changed to val: " + expSprVal[numberPicker.getValue()]);
                    }
                    Log.d(TAG, "Arrval changed to val: " + numberPicker.getValue());
                }
            }
        }, 500);//set time
    }

    @Override
    public void onClick(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        //tag gets set in getChildView()....
        if (view.getTag() == "TOUCHSHUTTER") {
            Log.d(TAG, "click touch");
            if (checked)
                listener.saveSetting("TOUCHSHUTTER", "<TOUCHSHUTTER/ON>");
            else
                listener.saveSetting("TOUCHSHUTTER", "<TOUCHSHUTTER/OFF>");

        } else if (view.getTag() == "RAW") {
            Log.d(TAG, "click rawImage");
            if (checked)
                listener.saveSetting("RAW", "<RAW/ON>");
            else
                listener.saveSetting("RAW", "<RAW/OFF>");

        } else if (view.getTag() == "RECVIEW") {
            Log.d(TAG, "click Recview");
            if (checked)
                listener.saveSetting("RECVIEW", "<RECVIEW/ON>");
            else
                listener.saveSetting("RECVIEW", "<RECVIEW/OFF>");
        } else if (view.getTag()=="TIMELAPSE"){
            Log.d(TAG, "click Timelapse");
            if (checked)
                listener.saveSetting("TIMELAPSE", "<TIMELAPSE/ON>");
            else
                listener.saveSetting("TIMELAPSE", "<TIMELAPSE/OFF>");
        }
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
