package com.example.reband;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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

    String address;
    BluetoothSocket btSocket = Headset.getInstance().getCurrentBluetoothConnection();
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean isBtConnected = false;
    TextView topText, bottomText, value;
    int X, Y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        topText = (TextView)findViewById(R.id.top_text);
        bottomText = (TextView)findViewById(R.id.bottom_text);
        value = (TextView)findViewById(R.id.value);
        final Intent i = getIntent();
        address = i.getStringExtra("ADDRESS");
        if(btSocket == null) msg("no");
        else msg("yes");

        new CountDownTimer(6000, 1000){
            public void onTick(long millisUntilFinished){
                value.setText(Long.toString(millisUntilFinished/1000));
            }
            public void onFinish() {
                switch(i.getStringExtra("TYPE")){
                    case "A":
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
                readX();
            }
        });
    }

    private void random() {
    }

    private void headExtension() {
        topText.setText("Lift your head until the number hits 0");
        topText.setTextSize(24);
        final Handler h = new Handler();
        final int delay = 10;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                value.setText(String.valueOf(readX()));
                h.postDelayed(this, delay);
            }
        }, delay);
    }

    private void headFlexion(){
    }

    private void lateralFlexion(){
    }

    private int readX(){
        write("A");
        String sent = read();
        if(sent != "") X = Integer.parseInt(sent);
        else X = 100;
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
        String recieved = "";
        try {
            if(btSocket != null && btSocket.getInputStream().available() > 0) {
                try {
                    byte[] b = new byte[1000];
                    btSocket.getInputStream().read(b);
                    recieved = new String(b);
                } catch(IOException e){
                    msg("Error");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recieved;
    }

    public void write(String msg){
        try {
            btSocket.getOutputStream().write(msg.getBytes());
        } catch (IOException e) {
            msg("Error");
        }
    }
}
