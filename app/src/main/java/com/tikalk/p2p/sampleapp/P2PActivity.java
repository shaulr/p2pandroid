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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.tikalk.p2p.P2PManager;
import com.tikalk.p2p.R;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class P2PActivity extends Activity {

    public static final String TAG = "wifidirectdemo";
    private P2PManager p2PManager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        p2PManager = new P2PManager(P2PManager.ConnectionType.wiFiDirect, P2PManager.ConnectionRole.client);
        // add necessary intent values to be matched.

//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
//
//        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
//        channel = wifiP2pManager.initialize(this, getMainLooper(), null);
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        p2PManager.init(P2PManager.ConnectionRole.client);
//        receiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, channel, this);
//        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
//        unregisterReceiver(receiver);
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.atn_direct_enable:
//                if (manager != null && channel != null) {

                    // Since this is the system wireless settings activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.

                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
//                } else {
//                    Log.e(TAG, "channel or manager is null");
//                }
                return true;

            case R.id.atn_direct_discover:
                if (!isWifiP2pEnabled) {
                    Toast.makeText(P2PActivity.this, R.string.p2p_off_warning,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                        .findFragmentById(R.id.frag_list);
                fragment.onInitiateDiscovery();
                p2PManager.discoverPeers();
//                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
//
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(P2PActivity.this, "Discovery Initiated",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(int reasonCode) {
//                        Toast.makeText(P2PActivity.this, "Discovery Failed : " + reasonCode,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public void showDetails(WifiP2pDevice device) {
//        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
//                .findFragmentById(R.id.frag_detail);
//        fragment.showDetails(device);
//
//    }

//    @Override
//    public void connect(WifiP2pConfig config) {
//        p2PManager.connect();
////        manager.connect(channel, config, new ActionListener() {
////
////            @Override
////            public void onSuccess() {
////                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
////            }
////
////            @Override
////            public void onFailure(int reason) {
////                Toast.makeText(P2PActivity.this, "Connect failed. Retry.",
////                        Toast.LENGTH_SHORT).show();
////            }
////        });
//    }

//    @Override
//    public void disconnect() {
//        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
//                .findFragmentById(R.id.frag_detail);
//        fragment.resetViews();
////        manager.removeGroup(channel, new ActionListener() {
////
////            @Override
////            public void onFailure(int reasonCode) {
////                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
////
////            }
////
////            @Override
////            public void onSuccess() {
////                fragment.getView().setVisibility(View.GONE);
////            }
////
////        });
//    }
//
////    @Override
////    public void onChannelDisconnected() {
////        // we will try once more
////        if (manager != null && !retryChannel) {
////            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
////            resetData();
////            retryChannel = true;
////            manager.initialize(this, getMainLooper(), this);
////        } else {
////            Toast.makeText(this,
////                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
////                    Toast.LENGTH_LONG).show();
////        }
////    }

//    @Override
//    public void cancelDisconnect() {
//
////        /*
////         * A cancel abort request by user. Disconnect i.e. removeGroup if
////         * already connected. Else, request WifiP2pManager to abort the ongoing
////         * request
////         */
////        if (manager != null) {
////            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
////                    .findFragmentById(R.id.frag_list);
////            if (fragment.getDevice() == null
////                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
////                disconnect();
////            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
////                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {
////
////                manager.cancelConnect(channel, new ActionListener() {
////
////                    @Override
////                    public void onSuccess() {
////                        Toast.makeText(P2PActivity.this, "Aborting connection",
////                                Toast.LENGTH_SHORT).show();
////                    }
////
////                    @Override
////                    public void onFailure(int reasonCode) {
////                        Toast.makeText(P2PActivity.this,
////                                "Connect abort request failed. Reason Code: " + reasonCode,
////                                Toast.LENGTH_SHORT).show();
////                    }
////                });
////            }
////        }
//
//    }
}
