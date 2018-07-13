//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.androidnative.gcm;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends BroadcastReceiver{
	public static final String ACTION_OPEN = "com.androidnative.push.intent.OPEN";
	public static final String ACTION_RECEIVE = "com.androidnative.push.intent.RECEIVE";

	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		Log.d("AndroidNative", "GcmBroadcastReceiver action = " + action);
		if (action.equals(ACTION_OPEN)) {
		    ///Because launches activity in BroadcastReceiver,
		    /// so uses goAsync() to flag that it needs more time to finish after onReceive() is complete
            final PendingResult pendingResult = goAsync();
			Bundle data = intent.getExtras();
			String browserUrl = data.getString("browser_url");
            Log.d("AndroidNative", "GcmBroadcastReceiver browser_url: "+browserUrl);

            AsyncTask<String, Void, Void> asyncTask = new AsyncTask<String, Void, Void>() {
                @Override
                protected Void doInBackground(String... strings) {
                    Intent launcher = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                    launcher.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(launcher);

                    ///Open ads_url send from push notification
                    if(strings[0].length()>0){
                        if(!strings[0].startsWith("http")){
                            strings[0] = "http://"+strings[0];
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strings[0]));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (browserIntent.resolveActivity(context.getPackageManager()) != null) {
                            browserIntent.setPackage("com.android.chrome");
                            try {
                                context.startActivity(browserIntent);
                            } catch (ActivityNotFoundException e) {
                                browserIntent.setPackage(null);
                                context.startActivity(browserIntent);
                            }
                        }
                    }

                    // Must call finish() so the BroadcastReceiver can be recycled.
                    pendingResult.finish();
                    return null;
                }
            };
            asyncTask.execute(browserUrl);


		} else {
			Log.d("AndroidNative", "Can not get open action");
		}

	}
}
