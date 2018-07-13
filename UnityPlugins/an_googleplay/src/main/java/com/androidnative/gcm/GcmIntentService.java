////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package com.androidnative.gcm;
//
//import android.annotation.SuppressLint;
//import android.app.ActivityManager;
//import android.app.IntentService;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.ActivityManager.RunningAppProcessInfo;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.PowerManager;
//import android.os.PowerManager.WakeLock;
//import android.support.v4.app.NotificationCompat.BigTextStyle;
//import android.support.v4.app.NotificationCompat.Builder;
//import android.util.Log;
//
////import com.google.android.gms.gcm.GcmListenerService;
////import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.gcm.GcmListenerService;
//import com.unity3d.player.UnityPlayer;
//import java.util.Iterator;
//import java.util.List;
//import java.util.StringTokenizer;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class GcmIntentService extends GcmListenerService {
//    public static final int NOTIFICATION_ID = 1;
//    private NotificationManager mNotificationManager;
//    Builder builder;
//    public static final String TAG = "";
//
//
//    @Override
//    public void onMessageReceived(String s, Bundle bundle) {
//        super.onMessageReceived(s, bundle);
//        sendNotification(bundle);
//    }
//
//    @SuppressLint({"NewApi"})
//    public void SaveMessgaBundle(Bundle extras) {
//        SharedPreferences mSharedPreferences = this.getApplicationContext().getSharedPreferences("MyPref", 0);
//        Editor e = mSharedPreferences.edit();
//        e.putString("property_message", extras.toString());
//        e.commit();
//        Log.i(TAG, "Push Notification Saved");
//    }
//
//    @SuppressLint({"NewApi", "WrongConstant"})
//    private void sendNotification(Bundle extras) {
//        Log.i(TAG, "Push Notification Sended: ");
//
//        try {
//            this.SaveMessgaBundle(extras);
//            SharedPreferences prefs = this.getApplicationContext().getSharedPreferences("AN_PushNotificationBundle", 0);
//            String smallIconName = prefs.getString("SMALL_ICON_NAME", "");
//            String largeIconName = prefs.getString("LARGE_ICON_NAME", "");
//            String soundName = prefs.getString("SOUND_NAME", "");
//            boolean vibro = prefs.getBoolean("VIBRATION", false);
//            boolean showIfAppForeground = prefs.getBoolean("SHOW_WHEN_APP_FOREGROUND", true);
//            boolean replaceOldWithNew = prefs.getBoolean("REPLACE_OLD_WITH_NEW", false);
//            String color = prefs.getString("NOTIFICATION_COLOR", "255|255|255|255");
//            Log.d(TAG, "Push Notification Show when App Is Foreground " + showIfAppForeground);
//            boolean willBeShown = true;
//            if(!showIfAppForeground && isForegroundRunning(this.getApplicationContext())) {
//                Log.d(TAG, "App is Foreground, so don't show received push notification");
//                willBeShown = false;
//            }
//
//            String title = extras.getString("title");
//            String message = extras.getString("alert");
//            String data = extras.getString("json");
//            Log.d(TAG, "GCM Push Notification data received " + data);
//            String bigPicture = "";
//            String notificationAlert = null;
//            String browserUrl = "";
//
//            try {
//                JSONObject json = new JSONObject(data);
//                bigPicture = json.optString("big_picture_url");
//                browserUrl = json.optString("browser_url");
//                notificationAlert = json.optString("notification_alert");
//                Log.d(TAG, "Big Picture Push Notification style detedted. Big Piucture URL: " + bigPicture);
//            } catch (JSONException var31) {
//                Log.d(TAG, "Push Notification JSON parse error " + var31.getMessage());
//            }
//
//            this.mNotificationManager = (NotificationManager)this.getSystemService("notification");
//            StringBuilder builder = new StringBuilder();
//            builder.append(message);
//            builder.append("|");
//            builder.append(data);
//            if(!willBeShown) {
//                try {
//                    UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "GCMNotificationCallback", builder.toString());
//                    Log.d(TAG, "[sendPushCallback] data: " + builder.toString());
//                } catch (UnsatisfiedLinkError var29) {
//                    Log.d(TAG, "Trying to call GCM push received callback, but the App closed!");
//                }
//
//                return;
//            }
//
//            Intent contentIntent = new Intent(GcmBroadcastReceiver.ACTION_OPEN);
//            contentIntent.putExtra("browser_url", browserUrl);
//            int requestID = replaceOldWithNew?1:(int)System.currentTimeMillis();
//            Log.d(TAG, "Data retrived, requestID: " + requestID);
//            PendingIntent pIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 1, contentIntent, 134217728);
//            int iconId = 2130837504;
//            Resources res = this.getResources();
//            int id = res.getIdentifier(smallIconName, "drawable", this.getPackageName());
//            iconId = id == 0?iconId:id;
//            Builder mBuilder = (new Builder(this)).setSmallIcon(iconId).setContentTitle(title).setContentText(message);
//            StringTokenizer tokenizer = new StringTokenizer(color, "|");
//            mBuilder.setColor(Color.argb(Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken())));
//            id = res.getIdentifier(largeIconName, "drawable", this.getPackageName());
//            if(id != 0) {
//                Bitmap largeIcon = BitmapFactory.decodeResource(res, id);
//                mBuilder.setLargeIcon(largeIcon);
//            }
//
//            if(notificationAlert != null && notificationAlert.equals("SILENT")) {
//                mBuilder.setDefaults(4);
//            } else {
//                id = res.getIdentifier(soundName, "raw", this.getPackageName());
//                if(id != 0) {
//                    Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + id);
//                    mBuilder.setDefaults(4);
//                    mBuilder.setSound(uri);
//                } else {
//                    mBuilder.setDefaults(5);
//                }
//            }
//
//            if(vibro) {
//                mBuilder.setVibrate(new long[]{500L, 500L, 500L, 500L});
//            } else {
//                mBuilder.setVibrate(new long[0]);
//            }
//
//            if(!bigPicture.equals("")) {
//                PictureLoadingListener listener = new PictureLoadingListener(requestID, mBuilder, this.mNotificationManager, pIntent, title, message, data);
//                PictureLoadingTask loadingTask = new PictureLoadingTask(listener);
//                loadingTask.execute(new String[]{bigPicture});
//            } else {
//                mBuilder.setStyle((new BigTextStyle()).bigText(message));
//                mBuilder.setContentIntent(pIntent).setAutoCancel(true);
//                this.mNotificationManager.notify(requestID, mBuilder.build());
//                PowerManager pm = (PowerManager)this.getApplicationContext().getSystemService("power");
//                WakeLock wakeLock = pm.newWakeLock(268435456, TAG);
//                wakeLock.acquire();
//
//                try {
//                    UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "GCMNotificationCallback", builder.toString());
//                    Log.d(TAG, "[sendPushCallback] data: " + builder.toString());
//                } catch (UnsatisfiedLinkError var30) {
//                    Log.d(TAG, "Trying to call GCM push received callback, but the App closed!");
//                }
//            }
//        } catch (Exception var32) {
//            Log.d(TAG, "Failed to schedule notification");
//            var32.printStackTrace();
//        }
//
//    }
//
//    public static boolean isForegroundRunning(Context context) {
//        @SuppressLint("WrongConstant") ActivityManager am = (ActivityManager)context.getSystemService("activity");
//        List<RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//        Iterator var3 = runningProcesses.iterator();
//
//        while(true) {
//            RunningAppProcessInfo processInfo;
//            do {
//                if(!var3.hasNext()) {
//                    return false;
//                }
//
//                processInfo = (RunningAppProcessInfo)var3.next();
//            } while(processInfo.importance != 100);
//
//            String[] var5 = processInfo.pkgList;
//            int var6 = var5.length;
//
//            for(int var7 = 0; var7 < var6; ++var7) {
//                String activeProcess = var5[var7];
//                if(activeProcess.equals(context.getPackageName())) {
//                    return true;
//                }
//            }
//        }
//    }
//}
