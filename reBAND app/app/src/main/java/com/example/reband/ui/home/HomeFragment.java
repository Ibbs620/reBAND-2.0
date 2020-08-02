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
    Button btBtn;
    private String text;
    private TextView statusText, addressText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        statusText = root.findViewById(R.id.status_text);
        addressText = root.findViewById(R.id.id_text);
        mainActivity = (MainActivity)getActivity();
        btBtn = root.findViewById(R.id.bt_button);

        statusText.setText(mainActivity.giveStatus());
        new Status().execute();

        btBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(btBtn.getText().toString()){
                    case "Disconnect":
                        mainActivity.disconnect();
                        btBtn.setText("Reconnect");
                        statusText.setTextColor(Color.RED);
                        statusText.setText("Not Connected");
                        break;
                    case "Reconnect":
                        new Status().execute();
                        mainActivity.connect();
                        break;
                    case "Pair Device":
                        mainActivity.connectDevice();;
                        break;
                }
            }
        });
        return root;
    }

    private class Status extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            while(!mainActivity.doneChecking){}
            return mainActivity.giveStatus();
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            statusText.setText(result);
            addressText.setText(mainActivity.giveAddress());
            if(result == "Connected") {
                statusText.setTextColor(Color.GREEN);
                btBtn.setText("Disconnect");
            }
            else {
                statusText.setTextColor(Color.RED);
                btBtn.setText("Reconnect");
            }
            mainActivity.msg(result);
        }
    }
}
