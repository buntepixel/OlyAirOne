package com.example.mail.fragmenttest;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class LiveViewFragment extends Fragment {
    int[] modeArr = new int[]{R.drawable.ic_iautomode, R.drawable.ic_programmmode, R.drawable.ic_aparturemode, R.drawable.ic_shuttermode, R.drawable.ic_manualmode, R.drawable.ic_artmode, R.drawable.ic_videomode};
    int currDriveMode = 0;

    private CameraLiveImageView imageView;

    private OnLiveViewInteractionListener mListener;

    public interface OnLiveViewInteractionListener {
        void onMainSettingsButtonPressed(int currDriveMode);
    }

    public LiveViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            return;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_view, container, false);
        view.setId(View.generateViewId());
        imageView = (CameraLiveImageView)view.findViewById(R.id.cameraLiveImageView);

        //recordMode
        final ImageButton ib_RecordMode = (ImageButton) view.findViewById(R.id.ib_RecordMode);
        ib_RecordMode.setOnClickListener(new View.OnClickListener() {
            int counter = 0;

            @Override
            public void onClick(View v) {
                counter++;
                int counterMod = counter % modeArr.length;
                ib_RecordMode.setImageResource(modeArr[counterMod]);
                //currDriveMode = counterMod;
                if (mListener != null)
                    mListener.onMainSettingsButtonPressed(counter % (modeArr.length));
                //SetMainSettingsButtons(counter % (modeArr.length));
                //Log.d(TAG, "start"+ counter);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnLiveViewInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTriggerFragmInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
