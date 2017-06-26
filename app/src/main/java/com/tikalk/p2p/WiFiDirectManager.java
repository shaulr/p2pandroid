package com.tikalk.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.tikalk.p2p.sampleapp.DeviceListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaul on 20/06/2017.
 */

public class WiFiDirectManager extends ConnectionManager implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener,
        WifiP2pManager.ChannelListener, DeviceActionListener {
    private static final String TAG = "WiFiDirectManager" ;
    private final P2PManager.ConnectionRole role;
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private int status;

    public WiFiDirectManager(P2PManager.ConnectionRole role) {
        this.role = role;
    }

    @Override
    public boolean getPeersList() {
        return false;
    }

    @Override
    public boolean connectToPeer(ConnectionPeer peer) {
//        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
//
//            @Override
//            public void onSuccess() {
//                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
//            }
//
//            @Override
//            public void onFailure(int reason) {
////                Toast.makeText(P2PActivity.this, "Connect failed. Retry.",
////                        Toast.LENGTH_SHORT).show();
//            }
//        });
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
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
//                Toast.makeText(P2PActivity.this, "Connect failed. Retry.",
//                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
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

        return false;
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

        List<ConnectionPeer> peersList = new ArrayList<ConnectionPeer>();
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
}
