package com.example.mail.fragmenttest;


import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class WificredentialsDialogueFragment extends DialogFragment {
    private static final String TAG = WificredentialsDialogueFragment.class.getSimpleName();

    static WificredentialsDialogueFragment newInstance() {
        WificredentialsDialogueFragment f = new WificredentialsDialogueFragment();
        return f;
    }

    EditText ssid, pw;
    private SaveCredentialsListener mListener;

    public interface SaveCredentialsListener {
        void OnSaveCredentials(String ssid, String pw);
    }

    public void setSaveCredentialsListener(SaveCredentialsListener listener) {
        this.mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialogue_wificredentials, container, false);
        Button save = (Button) v.findViewById(R.id.btn_save);
        Button cancel = (Button) v.findViewById(R.id.btn_cancel);
        ssid = (EditText) v.findViewById(R.id.et_SSID);
        pw = (EditText) v.findViewById(R.id.et_password);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    //mListener.OnSaveCredentials("SSID;)", "MyPassword");
                    Log.d(TAG, "SSID: " + ssid.getText().toString() + " pw: " + pw.getText().toString());
                    Context context = getActivity();
                    //setting credentials
                    SharedPreferences mySettings = context.getSharedPreferences(context.getResources().getString(R.string.pref_wifinetwork), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mySettings.edit();
                    editor.putString(getResources().getString(R.string.pref_ssid), ssid.getText().toString());
                    editor.putString(getResources().getString(R.string.pref_Pw), pw.getText().toString());
                    editor.commit();
                    mListener.OnSaveCredentials(ssid.getText().toString(), pw.getText().toString());
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return v;
    }
  /*  public void Dismiss(){
        getDialog().dismiss();
    }*/

}
