package com.sahragard.myremote;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by Coco on 2016-02-22.
 */
public class Bluetooth {
    private Context context;
    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private List<BluetoothDevice> foundDevices = new ArrayList<BluetoothDevice>();
    private List<String> foundDeviceNames = new ArrayList<String>();
    private Thread workerThread;
    private Thread inputThread;
    private Scanner input;
    private PrintWriter output;

    /*
    When creating a new instance of the bluetooth class, you must enter the context.
    In our example our context is "MainActivity.this".
    As for the bluetooth adapter, you must first declare a variable by writing:
    BluetoothAdapter adapterName = BluetoothAdapter.getDefaultAdapter(); This is your adapter that you will enter in the constructor.
    */
    public Bluetooth(final Context context, BluetoothAdapter adapter) {
        this.context = context;
        this.adapter = adapter;

        //Output thread
        final Handler handler = new Handler();
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    //Do work
                }
            }
        });

        //Input thread
        final Handler handler1 = new Handler();

        inputThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    while (input.hasNextLine())
                        Toast.makeText(context, input.nextLine(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Returns the list of found devices
    public List<BluetoothDevice> getFoundDevices() {
        return foundDevices;
    }

    //Returns the list of string representations of the found devices
    //This is used to fill the spinner with items
    public List<String> getFoundDeviceNames() {
        return foundDeviceNames;
    }

    //Finds devices to connect to
    public void findDevices() {
        //Clears the lists in order to prevent duplicates
        getFoundDeviceNames().clear();
        getFoundDevices().clear();

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        context.registerReceiver(mReceiver, filter);
    }

    //Connect to device
    public void connect(BluetoothDevice device) {
        if (device != null) {
            try {
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                BluetoothSocket btSocket = device.createRfcommSocketToServiceRecord(uuid);
                btSocket.connect();
                output = new PrintWriter(btSocket.getOutputStream());
                workerThread.start();
                input = new Scanner(btSocket.getInputStream());
                inputThread.start();
                Toast.makeText(context, "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Toast.makeText(context, "Connection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Send instructions to the connected device
    public void send(String msg) {
        output.println(msg);
        output.flush();
    }

    //This is the function that returns devices to us
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //When discovery of devices starts
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                Toast.makeText(context, "Discovery started", Toast.LENGTH_SHORT).show();

            //When discovery of devices finishes
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                Toast.makeText(context, "Discovery finished", Toast.LENGTH_SHORT).show();

            //When a bluetooth device is foudn
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Below statements are made in order to prevent the program from crashing
                if (device != null) {
                    if (device.getName().length() > 0) {
                        getFoundDevices().add(0, device);
                        if (device.getName().equals("Avengers")) {
                            //Add it to the top of the list in order to save time
                            getFoundDeviceNames().add(0, device.getName());
                            //Stop discovery of devices, our device has been found
                            adapter.cancelDiscovery();
                        } else {
                            getFoundDeviceNames().add(device.getName());
                        }
                    }
                }
                //Shows a toast of a found device
                Toast.makeText(context, "Found device: " + device.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
