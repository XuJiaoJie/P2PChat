package com.xjhaobang.p2pchat.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.xjhaobang.p2pchat.bean.SocketBeen;
import com.xjhaobang.p2pchat.utils.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by PC on 2017/6/2.
 */

public class TransferService extends IntentService {
    private static final String TAG = "TransferService";
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
    public static final String ACTION_SEND_MSG = "com.example.android.wifidirect.SEND_MSG";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    public static final String EXTRAS_SEND_SOCKET_BEEN = "socketBeen";

    public TransferService(String name) {
        super(name);
    }

    public TransferService() {
        super("TransferService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                OutputStream stream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream is = null;
                try {
                    is = cr.openInputStream(Uri.parse(fileUri));
                } catch (FileNotFoundException e) {
                    Log.d(TAG, e.toString());
                }
                FileUtil.copyFile(is, stream);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else if (intent.getAction().equals(ACTION_SEND_MSG)){
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            SocketBeen socketBeen = (SocketBeen) intent.getSerializableExtra(EXTRAS_SEND_SOCKET_BEEN);
            Socket socket = new Socket();
            try {
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                ObjectOutputStream os  = new ObjectOutputStream(socket.getOutputStream());
                os.writeObject(socketBeen);
                Log.e(TAG, "onHandleIntent: " + socketBeen.getMsg());
                os.flush();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
