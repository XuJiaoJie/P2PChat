package com.xjhaobang.p2pchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xjhaobang.p2pchat.R;
import com.xjhaobang.p2pchat.adapter.FriendsListAdapter;
import com.xjhaobang.p2pchat.bean.SocketBeen;
import com.xjhaobang.p2pchat.constant.Constant;
import com.xjhaobang.p2pchat.listener.OnClickRecyclerViewListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by PC on 2017/6/3.
 */

public class FriendsListActivity extends AppCompatActivity implements OnClickRecyclerViewListener {
    private static final String TAG = "FriendsListActivity";
    @InjectView(R.id.tv_user_self_name)
    TextView mTvUserSelfName;
    @InjectView(R.id.tv_user_self_ip)
    TextView mTvUserSelfIp;
    @InjectView(R.id.rv_friends_list)
    RecyclerView mRvFriendsList;
    @InjectView(R.id.ib_link)
    ImageButton mIbLink;
    private List<SocketBeen> mBeenList;
    private List<SocketBeen> mTempList;
    private FriendsListAdapter mAdapter;
    private TreeMap<String, List<SocketBeen>> mMap;
    private WiFiDirectActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initData() {
        mBeenList = new ArrayList<>();
        mTempList = new ArrayList<>();
        mMap = new TreeMap<>();
        SocketBeen been = (SocketBeen) getIntent().getSerializableExtra("friendsListBeen");
        if (been != null) {
            mBeenList.add(been);
            mTempList.addAll(mBeenList);
            mMap.put(been.getOtherIP(), mTempList);
        }
        mAdapter = new FriendsListAdapter(this, mBeenList);
        mAdapter.setOnRecyclerViewListener(this);
    }

    private void initView() {
        mRvFriendsList.setHasFixedSize(true);
        mRvFriendsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvFriendsList.setAdapter(mAdapter);
        mTvUserSelfName.setText(Constant.userName);
        mTvUserSelfIp.setText(Constant.userIP);
    }

    @Override
    public void onItemClick(int position) {
        Log.e(TAG, "onItemClick: ");
        Intent intent = new Intent(this, ChatActivity.class);
        String key1 = mBeenList.get(position).getUserIP();
        String key2 = mBeenList.get(position).getOtherIP();
        String key;
        if (key1.equals(Constant.userIP)) {
            key = key2;
        } else {
            key = key1;
        }
        intent.putExtra("chatList", (Serializable) mMap.get(key));
        intent.putExtra("chatOtherIP", key);
        startActivityForResult(intent, 0);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SocketBeen socketBeen) {
        if (mMap.containsKey(socketBeen.getUserIP())) {
            mMap.get(socketBeen.getUserIP()).add(socketBeen);
        } else {
            List<SocketBeen> list = new ArrayList<>();
            list.add(socketBeen);
            mMap.put(socketBeen.getUserIP(), list);
            mBeenList.add(socketBeen);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String s) {
        Log.e(TAG, "onEvent: "  + s);
        Toast.makeText(getApplicationContext(),""+ s ,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            List<SocketBeen> list = (ArrayList<SocketBeen>) data.getSerializableExtra("returnList");
            String key = data.getStringExtra("returnString");
            if (mMap.containsKey(key)) {
                mMap.get(key).clear();
                mMap.get(key).addAll(list);
            }
        }
    }

    @OnClick(R.id.ib_link)
    public void onViewClicked() {
        startActivity(new Intent(this,WiFiDirectActivity.class));
    }
}
