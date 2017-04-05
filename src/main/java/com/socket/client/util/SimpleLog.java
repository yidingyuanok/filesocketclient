package com.socket.client.util;

import android.util.Log;

import com.filesocketclient.BuildConfig;


public class SimpleLog {

    /*log_debug_switch_start*/
    protected static boolean myDebug = BuildConfig.DEBUG;
    /*log_debug_switch_end*/

    public static boolean CLIENT_DEBUG = BuildConfig.DEBUG;


    public static String LOGTAG = "Client";


    public static void setDebug(boolean isDebug) {
        myDebug = isDebug;
    }

    public static boolean isDebug() {
        return myDebug;
    }

    public static void d(String msg) {
        if (myDebug)
            Log.d(LOGTAG, msg);
    }

    public static void d(String tag, String msg) {
        if (myDebug)
            Log.d(tag, msg);
    }

    public static void e(String msg) {
        if (myDebug) {
            Log.e(LOGTAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (myDebug) {
            Log.e(tag, msg);
        }
    }

}
