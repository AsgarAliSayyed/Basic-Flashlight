package com.example.flashlightf;

import static android.content.Context.CAMERA_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import static com.example.flashlightf.R.id.sos_button1;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.provider.MediaStore;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // define the display assembly compass picture

    boolean isFlashlightOn = false;
    CameraManager cameraManager;
    String cameraId;
    TextView flashlightStatus;
    ImageView image;
    Camera camera;
    ImageButton imageSetting;
    ImageView imageOF;
    private static final int CAMERA_REQUEST =1888;
    private boolean isFlashOn = false;
    private boolean isSOSRunning = false;
    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;
    TextView tvHeading;
    ImageButton menu;
    ImageButton sosButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sosButton = findViewById(sos_button1);
        Button flashlightButton = findViewById(R.id.flashlightButton);
        imageOF = findViewById(R.id.imageView);
        imageSetting = findViewById(R.id.setting_button);
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        menu = findViewById(R.id.menu);

        menu.setOnClickListener(v ->{
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        });

        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        flashlightButton.setOnClickListener(v -> toggleFlashlight());

        imageSetting.setOnClickListener(v ->{
            Intent i = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(i);
        });

        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSOSRunning) {
                    isSOSRunning = true;
                    blinkFlash();
                } else {
                    isSOSRunning = false;
                    stopFlash();
                }
            }
        });
    }

public void toggleFlashlight() {
    try {

        if (isFlashlightOn) {
            cameraManager.setTorchMode(cameraId, false);
            isFlashlightOn = false;
            imageOF.setImageResource(R.drawable.timg);

        } else {
            cameraManager.setTorchMode(cameraId, true);
            isFlashlightOn = true;
            imageOF.setImageResource(R.drawable.timg_1);
        }
    } catch (CameraAccessException e) {
        e.printStackTrace();
    }
}
    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            camera = Camera.open(id);
            qOpened = (camera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        // Release the camera and preview
    }
    private void blinkFlash() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isSOSRunning) {
                    try {
                        // Turn on the flash
                        cameraManager.setTorchMode(cameraId, true);
                        Thread.sleep(50); // 50ms on

                        // Turn off the flash
                        cameraManager.setTorchMode(cameraId, false);
                        Thread.sleep(50); // 50ms off
                    } catch (CameraAccessException | InterruptedException e) {
                        // Handle the exception
                    }
                }
            }
        }).start();
    }
    private void stopFlash() {
        try {
            cameraManager.setTorchMode(cameraId, false); // Turn off the flash
        } catch (CameraAccessException e) {
            // Handle the exception
        }
    }
}
