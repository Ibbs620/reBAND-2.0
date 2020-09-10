package com.example.reband;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.icu.text.StringPrepParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Exercise extends AppCompatActivity {

    BluetoothSocket btSocket = Headset.getInstance().getCurrentBluetoothConnection();
    TextView topText, bottomText, value;
    int X, Y;
    Handler handler;
    private boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        topText = findViewById(R.id.top_text);
        bottomText = findViewById(R.id.bottom_text);
        value = findViewById(R.id.value);
        if(btSocket == null) msg("no");
        else msg("yes");
        new CountDownTimer(6000, 1000){
            public void onTick(long millisUntilFinished){
                value.setText(Long.toString(millisUntilFinished/1000));
            }
            public void onFinish() {
                Intent i = getIntent();
                switch(i.getStringExtra("TYPE")){
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
            }
        }.start();
        value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value.setText(String.valueOf(readX()));
            }
        });
    }

    private void random() {
    }

    private void headExtension() {
        topText.setText("Lift your head until the number hits 0");
        topText.setTextSize(24);
        handler = new Handler();
        running = true;
        handler.postDelayed(new Runnable(){
            public void run(){
                readX();
                if(X < 20000 && X  > -20000){
                    if(X > 14000){
                        X = 0;
                    } else {
                        X = Math.abs(X - 14000) / 140;
                    }
                    value.setText(String.valueOf(X));
                }
                handler.postDelayed(this, 100);
            }
        }, 100);
    }
    protected void onStop() {
        super.onStop();
        running = false;
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


}
