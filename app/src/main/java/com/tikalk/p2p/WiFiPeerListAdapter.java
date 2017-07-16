package com.tikalk.p2p;

/**
 * Created by shaulr on 16/07/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Array adapter for ListFragment that maintains WifiP2pDevice list.
 */
public class WiFiPeerListAdapter extends ArrayAdapter<ConnectionPeer> {

    private static List<ConnectionPeer> items = new ArrayList<>();;
    private Activity activity;

    /**
     * @param context
     * @param textViewResourceId
     */
    public WiFiPeerListAdapter(Context context, Activity activity, int textViewResourceId) {
        super(context, textViewResourceId, items);
        this.activity = activity;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_devices, null);
        }
        ConnectionPeer device = items.get(position);
        if (device != null) {
            TextView top = (TextView) v.findViewById(R.id.device_name);
            TextView bottom = (TextView) v.findViewById(R.id.device_details);
            if (top != null) {
                top.setText(device.getName());
            }
            if (bottom != null) {
                bottom.setText(device.getStatus());
            }
        }

        return v;

    }
}
