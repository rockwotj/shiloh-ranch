package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;
import android.util.Log;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.appspot.shiloh_ranch.api.model.Category;
import com.appspot.shiloh_ranch.api.model.CategoryCollection;
import com.appspot.shiloh_ranch.database.Database;

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
            query.setLastSync(DateTimeUtils.convertUnixTimeToDate(lastSync));
            CategoryCollection categories = query.execute();
            List<Category> items = new ArrayList<>();
            Database db = Database.getDatabase(mContext);
            for (Category c : categories.getItems()) {
                items.add(c);
                if (db.getPost(c.getEntityKey()) == null)
                    db.insert(c);
                else
                    db.update(c);
            }
            if (categories.getNextPageToken() != null) {
                for (Category c : update(categories.getNextPageToken())) {
                    items.add(c);
                    if (db.getPost(c.getEntityKey()) == null)
                        db.insert(c);
                    else
                        db.update(c);
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
