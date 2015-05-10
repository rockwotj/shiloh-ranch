package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;
import android.util.Log;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.appspot.shiloh_ranch.api.model.Category;
import com.appspot.shiloh_ranch.api.model.Deletion;
import com.appspot.shiloh_ranch.api.model.DeletionCollection;
import com.appspot.shiloh_ranch.api.model.Event;
import com.appspot.shiloh_ranch.api.model.Post;
import com.appspot.shiloh_ranch.api.model.Sermon;
import com.appspot.shiloh_ranch.database.Database;

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
            List<Deletion> items = new ArrayList<>();
            Database db = Database.getDatabase(mContext);
            if (deletions.getItems() != null) {
                for (Deletion d : deletions.getItems()) {
                    items.add(d);
                    String kind = d.getKind();
                    switch (kind) {
                        case "Sermon":
                            db.delete(Sermon.class, d.getDeletionKey());
                            break;
                        case "Post":
                            db.delete(Post.class, d.getDeletionKey());
                            break;
                        case "Event":
                            db.delete(Event.class, d.getDeletionKey());
                            break;
                        case "Category":
                            db.delete(Category.class, d.getDeletionKey());
                            break;
                        default:
                            Log.e("SRCC", "Error trying to delete type: " + kind);
                            break;
                    }
                }
            }
            if (deletions.getNextPageToken() != null) {
                for (Deletion d : update(deletions.getNextPageToken())) {
                    items.add(d);
                    String kind = d.getKind();
                    switch (kind) {
                        case "Sermon":
                            db.delete(Sermon.class, d.getDeletionKey());
                            break;
                        case "Post":
                            db.delete(Post.class, d.getDeletionKey());
                            break;
                        case "Event":
                            db.delete(Event.class, d.getDeletionKey());
                            break;
                        case "Category":
                            db.delete(Category.class, d.getDeletionKey());
                            break;
                        default:
                            Log.e("SRCC", "Error trying to delete type: " + kind);
                            break;
                    }
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
