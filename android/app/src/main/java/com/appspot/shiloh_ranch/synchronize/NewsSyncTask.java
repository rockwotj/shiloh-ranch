package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;

import com.appspot.shiloh_ranch.api.ShilohRanch;

import java.io.IOException;

/**
 * Created by rockwotj on 3/8/2015.
 */
public class NewsSyncTask extends SyncTask {

    public NewsSyncTask(Context context, ShilohRanch service, ISyncTaskCallback callback) {
        super(context, service, callback);
    }

    @Override
    public Sync getUpdater() throws IOException {
        return new UpdateNews(mContext, mService);
    }
}
