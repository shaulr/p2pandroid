/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tikalk.p2p.sampleapp;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tikalk.p2p.ConnectionPeer;
import com.tikalk.p2p.DeviceActionListener;
import com.tikalk.p2p.IConnectionListener;
import com.tikalk.p2p.P2PManager;
import com.tikalk.p2p.R;
import com.tikalk.p2p.WiFiPeerListAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 */
public class  DeviceListFragment extends Fragment implements IConnectionListener, AdapterView.OnItemClickListener {

    private List<ConnectionPeer> peers = new ArrayList<>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;
    private P2PManager p2PManager;
    private ListView peerList;
    private ListAdapter listAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);
        peerList = (ListView)mContentView.findViewById(R.id.peerList);
        peerList.setOnItemClickListener(this);
        return mContentView;
    }

//    /**
//     * @return this device
//     */
//    public WifiP2pDevice getDevice() {
//        return device;
//    }

    private static String getDeviceStatus(int deviceStatus) {
        Log.d(P2PActivity.TAG, "Peer status :" + deviceStatus);
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



    @Override
    public void onConnected() {

    }

    @Override
    public void onGotListOfPeers(List<ConnectionPeer> peersList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peersList);
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d(P2PActivity.TAG, "No devices found");
            return;
        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onRecieveData(byte[] data) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onDeviceUpdated(String info) {

    }

    @Override
    public void onEnabled(boolean enabled) {

    }

    @Override
    public void onPeerUpdated(ConnectionPeer peer) {
        peers.add(peer);
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void showDetails(ConnectionPeer peer) {

    }



    /**
     * Update UI for this device.
     * 
     * @param device WifiP2pDevice object
     */
    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
        view.setText(device.deviceName);
        view = (TextView) mContentView.findViewById(R.id.my_status);
        view.setText(getDeviceStatus(device.status));
    }



    public void clearPeers() {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    /**
     * 
     */
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        
                    }
                });
    }


    public void setP2PManager(P2PManager p2PManager) {
        this.p2PManager = p2PManager;
        this.setListAdapter(p2PManager.getDeviceAdapter(getActivity(), R.layout.row_devices));
        this.p2PManager.discoverPeers();
        this.p2PManager.registerListener(this);
    }

    public ListAdapter getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(ListAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        ((DeviceActionListener) getActivity()).showDetails(device);
    }
}
