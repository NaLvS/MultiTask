package com.sky.multitask.bean;

import android.util.Log;

public class Task implements Runnable {
    private static final String TAG = "Task";
    private static final int DURATION = 30;
    public int priority;
    public boolean started;
    public boolean paused;
    public String name;
    private OnTaskGoingOn onTaskGoingOn;

    public Task(String name, int priority, OnTaskGoingOn finished) {
        this.priority = priority;
        this.onTaskGoingOn = finished;
        this.name = name;
    }

    @Override
    public void run() {
        int duration = 0;
        p:
        while (started && duration < DURATION) {
            while (paused) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue p;
            }
            String message = "Task " + name + " is Running! " + duration + "/" + DURATION;
            Log.d(TAG, message);
            onTaskGoingOn.onTaskDoing(message);
            duration += 1;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        started = false;
        onTaskGoingOn.onTaskFinish(this);
    }

    public interface OnTaskGoingOn {
        void onTaskFinish(Task task);

        void onTaskDoing(String message);
    }
}
