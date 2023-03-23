package com.example.furnacethermometer;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class myApp extends Application {
    private static final String TAG = "myApp";
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Application created");
        // Required initialization logic here!
        if (isExternalStorageWritable()) {

            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/MyPersonalAppFolder");
            File logDirectory = new File(appDirectory + "/logs");
            File logFile = new File(logDirectory, "logcat_" + System.currentTimeMillis() + ".txt");
            Log.d(TAG, appDirectory.toString());

            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
                Log.d(TAG, "App folder created");
            }

            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
                Log.d(TAG, "App log folder created");
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
//                process = Runtime.getRuntime().exec( "logcat -f " + logFile + " *:S MyActivity:D MyActivity2:D");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (isExternalStorageReadable()) {
            // only readable
            Log.d(TAG, "isExternalStorageReadable only readable");
        } else {
            // not accessible
            Log.d(TAG, "isExternalStorageReadable not accessible");
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
