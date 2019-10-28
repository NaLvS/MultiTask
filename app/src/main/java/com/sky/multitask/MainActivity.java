package com.sky.multitask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sky.multitask.bean.Task;

import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPriority;
    private EditText etName;
    private TextView tvContent;
    private Task recordingTask;

    private Stack<Task> tasks = new Stack<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        etPriority = findViewById(R.id.et_priority);
        etName = findViewById(R.id.et_name);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_stop_all).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        tvContent = findViewById(R.id.tv_content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                String name = etName.getText().toString().trim();
                String priority = etPriority.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(priority)) {
                    try {
                        int pr = Integer.parseInt(priority);
                        if (recordingTask == null) {
                            startNewTask(name, pr);
                        } else {
                            if (pr >= recordingTask.priority) {
                                recordingTask.paused = true;
                                tasks.push(recordingTask);
                                startNewTask(name, pr);
                            }
                        }
                    } catch (Exception e) {
                        //
                    }
                }
                break;
            case R.id.btn_stop_all:
                stopAll();
                break;
            case R.id.btn_clear:
                tvContent.setText("");
                break;
        }
    }

    private void stopAll() {
        if (recordingTask != null) {
            recordingTask.started = false;
        }
        for (Task task : tasks) {
            task.started = false;
        }
    }

    private void startNewTask(String name, int priority) {
        Task task = new Task(name, priority, new Task.OnTaskGoingOn() {
            @Override
            public void onTaskFinish(final Task task) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvContent.append(task.name + "finished!");
                        tvContent.append("\r\n");
                    }
                });
                if (tasks.isEmpty()) {
                    recordingTask = null;
                    return;
                }
                Task pop = tasks.pop();
                recordingTask = pop;
                pop.paused = false;
            }

            @Override
            public void onTaskDoing(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvContent.append(message);
                        tvContent.append("\r\n");
                    }
                });
            }
        });
        task.started = true;
        Thread thread = new Thread(task);
        thread.start();
        recordingTask = task;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAll();
    }
}
