package com.example.furnacethermometer;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

//This class gets webpage from the thermometer, parses it with the Jsoup and then returns value as string

public class GetThermometerValue{
    private static final String TAG = "GetThermometerValue";
    String ipAddress;
    GetThermometerValue(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String readStringHtml() {
        String html;
        try {
            html = Jsoup.connect(this.ipAddress).get().html();
            Document document = Jsoup.parse(html);
            Element link = document.select("p").first();
            String strNumber = link.text().split(" ")[0];
            Log.i(TAG, "Current thread is" + Thread.currentThread());
            Log.i(TAG, "Result is " + Integer.parseInt(strNumber));
            return strNumber;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Log.e(TAG, "Current thread is: "+Thread.currentThread().getId());
            return null;
        }

    }


}
