package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.appspot.shiloh_ranch.api.ShilohRanch;

import java.io.IOException;

/**
 * Created by rockwotj on 3/8/2015.
 */
public abstract class SyncTask extends AsyncTask<Void, Void, Void> {

    protected final Context mContext;
    protected final ShilohRanch mService;
    private final ISyncTaskCallback mCallback;

    public SyncTask(Context context, ShilohRanch service, ISyncTaskCallback callback) {
        this.mContext = context;
        this.mService = service;
        this.mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            getUpdater().update();
        } catch (IOException e) {
            Log.e("SRCC", "Could not retrieve any of the model", e);
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void voids) {
        if (mCallback != null)
            mCallback.onCompletion();
    }

    public abstract Sync getUpdater() throws IOException;

}
