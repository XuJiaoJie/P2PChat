package com.xjhaobang.p2pchat.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.xjhaobang.p2pchat.R;
import com.xjhaobang.p2pchat.adapter.ChatMessageAdapter;
import com.xjhaobang.p2pchat.bean.SocketBeen;
import com.xjhaobang.p2pchat.constant.Constant;
import com.xjhaobang.p2pchat.service.TransferService;
import com.xjhaobang.p2pchat.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by PC on 2017/6/5.
 */

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    @InjectView(R.id.lv_chat_content)
    ListView mLvChatContent;
    @InjectView(R.id.id_chat_send)
    Button mIdChatSend;
    @InjectView(R.id.id_chat_msg)
    EditText mIdChatMsg;
    @InjectView(R.id.ib_file_transfer)
    ImageButton mIbFileTransfer;
    private List<SocketBeen> mList;
    private ChatMessageAdapter mAdapter;
    private String mOtherIP;
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        mList = new ArrayList<>();
        mList = (ArrayList<SocketBeen>) getIntent().getSerializableExtra("chatList");
        mOtherIP = getIntent().getStringExtra("chatOtherIP");
        mAdapter = new ChatMessageAdapter(this, mList);
        mLvChatContent.setAdapter(mAdapter);
    }

    @OnClick({R.id.ib_file_transfer, R.id.id_chat_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_file_transfer:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                break;
            case R.id.id_chat_send:
                if (mIdChatMsg.getText().toString().isEmpty()) {
                    Toast.makeText(this, "发送消息不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    SocketBeen been = new SocketBeen();
                    been.setUserIP(Constant.userIP);
                    been.setOtherIP(mOtherIP);
                    been.setTime(TimeUtil.getCurrentTime());
                    been.setMsg(mIdChatMsg.getText().toString());
                    been.setUserName(Constant.userName);
                    mList.add(been);
                    mIdChatMsg.setText("");
                    mAdapter.notifyDataSetInvalidated();
                    mLvChatContent.setSelection(mList.size() - 1);
                    sendChatMsg(been);
                }
                break;
        }
    }

    /**
     * 开启发送消息服务
     */
    private void sendChatMsg(SocketBeen socketBeen) {
        Intent serviceIntent = new Intent(this, TransferService.class);
        serviceIntent.setAction(TransferService.ACTION_SEND_MSG);
        serviceIntent.putExtra(TransferService.EXTRAS_GROUP_OWNER_ADDRESS, socketBeen.getOtherIP());
        serviceIntent.putExtra(TransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        serviceIntent.putExtra(TransferService.EXTRAS_SEND_SOCKET_BEEN, socketBeen);
        startService(serviceIntent);
    }

    /**
     * 接受消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SocketBeen socketBeen) {
        mList.add(socketBeen);
        mAdapter.notifyDataSetChanged();
        mLvChatContent.setSelection(mList.size() - 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String result){
        if (result != null) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("returnList", (Serializable) mList);
        intent.putExtra("returnString", mOtherIP);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        Intent serviceIntent = new Intent(this, TransferService.class);
        serviceIntent.setAction(TransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(TransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(TransferService.EXTRAS_GROUP_OWNER_ADDRESS, mOtherIP);
        serviceIntent.putExtra(TransferService.EXTRAS_GROUP_OWNER_PORT, 8888);
        startService(serviceIntent);
    }
}
