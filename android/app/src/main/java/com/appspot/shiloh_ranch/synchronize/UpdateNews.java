package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.appspot.shiloh_ranch.api.model.Post;
import com.appspot.shiloh_ranch.api.model.PostCollection;

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
            List<Post> items = new ArrayList<>(posts.getItems());
            if (posts.getNextPageToken() != null) {
                items.addAll(update(posts.getNextPageToken()));
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
