package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;
import android.util.Log;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.appspot.shiloh_ranch.api.model.Sermon;
import com.appspot.shiloh_ranch.api.model.SermonCollection;
import com.appspot.shiloh_ranch.database.Database;

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
            List<Sermon> items = new ArrayList<>();
            Database db = Database.getDatabase(mContext);
            if (sermons.getItems() != null) {
                for (Sermon s : sermons.getItems()) {
                    items.add(s);
                    if (db.getPost(s.getEntityKey()) == null)
                        db.insert(s);
                    else
                        db.update(s);
                }
            }
            if (sermons.getNextPageToken() != null) {
                for (Sermon s : update(sermons.getNextPageToken())) {
                    items.add(s);
                    if (db.getPost(s.getEntityKey()) == null)
                        db.insert(s);
                    else
                        db.update(s);
                }
            }
            if (!items.isEmpty()) {
                lastSync = DateTimeUtils.convertDateToUnixTime(items.get(0).getTimeAdded());
                if (lastSync > 0) {
                    setLastSyncTime(lastSync);
                }
            }
            Log.d("SRCC", "Got " + items.size() + " of " + getModelName());
            return items;
        } else {
            Log.d("SRCC", "No new " + getModelName());
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
