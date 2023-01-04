package com.example.furnacethermometer;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

public class RunnableTask implements Runnable {
    private static final String TAG = "RunnableTask";
    Handler runnableHandler = new Handler();
//    https://stackoverflow.com/questions/18856376/android-why-cant-i-create-a-handler-in-new-thread
    TextView tv;
    volatile boolean stopThread;
    int timesRun = 0;

    GetThermometerValue thermometerValue;

    RunnableTask(TextView textView) {
        this.tv = textView;
        this.stopThread = false;

        thermometerValue = new GetThermometerValue("http://192.168.0.120/");
    }

    @Override
    public void run() {
        if (stopThread) {
            Log.d(TAG, "Thread " + Thread.currentThread().getId() + " stopped");
            return;
        }
        backgroundTask();
        runnableHandler.postDelayed(this, 3000); // Repeat every 3 seconds
    }

    public void setStopThread(){
        stopThread=true;
    }

    public void testTask(){
        String msg = "Times handler run: " + timesRun + ". Running in thread " + Thread.currentThread().getId();
        MainActivity.changeTV(tv, msg);
        timesRun += 1;
        Log.d(TAG, msg);
    }


    public void backgroundTask(){
        String response = thermometerValue.readStringHtml();
        Log.i(TAG, "Response thermometer value is " + response);

    }

}
