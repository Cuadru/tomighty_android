package com.cuadru.tomighty.tomightyalarm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Created by juangdelvalle on 8/18/14.
 */
public class BackgroundListenerService extends IntentService {

    public static final String ACTION_LISTEN_BROADCAST = "com.cuadru.tomighty.LISTEN_BROADCAST";
    public static final String PORT = "port";
    public static final String PACKAGE_SIZE = "package_size";

    private static final String LOCK_NAME = BackgroundListenerService.class.getName() + ".Lock";
    private static volatile PowerManager.WakeLock lockStatic = null;

    public BackgroundListenerService() {
        super("BackgroundListenerService");
        setIntentRedelivery(true);
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME);
            lockStatic.setReferenceCounted(true);
        }

        return lockStatic;
    }

    public static void startService(Context context, Intent intent) {
        getLock(context.getApplicationContext()).acquire();
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager.WakeLock lock = getLock(this.getApplicationContext());
        if (!lock.isHeld() || (flags & START_FLAG_REDELIVERY) != 0) {
            lock.acquire();
        }
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    protected void onHandleIntent(Intent intent) {

        try {
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

                    BroadcastReceiver.completeWakefulIntent(intent);

                    BroadcastReceiver.startWakefulService(getApplicationContext(), intent);

//                startService(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Error!",
                            Toast.LENGTH_LONG).show();
                }
            }
        } finally {
            PowerManager.WakeLock lock = getLock(this.getApplicationContext());
            if (lock.isHeld()) lock.release();
        }

    }
}
