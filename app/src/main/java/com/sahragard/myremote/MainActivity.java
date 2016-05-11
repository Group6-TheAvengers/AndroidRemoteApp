package com.sahragard.myremote;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private Button connectDevice,lineFollowingButton, disconnectButton;
    private boolean linefollowing = false;
    private RelativeLayout layout_joystick;
    private JoyStickClass js;
    private Bluetooth bt;
    private Switch btSwitch;
    // NEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Loads interface of connect screen
        loadConnect();
    }

    // Interface of connect screen
    public void loadConnect() {
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bt = new Bluetooth(MainActivity.this, btAdapter, MainActivity.this);
        btSwitch = (Switch) findViewById(R.id.btSwitch);

        // Sets the status of the switch
        setSwitch();

        //Start bluetooth and find devices
        btSwitch.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (!btAdapter.isEnabled()) {
                    bt.getDeviceList().clear();
                    btSwitch.setChecked(true);
                    bt.enableBT();

                    //Waits until bluetooth is enabled, otherwise discovery will not start
                    while (!btAdapter.isEnabled()) {
                    }

                    btAdapter.startDiscovery();
                    bt.findDevices();
                } else {
                    btSwitch.setChecked(false);
                    bt.disableBT();
                    //Clear list to prevent duplicate entries
                    bt.getDeviceList().clear();
                }
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

                            // Load control interface
                            loadControls();
                        }
                    }
                }
            }
        });
    }

    // Load control interface
    public void loadControls() {
        setContentView(R.layout.dashboard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Enable line following
        lineFollowingButton = (Button) findViewById(R.id.lineFollowingButton);
        lineFollowingButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (linefollowing == false) {
                    // Enables line following
                    bt.send("q");
                    linefollowing = true;
                } else {
                    // Disables line following
                    bt.send("m");
                    linefollowing = false;
                }
            }
        });

        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // Disconnect from device to make it available again
                // Then loads interface of connect screen
                bt.disconnectDevice();
                loadConnect();
            }
        });

        //Draw Outer JoyStick
        layout_joystick = (RelativeLayout) findViewById(R.id.layout_joystick);
        //Create Inner JoyStick
        js = new JoyStickClass(getApplicationContext()
                , layout_joystick, R.drawable.joystick_dot, bt);
        //Set the size of Inner Joystick
        js.setStickSize(150, 150);
        //Set the size of Outer Joystick
        js.setLayoutSize(500, 500);
        //Opacity of Joystick Inner Background
        js.setLayoutAlpha(150);
        //Opacity of Outer Joystick
        js.setStickAlpha(100);
        //Create Joystick Boundary
        js.setOffset(90);
        //Set the distance to when the outer joystick can active
        js.setMinimumDistance(30);
        //Draw inner Joystick
        js.drawStick();

    }

    // Set the switch to on or off
    public void setSwitch() {
        if (btAdapter.isEnabled()) {
            btSwitch.setChecked(true);
            // Clear the device list from previous items
            bt.getDeviceList().clear();
            btAdapter.startDiscovery();
            bt.findDevices();

        } else if(!btAdapter.isEnabled()) {
            btSwitch.setChecked(false);
        }
    }
}