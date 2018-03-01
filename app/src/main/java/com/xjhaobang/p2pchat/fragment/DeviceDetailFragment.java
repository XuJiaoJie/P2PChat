package com.xjhaobang.p2pchat.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xjhaobang.p2pchat.R;
import com.xjhaobang.p2pchat.bean.SocketBeen;
import com.xjhaobang.p2pchat.constant.Constant;
import com.xjhaobang.p2pchat.listener.DeviceActionListener;
import com.xjhaobang.p2pchat.service.ServerTransferService;
import com.xjhaobang.p2pchat.ui.FriendsListActivity;
import com.xjhaobang.p2pchat.utils.TimeUtil;

/**
 * Created by PC on 2017/6/2.
 */

public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {
    private static final String TAG = "DeviceDetailFragment";
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "按返回键取消",
                        "正在连接:" + device.deviceAddress, true, true);
                ((DeviceActionListener) getActivity()).connect(config);

            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });

        return mContentView;
    }


    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text) + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes) : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("这个组拥有者的IP- " + info.groupOwnerAddress.getHostAddress());

        if (info.groupFormed && info.isGroupOwner) {
            getActivity().startService(new Intent(getActivity(), ServerTransferService.class));
            startActivity(new Intent(getActivity(), FriendsListActivity.class));
        } else if (info.groupFormed) {
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
            SocketBeen been = new SocketBeen();
            been.setUserName("待定");
            been.setMsg("....");
            been.setOtherIP(info.groupOwnerAddress.getHostAddress());
            been.setUserIP(Constant.userIP);
            been.setTime(TimeUtil.getCurrentTime());
            Intent intent = new Intent(getActivity(), FriendsListActivity.class);
            intent.putExtra("friendsListBeen", been);
//            EventBus.getDefault().post("IP。。。。");
            startActivity(intent);
            getActivity().startService(new Intent(getActivity(), ServerTransferService.class));
        }

        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
//        ((DeviceActionListener) getActivity()).disconnect();
        getActivity().finish();
//        EventBus.getDefault().post(getActivity());
    }

    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        this.getView().setVisibility(View.GONE);
    }


}
