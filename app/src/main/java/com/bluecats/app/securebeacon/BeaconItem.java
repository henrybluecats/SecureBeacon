package com.bluecats.app.securebeacon;

import com.bluecats.sdk.BCBeacon;

import java.util.Objects;

public class BeaconItem {

    public String btAddr;
    public String mac;
    public String name;
    public int rssi = Integer.MIN_VALUE;
    public double dist = -0.9f;

    public BeaconItem(BCBeacon beacon) {
        btAddr = beacon.getBluetoothAddress();
        name = MainActivity.findNameByBtAddr(btAddr);
        dist = beacon.getAccuracy();
        rssi = beacon.getRSSI();
        mac = beacon.getPeripheralIdentifier();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeaconItem that = (BeaconItem) o;
        return btAddr.equalsIgnoreCase(that.btAddr);
    }

    @Override
    public int hashCode() {

        return btAddr.hashCode();
    }
}
