package com.example.mail.OlyAirONE;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraConnectionListener;
import jp.co.olympus.camerakit.OLYCameraKitException;


public class ImageViewActivity extends AppCompatActivity implements OLYCameraConnectionListener {
    private static final String TAG = ImageViewActivity.class.getSimpleName();
    public static OLYCamera camera;
    Fragment fImgGridView;

    private static final String FRAGMENT_TAG_IMGGRIDVIEW= "imgGridView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        camera = new OLYCamera();
        setContentView(R.layout.activity_image_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageGridViewFragment fImgGridView = new ImageGridViewFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fl_imgViewAction_content, fImgGridView, FRAGMENT_TAG_IMGGRIDVIEW);
        transaction.commit();
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
