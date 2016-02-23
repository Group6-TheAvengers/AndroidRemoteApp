package com.sahragard.myremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    public TextView statusUpdate;
    public Button enableBT;
    public Button disableBT;
    public Button connectDevice;
    public boolean doOnce = true;

    RelativeLayout layout_joystick;
    JoyStickClass js;

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


        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new JoyStickClass(getApplicationContext()
                ,layout_joystick , R.drawable.image_button);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);
        js.drawStick();


        layout_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent arg1) {
                js.move(arg1);
                if (arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {

                    int direction = js.get4Direction();
                    if (direction == JoyStickClass.STICK_UP) {
                        bt.send("w");
                    } else if (direction == JoyStickClass.STICK_RIGHT) {
                        bt.send("d");
                    } else if (direction == JoyStickClass.STICK_DOWN) {
                        bt.send("s");
                    } else if (direction == JoyStickClass.STICK_LEFT) {
                        bt.send("a");
                    } else if (direction == JoyStickClass.STICK_NONE) {
                        bt.send("f");
                    }
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    bt.send("f");
                }
                return true;
            }
        });



    }
}