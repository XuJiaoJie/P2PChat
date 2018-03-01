package com.xjhaobang.p2pchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xjhaobang.p2pchat.bean.SocketBeen;
import com.xjhaobang.p2pchat.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by PC on 2017/6/6.
 */

public class ServerTransferService extends Service {
    private static final String TAG = "ServerTransferService";
    private ServerThread serverThread;
    private FileServerThread mFileServerThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serverThread = new ServerThread();
        serverThread.start();
        mFileServerThread = new FileServerThread();
        mFileServerThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private class ServerThread extends Thread {
        ServerSocket serverSocket;
        Socket socket;
        ObjectInputStream is;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8988);
                while (true) {
                    socket = serverSocket.accept();
                    is = new ObjectInputStream(socket.getInputStream());
                    Object object = is.readObject();
                    SocketBeen socketBeen = (SocketBeen) object;
                    Log.e(TAG, "run: " + socketBeen.getMsg());
                    EventBus.getDefault().post(socketBeen);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class FileServerThread extends Thread {
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(8888);
                while (true){
                    Socket client = serverSocket.accept();
                    final File f = new File(Environment.getExternalStorageDirectory() + "/"
                            + getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                            + ".jpg");
                    File dirs = new File(f.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    f.createNewFile();
                    InputStream inputstream = client.getInputStream();
                    FileUtil.copyFile(inputstream, new FileOutputStream(f));
                    EventBus.getDefault().post(f.getAbsolutePath());
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }


}
