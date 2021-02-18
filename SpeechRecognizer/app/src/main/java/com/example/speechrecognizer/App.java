package com.example.speechrecognizer;

import android.app.Application;
import com.example.speechrecognizer.commnon.network.Dispatcher;


public class App extends Application {
    private Dispatcher tClient = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Dispatcher getDispatcher() {
        return tClient;
    }

    public void setDispatcher(Dispatcher client) {
        tClient = client;
    }
}
