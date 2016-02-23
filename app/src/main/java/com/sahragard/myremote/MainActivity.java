package com.sahragard.myremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    public TextView statusUpdate;
    public Button enableBT;
    public Button disableBT;
    public Button connectDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Bluetooth bt = new Bluetooth(MainActivity.this, btAdapter, MainActivity.this);

        //Displays bluetooth status in the top
        statusUpdate = (TextView) findViewById(R.id.statusUpdate);

        if (btAdapter.isEnabled()) {
            statusUpdate.setText("Bluetooth on");
            btAdapter.startDiscovery();
            bt.findDevices();

        } else {
            statusUpdate.setText("Bluetooth off");
        }


        //Start bluetooth and find devices
        enableBT = (Button) findViewById(R.id.connect);
        enableBT.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (!btAdapter.isEnabled()) {
                    bt.enableBT();

                    //Waits until bluetooth is enabled, otherwise discovery will not start
                    while (!btAdapter.isEnabled()) {
                    }

                    btAdapter.startDiscovery();
                    bt.findDevices();



                    statusUpdate.setText("Bluetooth on");
                } else {
                    Toast.makeText(MainActivity.this, "Bluetooth already enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Disable bluetooth
        disableBT = (Button) findViewById(R.id.disconnect);
        disableBT.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                bt.disableBT();

                //Clear list to prevent duplicate entries
                bt.getDeviceList().clear();
                statusUpdate.setText("Bluetooth off");
            }
        });

        //Connect to selected device
        connectDevice = (Button) findViewById(R.id.connectDevice);
        connectDevice.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                for (BluetoothDevice device : bt.getBluetoothDevices()) {
                    if (device != null) {
                        if (device.getName().equals(bt.selectedDeviceName)) {
                            bt.connect(device);
                        }
                    }
                }
            }
        });


        final Button up = (Button) findViewById(R.id.upButton);
        up.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                bt.send("w");
            }
        });

        final Button down = (Button) findViewById(R.id.downButton);
        down.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                bt.send("s");
            }
        });

        final Button brake = (Button) findViewById(R.id.brake);
        brake.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                bt.send("f");
            }
        });

        final Button drift = (Button) findViewById(R.id.drift);
        drift.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                bt.send("e");
            }
        });

        final Button left = (Button) findViewById(R.id.leftButton);
        left.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                bt.send("a");
            }
        });

        final Button right = (Button) findViewById(R.id.rightButton);
        right.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                bt.send("d");
            }
        });


    }
}