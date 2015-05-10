package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;
import android.util.Log;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.appspot.shiloh_ranch.api.model.Post;
import com.appspot.shiloh_ranch.api.model.PostCollection;
import com.appspot.shiloh_ranch.database.Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by rockwotj on 3/7/2015.
 */
public final class UpdateNews extends Sync<Post> {

    public UpdateNews(Context context, ShilohRanch service) {
        super(context, service);
    }

    @Override
    public String getModelName() {
        return Post.class.getName();
    }

    @Override
    public List<Post> update() throws IOException {
        long lastSync = getLastSyncTime();
        boolean needsUpdate = mService.update().posts(lastSync).execute().getNeedsUpdate();
        if (needsUpdate) {
            ShilohRanch.Posts query = mService.posts();
            query.setLastSync(DateTimeUtils.convertUnixTimeToDate(lastSync));
            PostCollection posts = query.execute();
            List<Post> items = new ArrayList<>();
            Database db = Database.getDatabase(mContext);
            if (posts.getItems() != null) {
                for (Post p : posts.getItems()) {
                    items.add(p);
                    if (db.getPost(p.getEntityKey()) == null)
                        db.insert(p);
                    else
                        db.update(p);
                }
            }
            if (posts.getNextPageToken() != null) {
                for (Post p : update(posts.getNextPageToken())) {
                    items.add(p);
                    if (db.getPost(p.getEntityKey()) == null)
                        db.insert(p);
                    else
                        db.update(p);
                }
            }
            if (!items.isEmpty()) {
                Log.d("SRCC", "LastSync for News is now: " + items.get(0).getTimeAdded());//2015-03-10T17:54:16.000000
                lastSync = DateTimeUtils.convertDateToUnixTime(items.get(0).getTimeAdded());
                Log.d("SRCC", "Which in ms is: " + lastSync);
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
    protected List<Post> update(String pageToken) throws IOException {
        ShilohRanch.Posts query = mService.posts();
        query.setPageToken(pageToken);
        PostCollection posts = query.execute();
        List<Post> items = new ArrayList<>(posts.getItems());
        if (posts.getNextPageToken() != null) {
            items.addAll(update(posts.getNextPageToken()));
        }
        return items;
    }
}
