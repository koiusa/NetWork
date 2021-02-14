package com.example.speechrecognizer.commnon.network;

import android.app.Activity;

import com.example.speechrecognizer.commnon.MessageBrowser;
import com.example.speechrecognizer.commnon.network.client.SocketClient;

import java.net.InetSocketAddress;

public class Dispatcher implements SocketClient.CallBackTask {
    private Activity _activity = null;
    private SocketClient ak;
    private Integer count = 0;
    private InetSocketAddress inetSocketAddress = null;
    private MessageBrowser messageBrowser = null;
    public Dispatcher(Activity activity){
        _activity = activity;
        messageBrowser = new MessageBrowser(activity);
    }

    public void connect(String IPAddress, int port){
        // ソケット通信用にポート設定。送信したいデータとIPアドレス設定。
        inetSocketAddress = new InetSocketAddress(IPAddress, port);
    }

    // 非同期タスクのコールバックが呼ばれるたびに処理を行う。
    @Override
    public void CallBack(String result) {
        messageBrowser.show(result);
    }

    public void send(String msg) {
        ak = new SocketClient(msg);
        ak.setOnCallBack(this);
        ak.execute(inetSocketAddress);  // 非同期タスクを実行
    }
}
