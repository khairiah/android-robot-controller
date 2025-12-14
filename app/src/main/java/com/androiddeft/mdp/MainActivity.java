package com.androiddeft.mdp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.AsyncListUtil;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddeft.mdp.Adapters.GridAxisAdapter;
import com.androiddeft.mdp.Adapters.GridViewAdapter;
import com.androiddeft.mdp.fragments.bluetooth.BluetoothChatFragment;
import com.androiddeft.mdp.fragments.bluetooth.BluetoothChatService;
import com.androiddeft.mdp.fragments.bluetooth.main_fragment;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

  //for logging
  public static final String TAG = "MainActivity";


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

    private Handler mHandler;

    private GridView mGridView;
    private GridView mXAxis;
    private GridView mYAxis;

    private GridViewAdapter mGridViewAdapter;
    private GridAxisAdapter mXAxisAdapter;
    private GridAxisAdapter mYAxisAdapter;
    private Button testbtn;
    Bundle bundle1;

    private Button mGridUpdateBtn;




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
    //    private TextView mRobotStatusTV;

    private BluetoothAdapter mBTAdapter;
    private BluetoothDevice mBTDevice;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private SensorManager sensorManager;
    private Sensor sensor;


    private Toast mToast;
    private Boolean isAuto = true;
    private TextView mConnectTV;
    private TextView mAutoTV;
    private TextView testing;
    private AsyncListUtil.ViewCallback vb ;

    private ArrayList<Character> mMapDescriptor;
    private static final String DEFAULT_MAP_DESCRIPTOR_STRING = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000434000000000000444000000000000444000000000000";





    //  Arena Frame
    private RelativeLayout arenaFrame;


    private static final int REQUEST_RECONFIGURE_STRING = 1;


  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Display the MAIN FRAGMENT
       final main_fragment fragment = new main_fragment();
        displaySelectedFragment(fragment);







    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_option_menu, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
      switch (item.getItemId()) {
        //for reconfiguring string commands - command 1 and command 2 (Task C8)
        case R.id.reconString: {
          Intent reconStringIntent = new Intent(this,ConfigurableCommActivity.class);
          startActivityForResult(reconStringIntent, REQUEST_RECONFIGURE_STRING);
          return true;
        }
        //add other cases here - always start activity for result

      }
      return super.onOptionsItemSelected(item);
    }





    /**
     * Loads the specified fragment to the frame
     *
     * @param fragment
     */
    private void displaySelectedFragment(main_fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }



}
