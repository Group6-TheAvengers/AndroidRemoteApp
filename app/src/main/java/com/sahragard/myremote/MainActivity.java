package com.sahragard.myremote;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private TextView statusUpdate, currentSpeed, currentDistance;
    private Button enableBT,disableBT, connectDevice,lineFollowingButton;
    private boolean linefollowing = false;
    private RelativeLayout layout_joystick;
    private JoyStickClass js;
    private boolean connectScreen = true;
    private Bluetooth bt;
    private TextView textView;
    private Switch btSwitch;
    // NEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = new Bluetooth(MainActivity.this, btAdapter, MainActivity.this);
        currentSpeed = (TextView) findViewById(R.id.currentSpeed);
        currentDistance = (TextView) findViewById(R.id.currentDistance);
        btSwitch = (Switch) findViewById(R.id.btSwitch);

        //Displays bluetooth status in the top
        if (btAdapter.isEnabled()) {
            btSwitch.setChecked(true);
            btAdapter.startDiscovery();
            bt.findDevices();

        } else {
            btSwitch.setChecked(false);
        }

        //Start bluetooth and find devices
        btSwitch.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (!btAdapter.isEnabled()) {
                    bt.enableBT();

                    //Waits until bluetooth is enabled, otherwise discovery will not start
                    while (!btAdapter.isEnabled()) {
                    }

                    btAdapter.startDiscovery();
                    bt.findDevices();
                } else {
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
                            loadInterface();
                            bt.connect(device);
                        }
                    }
                }
            }
        });
    }

    public void loadInterface() {

        //Draw Outer JoyStick
        layout_joystick = (RelativeLayout) findViewById(R.id.layout_joystick);
        //Create Inner JoyStick
        js = new JoyStickClass(getApplicationContext()
                ,layout_joystick , R.drawable.joystick_dot, bt, textView);
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



        //Enable line following
        lineFollowingButton = (Button) findViewById(R.id.lineFollowingButton);
        lineFollowingButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(linefollowing == false) {
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

    }
}