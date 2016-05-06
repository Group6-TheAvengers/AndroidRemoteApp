package com.sahragard.myremote;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bluetooth {
    private Context context;
    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
    private List<String> bluetoothDeviceNames = new ArrayList<String>();
    private Thread workerThread, inputThread;
    private BufferedReader input;
    private PrintWriter output;
    private Activity activity;
    private BluetoothSocket btSocket;
    public String selectedDeviceName = "";
    private String speedString;
    private TextView currentSpeed, currentDistance;
    private String distance;
    private boolean isConnected = false;
    private Spinner deviceSpinner;
    private boolean addTextViews = true;
    //NEW

    /*
    When creating a new instance of the bluetooth class, you must enter the context.
    In our example our context is "MainActivity.this".
    As for the bluetooth adapter, you must first declare a variable by writing:
    BluetoothAdapter adapterName = BluetoothAdapter.getDefaultAdapter(); This is your adapter that you will enter in the constructor.
    */
    public Bluetooth(final Context context, BluetoothAdapter adapter, final Activity activity) {
        this.context = context;
        this.adapter = adapter;
        this.activity = activity;

        //Output thread
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    //Do work
                }
            }
        });

        //Input thread
        inputThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    while (input != null) {
                        try {
                            if (isConnected() == true) {
                                if(addTextViews == true) {
                                    addTextViews = false;
                                    currentSpeed = (TextView) activity.findViewById(R.id.currentSpeed);
                                    currentDistance = (TextView) activity.findViewById(R.id.currentDistance);
                                }
                            }
                            System.out.println(input.readLine());
                            if (input.readLine().startsWith("s")) {
                                speedString = input.readLine().substring(1);
                            } else {
                                distance = input.readLine().substring(1);
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (addTextViews == false) {
                                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                        currentSpeed.setText(distance + " m/s");
                                        currentDistance.setText(speedString + "m");
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    //Enable bluetooth
    public void enableBT() {
        if (!adapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBluetooth, 0);
        } else {
            Toast.makeText(context, "Bluetooth already enabled", Toast.LENGTH_SHORT).show();
        }
    }
    //Get connected status
    public boolean isConnected(){
        return isConnected;
    }
    //Disable bluetooth
    public void disableBT() {
        if (adapter.isEnabled()) {
            adapter.disable();
        } else {
            Toast.makeText(context, "Bluetooth already disabled", Toast.LENGTH_SHORT).show();
        }
    }

    //Returns the list of found devices
    public List<BluetoothDevice> getBluetoothDevices() {
        return bluetoothDevices;
    }

    //Returns the list of string representations of the found devices
    //This is used to fill the spinner with items
    public List<String> getBluetoothDeviceNames() {
        return bluetoothDeviceNames;
    }

    //Finds devices to connect to
    public void findDevices() {
        //Clears the lists in order to prevent duplicates
        getBluetoothDeviceNames().clear();
        getBluetoothDevices().clear();

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        context.registerReceiver(mReceiver, filter);
    }

    public String getDistance() {
        return (String) currentDistance.getText();
    }

    public Spinner getDeviceSpinner() {
        deviceSpinner = (Spinner) activity.findViewById(R.id.spinner);

        return deviceSpinner;
    }

    public void fillSpinner(final Spinner spinner) {
        ArrayAdapter deviceAdapter = getDeviceList();

        //Spinner that contains devices to connect to
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //IF TOSTRING ERROR ARISES CHECK THIS
                selectedDeviceName = spinner.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView parent) {
            }
        });

        spinner.setAdapter(deviceAdapter);
    }

    //Returns the array adapter which contains the strings for the spinner
    public ArrayAdapter getDeviceList() {
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, getBluetoothDeviceNames());
        return arrayAdapter;
    }

    //Connect to device
    public void connect(BluetoothDevice device) {
        if (device != null) {
            try {
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                btSocket = device.createRfcommSocketToServiceRecord(uuid);
                btSocket.connect();
                output = new PrintWriter(btSocket.getOutputStream());
                workerThread.start();
                input = new BufferedReader(new InputStreamReader(btSocket.getInputStream()));
                inputThread.start();
                Toast.makeText(context, "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();
                isConnected = true;
            } catch (IOException e) {
            }
        }
    }

    //Send instructions to the connected device
    public void send(String msg) {
        try {
            output.println(msg);
            output.flush();
        } catch (Exception e) {
        }
    }


    //This is the function that returns devices to us
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();

                //When discovery of devices starts
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    //discovery starts, we can show progress dialog or perform other tasks
                    Toast.makeText(context, "Discovery started", Toast.LENGTH_SHORT).show();

                    //When discovery of devices finishes
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    //discovery finishes, dismis progress dialog
                    Toast.makeText(context, "Discovery finished", Toast.LENGTH_SHORT).show();
                    fillSpinner(getDeviceSpinner());
                    context.unregisterReceiver(mReceiver);
                    //When a bluetooth device is found
                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //bluetooth device found
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //Below statements are made in order to prevent the program from crashing
                    if (device != null) {
                        if (device.getName().length() > 0) {
                            getBluetoothDevices().add(0, device);
                            if (device.getName().equals("Avengers")) {
                                //Add it to the top of the list in order to save time
                                getBluetoothDeviceNames().add(0, device.getName());
                                //Stop discovery of devices, our device has been found
                                adapter.cancelDiscovery();
                            } else {
                                getBluetoothDeviceNames().add(0, device.getName());
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    };
}
