package com.example.furnacethermometer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.furnacethermometer.lib.RefreshTemperatureRunnableTask;
import com.example.furnacethermometer.lib.UpdateInterfaceRunnable;
import com.example.furnacethermometer.lib.UpdateNotificationRunnable;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ThermometerMainActivity";
    private static final String BACKGROUND_NOTIFICATION_CHANNEL_ID = "temperature_notification";
    private static final String ALERT_NOTIFICATION_CHANNEL_ID = "alert_temperature_notification";

    //    Shared preferences finals
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String PREFS_CHANGED = "prefs_changed";
    public static final String LANGUAGE = "language";

    public static final String IP_ADDRESS = "ip_address";
    public static final String T_LIMIT_ONE = "t_limit_one";
    public static final String T_LIMIT_TWO = "t_limit_two";
    public static final String T_REFRESH_TIME = "t_refresh_time";
    public static final String NOTIF_REFRESH_TIME = "notif_refresh_time";
    public static final String INTERFACE_REFRESH_TIME = "interface_refresh_time";

    //    Shared preferences variables
    private String ipAddress;
    private int tLimitOne;
    private int tLimitTwo;
    private int refreshTime;
    private int notifTime;
    private int interfaceRefresh;

    //    Flags
    private boolean taskStartedFlag = false;

    //    Language
    String language = Locale.getDefault().getDisplayLanguage();

    //    Interface elements
    private TextView textViewDisplay;
    private Button startButton;


    //    Background tasks and handlers
    private RefreshTemperatureRunnableTask refreshTemperatureRunnableTask;
    final Handler mainHandler = new Handler(Looper.getMainLooper());
    final HandlerThread backgroundHandlerThread = new HandlerThread("HandlerThreadName");
    private Handler backgroundHandler;

    //    Runnables
    private UpdateInterfaceRunnable interfaceUpdateTask;
    private UpdateNotificationRunnable notificationUpdateTask;

    //    SharedPreference
    private boolean sharedPreferenceChanged = false;

    //    Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Application created");
//        SharedPreferences
        loadData();

//        Language
        Log.d(TAG, "Current language is " + language);

//        Create channel for non-alert notifications
        createNotificationChannel();
//        Create channel for alert notifications
        createAlertNotificationChannel();


//        Initialize background handler
        this.backgroundHandler = createBackgroundHandler();
//        Initialize background runnable tasks
        this.refreshTemperatureRunnableTask = new RefreshTemperatureRunnableTask(this.backgroundHandler, this.ipAddress, this.refreshTime);


//        Initialize interface components in code
        this.textViewDisplay = (TextView) findViewById(R.id.textViewDisplay);
        this.startButton = (Button) findViewById(R.id.startBtn);

//        Recreate saved instance
        if (savedInstanceState != null) {
            Log.i("Main", "Instance recreated");
            if (savedInstanceState.getBoolean("taskStartedFlag")) {
                this.taskStartedFlag = true;
                startButton.setText("Stop");
            }
        }


    }

    //    Save current state of application in case it recreates
//    Recreating interface instance is currently unimportant, because upon recreation application lose background task,
//    and this problem of zombie thread was resolved by simply eliminating thread in onDestroy and restricting user from changing screen orientation, but saving current instance is pointless
//    until I figure out how to save thread in saved instance
    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
//        Save flag for button
        if (taskStartedFlag) {
            savedState.putBoolean("taskStartedFlag", true);
        }
//        Save current temperature
        savedState.putString("storedTemperature", refreshTemperatureRunnableTask.getCurrentTemperature());
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
        if (sharedPreferenceChanged) {
            Log.d(TAG, "onResume: Shared preference changed");
            saveData();
            reinitializeRunnableTasks();
        }
        Log.d(TAG, "Application started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Application resumed");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Application paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Application stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Application destroyed");
        refreshTemperatureRunnableTask.setStopThread();
        stopHandlerTasks();
        createNotification("Температура", "Приложение завершило работу", 2222, ALERT_NOTIFICATION_CHANNEL_ID, 2);
    }


    //    Interface
    public static void changeTV(TextView tv, String text) {
        tv.setText(text);
    }

    //    Interface buttons
    public void startBtnAction(View view) {
        if (!taskStartedFlag) {
            try {
                refreshTemperatureRunnableTask.setStartThread();
                startHandlerTasks();
                taskStartedFlag = true;
                startButton.setText(R.string.stop);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                refreshTemperatureRunnableTask.setStopThread();
                stopHandlerTasks();
                startButton.setText(R.string.start);
                taskStartedFlag = false;
            } catch (Exception e){
                Log.e(TAG, "startBtnAction: " + e.getMessage());
            }
        }

    }

    public void settingsBtnAction(View view) {
        try {
            taskStartedFlag = false;
            startButton.setText(R.string.start);
            refreshTemperatureRunnableTask.setStopThread();
            stopHandlerTasks();
        } catch (Exception e) {
            Log.e(TAG, "settingsBtnAction: cant finish threads");
        }


//        Open settings activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    //    Handlers, background, technical stuff
    public void reinitializeRunnableTasks() {
        //        Initialize background runnable tasks
        this.refreshTemperatureRunnableTask = new RefreshTemperatureRunnableTask(this.backgroundHandler, this.ipAddress, this.refreshTime);
        this.interfaceUpdateTask = new UpdateInterfaceRunnable(textViewDisplay, this.mainHandler, this.refreshTemperatureRunnableTask, this.interfaceRefresh);
        this.notificationUpdateTask = new UpdateNotificationRunnable(this.mainHandler, this.refreshTemperatureRunnableTask, this, this.tLimitOne, this.tLimitTwo, this.notifTime);
    }


    public void startHandlerTasks() throws InterruptedException {
//        Start updating temperature value
        backgroundHandler.post(this.refreshTemperatureRunnableTask);
//        Start updating interface
        this.interfaceUpdateTask = new UpdateInterfaceRunnable(textViewDisplay, this.mainHandler, this.refreshTemperatureRunnableTask, this.interfaceRefresh);
        this.mainHandler.post(interfaceUpdateTask);

//        Start sending notifications
        this.notificationUpdateTask = new UpdateNotificationRunnable(this.mainHandler, this.refreshTemperatureRunnableTask, this, this.tLimitOne, this.tLimitTwo, this.notifTime);
        this.mainHandler.post(this.notificationUpdateTask);
    }

    public void stopHandlerTasks() {
        try {
            notificationUpdateTask.setStopThread();
            interfaceUpdateTask.setStopThread();
        } catch (Exception e) {
            Log.e(TAG, "Could not stop threads normally " + e.getMessage());
        }
    }


    public Handler createBackgroundHandler() {
//        Start handler thread
        backgroundHandlerThread.start();

        return new Handler(backgroundHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Process received messages here!
            }
        };
    }


    //    Shared preferences
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        this.sharedPreferenceChanged = sharedPreferences.getBoolean(PREFS_CHANGED, false);

        this.ipAddress = sharedPreferences.getString(IP_ADDRESS, "http://192.168.0.120/");
        this.tLimitOne = sharedPreferences.getInt(T_LIMIT_ONE, 55);
        this.tLimitTwo = sharedPreferences.getInt(T_LIMIT_TWO, 65);
        this.language = sharedPreferences.getString(LANGUAGE, "en");
        this.refreshTime = sharedPreferences.getInt(T_REFRESH_TIME, 5);
        this.notifTime = sharedPreferences.getInt(NOTIF_REFRESH_TIME, 10);
        this.interfaceRefresh = sharedPreferences.getInt(INTERFACE_REFRESH_TIME, 1);
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREFS_CHANGED, false);
        editor.apply();
    }

    //    Notifications
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Thermometer background";
            String description = "Notifications for temperature in the furnace";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(BACKGROUND_NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createAlertNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Thermometer alerts";
            String description = "alerts for temperature in the furnace";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(ALERT_NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification(String textTitle, String textContent, int notificationId, String NOTIFICATION_CHANNEL_ID, int priority) {
        if (priority == 2) {
            Uri alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(textTitle)
                    .setContentText(textContent)
                    .setSound(alertSound)
                    .setPriority(priority);
//                .setPriority(NotificationCompat.PRIORITY_LOW);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
// notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(textTitle)
                    .setContentText(textContent)
                    .setPriority(priority);
//                .setPriority(NotificationCompat.PRIORITY_LOW);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
// notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, builder.build());
        }
    }

    public static void staticCreateNotification(String textTitle, String textContent, int notificationId, String NOTIFICATION_CHANNEL_ID, MainActivity activity, int priority) {
        if (priority == 2) {
            Uri alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(textTitle)
                    .setContentText(textContent)
                    .setSound(alertSound)
                    .setPriority(priority);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(textTitle)
                    .setContentText(textContent)
                    .setPriority(priority);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, builder.build());
        }
    }
}