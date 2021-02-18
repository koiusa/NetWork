package com.example.speechrecognizer.commnon.network.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

public class TcpClient extends Thread {

    private CallBackTask callbacktask;
    private Socket socket = null;
    private boolean running = false;
    private Queue<String> queue = null;
    public TcpClient()
    {
        socket = new Socket();
        queue = new ArrayDeque<String>();
    }

    public void dispose(){
        socket = null;
    }

    public void send(String message){
        if (socket != null) {
            if (socket.isConnected()) {
                queue.add(message);
            }
        }
    }

    public void disConnect() {
        running = false;
        if (socket.isConnected()) {
            try {
                socket.shutdownOutput();
                socket.shutdownInput();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void connect(InetSocketAddress... inetSocketAddresses){
        try {
            socket.connect(inetSocketAddresses[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        running = true;
        while (running) {
            if (queue.size() > 0) {
                StartSend(queue.poll());
                StartReceive();
            }
        }
    }

    private void StartSend(String message){
        BufferedWriter writer = null;
        try {
           if (socket.isConnected()) {
               writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
               writer.write(message);
               writer.flush();
           }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void StartReceive(){
        try {
            if (socket.isConnected()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String result = reader.readLine();
                callbacktask.CallBack(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnCallBack(CallBackTask t_object)
    {
        callbacktask = t_object;
    }

    // コールバック用のインターフェース定義
    public interface CallBackTask
    {
        void CallBack(String result);
    }
}
