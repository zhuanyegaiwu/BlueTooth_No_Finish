package com.xhs.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 0x001;
    private static final int CAN_BE_FOUND_CODE = 0x002;
    private static final int CAN_BE_FOUND_TIME =0x600;
    private ArrayList<String> mArrayAdapter=new ArrayList<>();
    private Button mSearch;
    private Button mDiscovering;
    private Button mSearch_device;
    private BluetoothAdapter mBluetoothAdapter;
    private Button mBluetoothState;
    private Button mCanBeFound;
    private Button mIsCanBeFound;
    private Button mStartClient;
    private Button mStartServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Log.e("TAG","UUID="+Constant.UU_ID);
        initView();
        initBlueTooth();
    }

    private void initView() {


        mStartServer = (Button) findViewById(R.id.start_server);
        mStartServer.setOnClickListener(this);
        mStartClient= (Button) findViewById(R.id.start_client);
        mStartClient.setOnClickListener(this);


        mIsCanBeFound = (Button) findViewById(R.id.is_can_be_found);
        mIsCanBeFound.setOnClickListener(this);

        mCanBeFound = (Button) findViewById(R.id.can_be_found);
        mCanBeFound.setOnClickListener(this);

        mBluetoothState = (Button) findViewById(R.id.bluetooth_state);
        mBluetoothState.setOnClickListener(this);
        mSearch = (Button) findViewById(R.id.search);
        mSearch.setOnClickListener(this);
        mDiscovering = (Button) findViewById(R.id.discovering);
        mDiscovering.setOnClickListener(this);
        mSearch_device = (Button) findViewById(R.id.search_device);
        mSearch_device.setOnClickListener(this);

    }

    private void initBlueTooth() {
        //Get the BluetoothAdapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,"不支持蓝牙",Toast.LENGTH_SHORT).show();
            return;
        }
        //Enable Bluetooth
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private ArrayList<BluetoothDevice> bluetoothDevices=new ArrayList<>();
    @Override
    public void onClick(View view) {
        IntentFilter filter;
        switch (view.getId()){
            case R.id.search:
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                // If there are paired devices
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        bluetoothDevices.add(device);
                        // Add the name and address to an array adapter to show in a ListView
                        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                    for (int i = 0; i <mArrayAdapter.size() ; i++) {
                        Log.e("TAG","扫描到蓝牙设备="+mArrayAdapter.get(i));
                        Toast.makeText(this,"扫描到蓝牙设备="+mArrayAdapter.get(i),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this,"暂无配对蓝牙设备",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.discovering:
                // Register the BroadcastReceiver
                Toast.makeText(this,"监听已开启",Toast.LENGTH_SHORT).show();
                 filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
                break;
            case R.id.search_device:
                Toast.makeText(this,"搜索可发现设备",Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.startDiscovery();
                break;
            case R.id.bluetooth_state:
                filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                registerReceiver(receiver, filter);
                break;
            case R.id.can_be_found:
                Toast.makeText(this,"设置本地蓝牙可发现模式",Toast.LENGTH_SHORT).show();
                Intent discoverableIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //定义持续时间
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, CAN_BE_FOUND_TIME);
                startActivityForResult(discoverableIntent, CAN_BE_FOUND_CODE);
                break;
            case R.id.is_can_be_found:
                //注册广播，监听模式改变
                filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(mModleReceiver,filter);
                break;
            case R.id.start_client:
                ConnectThread connectThread=new ConnectThread(bluetoothDevices.get(0),mBluetoothAdapter);
                connectThread.run();
                break;
            case R.id.start_server:
                AcceptThread acceptThread=new AcceptThread(bluetoothDevices.get(0),mBluetoothAdapter);
                acceptThread.run();
                break;
        }

    }

    private BroadcastReceiver mModleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int mode  =  intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,1);
            switch (mode){
                case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                    Toast.makeText(MainActivity.this, "现在是可发现模式", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                    Toast.makeText(MainActivity.this, "现在不是可发现模式，但是可以连接", Toast.LENGTH_SHORT).show();
            }
        }
    };
    //蓝牙状态改变监听
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 1);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(MainActivity.this, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(MainActivity.this, "蓝牙已打开", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Toast.makeText(MainActivity.this, "正在打开蓝牙", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Toast.makeText(MainActivity.this, "正在关闭蓝牙", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(MainActivity.this, "未知状态", Toast.LENGTH_SHORT).show();
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(MainActivity.this,"很高兴发现新设备",Toast.LENGTH_SHORT).show();
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            for (int i = 0; i <mArrayAdapter.size() ; i++) {
                Log.e("TAG","扫描到蓝牙设备="+mArrayAdapter.get(i));
                Toast.makeText(MainActivity.this,"扫描到蓝牙设备="+mArrayAdapter.get(i),Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                switch (resultCode){
                    case RESULT_OK:
                        Toast.makeText(this,"蓝牙已开启",Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(this,"蓝牙拒绝开启",Toast.LENGTH_SHORT).show();
                }
                break;
            case CAN_BE_FOUND_CODE:
                switch (resultCode){
                    case CAN_BE_FOUND_TIME:
                        Toast.makeText(this,"设置本地蓝牙可见成功",Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(this,"设置本地蓝牙可见失败",Toast.LENGTH_SHORT).show();
                }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
    }
}
