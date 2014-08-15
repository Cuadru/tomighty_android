package com.cuadru.tomighty.android;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class NetworkListenerActivity extends Activity {
	private static final int PORT = 8080;
	private static final int PACKAGE_SIZE = 1500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new ListenNetworkBroadcastTask().execute();
	}

	class ListenNetworkBroadcastTask extends AsyncTask<Void, Void, Boolean> {

		private MediaPlayer mediaPlayer;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mediaPlayer = MediaPlayer.create(NetworkListenerActivity.this,
					R.raw.alarm);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		}

		@Override
		protected Boolean doInBackground(Void... voids) {
			boolean success = false;

			byte[] packageData = new byte[PACKAGE_SIZE];

			try {

				DatagramPacket p = new DatagramPacket(packageData,
						packageData.length);

				DatagramSocket socket = new DatagramSocket(null);
				socket.setReuseAddress(true);
				socket.setBroadcast(true);
				socket.bind(new InetSocketAddress(PORT));
				socket.receive(p);

				byte[] data = p.getData();
				if (p != null && data != null) {

					String message = new String(data).trim();

					if (message.equals("TIMER_STOPED")) {

						mediaPlayer.start();

						Vibrator v = (Vibrator) NetworkListenerActivity.this
								.getSystemService(Context.VIBRATOR_SERVICE);
						v.vibrate(1000);

						success = true;
					}
				}

				socket.close();

			} catch (Exception e) {
				Log.e("UdpTest", "BroadcastException", e);
			}

			return success;
		}

		@Override
		protected void onPostExecute(Boolean success) {

			if (success) {
				Toast.makeText(getApplicationContext(), "Broadcast received!",
						Toast.LENGTH_LONG).show();

				new ListenNetworkBroadcastTask().execute();

			} else {
				Toast.makeText(getApplicationContext(), "Error!",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
