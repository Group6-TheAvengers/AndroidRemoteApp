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


    public Bluetooth(final Context context, BluetoothAdapter adapter) {
        this.context = context;
        this.adapter = adapter;

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
                        Toast.makeText(context, input.nextLine(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public List<BluetoothDevice> getFoundDevices() {
        return foundDevices;
    }

    public List<String> getFoundDeviceNames() {
        return foundDeviceNames;
    }

    public void findDevices() {
        getFoundDeviceNames().clear();
        getFoundDevices().clear();

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        context.registerReceiver(mReceiver, filter);
    }

    public void connect(BluetoothDevice device) {
        if(device != null) {
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

    public void send(String msg) {
        output.println(msg);
        output.flush();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                Toast.makeText(context, "Discovery started", Toast.LENGTH_SHORT).show();

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                Toast.makeText(context, "Discovery finished", Toast.LENGTH_SHORT).show();

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    if (device.getName().length() > 0) {
                        getFoundDevices().add(0, device);
                        if (device.getName().equals("Avengers")) {
                            getFoundDeviceNames().add(0, device.getName());
                            adapter.cancelDiscovery();
                        } else {
                            getFoundDeviceNames().add(device.getName());
                        }
                    }
                }
                Toast.makeText(context, "Found device: " + device.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
