//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.androidnative.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import com.unity3d.player.UnityPlayer;

public class PictureLoadingListener implements PictureLoadingTaskListener {
    private int _requestId = 0;
    private Builder _builder = null;
    private NotificationManager _manager = null;
    private PendingIntent _intent = null;
    private String _title = "";
    private String _message = "";
    private String _data = "";

    private PictureLoadingListener() {
    }

    public PictureLoadingListener(int requestId, Builder builder, NotificationManager manager, PendingIntent intent, String title, String message, String data) {
        this._requestId = requestId;
        this._builder = builder;
        this._manager = manager;
        this._intent = intent;
        this._title = title;
        this._message = message;
        this._data = data;
    }

    public void onPictureLoaded(Bitmap picture) {
        this._builder.setStyle((new BigPictureStyle()).bigPicture(picture).setBigContentTitle(this._title).setSummaryText(this._message));
        this._builder.setContentIntent(this._intent).setAutoCancel(true);
        this._manager.notify(this._requestId, this._builder.build());

        try {
            StringBuilder builder = new StringBuilder();
            builder.append(this._message);
            builder.append("|");
            builder.append(this._data);
            UnityPlayer.UnitySendMessage("GoogleCloudMessageService", "GCMNotificationCallback", builder.toString());
            Log.d("AndroidNative", "[sendPushCallback] data: " + builder.toString());
        } catch (UnsatisfiedLinkError var3) {
            Log.d("AndroidNative", "Trying to call GCM push received callback, but the App closed!");
        }

    }
}
