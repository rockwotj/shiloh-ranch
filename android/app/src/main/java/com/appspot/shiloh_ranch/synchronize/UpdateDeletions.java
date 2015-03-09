package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.appspot.shiloh_ranch.api.model.Deletion;
import com.appspot.shiloh_ranch.api.model.DeletionCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by rockwotj on 3/7/2015.
 */
public final class UpdateDeletions extends Sync<Deletion> {

    public UpdateDeletions(Context context, ShilohRanch service) {
        super(context, service);
    }

    @Override
    public String getModelName() {
        return Deletion.class.getName();
    }

    @Override
    public List<Deletion> update() throws IOException {
        long lastSync = getLastSyncTime();
        boolean needsUpdate = mService.update().deletions(lastSync).execute().getNeedsUpdate();
        if (needsUpdate) {
            ShilohRanch.Deletions query = mService.deletions();
            query.setLastSync(DateTimeUtils.convertUnixTimeToDate(lastSync));
            DeletionCollection deletions = query.execute();
            List<Deletion> items = new ArrayList<>(deletions.getItems());
            if (deletions.getNextPageToken() != null) {
                items.addAll(update(deletions.getNextPageToken()));
            }
            if (!items.isEmpty()) {
                lastSync = DateTimeUtils.convertDateToUnixTime(items.get(0).getTimeAdded());
                if (lastSync > 0) {
                    setLastSyncTime(lastSync);
                }
            }
            return items;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    protected List<Deletion> update(String pageToken) throws IOException {
        ShilohRanch.Deletions query = mService.deletions();
        query.setPageToken(pageToken);
        DeletionCollection deletions = query.execute();
        List<Deletion> items = new ArrayList<>(deletions.getItems());
        if (deletions.getNextPageToken() != null) {
            items.addAll(update(deletions.getNextPageToken()));
        }
        return items;
    }
}
