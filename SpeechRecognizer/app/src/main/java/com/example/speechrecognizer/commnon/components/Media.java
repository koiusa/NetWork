package com.example.speechrecognizer.commnon.components;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

/*
結果を保存するクラス
 */
public class Media {
    class FileAccessThread extends Thread {
        public FileAccessThread(String message){
            msgBuffer.add(message);
        }
        @Override
        public void run() {
            if (msgBuffer.size() > 0) {
                write(msgBuffer.poll());
            }
        }
    }

    private Activity _activity = null;
    private FileWriter filewriter;
    private Queue<String> msgBuffer = null;
    private final String FILENAME = "results.txt";
    private boolean initialized = false;
    public Media(Activity activity){
        _activity = activity;
    }

    public void initialize() {
        createDirectory();
        msgBuffer = new ArrayDeque<String>();
        initialized = true;
    }

    public boolean getEnabled(){
        return initialized;
    }

    public boolean createDirectory(){
        File dir = _activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (result) {
                System.out.println("createDirectory : Success");
            }else{
                return false;
            }
        }
        return true;
    }

    private void write(String string){
        try {
            File dir = _activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(dir.getPath() + "/" + FILENAME);
            filewriter = new FileWriter(file,false);
            filewriter.write(string);
            filewriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void commit(String string){
        if (initialized) {
            new FileAccessThread(string).start();
        }
    }
}
