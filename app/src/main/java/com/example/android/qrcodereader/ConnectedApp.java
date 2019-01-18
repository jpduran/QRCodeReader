package com.example.android.qrcodereader;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class ConnectedApp extends AppCompatActivity {

    private Socket mSocket;
    private float batteryPct;

    {
        try {
            mSocket = IO.socket("http://jpduran-web-socket.herokuapp.com");
        } catch (URISyntaxException e){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connected_app);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        String roomId= getIntent().getStringExtra("EXTRA_ROOM_ID");

        TextView textView = findViewById(R.id.text_view);
        textView.setText(roomId);

        mSocket.connect();
        mSocket.emit("join", roomId);
        mSocket.emit("joined", "THE APP CONNECTED");
        mSocket.on("locationAsked", sendLocation);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        batteryPct = level / (float)scale;
    }

    private Emitter.Listener sendLocation = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("location", batteryPct*100 + "%");
        }
    };


}
