package com.example.reband;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.StringPrepParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Exercise extends AppCompatActivity {

    BluetoothSocket btSocket = Headset.getInstance().getCurrentBluetoothConnection();
    TextView topText, bottomText, value;
    int X, Y;
    Handler handler, checkValues;
    Runnable runnable, checkValuesRunnable;
    private boolean exerciseComplete = false;
    int stretchesDone = 0;
    int stretchesRequired = 3;
    int ms = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        topText = findViewById(R.id.top_text);
        bottomText = findViewById(R.id.bottom_text);
        value = findViewById(R.id.value);
        if(btSocket == null) msg("no");
        else msg("yes");
        new CountDownTimer(5000, 1000){
            public void onTick(long millisUntilFinished){
                value.setText(Double.toString(Math.ceil(millisUntilFinished/1000)).substring(0,1));
            }
            public void onFinish() {
                Intent i = getIntent();
                final String exerciseID = i.getStringExtra("TYPE");
                switch(exerciseID){
                    case "A":
                        write("A");
                        headExtension();
                        break;
                    case "B":
                        headFlexion();
                        break;
                    case "C":
                        lateralFlexion();
                        break;
                    case "D":
                        random();
                        break;
                }
                HandlerThread ht = new HandlerThread("nonUIThread");
                ht.start();
                checkValues = new Handler(ht.getLooper());
                checkValues.postDelayed(checkValuesRunnable = new Runnable() {
                    @Override
                    public void run() {
                        readX();
                        ms += 100;
                        checkValues.postDelayed(this, 100);

                    }
                },100);
            }
        }.start();
    }

    private void random() {
    }

    private void headExtension() {
        topText.setTextSize(24);
        handler = new Handler();

        handler.postDelayed(runnable = new Runnable(){
            int time = 0;
            boolean finished = false;
            public void run(){
                bottomText.setText(stretchesRequired - stretchesDone + " stretches left");
                if(stretchesDone == stretchesRequired){
                    finish();
                    handler.removeCallbacks(this);
                } else if(Math.abs(X) < 20000){
                    if(finished) {
                        if(time >= 3000){
                            finished = false;
                            time = 0;
                            stretchesDone++;
                        } else if(Math.abs(X) < 4000){
                            time += 100;
                            String text = "Keep resting for " + Math.ceil((3000 - time) / 1000 + 1);
                            text = text.substring(0, text.length() - 2);
                            topText.setText(text);
                        } else {
                            if(time > 0) msg("Please rest your head before continuing");
                            time = 0;
                        }
                    } else if(X > 14000 || (time % 900 != 0 && time != 0)){
                        if(time >= 5000){
                            topText.setText("Now rest your head");
                            finished = true;
                            time = 0;
                        } else {
                            value.setText("0");
                            time += 100;
                            String text = "Now hold your position for " + Math.ceil((5000 - time) / 1000 + 1);
                            text = text.substring(0, text.length() - 2);
                            topText.setText(text);
                        }
                    } else {
                        topText.setText("Lift your head until the number hits 0");
                        if(time > 200) msg("Please try to hold the position");
                        X = Math.abs(X - 14000) / 140;
                        value.setText(String.valueOf(X));
                        time = 0;
                    }
                }
                handler.postDelayed(this, 100);
            }
        }, 100);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent displayResults = new Intent(Exercise.this, Results.class);
        displayResults.putExtra("TIME_EXERCISED", ms);
        displayResults.putExtra("STRETCHES_COMPLETED", stretchesDone);
        startActivity(displayResults);
    }

    private void headFlexion(){

    }

    private void lateralFlexion(){

    }

    private int readX(){
        write("A");
        try {
            X = Integer.parseInt(read());
        } catch (NumberFormatException|StringIndexOutOfBoundsException ex){
            X = 0;
        }
        return X;
    }

    public void msg(String msg){
        Toast.makeText(Exercise.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void sleep(int time){
        try
        {
            Thread.sleep(time);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    public String read(){
        String received = "Error";
        byte[] b = new byte[256];
        try {
            int l = btSocket.getInputStream().read(b);
            received = new String(b, 0, l);
        } catch(IOException e){
            msg("error");
        }
        return received;
    }

    public void write(String msg){
        try {
            btSocket.getOutputStream().write(msg.getBytes());
        } catch (IOException e) {
            msg("Error");
        }
    }
    @Override
    public void onBackPressed()
    {
        finish();
        handler.removeCallbacks(runnable);
        checkValues.removeCallbacks(checkValuesRunnable);
        return;
    }

}