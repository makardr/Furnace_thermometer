package com.example.furnacethermometer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
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


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ThermometerMainActivity";
    private static final String NOTIFICATION_CHANNEL_ID = "temperature_notification";
    private String ipAddress = "http://192.168.0.120/";

    //    Flags
    private boolean taskStartedFlag = false;
    private boolean messageSend = false;

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


    //    Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Application created");

//        Create channel for non-alert notifications
        createNotificationChannel();
//        Create channel for alert notifications?


//        Initialize background handler
        backgroundHandler = createBackgroundHandler();

//        Initialize runnable task
        this.refreshTemperatureRunnableTask = new RefreshTemperatureRunnableTask(backgroundHandler, ipAddress);
//        Log.i(TAG, "onCreate: current temperature is " + refreshTemperatureRunnableTask.getCurrentTemperature());

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
//    and this problem of zombie thread was resolved by simply eliminating thread in onDestroy, but saving current instance is pointless
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
                startButton.setText("Stop");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            taskStartedFlag = false;
            startButton.setText("Start");
            refreshTemperatureRunnableTask.setStopThread();
            stopHandlerTasks();
        }

    }

    public void settingsBtnAction(View view) {

    }


//    Handlers, background, technical stuff

    public void startHandlerTasks() throws InterruptedException {
//        Start updating temperature value
        backgroundHandler.post(refreshTemperatureRunnableTask);
//        Start updating interface
        this.interfaceUpdateTask = new UpdateInterfaceRunnable(textViewDisplay, mainHandler, refreshTemperatureRunnableTask);
        mainHandler.post(interfaceUpdateTask);

//        Start sending notifications
//        this.notificationUpdateTask = notificationUpdateTaskBuilder();
        this.notificationUpdateTask = new UpdateNotificationRunnable(mainHandler, refreshTemperatureRunnableTask, this);
        mainHandler.post(notificationUpdateTask);


    }

    public void stopHandlerTasks() {
        notificationUpdateTask.setStopThread();
        interfaceUpdateTask.setStopThread();
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


    //    Notifications
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Thermometer alerts";
            String description = "Notifications for temperature in the furnace";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification(String textTitle, String textContent, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

    public static void staticCreateNotification(String textTitle, String textContent, int notificationId, String NOTIFICATION_CHANNEL_ID, MainActivity activity) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);
// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

    public void checkTemperatureForNotification() {
        if (Integer.parseInt(refreshTemperatureRunnableTask.getCurrentTemperature()) >= 50 && !messageSend) {
            messageSend = true;
            Log.d(TAG, "Notification alert for temperature over 50 degrees should be sent once");
        }
        if (Integer.parseInt(refreshTemperatureRunnableTask.getCurrentTemperature()) < 50 && messageSend) {
            messageSend = false;
            Log.d(TAG, "Flag is false, notification should not be sent");
        }
    }
}