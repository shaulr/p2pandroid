package com.tikalk.p2p;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;


/**
 * Created by shaul on 20/06/2017.
 */

public class BluetoothConnectionManager extends ConnectionManager {
    private BluetoothAdapter mBtAdapter;
    private ListAdapter adapter;
    @Override
    public boolean getPeersList() {
        return false;
    }

    @Override
    public boolean connectToPeer(Object params, ConnectionPeer peer) {
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

    @Override
    public ListAdapter getAdapter(Activity activity, int row_devices) {
        if(mBtAdapter == null) {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            adapter = fromBtAdapter(mBtAdapter, activity,  row_devices);
        }
        return adapter;
    }

    private ListAdapter fromBtAdapter(BluetoothAdapter mBtAdapter, Activity activity, int row_devices) {
        return new ListAdapter() {
            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int i) {
                return false;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                return null;
            }

            @Override
            public int getItemViewType(int i) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
    }
}
