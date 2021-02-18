package com.example.speechrecognizer.commnon.network;

import android.app.Activity;

import com.example.speechrecognizer.commnon.MessageBrowser;
import com.example.speechrecognizer.commnon.network.client.TcpClient;

import java.net.InetSocketAddress;

public class Dispatcher implements TcpClient.CallBackTask {
    private Activity _activity = null;
    private TcpClient ak;
    private Integer count = 0;
    private InetSocketAddress inetSocketAddress = null;
    private MessageBrowser messageBrowser = null;
    public Dispatcher(Activity activity){
        _activity = activity;
        messageBrowser = new MessageBrowser(activity);
        init();
    }

    public void startSequence(String iPAddress, int port){
        init();
        connect(iPAddress, port);
        start();
    }

    private void init()
    {
        if (ak == null) {
            ak = new TcpClient();
            ak.setOnCallBack(this);
        }
    }

    private void connect(String iPAddress, int port){
        new Thread (new Runnable() {
            public void run() {
                try {
                    if (ak != null) {
                        //メインスレッドではエラーとなる
                        ak.connect(new InetSocketAddress(iPAddress, port));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void start(){
        new Thread (new Runnable() {
            public void run() {
                try {
                    if (ak != null) {
                        ak.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void disConnect(){
        if (ak != null) {
            ak.disConnect();
            ak.dispose();
            ak = null;
        }
    }

    // 非同期タスクのコールバックが呼ばれるたびに処理を行う。
    @Override
    public void CallBack(String result) {
        messageBrowser.show(result);
    }

    public void send(String msg) {
        if (ak != null) {
            ak.send(msg);
        }
    }
}
