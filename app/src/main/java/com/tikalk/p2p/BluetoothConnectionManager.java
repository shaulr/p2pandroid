package com.tikalk.p2p;

import android.content.Context;


/**
 * Created by shaul on 20/06/2017.
 */

public class BluetoothConnectionManager extends ConnectionManager {
    @Override
    public boolean getPeersList() {
        return false;
    }

    @Override
    public boolean connectToPeer(ConnectionPeer peer) {
        return false;
    }

    @Override
    public boolean disconnectFromPeer() {
        return false;
    }


    @Override
    public boolean send(byte[] data) {
        return false;
    }

    @Override
    public boolean init(Context context) {
        return false;
    }
}
