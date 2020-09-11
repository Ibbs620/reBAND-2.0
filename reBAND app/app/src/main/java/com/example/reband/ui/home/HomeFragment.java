package com.example.reband.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.reband.MainActivity;
import com.example.reband.R;

public class HomeFragment extends Fragment {
    boolean connected;
    MainActivity mainActivity;
    Button pair;
    private String text;
    private TextView statusText, addressText;
    Handler checkStatus;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        statusText = root.findViewById(R.id.status_text);
        addressText = root.findViewById(R.id.id_text);
        mainActivity = (MainActivity) getActivity();
        pair = root.findViewById(R.id.pairBtn);
        pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.pairDevice();
            }
        });

        checkStatus = new Handler();
        checkStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                String state = mainActivity.giveStatus();
                statusText.setText(state);
                if(state == "Connected") statusText.setTextColor(Color.GREEN);
                else statusText.setTextColor(Color.RED);
                checkStatus.postDelayed(this, 1000);
                addressText.setText(mainActivity.giveAddress());
            }
        },1000);
        return root;
    }
}
