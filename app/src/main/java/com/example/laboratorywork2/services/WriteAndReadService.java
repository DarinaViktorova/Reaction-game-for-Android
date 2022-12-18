package com.example.laboratorywork2.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.laboratorywork2.BuildConfig;
import com.example.laboratorywork2.observer.Observable;
import com.example.laboratorywork2.observer.Observer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WriteAndReadService extends IntentService implements Observable {
    public static final String TAG = WriteAndReadService.class.getSimpleName();
    public static final String ACTION_WRITE_TO_FILE = BuildConfig.APPLICATION_ID + ".ACTION_WRITE_TO_FILE";
    public static final String ACTION_READ_FILE = BuildConfig.APPLICATION_ID + ".ACTION_READ_FILE";

    public static final String GRIDSIZES = "gridSizes";
    public static final String TIMER = "timer";
    public static final String FRAMERATES = "frameRates";
    public static final String POINTS = "points";

    final String SAVED_TEXT = "saved_text";

    private Observer observer;
    SharedPreferences sPref;

    public WriteAndReadService() {
        super("WriteAndReadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        assert intent != null;
        Serializable observerSerializable = intent.getSerializableExtra("observer");

        registerObserver((Observer) observerSerializable);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent == null) return;
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            case ACTION_WRITE_TO_FILE:
                writeToFile(intent);
                break;
            case ACTION_READ_FILE:
                readFile();
                break;
        }
    }
    protected void writeToFile(Intent intent){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = String.valueOf(dtf.format(now));
        Bundle bundle = intent.getExtras();

        String gridSizes = bundle.getString(GRIDSIZES);
        String timer = bundle.getString(TIMER);
        String frameRates = bundle.getString(FRAMERATES);
        String points = bundle.getString(POINTS);

        String result = date + "\n\nGrid size: " + gridSizes + "\nTimer: " + timer + "\nFrame rate range: " + frameRates + "\nPoints: " + points + "\n\n\n";

        System.out.println(result);
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String savedHistory=sPref.getString(SAVED_TEXT,"");
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, result + savedHistory);
        if(ed.commit()){
            notifyObservers("Write in File");
        }
    }

    void readFile() {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_TEXT, "");
        notifyObservers(savedText);
    }

    @Override
    public void registerObserver(Observer o) {
        observer=o;
    }

    @Override
    public void notifyObservers(String message) {
        if(observer!=null){
            observer.update(message);
        }
    }
}
