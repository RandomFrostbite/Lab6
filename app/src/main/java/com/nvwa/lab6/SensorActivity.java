package com.nvwa.lab6;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    Sensor mSensor;

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.mSensorManager.unregisterListener(this, mSensor);
    }
}
