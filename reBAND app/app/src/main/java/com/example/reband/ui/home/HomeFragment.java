package com.example.reband.ui.home;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        statusText = root.findViewById(R.id.status_text);
        addressText = root.findViewById(R.id.id_text);
        mainActivity = (MainActivity) getActivity();
        pair = root.findViewById(R.id.pairBtn);
        new Status().execute();
        pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.pairDevice();
            }
        });
        return root;
    }

    private class Status extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            while (!mainActivity.doneChecking){}
            return mainActivity.giveStatus();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            statusText.setText(result);
            if(result == "Connected") statusText.setTextColor(Color.GREEN);
            else statusText.setTextColor(Color.RED);
            addressText.setText(mainActivity.giveAddress());
        }
    }
}
