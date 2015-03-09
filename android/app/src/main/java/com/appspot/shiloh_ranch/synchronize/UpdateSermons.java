package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.appspot.shiloh_ranch.api.model.Sermon;
import com.appspot.shiloh_ranch.api.model.SermonCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by rockwotj on 3/7/2015.
 */
public final class UpdateSermons extends Sync<Sermon> {

    public UpdateSermons(Context context, ShilohRanch service) {
        super(context, service);
    }

    @Override
    public String getModelName() {
        return Sermon.class.getName();
    }

    @Override
    public List<Sermon> update() throws IOException {
        long lastSync = getLastSyncTime();
        boolean needsUpdate = mService.update().sermons(lastSync).execute().getNeedsUpdate();
        if (needsUpdate) {
            ShilohRanch.Sermons query = mService.sermons();
            query.setLastSync(DateTimeUtils.convertUnixTimeToDate(lastSync));
            SermonCollection sermons = query.execute();
            List<Sermon> items = new ArrayList<>(sermons.getItems());
            if (sermons.getNextPageToken() != null) {
                items.addAll(update(sermons.getNextPageToken()));
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
    protected List<Sermon> update(String pageToken) throws IOException {
        ShilohRanch.Sermons query = mService.sermons();
        query.setPageToken(pageToken);
        SermonCollection sermons = query.execute();
        List<Sermon> items = new ArrayList<>(sermons.getItems());
        if (sermons.getNextPageToken() != null) {
            items.addAll(update(sermons.getNextPageToken()));
        }
        return items;
    }
}
