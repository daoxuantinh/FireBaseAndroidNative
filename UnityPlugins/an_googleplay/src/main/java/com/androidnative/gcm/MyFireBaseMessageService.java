package com.androidnative.gcm;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.androidnative.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class MyFireBaseMessageService extends FirebaseMessagingService {
	private static String TAG = "FireBase :: ";
	private static String ADMIN_CHANNEL_ID = "channel1";
	public static final int NOTIFICATION_ID = 1;
	public static String adsURL;
	private NotificationManager mNotificationManager;
	private BroadcastReceiver receiver;


	public static final String channel_id = "firebase_channel";
	public static final String ACTION_OPEN = "com.androidnative.push.intent.OPEN";
	public static final String ACTION_RECEIVE = "com.androidnative.push.intent.RECEIVE";

	String title;
	String body;
	String image;
	String bigpic;
	Bitmap bigimage = null;

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		Log.d(TAG, "tinh :: MyFireBaseMessageService.onMessageReceived: ");
		this.sendNotification(remoteMessage);
		IntentFilter intentFilter = new IntentFilter(GcmBroadcastReceiver.ACTION_OPEN);
		intentFilter.addDataScheme("package");
		receiver = new GcmBroadcastReceiver();
		registerReceiver(receiver, intentFilter);
//		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	@SuppressLint({"NewApi"})
	public void SaveMessgaBundle(RemoteMessage extras) {
		SharedPreferences mSharedPreferences = this.getApplicationContext().getSharedPreferences("MyPref", 0);
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putString("property_message", extras.toString());
		e.commit();
		Log.i("AndroidNative", "Push Notification Saved");
	}

	@SuppressLint({"NewApi", "WrongConstant"})
	private void sendNotification(RemoteMessage extras) {
		Log.i("AndroidNative", "Push Notification Sended: ");

		try {
			this.SaveMessgaBundle(extras);
			SharedPreferences prefs = this.getApplicationContext().getSharedPreferences("AN_PushNotificationBundle", 0);
			String smallIconName = prefs.getString("SMALL_ICON_NAME", "");
			String largeIconName = prefs.getString("LARGE_ICON_NAME", "");
			String soundName = prefs.getString("SOUND_NAME", "");
			boolean vibro = prefs.getBoolean("VIBRATION", false);
			boolean showIfAppForeground = prefs.getBoolean("SHOW_WHEN_APP_FOREGROUND", true);
			boolean replaceOldWithNew = prefs.getBoolean("REPLACE_OLD_WITH_NEW", false);
			String color = prefs.getString("NOTIFICATION_COLOR", "255|255|255|255");
			Log.d("AndroidNative", "Push Notification Show when App Is Foreground " + showIfAppForeground);
			boolean willBeShown = true;
			if (!showIfAppForeground && isForegroundRunning(this.getApplicationContext())) {
				Log.d("AndroidNative", "App is Foreground, so don't show received push notification");
				willBeShown = false;
			}

			String title = extras.getData().get("title");
			String message = extras.getData().get("body");
			String bigPicture = extras.getData().get("bigpic");
			String browserUrl = extras.getData().get("browser_url");
//			String data = extras.getData().get("json");
//			Log.d("AndroidNative", "GCM Push Notification data received " + data);
//			String bigPicture = "";
			String notificationAlert = null;
//			String browserUrl = "";

//			try {
//				JSONObject json = new JSONObject(data);
//				bigPicture = json.optString("big_picture_url");
//				browserUrl = json.optString("browser_url");
//				notificationAlert = json.optString("notification_alert");
//				Log.d("AndroidNative", "Big Picture Push Notification style detedted. Big Piucture URL: " + bigPicture);
//			} catch (JSONException var32) {
//				Log.d("AndroidNative", "Push Notification JSON parse error " + var32.getMessage());
//			}

			this.mNotificationManager = (NotificationManager)this.getSystemService("notification");

			///Android API ≥ 26のため、通知チャネルを作成する。
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				// The user-visible name of the channel.
				CharSequence app_name = "Smartpass_FireBase";
				// The user-visible description of the channel.
				String description = "Smartpass FireBase 通知チャネルです。";
				int importance = NotificationManager.IMPORTANCE_DEFAULT;
				NotificationChannel mChannel;
				mChannel = new NotificationChannel(channel_id, app_name, importance);
				// Configure the notification channel.
				mChannel.setDescription(description);
				mChannel.enableLights(true);
				// Sets the notification light color for notifications posted to this
				// channel, if the device supports this feature.
				mChannel.setLightColor(Color.RED);
				mNotificationManager.createNotificationChannel(mChannel);
			}
			StringBuilder builder = new StringBuilder();
			builder.append(message);
			builder.append("|");
//			builder.append(data);
			if (!willBeShown) {
				try {
					UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "GCMNotificationCallback", builder.toString());
					Log.d("AndroidNative", "[sendPushCallback] data: " + builder.toString());
				} catch (UnsatisfiedLinkError var30) {
					Log.d("AndroidNative", "Trying to call GCM push received callback, but the App closed!");
				}

				return;
			}

//			Intent contentIntent = new Intent("com.androidnative.push.intent.OPEN");
//			Intent contentIntent = new Intent(this, TempActivity.class);
			Intent contentIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
			contentIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			contentIntent.putExtra("browser_url", browserUrl);
			int requestID = replaceOldWithNew ? 1 : (int)System.currentTimeMillis();
			Log.d("AndroidNative", "Data retrived, requestID: " + requestID);
			PendingIntent pIntent = PendingIntent.getActivity(this.getApplicationContext(), 1, contentIntent, 134217728);
			int iconId = 2130837504;
			Resources res = this.getResources();
			int id = res.getIdentifier(smallIconName, "drawable", this.getPackageName());
			iconId = id == 0 ? iconId : id;
			NotificationCompat.Builder mBuilder = (new NotificationCompat.Builder(this, channel_id)).setSmallIcon(iconId).setContentTitle(title).setContentText(message).setChannelId(channel_id);
			StringTokenizer tokenizer = new StringTokenizer(color, "|");
			mBuilder.setColor(Color.argb(Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken())));
			id = res.getIdentifier(largeIconName, "drawable", this.getPackageName());
			if (id != 0) {
				Bitmap largeIcon = BitmapFactory.decodeResource(res, id);
				mBuilder.setLargeIcon(largeIcon);
			}

			if (notificationAlert != null && notificationAlert.equals("SILENT")) {
				mBuilder.setDefaults(4);
			} else {
				id = res.getIdentifier(soundName, "raw", this.getPackageName());
				if (id != 0) {
					Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + id);
					mBuilder.setDefaults(4);
					mBuilder.setSound(uri);
				} else {
					mBuilder.setDefaults(5);
				}
			}

			if (vibro) {
				mBuilder.setVibrate(new long[]{500L, 500L, 500L, 500L});
			} else {
				mBuilder.setVibrate(new long[0]);
			}

			if (!bigPicture.equals("")) {
				PictureLoadingListener listener = new PictureLoadingListener(requestID, mBuilder, this.mNotificationManager, pIntent, title, message, data);
				PictureLoadingTask loadingTask = new PictureLoadingTask(listener);
				loadingTask.execute(new String[]{bigPicture});
			} else {
				mBuilder.setStyle((new NotificationCompat.BigTextStyle()).bigText(message));
				mBuilder.setContentIntent(pIntent).setAutoCancel(true);
				this.mNotificationManager.notify(requestID, mBuilder.build());
				PowerManager pm = (PowerManager)this.getApplicationContext().getSystemService("power");
				PowerManager.WakeLock wakeLock = pm.newWakeLock(268435456, "AndroidNative");
				wakeLock.acquire();
				try {
					UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "GCMNotificationCallback", builder.toString());
					Log.d("AndroidNative", "[sendPushCallback] data: " + builder.toString());
				} catch (UnsatisfiedLinkError var31) {
					Log.d("AndroidNative", "Trying to call GCM push received callback, but the App closed!");
				}
			}
		} catch (Exception var33) {
			Log.d("AndroidNative", "Failed to schedule notification");
			var33.printStackTrace();
		}

	}

	public static boolean isForegroundRunning(Context context) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
		Iterator var3 = runningProcesses.iterator();

		while(true) {
			ActivityManager.RunningAppProcessInfo processInfo;
			do {
				if (!var3.hasNext()) {
					return false;
				}

				processInfo = (ActivityManager.RunningAppProcessInfo) var3.next();
			} while(processInfo.importance != 100);

			String[] var5 = processInfo.pkgList;
			int var6 = var5.length;

			for(int var7 = 0; var7 < var6; ++var7) {
				String activeProcess = var5[var7];
				if (activeProcess.equals(context.getPackageName())) {
					return true;
				}
			}
		}
	}
}