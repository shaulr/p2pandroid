package com.tikalk.p2p;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.tikalk.p2p.sampleapp.DeviceListFragment;
import com.tikalk.p2p.sampleapp.FileTransferService;
import com.tikalk.p2p.sampleapp.P2PActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import static com.tikalk.p2p.P2PManager.ConnectionRole.server;

/**
 * Created by shaul on 20/06/2017.
 */

public class WiFiDirectManager extends ConnectionManager implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener,
        WifiP2pManager.ChannelListener, DeviceActionListener, WifiP2pManager.ActionListener {

    private static final String TAG = "WiFiDirectManager" ;
    private final P2PManager.ConnectionRole role;
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private int status;
    List<ConnectionPeer> peersList = new ArrayList<>();
    private WiFiPeerListAdapter deviceListAdapter;
    private boolean stopListening = false;
    private final static int PORT = 9002;
    private Socket socket;
    private ServerSocket serverSocket;
    private OutputStream os;
    private InputStream is;
    private SocketReadThread readThread;
    public WiFiDirectManager(P2PManager.ConnectionRole role) {
        this.role = role;
    }

    @Override
    public boolean getPeersList() {
        manager.discoverPeers(channel, this);
        return true;
    }

    @Override
    public boolean connectToPeer(Object params, ConnectionPeer peer) {
        WifiP2pConfig config = (WifiP2pConfig) params;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                if(listener != null) listener.onError("could not connect " + reason);
            }
        });
        return false;
    }

    @Override
    public void showDetails(WifiP2pDevice device) {
        listener.showDetails(ConnectionPeer.fromWiFiP2PDevice(device));
    }

    @Override
    public void cancelDisconnect() {
        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {

            if (status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (status == WifiP2pDevice.AVAILABLE
                    || status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
//                        Toast.makeText(P2PActivity.this, "Aborting connection",
//                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
//                        Toast.makeText(P2PActivity.this,
//                                "Connect abort request failed. Reason Code: " + reasonCode,
//                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    @Override
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                if(role == server)
                    startServerSocket();
                else
                    connectToServer("192.168.49.1");
            }

            @Override
            public void onFailure(int reason) {
//                Toast.makeText(P2PActivity.this, "Connect failed. Retry.",
//                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void connectToServer(final String ip) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, PORT);
                    if(socket == null) {
                        listener.onError("could not connect to " + ip);
                        return;
                    }
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                } catch (IOException e) {
                    Log.d(TAG, "could not connect to server " + e.getMessage());
                }
            }
        });
        thread.start();

    }
    private void startServerSocket() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    serverSocket = new ServerSocket(PORT);

                    while (!stopListening) {
                        //Server is waiting for client here, if needed
                        socket = serverSocket.accept();

                        os = socket.getOutputStream();
                        is = socket.getInputStream();
                        readThread = new SocketReadThread(is, listener);
                        readThread.start();
                        //sever connected
                        listener.onConnected();
                    }

                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }

        });
        thread.start();
    }


    @Override
    public void disconnect() {
        try {
            if(readThread != null) {
                readThread.interrupt();
                readThread = null;
            }
            if(os != null) os.close();
            if(is  != null) is.close();
            if(socket != null) socket.close();
            if(serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            Log.d(TAG, "error closing socket " + e.getMessage());
        }
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

            }

            @Override
            public void onSuccess() {
                listener.onDisconnected();

            }

        });
    }

    @Override
    public boolean disconnectFromPeer() {
        if(listener != null) listener.onDisconnected();

        return true;
    }

    @Override
    public boolean send(byte[] data) {
        try {
            if(os != null) os.write(data);
            } catch (IOException e) {
                Log.d(TAG, "error writing to socket" + e.getMessage());
            }

        return false;
    }

    @Override
    public boolean init(Context context) {
        this.context = context;
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, context.getMainLooper(), null);

        receiver = new WiFiDirectBroadcastReceiver(this, manager, channel, context);
        context.registerReceiver(receiver, intentFilter);
        channel = manager.initialize(context, context.getMainLooper(), null); // callback for loss of framework communication should be added

        return false;
    }

    @Override
    public ListAdapter getAdapter(Activity activity, int row_devices) {
        if(deviceListAdapter == null) {
            deviceListAdapter  = new WiFiPeerListAdapter(activity, activity, row_devices);
        }
        return deviceListAdapter;
    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//        peers.clear();
//        peers.addAll(peerList.getDeviceList());
//        ((DeviceListFragment.WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
//        if (peers.size() == 0) {
//            Log.d(P2PActivity.TAG, "No devices found");
//            return;
//        }
        if(listener == null)
            return;

        peersList.clear();

        for(WifiP2pDevice device : peers.getDeviceList()) {
            peersList.add(ConnectionPeer.fromWiFiP2PDevice(device));
        }
        listener.onGotListOfPeers(peersList);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
           // Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            retryChannel = true;
            manager.initialize(context, context.getMainLooper(), this);
        } else {
            listener.onDisconnected();
        }
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(int i) {

    }
}
