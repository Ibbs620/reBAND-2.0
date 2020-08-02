package com.example.reband;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Exercise extends AppCompatActivity {

    String address;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean isBtConnected = false;
    TextView topText, bottomText, value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        topText = (TextView)findViewById(R.id.top_text);
        bottomText = (TextView)findViewById(R.id.bottom_text);
        value = (TextView)findViewById(R.id.value);

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
        String received = "";
        if(btSocket != null) {
            try {
                byte[] b = new byte[3];
                btSocket.getInputStream().read(b, 0, 1);
                received = new String(b);
                btSocket.getInputStream().read();
                btSocket.getInputStream().read();
                return received;
            } catch(IOException e){
                msg("Error");
            }
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

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
        }
    }
}
