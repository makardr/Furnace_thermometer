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
    public static final String T_LIMIT = "t_text";


    EditText tLimitEtxt;
    EditText ipAddressEtxt;

    private int tLimitEtxtInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.tLimitEtxt = (EditText) findViewById(R.id.tLimitEtxt);
        this.ipAddressEtxt = (EditText) findViewById(R.id.ipAddressEtxt);


        loadData();
        updateViews();
    }

    public void saveSettingsAction(View view) {
//        Log.d(TAG, tLimitEtxt.getText().toString());
//        ipAddressEtxt.setText(tLimitEtxt.getText());
        try {
            saveData();
        } catch (Exception e){
            Log.e(TAG, String.valueOf(e));
        }

    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(T_LIMIT, Integer.parseInt(tLimitEtxt.getText().toString()));

        editor.apply();
    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        tLimitEtxtInt = sharedPreferences.getInt(T_LIMIT, 0);
    }

    public void updateViews(){
        try {
            tLimitEtxt.setText(Integer.toString(tLimitEtxtInt));
        } catch (Exception e){
            Log.e(TAG, String.valueOf(e));
        }

    }
}