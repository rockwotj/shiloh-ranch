package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;
import android.content.SharedPreferences;

import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.google.api.client.json.GenericJson;

import java.io.IOException;
import java.util.List;

/**
 * Created by rockwotj on 3/7/2015.
 * <p/>
 * The superclass for all of the individual data sync helper classes
 */
public abstract class Sync<Model extends GenericJson> {

    private static final String SYNC_PREFERENCES = "SYNC_PREFERENCES";
    protected final Context mContext;
    protected final ShilohRanch mService;

    protected Sync(Context mContext, ShilohRanch mService) {
        this.mContext = mContext;
        this.mService = mService;
    }

    public abstract String getModelName();

    /**
     * Should consider refactoring this process in case more data is ever needed
     *
     * @return
     * @throws IOException
     */
    public abstract List<Model> update() throws IOException;

    protected abstract List<Model> update(String pageToken) throws IOException;

    /**
     * Gets the last time that data was synced
     *
     * @return milliseconds from the last update since the epoch
     */
    protected long getLastSyncTime() {
        SharedPreferences preferences = mContext.getSharedPreferences(SYNC_PREFERENCES, 0);
        return preferences.getLong(getModelName(), 0);
    }

    /**
     * Sets the last time that data was synced.
     *
     * @param lastSync milliseconds from the last update since the epoch
     */
    protected void setLastSyncTime(long lastSync) {
        SharedPreferences preferences = mContext.getSharedPreferences(SYNC_PREFERENCES, 0);
        preferences.edit().putLong(getModelName(), lastSync).apply();
    }
}
