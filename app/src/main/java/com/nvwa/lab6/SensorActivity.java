package com.nvwa.lab6;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    Sensor mSensor;
    boolean isFlashOn = false, hasFlash = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        Intent sensTypeIntent = getIntent();
        int sensType = sensTypeIntent.getIntExtra( MainActivity.SENSOR_TYPE, -1 );
        if (sensType != -1)
            mSensor = MainActivity.mSensorManager.getDefaultSensor(sensType);
        else
            Toast.makeText( getApplicationContext(), "Wrong sensor type", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String valueText = "";
        String valStr = "Val: ";
        for (Float val : event.values) {
            if (val != 0)
                valueText += (valStr + val.toString() + "\n");
        }
        TextView valueTxtV = (TextView)findViewById(R.id.sensVals);
        valueTxtV.setText(valueText);

        if (event.sensor.getType() == Sensor.TYPE_LIGHT || event.sensor.getType() == Sensor.TYPE_PROXIMITY ) {
            float sensVal = event.values[0];

            if (hasFlash) {
                if ( (event.sensor.getType() == Sensor.TYPE_LIGHT && sensVal<100) ||
                        (event.sensor.getType() == Sensor.TYPE_PROXIMITY && sensVal == 0) ) {
                    turnOnFlash();
                } else
                    turnOffFlash();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        MainActivity.mSensorManager.registerListener(this, mSensor, 500000 );
        TextView labelTxtV = (TextView)findViewById(R.id.sensLabel);
        labelTxtV.setText(mSensor.getName());

        if (mSensor.getType() == Sensor.TYPE_LIGHT || mSensor.getType() == Sensor.TYPE_PROXIMITY ) {
            hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            if (!hasFlash){
                Toast.makeText(getApplicationContext(), "Your device doesn't have a flashlight", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.mSensorManager.unregisterListener(this, mSensor);

        if (isFlashOn)
            turnOffFlash();
    }

    private void turnOnFlash() {
        if (!isFlashOn) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Camera camera = Camera.open();
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview();
            } else {
                CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                String cameraId = null;
                try {
                    cameraId = camManager.getCameraIdList()[0];
                    camManager.setTorchMode(cameraId, true );
                } catch ( CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            isFlashOn = true;
        }
    }

    private void turnOffFlash() {
        if (!isFlashOn) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Camera camera = Camera.open();
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.startPreview();
            } else {
                CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                String cameraId = null;
                try {
                    cameraId = camManager.getCameraIdList()[0];
                    camManager.setTorchMode(cameraId, false);
                } catch ( CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            isFlashOn = false;
        }

    }
}
