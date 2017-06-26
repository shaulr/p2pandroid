package com.tikalk.p2p;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;

import java.util.List;

/**
 * Created by shaul on 20/06/2017.
 */

public abstract class ConnectionManager {
    protected IConnectionListener listener;
    public Context context;

    abstract public boolean getPeersList();
    abstract public boolean connectToPeer(ConnectionPeer peer);
    abstract public boolean disconnectFromPeer();
    abstract public boolean send(byte[] data);
    abstract public boolean init(Context context);
    public void setIsEnabled(boolean enabled) {
        if(listener != null)
            listener.onEnabled(enabled);
    }
    public void registerListener(IConnectionListener listener) {
        this.listener = listener;
    }

    public  void updatePeer(ConnectionPeer peer) {
        if(listener != null) {
            listener.onPeerUpdated(peer);
        }
    }
}
