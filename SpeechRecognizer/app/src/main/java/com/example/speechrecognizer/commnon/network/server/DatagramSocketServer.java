package com.example.speechrecognizer.commnon.network.server;

import android.os.AsyncTask;
import android.widget.EditText;

import com.example.speechrecognizer.commnon.network.Constants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


class DatagramReceiveSocket extends AsyncTask<DatagramPacket, Void, DatagramPacket> {

    private EditText mew;
    byte receiveBuff[] = new byte[1024];
    private CallBackTask mcallbacktask;

    public DatagramReceiveSocket(EditText ew)
    {
        mew = ew;
    }

    // バックグラウンドの処理。ここではTextViewの操作はできない。
    @Override
    protected DatagramPacket doInBackground(DatagramPacket... datagramPackets) {
        return datagramPackets[0];
    }

    // doInBackgroundの処理が終了した後に呼ばれる。ここではTextViewの操作はできる。
    @Override
    protected void onPostExecute(DatagramPacket datagramPacket) {

        String string = "NG";

        // 受信したデータはbyte配列なので、文字列に変換。
        try {
            string = new String(datagramPacket.getData(), "SHIFT_JIS");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mew.setText(string);

        mcallbacktask.CallBack();
    }

    public void setOnCallBack(CallBackTask t_object)
    {
        mcallbacktask = t_object;
    }
    // コールバック用のインターフェース定義
    interface CallBackTask
    {
        void CallBack();
    }
}

// AsyncTaskの引数は以下の用途で使用。
// 第一引数：doInBackgroundの引数
// 第二引数：onProgressUpdateの引数
// 第三引数：doInBackgroundの戻り値 and onPostExecuteの引数
public class DatagramSocketServer extends AsyncTask<Void, Void, String> implements DatagramReceiveSocket.CallBackTask {

    private EditText mew;
    DatagramReceiveSocket ak2;
    boolean misOk = true;


    public DatagramSocketServer(EditText ew)
    {
        mew = ew;
    }

    // バックグラウンドの処理。ここではTextViewの操作はできない。
    @Override
    protected String doInBackground(Void... voids) {
        // データ受信準備
        byte receiveBuff[] = new byte[1024];
        DatagramSocket datagramSocket = null;
        DatagramPacket datagramPacket = new DatagramPacket(receiveBuff, receiveBuff.length);

        // データ受信
        try {
            datagramSocket = new DatagramSocket(Constants.DEFAULT_TCP_IP_PORT);

            while (true) {
                if (misOk) {
                    // ここでデータを受信するまで待機
                    datagramSocket.receive(datagramPacket);

                    misOk = false;
                    ak2 = new DatagramReceiveSocket(mew);
                    ak2.setOnCallBack(this);
                    //ak2.execute(datagramSocket);  // 非同期タスクを実行
                    ak2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, datagramPacket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            datagramSocket.close();
        }

        return null;
    }

    // doInBackgroundの処理が終了した後に呼ばれる。ここではTextViewの操作はできる。
    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    public void CallBack() {
        ak2 = null;
        misOk = true;
    }
}

