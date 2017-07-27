package com.tikalk.p2p;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaul on 20/06/2017.
 */

public abstract class ConnectionManager {
    protected IConnectionListener listener;
    public Context context;

    abstract public boolean getPeersList();
    abstract public boolean connectToPeer(Object params, ConnectionPeer peer);
    abstract public boolean disconnectFromPeer();
    abstract public boolean send(byte[] data);
    abstract public boolean init(Context context);
    private List<ConnectionPeer> peers = new ArrayList<>();
    public void setIsEnabled(boolean enabled) {
        if(listener != null)
            listener.onEnabled(enabled);
    }
    public void registerListener(IConnectionListener listener) {
        this.listener = listener;
        if(peers.size() > 0)
            listener.onGotListOfPeers(peers);
    }

    public  void updatePeer(ConnectionPeer peer) {
        if(peers.contains(peer)) return;
        peers.add(peer);

        if(listener != null) {
            listener.onPeerUpdated(peer);
        }
    }

    abstract public ListAdapter getAdapter(Activity activity, int row_devices) ;
}

