package com.example.laboratorywork2.threads;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;

import com.example.laboratorywork2.observer.Observable;
import com.example.laboratorywork2.observer.Observer;

import java.util.Objects;

public class WriteAndReadWithThreads extends Thread implements Observable {
    final String SAVED_TEXT = "saved_text";
    private Observer observer;
    private Runnable runnable=null;
    SharedPreferences sPref;

    @Override
    public void run() {
        if(runnable!=null) runnable.run();
    }


    public void writeToFile(String record){
        runnable = () -> {
            sPref = Objects.requireNonNull((Activity)observer).getSharedPreferences("MyPref", MODE_PRIVATE);
            String savedHistory=sPref.getString(SAVED_TEXT,"");
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(SAVED_TEXT, record + savedHistory);
            if(ed.commit()){
                notifyObservers("Write in File");
            }
        };
    }

    public void readFile() {
        runnable = () -> {
            sPref = Objects.requireNonNull((Activity)observer).getSharedPreferences("MyPref", MODE_PRIVATE);
            String savedText = sPref.getString(SAVED_TEXT, "");
            notifyObservers(savedText);
        };
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
