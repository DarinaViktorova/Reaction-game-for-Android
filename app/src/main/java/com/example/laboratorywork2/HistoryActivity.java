package com.example.laboratorywork2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.laboratorywork2.observer.Observer;
import com.example.laboratorywork2.services.WriteAndReadService;

import java.io.Serializable;

public class HistoryActivity extends AppCompatActivity implements Observer, Serializable {

    private static transient TextView textRecords;
    private String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        textRecords = findViewById(R.id.records);
        launchService(WriteAndReadService.ACTION_READ_FILE);
        findViewById(R.id.go_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void update(String message) {
        runOnUiThread(() -> textRecords.setText(message));
    }

    private void launchService(String action) {
        Intent intent = new Intent(this, WriteAndReadService.class);
        intent.setAction(action);
        intent.putExtra("observer", this);
        startService(intent);
    }
}