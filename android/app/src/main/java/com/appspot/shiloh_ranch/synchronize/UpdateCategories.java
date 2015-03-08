package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;

import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.appspot.shiloh_ranch.api.model.Category;
import com.appspot.shiloh_ranch.api.model.CategoryCollection;
import com.appspot.shiloh_ranch.api.model.Post;
import com.appspot.shiloh_ranch.api.model.PostCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by rockwotj on 3/7/2015.
 */
public final class UpdateCategories extends Sync<Category> {

    public UpdateCategories(Context context, ShilohRanch service) {
        super(context, service);
    }

    @Override
    public String getModelName() {
        return Category.class.getName();
    }

    @Override
    public List<Category> update() throws IOException {
        long lastSync = getLastSyncTime();
        boolean needsUpdate = mService.update().categories(lastSync).execute().getNeedsUpdate();
        if (needsUpdate) {
            ShilohRanch.Categories query = mService.categories();
            query.setLastSync(convertUnixTimeToDate(lastSync));
            CategoryCollection categories = query.execute();
            List<Category> items = new ArrayList<>(categories.getItems());
            if (categories.getNextPageToken() != null) {
                items.addAll(update(categories.getNextPageToken()));
            }
            if (!items.isEmpty()) {
                lastSync = convertDateToUnixTime(items.get(0).getTimeAdded());
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
    protected List<Category> update(String pageToken) throws IOException {
        ShilohRanch.Categories query = mService.categories();
        query.setPageToken(pageToken);
        CategoryCollection categories = query.execute();
        List<Category> items = new ArrayList<>(categories.getItems());
        if (categories.getNextPageToken() != null) {
            items.addAll(update(categories.getNextPageToken()));
        }
        return items;
    }
}
