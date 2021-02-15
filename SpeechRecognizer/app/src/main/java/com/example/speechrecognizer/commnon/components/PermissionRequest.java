package com.example.speechrecognizer.commnon.components;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.speechrecognizer.R;
import com.example.speechrecognizer.commnon.MessageBrowser;
import com.example.speechrecognizer.commnon.Util;

public class PermissionRequest {
    final int PERMISSION_REQUEST_RECORD = 1;
    final int PERMISSION_REQUEST_STORAGE = 2;
    Activity _activity = null;
    boolean recordPermissionState = false;
    boolean storagePermissionState = false;
    final String[] EXTERNAL_PERMS = {
            Manifest.permission.RECORD_AUDIO
            };
    private MessageBrowser messageBrowser = null;
    public PermissionRequest(Activity activity){
        _activity = activity;
        messageBrowser = new MessageBrowser(activity);
    }
    public void showRecordPermissionCheck() {
        if (ContextCompat.checkSelfPermission(_activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(_activity, EXTERNAL_PERMS, PERMISSION_REQUEST_RECORD);
            }
    }
    public boolean recordPermissionCheck() {
        if (ContextCompat.checkSelfPermission(_activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(_activity, Manifest.permission.RECORD_AUDIO)){
                ActivityCompat.requestPermissions(_activity, EXTERNAL_PERMS, PERMISSION_REQUEST_RECORD);
            }else{
                messageBrowser.show(_activity.getString(R.string.Error_Msg_RecordPermissionIsDisabled));
                messageBrowser.resetPosition();
            }
            recordPermissionState = false;
        }else{
            recordPermissionState = true;
        }
        return recordPermissionState;
    }
    public  boolean storagePermissionRequest(){
        final String[] EXTERNAL_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if (ContextCompat.checkSelfPermission(_activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(_activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(_activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(_activity, Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(_activity, EXTERNAL_PERMS, PERMISSION_REQUEST_STORAGE);
            }else{
                messageBrowser.show(_activity.getString(R.string.Error_Msg_StoragePermissionIsDisabled));
                messageBrowser.resetPosition();
            }
            storagePermissionState = false;
        }else{
            storagePermissionState = true;
        }
        return storagePermissionState;
    }

    public void requestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        switch (requestCode) {
            case PERMISSION_REQUEST_RECORD: {
                if (grantResult.length > 0
                        && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(Util.getClassName(), Util.getMethodName());
                }else{
                    Log.d(Util.getClassName(), Util.getMethodName());
                }
                break;
            }
            case PERMISSION_REQUEST_STORAGE:
                break;
        }
        return;
    }
}
