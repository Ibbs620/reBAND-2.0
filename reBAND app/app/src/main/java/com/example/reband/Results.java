package com.example.reband;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Results extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent results = getIntent();
        int stretchesCompleted = results.getIntExtra("STRETCHES_COMPLETED", 0);
        long timeElapsed = results.getIntExtra("TIME_ELAPSED", 0);
        TextView displayTime = findViewById(R.id.timeElapsed);
        TextView displayStretches = findViewById(R.id.strechesCompleted);
        displayTime.setText(timeElapsed + "");
        displayStretches.setText(String.valueOf(stretchesCompleted));
    }
}
