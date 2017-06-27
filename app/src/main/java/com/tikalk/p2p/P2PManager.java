package com.tikalk.p2p;

/**
 * Created by shaul on 20/06/2017.
 */

public class P2PManager {
    private final ConnectionType connectionType;
    private ConnectionRole role;

    public void connect(Object params) {
        connectionManager.connectToPeer(params, null);
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

    public void init(ConnectionRole role) {
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
