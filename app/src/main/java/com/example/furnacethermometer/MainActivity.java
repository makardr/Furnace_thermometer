package com.example.furnacethermometer;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ThermometerMainActivity";

    //    Flags
    private Boolean taskStartedFlag = false;

    //    Interface elements
    private TextView textViewDisplay;
    private Button startButton;


    //    Background tasks
    private RefreshTemperatureRunnableTask refreshTemperatureRunnableTask;
    final Handler mainHandler = new Handler();
    final HandlerThread backgroundHandlerThread = new HandlerThread("HandlerThreadName");
    private Handler backgroundHandler;


    //    Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Application created");


//        Initialize background handler
        backgroundHandler = createBackgroundHandler();

//        Initialize runnable task
        this.refreshTemperatureRunnableTask = new RefreshTemperatureRunnableTask(backgroundHandler);


//        Initialize interface components in code
        this.textViewDisplay = (TextView) findViewById(R.id.textViewDisplay);
        this.startButton = (Button) findViewById(R.id.startBtn);

//        Example how to bind onClick from the code
//        Button startHandlerTaskButton = (Button) findViewById(R.id.startBtn);
//        startHandlerTaskButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                changeTV(textViewDisplay,"test");
//                try {
//                    startHandlerTask();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

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
                startHandlerTask();
                taskStartedFlag = true;
                startButton.setText("Stop");
                Log.i(TAG, "startBtnAction: Background task started");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            taskStartedFlag = false;
            startButton.setText("Start");
            refreshTemperatureRunnableTask.setStopThread();
            Log.i(TAG, "startBtnAction: Background task stopped");
        }

    }




//    Handlers, background, technical stuff

    public void startHandlerTask() throws InterruptedException {
//        Start updating temperature value
        backgroundHandler.post(refreshTemperatureRunnableTask);
//        Start updating interface
        mainHandler.post(interfaceUpdateTask());
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


    public Runnable interfaceUpdateTask() {
        Runnable task = new Runnable() {
            public void run() {
                changeTV(textViewDisplay, refreshTemperatureRunnableTask.getCurrentTemperature());
                mainHandler.postDelayed(this, 1000); // Repeat every 1 second
            }
        };
        return task;
    }
}