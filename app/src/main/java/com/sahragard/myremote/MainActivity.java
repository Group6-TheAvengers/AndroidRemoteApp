package com.sahragard.myremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    public TextView statusUpdate;
    public Button connectButton;
    public Button disconnectButton;
    public Button connectDevice;
    public Spinner conSpinner;
    ArrayAdapter<String> adapter;
    String selectedDeviceName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Bluetooth bt = new Bluetooth(MainActivity.this, btAdapter);

        /*

        REMINDER
        START DISCOVERY MUST BE TRIGGERED AFTER BLUETOOTH HAVE STARTED

         */

        //Displays bluetooth status in the top
        statusUpdate = (TextView) findViewById(R.id.statusUpdate);

        //Spinner that contains devices to connect to
        conSpinner = (Spinner) findViewById(R.id.spinner);
        conSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //IF TOSTRING ERROR ARISES CHECK THIS
                selectedDeviceName = conSpinner.getSelectedItem().toString();

            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }

        });




        //Start bluetooth and find devices
        connectButton = (Button) findViewById(R.id.connect);
        connectButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (!btAdapter.isEnabled()) {

                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);

                    btAdapter.startDiscovery();

                    bt.findDevices();
                    statusUpdate.setText("Bluetooth on");
                }
            }
        });
        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //adapter.clear();
                //Adapter that hold device strings
                adapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_spinner_item, bt.getFoundDeviceNames());

                conSpinner.setAdapter(adapter);
            }
        });

        //Disable bluetooth
        disconnectButton = (Button) findViewById(R.id.disconnect);
        disconnectButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                btAdapter.disable();
                adapter.clear();

                statusUpdate.setText("Bluetooth off");
                //Clear list to prevent duplicate entries

            }
        });

        //Connect to selected device
        connectDevice = (Button) findViewById(R.id.connectDevice);
        connectDevice.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                for (BluetoothDevice device : bt.getFoundDevices()) {
                    if (device != null && device.getName().equals(selectedDeviceName)) {
                        bt.connect(device);
                    }
                }
            }
        });


        if (btAdapter.isEnabled()) {
            btAdapter.cancelDiscovery();
            ;
            btAdapter.startDiscovery();
            statusUpdate.setText("Bluetooth on");
            bt.findDevices();

        } else {
            statusUpdate.setText("Bluetooth off");
        }


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