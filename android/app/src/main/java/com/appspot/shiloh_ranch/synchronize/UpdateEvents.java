package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;
import android.util.Log;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.appspot.shiloh_ranch.api.model.Event;
import com.appspot.shiloh_ranch.api.model.EventCollection;
import com.appspot.shiloh_ranch.database.Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by rockwotj on 3/7/2015.
 */
public final class UpdateEvents extends Sync<Event> {

    public UpdateEvents(Context context, ShilohRanch service) {
        super(context, service);
    }

    @Override
    public String getModelName() {
        return Event.class.getName();
    }

    @Override
    public List<Event> update() throws IOException {
        long lastSync = getLastSyncTime();
        boolean needsUpdate = mService.update().events(lastSync).execute().getNeedsUpdate();
        if (needsUpdate) {
            ShilohRanch.Events query = mService.events();
            query.setLastSync(DateTimeUtils.convertUnixTimeToDate(lastSync));
            EventCollection events = query.execute();
            List<Event> items = new ArrayList<>();
            Database db = Database.getDatabase(mContext);
            for (Event e : events.getItems()) {
                items.add(e);
                if (db.getPost(e.getEntityKey()) == null)
                    db.insert(e);
                else
                    db.update(e);
            }
            if (events.getNextPageToken() != null) {
                for(Event e : update(events.getNextPageToken())) {
                    items.add(e);
                    if (db.getPost(e.getEntityKey()) == null)
                        db.insert(e);
                    else
                        db.update(e);
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
            return new ArrayList<>();
        }
    }

    @Override
    protected List<Event> update(String pageToken) throws IOException {
        ShilohRanch.Events query = mService.events();
        query.setPageToken(pageToken);
        EventCollection events = query.execute();
        List<Event> items = new ArrayList<>(events.getItems());
        if (events.getNextPageToken() != null) {
            items.addAll(update(events.getNextPageToken()));
        }
        return items;
    }
}
