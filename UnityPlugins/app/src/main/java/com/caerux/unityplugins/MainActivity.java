package com.caerux.unityplugins;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView batLevel, batScale, batPct;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        batLevel = findViewById(R.id.tv_level);
        batScale = findViewById(R.id.tv_scale);
        batPct = findViewById(R.id.tv_pct);
        context = this;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID){
            case R.id.action_battery_status:
                getBatteryStatus(context);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void getBatteryStatus(Context context){
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);
        int bLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int bScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = bLevel/(float)bScale;
        batLevel.setText(Integer.toString(bLevel));
        batScale.setText(Integer.toString(bScale));
        batPct.setText(Float.toString(batteryPct));
    }
}
