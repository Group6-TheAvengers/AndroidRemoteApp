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
    List<BluetoothDevice> allDevices = new ArrayList<BluetoothDevice>();
    List<String> allDeviceNames = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String selectedDeviceName = "";
    BluetoothDevice connectToDevice;
    BluetoothSocket btSocket;
    PrintWriter output;
    Scanner input;
    UUID uuid;
    Thread workerThread;
    Thread inputThread;
    public BluetoothDevice device;
    private Intent enableBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*


        //START DISCOVERY MUST BE TRIGGERED AFTER BLUETOOTH HAVE STARTED

         */

        final Handler handler = new Handler();
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    //Do work
                }
            }
        });
        final Handler handler1 = new Handler();

        inputThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    while (input.hasNextLine())
                        Toast.makeText(MainActivity.this, input.nextLine(), Toast.LENGTH_SHORT).show();
                }
            }
        });


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

        //Adapter that hold device strings
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, allDeviceNames);


        //Start bluetooth and find devices
        connectButton = (Button) findViewById(R.id.connect);
        connectButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (!btAdapter.isEnabled()) {

                    enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);

                    btAdapter.startDiscovery();

                    findDevices();
                    statusUpdate.setText("Bluetooth on");
                }
            }
        });
        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                adapter.clear();
                btAdapter.startDiscovery();
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
                for (BluetoothDevice device : allDevices) {
                    if (device != null) {
                        if (device.getName().equals(selectedDeviceName)) {
                            Toast.makeText(MainActivity.this, selectedDeviceName, Toast.LENGTH_SHORT).show();
                            connectToDevice = device;

                            try {
                                uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                                btSocket = connectToDevice.createRfcommSocketToServiceRecord(uuid);
                                btSocket.connect();
                                output = new PrintWriter(btSocket.getOutputStream());
                                workerThread.start();
                                input = new Scanner(btSocket.getInputStream());
                                inputThread.start();
                                statusUpdate.setText("Connected to " + connectToDevice.getName());

                            } catch (IOException e) {

                            }
                        }
                    }
                }
            }
        });


        if (btAdapter.isEnabled()) {
            btAdapter.cancelDiscovery();
            ;
            btAdapter.startDiscovery();
            statusUpdate.setText("Bluetooth on");
            findDevices();

        } else {
            statusUpdate.setText("Bluetooth off");
        }


        final Button up = (Button) findViewById(R.id.upButton);
        up.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                send("w");
            }
        });

        final Button down = (Button) findViewById(R.id.downButton);
        down.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                send("s");
            }
        });

        final Button brake = (Button) findViewById(R.id.brake);
        brake.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                send("f");
            }
        });

        final Button drift = (Button) findViewById(R.id.drift);
        drift.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                send("e");
            }
        });

        final Button left = (Button) findViewById(R.id.leftButton);
        left.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                send("a");
            }
        });

        final Button right = (Button) findViewById(R.id.rightButton);
        right.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                send("d");
            }
        });


    }

    private void findDevices() {
        allDeviceNames.clear();
        allDevices.clear();

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                Toast.makeText(MainActivity.this, "Discovery started", Toast.LENGTH_SHORT).show();

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                Toast.makeText(MainActivity.this, "Discovery finished", Toast.LENGTH_SHORT).show();
                conSpinner.setAdapter(adapter);

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    allDevices.add(0, device);
                    if (device != null) {
                        if (device.getName().length() > 0) {
                            if (device.getName().equals("Avengers")) {
                                allDeviceNames.add(0, device.getName());
                                btAdapter.cancelDiscovery();
                            } else {
                                allDeviceNames.add(device.getName());
                            }
                        }
                    }
                }

                Toast.makeText(MainActivity.this, "Found device: " + device.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void send(String msg) {
        output.println(msg);
        output.flush();
    }
}