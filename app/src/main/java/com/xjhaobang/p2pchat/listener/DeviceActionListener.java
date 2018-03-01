package com.xjhaobang.p2pchat.listener;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by PC on 2017/6/2.
 */

public interface DeviceActionListener {

    void showDetails(WifiP2pDevice device);

    void cancelDisconnect();

    void connect(WifiP2pConfig config);

    void disconnect();
}

