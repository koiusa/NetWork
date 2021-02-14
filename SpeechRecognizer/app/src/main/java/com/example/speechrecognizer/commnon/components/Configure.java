package com.example.speechrecognizer.commnon.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.speechrecognizer.commnon.network.Constants;

public class Configure {
    public final String IP_ADDRESS = "IP_ADDRESS";
    public final String PORT = "PORT";

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
}
