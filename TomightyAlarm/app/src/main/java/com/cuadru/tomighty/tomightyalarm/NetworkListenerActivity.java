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

//        BroadcastReceiver receiver = new BroadcastReceiver();
//        receiver.onReceive(NetworkListenerActivity.this, mServiceIntent);

        BackgroundListenerService.startService(getApplicationContext(), mServiceIntent);

//        startService(mServiceIntent);

//        new ListenNetworkBroadcastTask().execute();
    }

//    class ListenNetworkBroadcastTask extends AsyncTask<Void, Void, Boolean> {
//
//        private MediaPlayer mediaPlayer;
//
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            mediaPlayer = MediaPlayer.create(NetworkListenerActivity.this, R.raw.alarm);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        }
//
//        protected Boolean doInBackground(Void... voids) {
//            boolean success = false;
//
//            byte[] packageData = new byte[PACKAGE_SIZE];
//
//            try {
//                DatagramPacket p = new DatagramPacket(packageData, packageData.length);
//                DatagramSocket socket = new DatagramSocket(null);
//                socket.setReuseAddress(true);
//                socket.setBroadcast(true);
//                socket.bind(new InetSocketAddress(PORT));
//                socket.receive(p);
//
//                byte[] data = p.getData();
//                if (p != null && data != null) {
//                    String message = new String(data).trim();
//
//                    if (message.equals("TIMER_STOPPED")) {
//                        mediaPlayer.start();
//                        Vibrator v = (Vibrator) NetworkListenerActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
//                        v.vibrate(1000);
//                        success = true;
//                    }
//                }
//
//                socket.close();
//            } catch (Exception e) {
//                Log.e("UdpTest", "BroadcastException", e);
//            }
//
//            return success;
//        }
//
//        protected void onPostExecute(Boolean success) {
//            if (success) {
//                Toast.makeText(getApplicationContext(), "Broadcast received!",
//                        Toast.LENGTH_LONG).show();
//
//                new ListenNetworkBroadcastTask().execute();
//            } else {
//                Toast.makeText(getApplicationContext(), "Error!",
//                        Toast.LENGTH_LONG).show();
//            }
//        }
//    }

}
