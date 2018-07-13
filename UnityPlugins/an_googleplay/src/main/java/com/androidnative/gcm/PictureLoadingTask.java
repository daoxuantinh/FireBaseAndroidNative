//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.androidnative.gcm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import java.io.InputStream;
import java.net.URL;

public class PictureLoadingTask extends AsyncTask<String, Void, Bitmap> {
    private PictureLoadingTaskListener _listener = null;

    private PictureLoadingTask() {
    }

    public PictureLoadingTask(PictureLoadingTaskListener listener) {
        this._listener = listener;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;

        try {
            InputStream in = (new URL(urldisplay)).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception var5) {
            Log.d("AndroidNative", "Big Picture Loading Error " + var5.getMessage());
            var5.printStackTrace();
        }

        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if(this._listener != null) {
            this._listener.onPictureLoaded(result);
        }

    }
}
