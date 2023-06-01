package com.example.furnacethermometer.lib;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.furnacethermometer.MainActivity;


public class MyAppPreferences extends Activity {

    private static final String SHARED_PREFS = "shared_prefs";
    private static final String PREFS_CHANGED = "prefs_changed";
    private static final String LANGUAGE = "language";
    private static final String IP_ADDRESS = "ip_address";
    private static final String T_LIMIT_ONE = "t_limit_one";
    private static final String T_LIMIT_TWO = "t_limit_two";
    private static final String T_REFRESH_TIME = "t_refresh_time";
    private static final String NOTIF_REFRESH_TIME = "notif_refresh_time";
    private static final String INTERFACE_REFRESH_TIME = "interface_refresh_time";

    public String language;
    public String idAddress;
    public int tLimitOne;
    public int tLimitTwo;
    public int refreshTime;
    public int notifTime;
    public int interfaceRefresh;

    private SharedPreferences sharedPreferences;

    private MainActivity context;

    //    private constructor to avoid client applications using the constructor
    public MyAppPreferences(MainActivity context) {
        this.context = context;
        this.sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        this.idAddress = sharedPreferences.getString(IP_ADDRESS, "http://192.168.0.120/");
        this.tLimitOne = sharedPreferences.getInt(T_LIMIT_ONE, 55);
        this.tLimitTwo = sharedPreferences.getInt(T_LIMIT_TWO, 65);
        this.language = sharedPreferences.getString(LANGUAGE, "en");
        this.refreshTime = sharedPreferences.getInt(T_REFRESH_TIME, 5);
        this.notifTime = sharedPreferences.getInt(NOTIF_REFRESH_TIME, 10);
        this.interfaceRefresh = sharedPreferences.getInt(INTERFACE_REFRESH_TIME, 1);
    }

    public void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(PREFS_CHANGED, true);
        editor.putString(LANGUAGE, language);
        editor.putString(IP_ADDRESS, idAddress);
        editor.putInt(T_LIMIT_ONE, tLimitOne);
        editor.putInt(T_LIMIT_TWO, tLimitTwo);
        editor.putInt(T_REFRESH_TIME, refreshTime);
        editor.putInt(NOTIF_REFRESH_TIME, notifTime);
        editor.putInt(INTERFACE_REFRESH_TIME, interfaceRefresh);

        editor.apply();
    }
//    private void loadData() {
//        this.idAddress = sharedPreferences.getString(IP_ADDRESS, "http://192.168.0.120/");
//        this.tLimitOne = sharedPreferences.getInt(T_LIMIT_ONE, 55);
//        this.tLimitTwo = sharedPreferences.getInt(T_LIMIT_TWO, 65);
//        this.language = sharedPreferences.getString(LANGUAGE, "en");
//        this.refreshTime = sharedPreferences.getInt(T_REFRESH_TIME, 5);
//        this.notifTime = sharedPreferences.getInt(NOTIF_REFRESH_TIME, 10);
//        this.interfaceRefresh = sharedPreferences.getInt(INTERFACE_REFRESH_TIME, 1);
//    }
}
