package com.androidnative.gcm;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.unity3d.player.UnityPlayer;

public class MyFireBaseIdService extends FirebaseInstanceIdService {

	private static final String TAG = MyFireBaseIdService.class.getSimpleName();
	private static String FIREBASE_TOKEN = "FireBaseTokenId";

	@Override
	public void onTokenRefresh() {
		// Get updated InstanceID token.
		FIREBASE_TOKEN = FirebaseInstanceId.getInstance().getToken();
		Log.d(TAG, "tinh :: MyFireBaseIdService.Refreshed RegistrationId: " + FIREBASE_TOKEN);
		this.registerInBackground();
	}


	private void registerInBackground() {
		(new AsyncTask<Void, Void, String>() {
			protected String doInBackground(Void... params) {
				String msg = "";

				try {
					msg = "Registration ID = " + FIREBASE_TOKEN;
					Log.d("FireBase :: ", msg);
					MyFireBaseIdService.this.sendRegistrationIdToBackend();
				} catch (Exception var4) {
					msg = "Error :" + var4.getMessage();
					Log.i("FireBase :: ", "registerInBackground error.");
					UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "OnRegistrationFailed", "");
				}

				return msg;
			}

			protected void onPostExecute(String msg) {
				Log.i("FireBase :: ", "FCM: " + msg);
			}
		}).execute(new Void[]{null, null, null});
	}

	private void sendRegistrationIdToBackend() {
		UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "OnRegistrationReviced", "firebase");
	}

	public String getToken(){
		return FirebaseInstanceId.getInstance().getToken();
	}
}

