package com.cuadru.tomighty.tomightyalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by juangdelvalle on 8/18/14.
 */
public class BroadcastReceiver extends WakefulBroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, BackgroundListenerService.class);
        startWakefulService(context, intent);
    }

}
