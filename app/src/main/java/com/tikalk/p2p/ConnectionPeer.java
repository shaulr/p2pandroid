package com.tikalk.p2p;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

/**
 * Created by shaul on 20/06/2017.
 */

public class ConnectionPeer {
    private static final String TAG = "ConnectionPeer";
    private String name;
    private String description;
    private P2PManager.ConnectionType connectionType;
    private Object address;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public P2PManager.ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(P2PManager.ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private static String getWiFiDeviceStatus(int deviceStatus) {
        Log.d(TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }
    public static ConnectionPeer fromWiFiP2PDevice(WifiP2pDevice device) {
        ConnectionPeer peer = new ConnectionPeer();
        peer.setConnectionType(P2PManager.ConnectionType.wiFiDirect);
        peer.setName(device.deviceName);
        peer.setStatus(getWiFiDeviceStatus(device.status));
        peer.setAddress(device.deviceAddress);
        return peer;
    }


}
