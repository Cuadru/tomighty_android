package com.cuadru.tomighty.tomightyalarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by juangdelvalle on 8/18/14.
 */
public class BackgroundListenerService extends IntentService {

    public static final String ACTION_LISTEN_BROADCAST = "com.cuadru.tomighty.LISTEN_BROADCAST";
    public static final String PORT = "port";
    public static final String PACKAGE_SIZE = "package_size";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    public BackgroundListenerService() {
        super("BackgroundListenerService");
    }

    private void sendNotification(String msg) {
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, NetworkListenerActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.service_notification))
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    protected void onHandleIntent(Intent intent) {

        if (intent.getAction() == ACTION_LISTEN_BROADCAST) {
            Bundle details = intent.getExtras();

            int packageSize = details.getInt(PACKAGE_SIZE);
            int port = details.getInt(PORT);

            MediaPlayer mediaPlayer;

            mediaPlayer = MediaPlayer.create(BackgroundListenerService.this, R.raw.alarm);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            boolean success = false;

            byte[] packageData = new byte[packageSize];

            try {
                DatagramPacket p = new DatagramPacket(packageData, packageData.length);
                DatagramSocket socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.setBroadcast(true);
                socket.bind(new InetSocketAddress(port));
                socket.receive(p);

                byte[] data = p.getData();
                if (p != null && data != null) {
                    String message = new String(data).trim();

                    if (message.equals("TIMER_STOPPED")) {
                        mediaPlayer.start();
                        Vibrator v = (Vibrator) BackgroundListenerService.this.getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(1000);
                        success = true;
                    }
                }

                socket.close();
            } catch (Exception e) {
                Log.e("UdpTest", "BroadcastException", e);
            }

            if (success) {
                Toast.makeText(getApplicationContext(), "Broadcast received!",
                        Toast.LENGTH_LONG).show();

                sendNotification(getString(R.string.service_notification_success));

                BroadcastReceiver.completeWakefulIntent(intent);

                BroadcastReceiver.startWakefulService(getApplicationContext(), intent);

//                startService(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Error!",
                        Toast.LENGTH_LONG).show();

                sendNotification(getString(R.string.service_notification_failed));
            }
        }

    }
}
