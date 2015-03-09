package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;

import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.appspot.shiloh_ranch.fragments.IContentFragment;

import java.io.IOException;

/**
 * Created by rockwotj on 3/8/2015.
 */
public class CategorySyncTask extends SyncTask {

    public CategorySyncTask(Context context, ShilohRanch service, ISyncTaskCallback callback) {
        super(context, service, callback);
    }

    @Override
    public Sync getUpdater() throws IOException {
        return new UpdateCategories(mContext, mService);
    }
}
