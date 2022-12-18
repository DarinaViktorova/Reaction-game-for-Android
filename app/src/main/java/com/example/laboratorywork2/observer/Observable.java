package com.example.laboratorywork2.observer;

public interface Observable {
    void registerObserver(Observer o);
    void notifyObservers(String message);
}
