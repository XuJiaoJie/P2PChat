package com.xjhaobang.p2pchat.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xjhaobang.p2pchat.R;
import com.xjhaobang.p2pchat.broadcast.WiFiDirectBroadcastReceiver;
import com.xjhaobang.p2pchat.fragment.DeviceDetailFragment;
import com.xjhaobang.p2pchat.fragment.DeviceListFragment;
import com.xjhaobang.p2pchat.listener.DeviceActionListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by PC on 2017/6/2.
 */

public class WiFiDirectActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener, DeviceActionListener {
    private static final String TAG = "WiFiDirectActivity";
    @InjectView(R.id.ib_menu)
    ImageButton mIbMenu;
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);
        ButterKnife.inject(this);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }

    @OnClick(R.id.ib_menu)
    public void onViewClicked() {
        Log.e(TAG, "onViewClicked: ");
        PopupMenu popuMenu = new PopupMenu(this,mIbMenu);
        popuMenu.getMenuInflater().inflate(R.menu.action_items,popuMenu.getMenu());
        popuMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.atn_direct_enable:
                        if (manager != null && channel != null) {
                            // Since this is the system wireless settings activity, it's
                            // not going to send us a result. We will be notified by
                            // WiFiDeviceBroadcastReceiver instead.
                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        } else {
                            Log.e(TAG, "channel or manager is null");
                        }
                        return true;

                    case R.id.atn_direct_discover:
                        if (!isWifiP2pEnabled) {
                            Toast.makeText(WiFiDirectActivity.this, R.string.p2p_off_warning,
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        final DeviceListFragment fragment = (DeviceListFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.frag_list);
                        fragment.onInitiateDiscovery();
                        manager.discoverPeers(channel, new ActionListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(WiFiDirectActivity.this, "搜索初始化",
                                        Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(int reasonCode) {
                                Toast.makeText(WiFiDirectActivity.this, "搜索失败: " + reasonCode,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true;
                }
                return false;
            }
        });
        popuMenu.show();
    }


    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "通道丢失，请重试", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "警告！连接可能永远丢失，请关闭或重启P2P开关",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);
    }

    @Override
    public void cancelDisconnect() {
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(WiFiDirectActivity.this, "连接信息：",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WiFiDirectActivity.this, "连接失败，原因: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiDirectActivity.this, "连接失败，请重试.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        manager.removeGroup(channel, new ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

            }
            @Override
            public void onSuccess() {
                fragment.getView().setVisibility(View.GONE);
            }

        });
    }

}
