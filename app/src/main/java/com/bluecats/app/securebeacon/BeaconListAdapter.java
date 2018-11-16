package com.bluecats.app.securebeacon;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class BeaconListAdapter extends RecyclerView.Adapter<BeaconListAdapter.ViewHolder> {
    private static final String TAG = "BeaconListAdapter";
    List<BeaconItem> data;

    public BeaconListAdapter() {
        data = new ArrayList<>();
    }

    Comparator<BeaconItem> comparator = new Comparator<BeaconItem>() {
        @Override
        public int compare(BeaconItem lh, BeaconItem rh) {
            if (lh.equals(rh)) {
                return 0;
            }

            if (lh.dist < 0) return Integer.MAX_VALUE;
            if (rh.dist < 0) return Integer.MAX_VALUE;
            return ((int)(lh.dist*10000) - (int)(rh.dist*10000));
        }
    };

    public void setItems(List<BeaconItem> items) {
        synchronized (data) {
            data.clear();
            data.addAll(items);
            Collections.sort(data, comparator);
        }
    }

    @NonNull
    @Override
    public BeaconListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_list_item, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeaconListAdapter.ViewHolder holder, int position) {
        synchronized (data) {
            BeaconItem item = new ArrayList<BeaconItem>(data).get(position);
            holder.populate(item);
        }
    }

    @Override
    public int getItemCount() {
        synchronized (data) {
            return data.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_btaddr;
        TextView tv_mac;
        TextView tv_dist;
        TextView tv_rssi;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView)itemView.findViewById(R.id.tv_name);
            tv_btaddr = (TextView)itemView.findViewById(R.id.tv_btaddr);
            tv_mac = (TextView)itemView.findViewById(R.id.tv_mac);
            tv_dist = (TextView)itemView.findViewById(R.id.tv_dist);
            tv_rssi = (TextView)itemView.findViewById(R.id.tv_rssi);
        }

        public void populate(BeaconItem item) {
            tv_name.setText(item.name);
            tv_btaddr.setText(item.btAddr);
            tv_mac.setText(item.mac);
            tv_dist.setText(String.format(" %.03fm", item.dist));
            tv_rssi.setText(String.format(" %ddBm", item.rssi));
        }
    }
}
