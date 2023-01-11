package com.example.furnacethermometer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String PREFS_CHANGED = "prefs_changed";
    public static final String LANGUAGE = "language";

    public static final String IP_ADDRESS = "ip_address";
    public static final String T_LIMIT_ONE = "t_limit_one";
    public static final String T_LIMIT_TWO = "t_limit_two";
    public static final String T_REFRESH_TIME = "t_refresh_time";
    public static final String NOTIF_REFRESH_TIME = "notif_refresh_time";
    public static final String INTERFACE_REFRESH_TIME = "interface_refresh_time";

    EditText ipAddressEtxt;
    EditText tLimitEtxt;
    EditText tLimitEtxt2;
    EditText refreshTimeEtxt;
    EditText notifTimeEtxt;
    EditText interfaceRefreshEtxt;

    String language;

    private String idAddress;
    private int tLimitOne;
    private int tLimitTwo;
    private int refreshTime;
    private int notifTime;
    private int interfaceRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.ipAddressEtxt = (EditText) findViewById(R.id.ipAddressEtxt);
        this.tLimitEtxt = (EditText) findViewById(R.id.tLimitEtxt);
        this.tLimitEtxt2 = (EditText) findViewById(R.id.tLimitEtxt2);
        this.refreshTimeEtxt = (EditText) findViewById(R.id.refreshTimeEtxt);
        this.notifTimeEtxt = (EditText) findViewById(R.id.notifTimeEtxt);
        this.interfaceRefreshEtxt = (EditText) findViewById(R.id.interfaceRefreshEtxt);

        loadData();
        updateViews();


    }

    public void setLocaleRussian(View view) {
        saveData();
        myApp.setLocale(SettingsActivity.this, "ru");
        startActivity(getIntent());
        finish();
        overridePendingTransition(0, 0);
    }

    public void setLocaleEnglish(View view) {
        saveData();
        myApp.setLocale(SettingsActivity.this, "en");
        startActivity(getIntent());
        finish();
        overridePendingTransition(0, 0);
    }

    public void saveSettingsAction(View view) {
        try {
            saveData();
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(PREFS_CHANGED, true);
        editor.putString(LANGUAGE, language);
        editor.putString(IP_ADDRESS, ipAddressEtxt.getText().toString());
        editor.putInt(T_LIMIT_ONE, Integer.parseInt(tLimitEtxt.getText().toString()));
        editor.putInt(T_LIMIT_TWO, Integer.parseInt(tLimitEtxt2.getText().toString()));
        editor.putInt(T_REFRESH_TIME, Integer.parseInt(refreshTimeEtxt.getText().toString()));
        editor.putInt(NOTIF_REFRESH_TIME, Integer.parseInt(notifTimeEtxt.getText().toString()));
        editor.putInt(INTERFACE_REFRESH_TIME, Integer.parseInt(interfaceRefreshEtxt.getText().toString()));

        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        idAddress = sharedPreferences.getString(IP_ADDRESS, "http://192.168.0.120/");
        tLimitOne = sharedPreferences.getInt(T_LIMIT_ONE, 55);
        tLimitTwo = sharedPreferences.getInt(T_LIMIT_TWO, 65);
        language = sharedPreferences.getString(LANGUAGE, "en");
        refreshTime = sharedPreferences.getInt(T_REFRESH_TIME, 5);
        notifTime = sharedPreferences.getInt(NOTIF_REFRESH_TIME, 10);
        interfaceRefresh = sharedPreferences.getInt(INTERFACE_REFRESH_TIME, 1);

    }

    public void updateViews() {
        try {
            ipAddressEtxt.setText(idAddress);
            tLimitEtxt.setText(Integer.toString(tLimitOne));
            tLimitEtxt2.setText(Integer.toString(tLimitTwo));
            refreshTimeEtxt.setText(Integer.toString(refreshTime));
            notifTimeEtxt.setText(Integer.toString(notifTime));
            interfaceRefreshEtxt.setText(Integer.toString(interfaceRefresh));

        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }

    }
}