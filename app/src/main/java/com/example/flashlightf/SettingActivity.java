package com.example.flashlightf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    private SeekBar seekBar;
    private ToggleButton toggleButton;
    private TextView speedTextView; // Add a TextView to display the speed
    private CameraManager cameraManager;
    private volatile boolean isFlashOn = false;
    private int flashSpeed = 0; // minimum speed
    private Thread blinkThread;
    ImageButton back_image;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        seekBar = findViewById(R.id.seekBar);
        toggleButton = findViewById(R.id.toggleButton);
        speedTextView = findViewById(R.id.speedTextView);
        back_image = findViewById(R.id.arrow);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        seekBar.setMax(14); // 0 to 14, so 15 steps
        seekBar.setProgress(0); // initial progress

        back_image.setOnClickListener(v ->{
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Change the speed of SOS flash light based on the seekbar progress
                flashSpeed = progress;
                // Update the speed text view to display the speed in milliseconds
                int speedInMs = 50 + (flashSpeed * 100); // 50ms to 1500ms
                speedTextView.setText(speedInMs + "ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    // Start flashing SOS
                    startSOSFlash();
                } else {
                    // Stop flashing SOS
                    stopSOSFlash();
                }
            }
        });
    }

    // Add the startSOSFlash() and stopSOSFlash() methods
    private void startSOSFlash() {
        isFlashOn = true;
        blinkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isFlashOn) {
                    flashSOS();
                }
            }
        });
        blinkThread.start();
    }

    private void stopSOSFlash() {
        isFlashOn = false;
        if (blinkThread != null) {
            blinkThread.interrupt();
        }
    }

    private void flashSOS() {
        String sosCode = "......";
        for (char c : sosCode.toCharArray()) {
            if (c == '.') {
                // Flash on for 100ms
                flashOn(100);
            } else if (c == '-') {
                // Flash on for 300ms
                flashOn(300);
            }
            // Pause between flashes
            try {
                int pauseDuration = 50 + (flashSpeed * 100); // 50ms to 1500ms
                Thread.sleep(pauseDuration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void flashOn(int duration) {
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}