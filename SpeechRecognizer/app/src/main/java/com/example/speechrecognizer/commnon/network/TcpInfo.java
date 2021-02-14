package com.example.speechrecognizer.commnon.network;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.speechrecognizer.commnon.Util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class TcpInfo {
    // モバイルネットワークインターフェースのIPアドレスを取得する。
// 注意：インターフェース2つとか、もろもろの異常系は考慮していない
    private static final String LOCAL_LOOPBACK_ADDR = "127.0.0.1";
    private static final String INVALID_ADDR = "0.0.0.0";

    public static String getMobileIPAddress() {
        String TAG = Util.getClassName();
        try {
            NetworkInterface ni = NetworkInterface.getByName("hso0"); // インターフェース名
            if (ni == null) {
                Log.d(TAG, "Failed to get mobile interface.");
                return null;
            }

            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                String address = ((InetAddress)((Enumeration) addresses).nextElement()).getHostAddress();
                if (!LOCAL_LOOPBACK_ADDR.equals(address) && !INVALID_ADDR.equals(address)) {
                    // Found valid ip address.
                    return address;
                }
            }
            return null;
        } catch (Exception e) {
            Log.d(TAG, "Exception occured. e=" + e.getMessage());
            return null;
        }
    }

    // Wi-FiインターフェースのIPアドレスを取得する。
// 注意：インターフェース2つとか、もろもろの異常系は考慮していない
    public static String getWifiIPAddress(Context context) {
        WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        int ipAddr = info.getIpAddress();
        String ipString = String.format("%02d.%02d.%02d.%02d",
                (ipAddr>>0)&0xff, (ipAddr>>8)&0xff, (ipAddr>>16)&0xff, (ipAddr>>24)&0xff);
        return ipString;
    }
}
