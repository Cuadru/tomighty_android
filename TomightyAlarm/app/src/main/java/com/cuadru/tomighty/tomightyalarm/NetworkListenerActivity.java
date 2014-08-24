package com.cuadru.tomighty.tomightyalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;


public class NetworkListenerActivity extends Activity {

    private static final int PORT = 8080;
    private static final int PACKAGE_SIZE = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_listener);

        Intent mServiceIntent = new Intent(this, BackgroundListenerService.class);
        mServiceIntent.setAction(BackgroundListenerService.ACTION_LISTEN_BROADCAST);
        mServiceIntent.putExtra(BackgroundListenerService.PACKAGE_SIZE, PACKAGE_SIZE);
        mServiceIntent.putExtra(BackgroundListenerService.PORT, PORT);
        mServiceIntent.setFlags(mServiceIntent.FLAG_ACTIVITY_NEW_TASK);
        mServiceIntent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON +
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        BroadcastReceiver receiver = new BroadcastReceiver();
        receiver.onReceive(NetworkListenerActivity.this, mServiceIntent);

//        BackgroundListenerService.startService(getApplicationContext(), mServiceIntent);

//        startService(mServiceIntent);

//        new ListenNetworkBroadcastTask().execute();
    }

}
