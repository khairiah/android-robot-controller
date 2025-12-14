

package com.androiddeft.mdp.fragments.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.util.AsyncListUtil;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddeft.mdp.Adapters.GridAxisAdapter;
import com.androiddeft.mdp.Adapters.GridViewAdapter;
import com.androiddeft.mdp.DeviceListActivity;
import com.androiddeft.mdp.MainActivity;
import com.androiddeft.mdp.R;
import com.androiddeft.mdp.Utils;
import com.androiddeft.mdp.constants.Constants;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class main_fragment extends Fragment {

  //tag for logging
  private static final String TAG = "MainActivity";

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int RECONFIGURE_STRING = 4;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    //for sending persistent strings
    private Button mSendCmd1;
    private Button mSendCmd2;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;


    private static final int NUM_ROWS = 20;
    private static final int NUM_COLS = 15;


    private static final int REQUEST_DEVICE_CONNECT_INSECURE = 1;
    public static final int REQUEST_COORDINATES = 2;



    private GridView mGridView;
    private GridView mXAxis;
    private GridView mYAxis;

    private GridViewAdapter mGridViewAdapter;
    private GridAxisAdapter mXAxisAdapter;
    private GridAxisAdapter mYAxisAdapter;
    private Button testbtn;
    Bundle bundle1;

    private Button mGridUpdateBtn;
    private Button beginExplore;

    private String mF1String;
    private String mF2String;
    private String mWayPointXCoord;
    private String mWayPointYCoord;
    private String mStartCoordinateXCoord;
    private String mStartCoordinateYCoord;
    private String mDescriptorStringOne;
    private String mDescriptorStringTwo;
    private String mExplorationTime;
    private String mFastestPathTime;

    private TextView mConnDeviceTV;
    private TextView mWayPointXCoordTV;
    private TextView mWayPointYCoordTV;
    private TextView mStartCoordinateXCoordTV;
    private TextView mStartCoordinateYCoordTV;
    private TextView mExplorationTimeTV;
    private TextView mFastestPathTimeTV;
    private TextView robotstatus;
    private Button update;
    private Switch btn_switch;

    private ImageButton up;
    private ImageButton down;
    private ImageButton left;
    private ImageButton right;
    //    private TextView mRobotStatusTV;

    private BluetoothAdapter mBTAdapter;
    private BluetoothDevice mBTDevice;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private SensorManager sensorManager;
    private Sensor sensor;
    private String  message = null;
    int count = 0;

    private Toast mToast;
    private Boolean isAuto = true;
    private TextView mConnectTV;
    private TextView mAutoTV;
    private AsyncListUtil.ViewCallback vb ;

    private ArrayList<Character> mMapDescriptor;
    private static final String DEFAULT_MAP_DESCRIPTOR_STRING = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000434000000000000444000000000000444000000000000";


    public String test = null;
    private  TextView testing;


    //  Arena Frame
    private RelativeLayout arenaFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set title of page
      //getActivity().setTitle(NavigationDrawerConstants.TAG_BLUETOOTH);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }


    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }

        SharedPreferences sharedPref = getActivity().getSharedPreferences("MyCommandFile", Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = sharedPref.edit();
       editor.clear();
       editor.commit();



    }

    @Override
    public void onResume() {
        super.onResume();
        count++;

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }


        SharedPreferences sharedPref = getActivity().getSharedPreferences("MyCommandFile", Context.MODE_PRIVATE);
        String type = sharedPref.getString("key_cmd0", null);

        if(type!=null)
        {
            if(type.equals("wayPoint"))
            {
                mWayPointXCoord = sharedPref.getString("key_cmd1", null);
                mWayPointYCoord = sharedPref.getString("key_cmd2", null);
                updateWayPointTV();
                message = "Waypoint:" + mWayPointXCoord + ", "+mWayPointYCoord ;
                if(count >2)
                {
                    sendMessage(message);
                }


            }
            else if(type.equals("StartPoints"))
            {
                mStartCoordinateXCoord = sharedPref.getString("key_cmd1", null);
                mStartCoordinateYCoord = sharedPref.getString("key_cmd2", null);
                updateStartCoordinatesTV();
                message = "Start Point:" + mStartCoordinateXCoord + ", "+mStartCoordinateYCoord ;
                if(count >2)
                {
                    sendMessage(message);
                }

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_frag,container,false);
        mMapDescriptor = Utils.getMapDescriptor(DEFAULT_MAP_DESCRIPTOR_STRING);

        mStartCoordinateXCoord = "0";
        mStartCoordinateYCoord = "19";
        mWayPointXCoord = "_";
        mWayPointYCoord = "_";

        mGridView = (GridView) v.findViewById(R.id.maze);
        mGridViewAdapter = new GridViewAdapter(getActivity(), NUM_ROWS, NUM_COLS, mMapDescriptor);

        mXAxis = (GridView) v.findViewById(R.id.x_axis);
        mYAxis = (GridView) v.findViewById(R.id.y_axis);

        mGridView.setNumColumns(NUM_COLS);
        mXAxis.setNumColumns(NUM_COLS);
        mYAxis.setNumColumns(1);

        mXAxisAdapter = new GridAxisAdapter(getActivity(), NUM_COLS);
        mYAxisAdapter = new GridAxisAdapter(getActivity(), NUM_ROWS);

        mGridView.setAdapter(mGridViewAdapter);
        mXAxis.setAdapter(mXAxisAdapter);
        mYAxis.setAdapter(mYAxisAdapter);


        mStartCoordinateXCoord = "0";
        mStartCoordinateYCoord = "19";
        mWayPointXCoord = "_";
        mWayPointYCoord = "_";

        up = (ImageButton)v.findViewById(R.id.imageButton);
        down = (ImageButton)v.findViewById(R.id.imageButton2);
        left = (ImageButton)v.findViewById(R.id.imageButton3);
        right = (ImageButton)v.findViewById(R.id.imageButton4);

        robotstatus = (TextView)v.findViewById(R.id.txtStatus);
        beginExplore= (Button)v.findViewById(R.id.btnExplore);
       btn_switch = (Switch) v.findViewById(R.id.switch_1);
       update = (Button)v.findViewById(R.id.btn_update);

       btn_switch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(btn_switch.isChecked())
               {
                   update.setEnabled(false);
               }
               else
               {
                   update.setEnabled(true);
               }
           }
       });


        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        message = "f";
                        sendMessage(message);
                        if(checkBT())
                        {
                            robotstatus.setText("Forward");
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if(checkBT())
                        {
                            robotstatus.setText("Stop");
                        }
                        break;

                }
                return false;
            }

        });


        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        message = "r";
                        sendMessage(message);
                        if(checkBT())
                        {
                            robotstatus.setText("Reverse");
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if(checkBT())
                        {
                            robotstatus.setText("Stop");
                        }
                        break;

                }
                return false;
            }

        });



        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        message = "tl";
                        sendMessage(message);
                        if(checkBT())
                        {
                            robotstatus.setText("Turn Left");
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if(checkBT())
                        {
                            robotstatus.setText("Stop");
                        }
                        break;

                }
                return false;
            }

        });

       right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        message = "tr";
                        sendMessage(message);
                        if(checkBT())
                        {
                            robotstatus.setText("Turn right");
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if(checkBT())
                        {
                            robotstatus.setText("Stop");
                        }
                        break;

                }
                return false;
            }

        });





        mWayPointXCoordTV = (TextView) v.findViewById(R.id.way_point_x);
        mWayPointYCoordTV = (TextView) v.findViewById(R.id.waypoint_y);
        mStartCoordinateXCoordTV = (TextView) v.findViewById(R.id.start_x);
        mStartCoordinateYCoordTV = (TextView) v.findViewById(R.id.start_y);


        updateStartCoordinatesTV();
        updateWayPointTV();




        return v;
    }


public boolean checkBT()
{
    if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
        Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
        return false;
    }
    else
        return true;
}

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    //changed to public for C8
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
      FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }

      android.support.v7.app.ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();

        if (null == actionBar) {
            //return;
          Log.d("BluetoothChatFragment", "Actionbar is null!");
        }

        //Set connection status to subtitle
        actionBar.setSubtitle(resId);

    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
      FragmentActivity activity = getActivity();
      if (null == activity) {
        return;
      }

      android.support.v7.app.ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();

      if (null == actionBar) {
        //return;
        Log.d("BluetoothChatFragment", "Actionbar is null");
      }

      actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();

                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    robotstatus.setText(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name

                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }


        }
        return false;
    }

    private void updateWayPointTV() {
        mWayPointXCoordTV.setText("X: " + mWayPointXCoord);
        mWayPointYCoordTV.setText("Y: " + mWayPointYCoord);
    }

    private void updateStartCoordinatesTV() {
        mStartCoordinateXCoordTV.setText("X: " + mStartCoordinateXCoord);
        mStartCoordinateYCoordTV.setText("Y: " + mStartCoordinateYCoord);
    }

}
