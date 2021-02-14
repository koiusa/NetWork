package com.example.speechrecognizer.commnon.network.server;

import android.os.AsyncTask;
import android.widget.EditText;

import com.example.speechrecognizer.commnon.network.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class ReceiveSocket extends AsyncTask<Socket, Void, String> {
    private EditText mew;
    private BufferedReader reader = null;
    String receiveString = "NG";

    public ReceiveSocket(EditText ew)
    {
        mew = ew;
    }

    // バックグラウンドの処理。ここではTextViewの操作はできない。
    @Override
    protected String doInBackground(Socket... sockets) {
        // 受信したデータを格納
        try {
            reader = new BufferedReader(new InputStreamReader(sockets[0].getInputStream()));
            receiveString = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return receiveString;
    }

    // doInBackgroundの処理が終了した後に呼ばれる。ここではTextViewの操作はできる。
    @Override
    protected void onPostExecute(String result) {
        mew.setText(result);
    }
}

// AsyncTaskの引数は以下の用途で使用。
// 第一引数：doInBackgroundの引数
// 第二引数：onProgressUpdateの引数
// 第三引数：doInBackgroundの戻り値 and onPostExecuteの引数
public class SocketServer extends AsyncTask<Void, Void, String> {

    private EditText mew;
    ReceiveSocket ak2;

    public SocketServer(EditText ew)
    {
        mew = ew;
    }

    // バックグラウンドの処理。ここではTextViewの操作はできない。
    @Override
    protected String doInBackground(Void... voids) {
        // データ受信準備
        ServerSocket serverSocket = null;
        Socket socket;

        // データ受信
        try {
            serverSocket = new ServerSocket(Constants.DEFAULT_TCP_IP_PORT);

            while (true) {
                // ここでデータを受信するまで待機
                socket = serverSocket.accept();

                ak2 = new ReceiveSocket(mew);

                //ak2.execute(socket);  // 非同期タスクを実行
                ak2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // doInBackgroundの処理が終了した後に呼ばれる。ここではTextViewの操作はできる。
    @Override
    protected void onPostExecute(String result) {
    }
}