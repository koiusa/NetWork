package com.example.speechrecognizer.commnon;

public class Util {
    public static String getClassName() {
        return Thread.currentThread().getStackTrace()[2].getClassName();
    }
    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }
}
