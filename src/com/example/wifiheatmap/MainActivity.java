package com.example.wifiheatmap;

import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity
    extends ActionBarActivity

{
    private TextView text;
    private TextView text2;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView)findViewById(R.id.numSteps);
        text2 = (TextView)findViewById(R.id.textView2);
        Button getWifiStrength = (Button)findViewById(R.id.set);
        getWifiStrength.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                WifiManager wifi =
                    (WifiManager)getSystemService(Context.WIFI_SERVICE);
                int wifiStrength = getWifiStrength(wifi);
                text.setText("Wifi Strength: " + wifiStrength);
            }
        });

    }


    protected void onPause()
    {
        super.onPause();
    }


    protected void onResume()
    {
        super.onResume();
    }


    public int getWifiStrength(WifiManager wifiManager)
    {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = wifiInfo.getRssi();
        return level;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.heatMap)
        {
            Intent launchNewIntent =
                new Intent(MainActivity.this, HeatMap.class);
            startActivityForResult(launchNewIntent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
