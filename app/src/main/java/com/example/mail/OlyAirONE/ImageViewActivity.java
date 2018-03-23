package com.example.mail.OlyAirONE;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraConnectionListener;
import jp.co.olympus.camerakit.OLYCameraKitException;


public class ImageViewActivity extends AppCompatActivity implements OLYCameraConnectionListener, FragmentImageGridView.ImagerGridViewInteractionListener {
    private static final String TAG = ImageViewActivity.class.getSimpleName();
    public static OLYCamera camera;
    private FragmentImageGridView fImgGridView;
    private FragmentImagePagerView fPagerViewFragment;

    public static final String FRAGMENT_TAG_IMGGRIDVIEW = "imgGridView";
    public static final String FRAGMENT_TAG_IMGPAGEVIEWE = "imgPageView";

    Executor connectionExecutor = Executors.newFixedThreadPool(1);
    android.app.FragmentManager fm;
    String currFragStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        camera = new OLYCamera();
        camera.setContext(getApplicationContext());
        camera.setConnectionListener(this);
        fm = getFragmentManager();
        if (savedInstanceState != null) {
            currFragStr = savedInstanceState.getString("currFragStr");
            Log.d(TAG, "currFragStr: " + currFragStr);
            if (currFragStr.equals(FRAGMENT_TAG_IMGGRIDVIEW))
                fImgGridView = (FragmentImageGridView) fm.getFragment(savedInstanceState, currFragStr);
            else if (currFragStr.equals(FRAGMENT_TAG_IMGPAGEVIEWE))
                fPagerViewFragment = (FragmentImagePagerView) fm.getFragment(savedInstanceState, currFragStr);
            return;
        }
        fImgGridView = new FragmentImageGridView();
        fImgGridView.setImageGridViewInteractionListener(this);
        currFragStr = FRAGMENT_TAG_IMGGRIDVIEW;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currFragStr", currFragStr);
        if (fm.findFragmentByTag(FRAGMENT_TAG_IMGGRIDVIEW) != null)
            fm.putFragment(outState, FRAGMENT_TAG_IMGGRIDVIEW, fImgGridView);
        else if (fm.findFragmentByTag(FRAGMENT_TAG_IMGPAGEVIEWE) != null) {
            fm.putFragment(outState, FRAGMENT_TAG_IMGPAGEVIEWE, fPagerViewFragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "START Resume");
        if (!camera.isConnected()) {
           // Log.d(TAG, "Cam Is NOT connected");
            startConnectingCamera();
        } else {
           // Log.d(TAG, "Cam Is connected");
            onConnectedToCamera();
        }
        Log.d(TAG, "END Resume");
    }

    @Override
    public void OnFragmentChange(String fragmentName) {
        currFragStr = fragmentName;
    }

    //------------------------
    //    Connecting Camera
    //------------------------
    private void startConnectingCamera() {
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "startConnecting Cam");
                try {
                    camera.connect(OLYCamera.ConnectionType.WiFi);
                } catch (OLYCameraKitException e) {
                    Log.d(TAG, "erron wifi");
                    alertConnectingFailed(e);
                    return;
                }
                try {
                    camera.changeRunMode(OLYCamera.RunMode.Playback);
                } catch (OLYCameraKitException e) {
                    Log.d(TAG, "erron Playback");
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
        Log.d(TAG, "Connected to Cam");
        Fragment currFrag = fm.findFragmentById(R.id.fl_imgViewAction_content);
        if (currFrag == null) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.fl_imgViewAction_content, fImgGridView, FRAGMENT_TAG_IMGGRIDVIEW);
            transaction.commit();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        //saveCamSettings();
        try {
            camera.disconnectWithPowerOff(false);
        } catch (OLYCameraKitException e) {
            Log.w(this.toString(), "To disconnect from the camera is failed.");
        }
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
}
