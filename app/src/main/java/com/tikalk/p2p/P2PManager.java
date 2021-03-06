package com.tikalk.p2p;

import android.app.Activity;
import android.content.Context;
import android.widget.ListAdapter;

/**
 * Created by shaul on 20/06/2017.
 */

public class P2PManager {
    private final ConnectionType connectionType;
    private ConnectionRole role;
    private Context context;

    public void connect(Object params) {
        connectionManager.connectToPeer(params, null);
    }

    public ListAdapter getDeviceAdapter(Activity activity, int row_devices) {
       return connectionManager.getAdapter( activity,  row_devices);
    }


    public enum ConnectionType { wiFiDirect, bluetooth, BLE}
    public enum ConnectionRole {client, server}

    private ConnectionManager connectionManager;

    public P2PManager(ConnectionType connectionType, ConnectionRole role) {
        this.connectionType = connectionType;
        this.role = role;
    }

    public void registerListener(IConnectionListener listener) {
        if(connectionManager == null) {
            throw new IllegalStateException();
        }

        connectionManager.registerListener(listener);
    }

    public void init(ConnectionRole role, Context context) {
        this.context = context;
        switch(connectionType) {
            case wiFiDirect:
                initWifiDirect(role);
                break;
            case bluetooth:
                initBluetooth();
                break;
            case BLE:
                initBLE();
                break;
        }
    }



    private void initBLE() {
        connectionManager = new BLEConnectionManager();
    }

    private void initBluetooth() {
        connectionManager = new BluetoothConnectionManager();
    }

    private void initWifiDirect(ConnectionRole role) {
        connectionManager = new WiFiDirectManager(role);
        connectionManager.init(context);
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public ConnectionRole getRole() {
        return role;
    }

    public void setRole(ConnectionRole role) {
        this.role = role;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }


    public void discoverPeers() {
        if(connectionManager != null) connectionManager.getPeersList();
    }
}
