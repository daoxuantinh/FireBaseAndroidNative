//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.androidnative.gcm;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import com.androidnative.gms.utils.AnUtility;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.iid.FirebaseInstanceId;
import com.unity3d.player.UnityPlayer;
import java.io.IOException;

public class ANCloudMessageService {
	public static final String SMALL_ICON_NAME = "SMALL_ICON_NAME";
	public static final String LARGE_ICON_NAME = "LARGE_ICON_NAME";
	public static final String SOUND_NAME = "SOUND_NAME";
	public static final String VIBRATION = "VIBRATION";
	public static final String SHOW_WHEN_APP_FOREGROUND = "SHOW_WHEN_APP_FOREGROUND";
	public static final String REPLACE_OLD_WITH_NEW = "REPLACE_OLD_WITH_NEW";
	public static final String NOTIFICATION_COLOR = "NOTIFICATION_COLOR";
	public static final String DATA_KEY = "ANDROID_NATIVE_GCM_DATA";
	public static final String PN_PREFS_KEY = "AN_PushNotificationBundle";
	public static final String PROPERTY_MESSAGE = "property_message";
	public static final String MESSAGE_SERVICE_LISTNER_NAME = "GoogleCloudMessageService";
//	private GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(AnUtility.GetLauncherActivity());
	private String regid;
	private String SENDER_ID = "Your-Sender-ID";
	private static ANCloudMessageService _inctance = null;
	static final String TAG = "AndroidNative";

	public static ANCloudMessageService GetInstance() {
		if (_inctance == null) {
			_inctance = new ANCloudMessageService();
		}

		return _inctance;
	}

	public ANCloudMessageService() {
	}

	public void InitNotificationParams(String smallIcon, String largeIcon, String sound, boolean vibration, boolean showWhenAppForeground, boolean replaceOldWithNew, String color) {
		SharedPreferences prefs = AnUtility.GetLauncherActivity().getSharedPreferences("AN_PushNotificationBundle", 0);
		Editor editor = prefs.edit();
		editor.putString("SMALL_ICON_NAME", smallIcon);
		editor.putString("LARGE_ICON_NAME", largeIcon);
		editor.putString("SOUND_NAME", sound);
		editor.putBoolean("VIBRATION", vibration);
		editor.putBoolean("SHOW_WHEN_APP_FOREGROUND", showWhenAppForeground);
		editor.putBoolean("REPLACE_OLD_WITH_NEW", replaceOldWithNew);
		editor.putString("NOTIFICATION_COLOR", color);
		if (prefs.contains("ANDROID_NATIVE_GCM_DATA")) {
			String json = prefs.getString("ANDROID_NATIVE_GCM_DATA", "");

			try {
				UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "GCMNotificationLaunchedCallback", json);
				Log.d("AndroidNative", "[sendPushCallback] data: " + json);
			} catch (UnsatisfiedLinkError var12) {
				Log.d("AndroidNative", "Trying to call GCM push received callback, but the App closed!");
			}

			editor.remove("ANDROID_NATIVE_GCM_DATA");
		}

		editor.commit();
	}

	@SuppressLint({"NewApi"})
	public void LoadLastMessage() {
		SharedPreferences mSharedPreferences = AnUtility.GetLauncherActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
		String MessgaeJSON = mSharedPreferences.getString("property_message", "");
		UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "OnLastMessageLoaded", MessgaeJSON);
	}

	public void RemoveLastMessageInfo() {
		SharedPreferences mSharedPreferences = AnUtility.GetLauncherActivity().getApplicationContext().getSharedPreferences("MyPref", 0);
		Editor editor = mSharedPreferences.edit();
		editor.remove("property_message");
		editor.commit();
	}

	public void registerDevice(String senderId) {
		if (AnUtility.checkPlayServices()) {
			this.SENDER_ID = senderId;
			this.registerInBackground();
		} else {
			Log.i("AndroidNative", "No valid Google Play Services APK found.");
			UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "OnRegistrationFailed", "");
		}

	}

	private void registerInBackground() {
		(new AsyncTask<Void, Void, String>() {
			protected String doInBackground(Void... params) {
				String msg = "";

				try {
					ANCloudMessageService.this.regid = FirebaseInstanceId.getInstance().getToken();

					msg = "Registration ID = " + ANCloudMessageService.this.regid;
					ANCloudMessageService.this.sendRegistrationIdToBackend();
				} catch (Exception var4) {
					msg = "Error :" + var4.getMessage();
					Log.i("AndroidNative", "registerInBackground error.");
					UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "OnRegistrationFailed", "");
				}

				return msg;
			}

			protected void onPostExecute(String msg) {
				Log.i("AndroidNative", "GCM: SENDER ID: " + ANCloudMessageService.this.SENDER_ID);
				Log.i("AndroidNative", "GCM: " + msg);
			}
		}).execute(new Void[]{null, null, null});
	}

	private void sendRegistrationIdToBackend() {
		UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "OnRegistrationReviced", this.regid);
	}
}
