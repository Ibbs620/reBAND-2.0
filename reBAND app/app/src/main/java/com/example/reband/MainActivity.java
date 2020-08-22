package com.example.reband;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

    String address;
    Headset headset;
    public BluetoothAdapter myBluetooth = null;
    public BluetoothSocket btSocket = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public boolean isBtConnected = false;
    public boolean doneChecking = false;
    public SharedPreferences sp;
    private String prefFile = "com.example.android.sp";
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        sp = getSharedPreferences(prefFile, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sp.edit();
        Intent devicelist = getIntent();
        address = sp.getString("ADDRESS", "");

        if (devicelist.hasExtra(DeviceList.EXTRA_ADDRESS)) {
            address = devicelist.getStringExtra(DeviceList.EXTRA_ADDRESS);
            prefEditor.putString("ADDRESS", address);
            prefEditor.apply();
            new ConnectBT().execute();
        } else if (address == "") {
            Intent i = new Intent(MainActivity.this, DeviceList.class);
            startActivity(i);
        } else {
            new ConnectBT().execute();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences.Editor prefEditor = sp.edit();
        prefEditor.putString("ADDRESS", address);
        prefEditor.apply();
    }

    public void headExtension(View view) {
        Intent i = new Intent(MainActivity.this, Exercise.class);
        i.putExtra("TYPE", "A");
        i.putExtra("ADDRESS", address);
        startActivity(i);
    }

    public void headFlexion(View view) {
        if (btSocket!=null)
        {
            write("A");
            String message = read();
            msg(message);
            sleep(100);
        }
    }

    public void lateralFlexion(View view) {

        if (btSocket!=null)
        {
            write("TF");
        }
    }

    public void randomExercise(View view) {
        if (btSocket!=null)
        {
            try
            {
                int yes = btSocket.getInputStream().read();
                msg(Integer.toString(yes));
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    public void msg(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    public String giveStatus(){
        if (address == "") return "NO DEVICE PAIRED";
        if(btSocket != null) {
            if(btSocket.isConnected()) return "Connected";
            return "Not Connected";
        }
        return "Not Connected";
    }

    public String giveAddress(){
        if (address == "") return "NO DEVICE PAIRED";
        else return address;
    }

    public String read(){
        String received = "";
        if(btSocket != null) {
            try {
                byte[] b = new byte[6];
                btSocket.getInputStream().read(b);
                received = new String(b);
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

    public void pairDevice() {
        Intent i = new Intent(MainActivity.this, DeviceList.class);
        startActivity(i);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            doneChecking = false;
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !btSocket.isConnected())
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice device = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e) {
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
                msg("Connection Failed, please try again");
                isBtConnected = false;
            }
            else
            {
                msg("Connected");
                isBtConnected = true;
                Headset.getInstance().setupBluetoothConnection(btSocket);
            }
            progress.dismiss();
            doneChecking = true;
        }
    }
}