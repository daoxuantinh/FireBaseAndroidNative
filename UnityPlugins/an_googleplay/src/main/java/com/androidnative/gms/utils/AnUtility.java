//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.androidnative.gms.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.unity3d.player.UnityPlayer;

public class AnUtility {
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public AnUtility() {
	}

	public static Activity GetLauncherActivity() {
		return UnityPlayer.currentActivity;
	}

	public static Context GetApplicationContex() {
		return GetLauncherActivity().getApplicationContext();
	}

	public static boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(GetLauncherActivity());
		if (resultCode != 0) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, GetLauncherActivity(), 9000).show();
			} else {
				Log.i("AndroidNative", "This device is not supported.");
				GetLauncherActivity().finish();
			}

			return false;
		} else {
			return true;
		}
	}
}
