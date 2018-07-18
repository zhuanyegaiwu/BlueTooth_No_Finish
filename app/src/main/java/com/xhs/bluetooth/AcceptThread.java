package com.xhs.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 布鲁斯.李 on 2018/7/18.
 * Email:zp18595658325@163.com
 */

public class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;

    public AcceptThread(BluetoothDevice bluetoothDevice, BluetoothAdapter mBluetoothAdapter) {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Server_Socket", UUID.fromString("1e6b70da-559d-440d-a445-5d4c881c7a20"));
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }


                try {
                    // If a connection was accepted
                    if (socket != null) {
                        // Do work to manage the connection (in a separate thread)
                   // manageConnectedSocket(socket);
                    mmServerSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }


    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}