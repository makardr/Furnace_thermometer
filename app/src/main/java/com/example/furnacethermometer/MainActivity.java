package com.example.furnacethermometer;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ThermometerMainActivity";
    private TextView textViewDisplay;
    private Handler mainHandler = new Handler();
    private RunnableTask runnableTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Application created");
        this.textViewDisplay = (TextView) findViewById(R.id.textViewDisplay);


//        Initialize runnable task
        this.runnableTask = new RunnableTask(textViewDisplay);

//        Example how to bind onClick from the code
        Button startHandlerTaskButton = (Button) findViewById(R.id.testBtn);
        startHandlerTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                changeTV(textViewDisplay,"test");
                try {
                    startHandlerTask();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

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


    public void startHandlerTask() throws InterruptedException {

//        Runnable task = new Runnable() {
//            int timesRun = 0;
//            public void run() {
//                String msg = "Times handler run: "+timesRun+". Running in thread "+ Thread.currentThread().getId();
//                changeTV(textViewDisplay,msg);
//                timesRun+=1;
//                Log.d(TAG, msg);
//                mainHandler.postDelayed(this, 3000); // Repeat every 3 seconds
//            }
//        };
        Log.e(TAG, "Current thread is: "+Thread.currentThread().getId());
        mainHandler.post(runnableTask);
    }


    public void startThread(View view) {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void stopThread(View view) {
//        TextView textViewDisplay=(TextView)findViewById(R.id.textViewDisplay);
//        textViewDisplay.setText("test");
        runnableTask.setStopThread();
    }

    public static void changeTV(TextView tv, String text) {
        tv.setText(text);
    }
}