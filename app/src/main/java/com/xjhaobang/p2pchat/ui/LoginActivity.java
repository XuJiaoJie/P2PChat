package com.xjhaobang.p2pchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xjhaobang.p2pchat.R;
import com.xjhaobang.p2pchat.constant.Constant;
import com.xjhaobang.p2pchat.utils.HostIPUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {
    @InjectView(R.id.et_login_username)
    EditText mEtLoginUsername;
//    @InjectView(R.id.et_login_password)
//    EditText mEtLoginPassword;
    @InjectView(R.id.btn_login_login)
    Button mBtnLoginLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_login_login)
    public void onViewClicked() {
        if (TextUtils.isEmpty(mEtLoginUsername.getText().toString())){
            Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT).show();
        }else {
            Constant.userName = mEtLoginUsername.getText().toString();
            Constant.userIP = HostIPUtil.getHostIP();
            Intent intent = new Intent(this,WiFiDirectActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
