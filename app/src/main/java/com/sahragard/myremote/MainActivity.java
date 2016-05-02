package com.sahragard.myremote;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //textView = (TextView)findViewById(R.id.textView);
        //textView2 = (TextView)findViewById(R.id.textView2);
        bt = new Bluetooth(MainActivity.this, btAdapter, MainActivity.this);
        currentSpeed = (TextView) findViewById(R.id.currentSpeed);
        currentDistance = (TextView) findViewById(R.id.currentDistance);

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
                //for (BluetoothDevice device : bt.getBluetoothDevices()) {
                   // if (device != null) {
                     //   if (device.getName().equals(bt.selectedDeviceName)) {
                            changeInterface();
                       // }
                    //}
                //}
            }
        });

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

        //Draw Outer JoyStick
        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);
        //Create Inner JoyStick
        js = new JoyStickClass(getApplicationContext()
                ,layout_joystick , R.drawable.image_button, bt);
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
        js.setMinimumDistance(0);
        //Draw inner Joystick
        js.drawStick();


        // HIDE
        layout_joystick.setVisibility(View.INVISIBLE);
        lineFollowingButton.setVisibility(View.INVISIBLE);
        currentSpeed.setVisibility(View.INVISIBLE);
        currentDistance.setVisibility(View.INVISIBLE);

    }

    public void changeInterface() {
        if (connectScreen) {
            connectScreen = false;
            // SHOW
            layout_joystick.setVisibility(View.VISIBLE);
            lineFollowingButton.setVisibility(View.VISIBLE);
            currentSpeed.setVisibility(View.VISIBLE);
            currentDistance.setVisibility(View.VISIBLE);

            //HIDE

            statusUpdate.setVisibility(View.INVISIBLE);
            connectDevice.setVisibility(View.INVISIBLE);
            enableBT.setVisibility(View.INVISIBLE);
            disableBT.setVisibility(View.INVISIBLE);
            bt.getDeviceSpinner().setVisibility(View.INVISIBLE);

        } else {
            connectScreen = true;
            // SHOW
            statusUpdate.setVisibility(View.VISIBLE);
            connectDevice.setVisibility(View.VISIBLE);
            enableBT.setVisibility(View.VISIBLE);
            disableBT.setVisibility(View.VISIBLE);
            bt.getDeviceSpinner().setVisibility(View.VISIBLE);

            //HIDE
            layout_joystick.setVisibility(View.VISIBLE);
            lineFollowingButton.setVisibility(View.VISIBLE);
            currentSpeed.setVisibility(View.VISIBLE);
            currentDistance.setVisibility(View.VISIBLE);


        }
    }
}