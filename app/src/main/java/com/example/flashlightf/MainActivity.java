package com.example.flashlightf;


import static com.example.flashlightf.R.id.sos_button1;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // define the display assembly compass picture

    boolean isFlashlightOn = false;
    CameraManager cameraManager;
    String cameraId;
    ImageButton imageSetting;
    ImageView imageOF;
    private boolean isSOSRunning = false;

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
    private void blinkFlash() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isSOSRunning) {
                    try {
                        // Turn on the flash
                        cameraManager.setTorchMode(cameraId, true);
                        Thread.sleep(10); // 50ms on

                        // Turn off the flash
                        cameraManager.setTorchMode(cameraId, false);
                        Thread.sleep(10); // 50ms off
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
