package com.example.wifiheatmap;

import android.net.wifi.WifiManager;
import android.view.View;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;

public class HeatMap
    extends ActionBarActivity
    implements SensorEventListener
{
    private TextView      stepNum;
    private TextView      compass;
    private TextView      directionText;
    private SensorManager sensorManager;
    private float         previousY;
    private float         currentY;
    private int           numSteps;
    private int           threshold;
    private TextView      wifiStrength;
    private TextView      startDegree;
    private MainActivity  main;
    private float[]       gravity;
    private float[]       geoMagnetic;
    private float         azimuth;
    private double        azimuthDouble;
    private boolean       first;
    private String        direction;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat_map);

        stepNum = (TextView)findViewById(R.id.numSteps);
        compass = (TextView)findViewById(R.id.compass);
        wifiStrength = (TextView)findViewById(R.id.wifiStrength);
        startDegree = (TextView)findViewById(R.id.startDegree);
        directionText = (TextView)findViewById(R.id.direction);

        threshold = 8;
        previousY = 0;
        currentY = 0;
        numSteps = 0;
        first = true;

        main = new MainActivity();
        initListening();
    }


    private void initListening()
    {
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void resetSteps()
    {
        numSteps = 0;
        stepNum.setText(String.valueOf(numSteps));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.heat_map, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.getWifiStrength)
        {
            Intent launchNewIntent =
                new Intent(HeatMap.this, MainActivity.class);
            startActivityForResult(launchNewIntent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onSensorChanged(SensorEvent event)
    {
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                gravity = event.values;
                float y = gravity[1];
                currentY = y;
                if (Math.abs(currentY - previousY) > threshold)
                {
                    numSteps++;
                    stepNum.setText("Number of Steps: " + numSteps);
                    if (numSteps % 2 == 0)
                    {
                        WifiManager wifi =
                            (WifiManager)getSystemService(Context.WIFI_SERVICE);
                        int strength = main.getWifiStrength(wifi);
                        wifiStrength.setText("Wifi Strength: " + strength);
                    }
                }
                previousY = y;

                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geoMagnetic = event.values;
                break;
        }
        if (geoMagnetic != null && gravity != null)
        {
            float R[] = new float[9];
            float I[] = new float[9];
            double firstDegree = 0;
            boolean works =
                SensorManager.getRotationMatrix(R, I, gravity, geoMagnetic);
            if (works)
            {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = orientation[0];
                azimuthDouble = Math.toDegrees(azimuth);
                String output = String.format("%.2f", azimuthDouble);
                if (first)
                {
                    firstDegree = azimuthDouble;
                    startDegree.setText("Start Degree: " + output);
                    first = false;
                }
                compass.setText("Angle(Degrees): " + output);
                checkChange(firstDegree, azimuthDouble);
            }

        }
    }


    private void checkChange(double first, double second)
    {
        // half of 45 which is the length to either side of compass direction
        if (Math.abs(first - second) > 22.5)
        {
            changeDirection(Math.abs(first - second));
        }
    }


    private void changeDirection(double a)
    {

    }


    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // eh unneeded
    }

}
