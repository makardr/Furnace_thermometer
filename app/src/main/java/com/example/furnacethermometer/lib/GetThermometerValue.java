package com.example.furnacethermometer.lib;

import android.util.Log;

import com.example.furnacethermometer.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.UnknownHostException;
import java.util.Objects;

//This class gets webpage from the thermometer, parses it with the Jsoup and then returns value as string

public class GetThermometerValue {
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
            try {
                Log.i(TAG, "Test Result: " + link.text());
            } catch (Exception e) {
                Log.i(TAG, e.toString());
            }
            return link.text().split(" ")[0];
        } catch (UnknownHostException hostException){
            Log.e(TAG, hostException.toString());
            return "404";
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }
}
