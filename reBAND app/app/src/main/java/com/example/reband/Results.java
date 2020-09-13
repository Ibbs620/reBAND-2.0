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

        TextView displayTime = findViewById(R.id.timeElapsed);
        TextView displayStretches = findViewById(R.id.strechesCompleted);

        Intent results = getIntent();
        int stretchesCompleted = results.getIntExtra("STRETCHES_COMPLETED", 0);
        int timeElapsed = results.getIntExtra("TIME_EXERCISED", 0);
        String time = timeElapsed / 60000 + ":";
        if(timeElapsed % 60000 < 10000)  time += "0";
        time += (timeElapsed % 60000)/1000;

        displayTime.setText(time);
        displayStretches.setText(String.valueOf(stretchesCompleted));
    }
}
