package com.example.speechrecognizer.commnon.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.speechrecognizer.commnon.network.Constants;

public class Configure {
    public final String IP_ADDRESS = "IP_ADDRESS";
    public final String PORT = "PORT";
    public final String SUFFIX = "SUFFIX";
    public final String USE_SUFFIX = "USE_SUFFIX";
    SharedPreferences sharedPreferences = null;
    public Configure(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    private void setString(String tag, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tag, value);
        editor.commit();
    }
    private void setInt(String tag, int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(tag, value);
        editor.commit();
    }
    private void setBoolean(String tag, boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(tag, value);
        editor.commit();
    }
    public void setIPAddress(String value){
        setString(IP_ADDRESS, value);
    }
    public String getIPAddress(){
        return sharedPreferences.getString(IP_ADDRESS, "");
    }
    public void setPort(int value){
        setInt(PORT, value);
    }
    public int getPort(){
        return sharedPreferences.getInt(PORT, Constants.DEFAULT_TCP_IP_PORT);
    }
    public void setSuffix(String value){setString(SUFFIX, value); }
    public String getSuffix(){
        return sharedPreferences.getString(SUFFIX, Constants.DEFAULT_SUFFIX);
    }
    public void setUseSuffix(boolean value){setBoolean(USE_SUFFIX, value); }
    public boolean getUseSuffix(){
        return sharedPreferences.getBoolean(USE_SUFFIX, Constants.DEFAULT_USE_SUFFIX);
    }
}
