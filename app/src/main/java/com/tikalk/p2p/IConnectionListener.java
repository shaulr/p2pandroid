package com.tikalk.p2p;

import java.util.List;

public interface IConnectionListener {
    void onConnected();
    void onGotListOfPeers(List<ConnectionPeer> peers);
    void onError(String error);
    void onRecieveData(byte[] data);
    void onDisconnected();
    void onDeviceUpdated(String info);
    void onEnabled(boolean enabled);
    void onPeerUpdated(ConnectionPeer peer);
    void showDetails(ConnectionPeer peer);
}
