package com.example.sriram.phoneapp;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.NetworkStats;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.CpuUsageInfo;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;



public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_ALL = 1 ;
    Button btBtn,imeiBtn, wifiBtn, dataBtn, signalBtn,audiojackBtn,usbBtn,CPUBtn,ramBtn,tempBtn;
    TextView btText,imeiText, wifiText, dataText, signalText,audiojackText,usbText,CPUText,ramText,tempText;
    static final int PERMISSION_READ_STATE = 123;
    static final int PERMISSION_ACCESS_STATE = 99;

    Button button;
    IntentFilter intentfilter,intentFilter2;
    float batteryTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imeiBtn = findViewById(R.id.imeiBtn);
        wifiBtn = (Button) findViewById(R.id.wifiBtn);
        dataBtn = (Button) findViewById(R.id.dataBtn);
        signalBtn = (Button) findViewById(R.id.signalBtn);
        btBtn = findViewById (R.id.btBtn);
        audiojackBtn = findViewById (R.id.audiojackBtn);
        usbBtn = findViewById (R.id.usbBtn);
        CPUBtn = findViewById (R.id.CPUBtn);
        ramBtn = findViewById (R.id.ramBtn);
        tempBtn = findViewById (R.id.tempBtn);

        wifiText = (TextView) findViewById(R.id.wifiText);
        imeiText = (TextView) findViewById(R.id.imeiText);
        dataText = (TextView) findViewById(R.id.dataText);
        signalText = (TextView) findViewById(R.id.signalText);
        btText = findViewById (R.id.btText);
        audiojackText = findViewById (R.id.audiojackText);
        usbText = findViewById (R.id.usbText);
        CPUText = findViewById (R.id.CPUtext);
        ramText = findViewById (R.id.ramText);
        tempText = findViewById (R.id.tempText);


        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        intentFilter2 = new IntentFilter (UsbManager.ACTION_USB_ACCESSORY_ATTACHED);


        ramBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

            }
        });

        CPUBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {




            }
        });

        usbBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
            Intent intent = MainActivity.this.registerReceiver (null,new IntentFilter (Intent.ACTION_BATTERY_CHANGED));
                assert intent != null;
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                Log.d("Tag","Plugged is " + plugged);

            if(plugged == BatteryManager.BATTERY_PLUGGED_USB){
                usbText.setText ("Usb is active!");
            }
            else {
                usbText.setText ("Usb is inactive!");
            }

            }
        });

        audiojackBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                AudioManager audioManager = (AudioManager) getSystemService (Context.AUDIO_SERVICE);
                if(audioManager.isWiredHeadsetOn ()){
                    audiojackText.setText ("AudioJack in use");
                }else{
                    audiojackText.setText ("AudioJack is not in use");
                }
            }
        });


        btBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    // Device does not support Bluetooth
                    Toast.makeText (getApplicationContext (),"Device doesn't support bluetooth",Toast.LENGTH_LONG).show ();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        // Bluetooth is not enable :)
                        btText.setText ("Bluetooth is disabled");

                    }
                    else{
                        btText.setText ("Bluetooth is enabled");
                    }
                }
            }
        });


       signalBtn.setOnClickListener (new View.OnClickListener () {
           @Override
           public void onClick(View v) {
               ConnectionQuality connectionQuality = ConnectionClassManager.getInstance ().getCurrentBandwidthQuality ();
               signalText.setText (connectionQuality.toString ());
           }
       });

        dataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reflection Used
                boolean mobileDataEnabled = false; // Assume disabled
                ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                try {
                    Class cmClass = Class.forName(cm.getClass().getName());
                    Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                    method.setAccessible(true); // Make the method callable
                    // get the setting for "mobile data"
                    mobileDataEnabled = (Boolean)method.invoke(cm);
                    if(mobileDataEnabled){
                        dataText.setText("Data Enabled");
                    }
                    else{
                        dataText.setText("Data Disabled");
                    }
                } catch (Exception e) {
                    // Some problem accessible private API
                    // TODO do whatever error handling you want here
                }

            }
        });

        imeiBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Button clicked", Toast.LENGTH_SHORT).show();
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    //Toast.makeText(getApplicationContext(), "prev dagdga", Toast.LENGTH_SHORT).show();

                    //Toast.makeText(getApplicationContext(), "dagdga", Toast.LENGTH_SHORT).show();
                    String imei = null;
                    if (telephonyManager != null)
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        imei = telephonyManager.getImei();
                    //Log.e("imei", "=" + imei);
                    if (imei != null && !imei.isEmpty()) {
                        imeiText.setText("YOUR IMEI : " + imei);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Exception ran",Toast.LENGTH_SHORT).show();
                }
            }

        });




        wifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if(wifiManager.isWifiEnabled()){
                    wifiText.setText("Wifi Enabled");
                }
                else{
                    wifiText.setText("Wifi Disabled");
                }
            }
        });



        tempBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                MainActivity.this.registerReceiver (broadcastReceiver2,intentfilter);
            }
        });
    }





    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver () {
        @Override
        public void onReceive(Context context, Intent intent) {
            batteryTemp = (float)(intent.getIntExtra (BatteryManager.EXTRA_TEMPERATURE,0))/10;

            tempText.setText ("Current Device Temperature : " + batteryTemp + " " + (char)0x00B0 + "C");

        }
    };



    @Override
    protected void onStart() {
        super.onStart();
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) { }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION}
                    , PERMISSION_ALL);
        }

//        if(permissionCheck1 == PackageManager.PERMISSION_GRANTED){
//
//        }
//        else{
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}
//                    , PERMISSION_ACCESS_STATE);
//        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);



        switch(requestCode){
            case PERMISSION_READ_STATE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        }
                else{

                    String response = "You don't have the required permission for this action!";
                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                }
            }

            case PERMISSION_ACCESS_STATE:
            {
                if(grantResults.length > 0 && (grantResults[1] == PackageManager.PERMISSION_GRANTED)){}
                else{
                    String response = "You don't have the required permission for this action!";
                    Toast.makeText(getApplicationContext(),response + grantResults.length,Toast.LENGTH_LONG).show();
                }
            }
        }
    }



}

