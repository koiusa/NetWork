package com.example.speechrecognizer.commnon;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Queue;

public class MessageBrowser {
    private final String TAG = Util.getClassName() ;

    class ShowToastThread extends Thread {
        public ShowToastThread(String message){
            partialResultsQueue.add(message);
        }
        @Override
        public void run(){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (partialResultsQueue.size() > 0) {
                        showToast(partialResultsQueue.poll());
                    }
                }
            });
        }
    }

    private int ShowToastPosition = 0;
    private final int MAX_SHOWTOAST = 3;
    private final int OFFSET_TOAST_POSITION = 40;
    private final int MARGIN_TOAST_POSITION = 80;
    private Activity _activity = null;
    private final Handler handler = new Handler();
    private Queue<String> partialResultsQueue = null;
    public MessageBrowser(Activity activity){
        _activity = activity;
        partialResultsQueue = new ArrayDeque<String>();
    }
    private void showToast(String text){
        Log.d(TAG, Util.getMethodName());
        ShowToastPosition = ShowToastPosition % MAX_SHOWTOAST;
        Toast toast =  new Toast(_activity.getApplicationContext());
        toast = toast.makeText(_activity.getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, ShowToastPosition * OFFSET_TOAST_POSITION + MARGIN_TOAST_POSITION );
        toast.show();
        ShowToastPosition++;
    }

    public void show(String message){
        if (message != null) {
            new ShowToastThread(message).start();
        }
    }

    public void resetPosition(){
        ShowToastPosition = 0;
    }
}
