package com.example.speechrecognizer.commnon.network.client;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

// AsyncTaskの引数は以下の用途で使用。
// 第一引数：doInBackgroundの引数
// 第二引数：onProgressUpdateの引数
// 第三引数：doInBackgroundの戻り値 and onPostExecuteの引数
public class DatagramSocketClient extends AsyncTask<DatagramPacket, Void, String> {

    private CallBackTask mcallbacktask;

    // バックグラウンドの処理。ここではTextViewの操作はできない。
    @Override
    protected String doInBackground(DatagramPacket... datagramPackets) {
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.send(datagramPackets[0]);
            datagramSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // doInBackgroundの処理が終了した後に呼ばれる。ここではTextViewの操作はできる。
    @Override
    protected void onPostExecute(String result) {
        mcallbacktask.CallBack(result);
    }

    public void setOnCallBack(CallBackTask t_object)
    {
        mcallbacktask = t_object;
    }

    // コールバック用のインターフェース定義
    public interface CallBackTask
    {
        void CallBack(String result);
    }
}