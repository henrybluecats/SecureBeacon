package com.bluecats.app.securebeacon;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeaconManager;
import com.bluecats.sdk.BCBeaconManagerCallback;
import com.bluecats.sdk.BCLogManager;
import com.bluecats.sdk.BlueCatsSDK;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String APP_TOKEN = "DmEbLZIxK3D8EVAi4BX21sZOKCNHXstaBcZgRf6W4sS2Mte4RDnv25obf62lRaSo"; //Use this App Token to enable BlueCats SDK running

    private RecyclerView rv_list;
    private BeaconListAdapter adapter;

    private BCBeaconManager beaconManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv_list = (RecyclerView)findViewById(R.id.list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        adapter = new BeaconListAdapter();

        rv_list.setLayoutManager(new LinearLayoutManager(this));
        rv_list.setAdapter(adapter);

        beaconManger = new BCBeaconManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BlueCatsSDK.didEnterBackground();
        stopBlueCatsSDK();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startBlueCatsSDK();
        BlueCatsSDK.didEnterForeground();

        checkPermission();
    }

    private void checkPermission() {
        int granded = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (granded != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 0x1010);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private BCBeaconManagerCallback callback = new BCBeaconManagerCallback() {
        @Override
        public void didRangeBlueCatsBeacons(List<BCBeacon> beacons) {
            StringBuilder sb = new StringBuilder();
            List<BeaconItem> items = new ArrayList<>();
            for (BCBeacon beacon: beacons) {
                if (TextUtils.isEmpty(beacon.getBluetoothAddress())) {
                    continue;
                }
//                if (!beacon.getBluetoothAddress().startsWith("0007")) {
//                    continue;
//                }

                BeaconItem item = new BeaconItem(beacon);
                items.add(item);

                sb.append(item.btAddr)./*append('|').append(item.mac).*/append(',');
            }
            adapter.setItems(items);
            Log.d(TAG, "beacons "+sb.toString());
            adapter.notifyDataSetChanged();
        }
    };

    public static String findNameByBtAddr(String bluetoothAddress) {

        /**
         * Add implementation here to map each bluetooth address to a meaningful name
         */
        return bluetoothAddress;
    }

    private void startBlueCatsSDK() {
        Map<String, String> options = new HashMap<>();

        options.put(BlueCatsSDK.BC_OPTION_USE_API, "false");
        options.put(BlueCatsSDK.BC_OPTION_CROWD_SOURCE_BEACON_UPDATES, "false");
        options.put(BlueCatsSDK.BC_OPTION_BEACON_VISIT_TRACKING_ENABLED, "false");
        options.put(BlueCatsSDK.BC_OPTION_USE_LOCAL_STORAGE, "false");
        options.put("BC_OPTION_LOCAL_TOKEN", "true");

        BlueCatsSDK.setOptions(options);

//        BCLogManager.getInstance().setLogLevel(BCLogManager.BC_LOG_TYPE_SCANNER, BCLogManager.BC_LOG_LEVEL_MORE);
//        BCLogManager.getInstance().setLogLevel(BCLogManager.BC_LOG_TYPE_NETWORK, BCLogManager.BC_LOG_LEVEL_MORE);

        BlueCatsSDK.startPurringWithAppToken(this.getApplicationContext(), APP_TOKEN);
        beaconManger.registerCallback(callback);
    }

    private void stopBlueCatsSDK() {

        beaconManger.unregisterCallback(callback);
        BlueCatsSDK.stopPurring();
    }
}
