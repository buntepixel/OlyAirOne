package com.example.mail.OlyAirONE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraConnectionListener;
import jp.co.olympus.camerakit.OLYCameraKitException;


public class ImageViewActivity extends AppCompatActivity implements OLYCameraConnectionListener {
    private static final String TAG = ImageViewActivity.class.getSimpleName();
    public static OLYCamera camera;
    ImageGridViewFragment fImgGridView;

    private static final String FRAGMENT_TAG_IMGGRIDVIEW = "imgGridView";
    Executor connectionExecutor = Executors.newFixedThreadPool(1);
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        camera = new OLYCamera();
        camera.setContext(getApplicationContext());
        camera.setConnectionListener(this);
        fm = getSupportFragmentManager();
        if (savedInstanceState != null) {
            fImgGridView = (ImageGridViewFragment) fm.getFragment(savedInstanceState, FRAGMENT_TAG_IMGGRIDVIEW);
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setSupportActionBar(toolbar);
        fImgGridView = new ImageGridViewFragment();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fm.findFragmentByTag(FRAGMENT_TAG_IMGGRIDVIEW) != null)
            fm.putFragment(outState, FRAGMENT_TAG_IMGGRIDVIEW, fImgGridView);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "START Resume");
        if (!camera.isConnected()) {
            Log.d(TAG, "Cam Is NOT connected");
            startConnectingCamera();
        } else {
            Log.d(TAG, "Cam Is connected");
            onConnectedToCamera();
        }
        Log.d(TAG, "END Resume");
    }

    //------------------------
    //    Connecting Camera
    //------------------------
    private void startConnectingCamera() {
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    camera.connect(OLYCamera.ConnectionType.WiFi);
                } catch (OLYCameraKitException e) {
                    Log.d(TAG,"erron wifi");
                    alertConnectingFailed(e);
                    return;
                }

                try {
                    camera.changeRunMode(OLYCamera.RunMode.Playback);
                } catch (OLYCameraKitException e) {
                    Log.d(TAG,"erron Playback");
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
        ImageGridViewFragment frag = (ImageGridViewFragment) fm.findFragmentById(R.id.fl_imgViewAction_content);
        if(frag==null){
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.fl_imgViewAction_content, fImgGridView, FRAGMENT_TAG_IMGGRIDVIEW);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        if (camera.isConnected())
            intent.putExtra("correctNetwork", false);
        else
            intent.putExtra("correctNetwork", true);
        startActivity(intent);
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
