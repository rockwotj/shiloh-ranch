package com.appspot.shiloh_ranch.synchronize;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.google.api.client.json.GenericJson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by rockwotj on 3/7/2015.
 *
 * The superclass for all of the individual data sync helper methods
 */
public abstract class Sync<Model extends GenericJson> {

    private static final String SYNC_PREFERENCES = "SYNC_PREFERENCES";
    private final DateFormat mDateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    protected final Context mContext;
    protected final ShilohRanch mService;

    protected Sync(Context mContext, ShilohRanch mService) {
        this.mContext = mContext;
        this.mService = mService;
    }

    public abstract String getModelName();

    public abstract List<Model> update() throws IOException;

    protected abstract List<Model> update(String pageToken) throws IOException;

    protected long getLastSyncTime() {
        SharedPreferences preferences = mContext.getSharedPreferences(SYNC_PREFERENCES, 0);
        return preferences.getLong(getModelName(), 0);
    }

    protected void setLastSyncTime(long lastSync) {
        SharedPreferences preferences = mContext.getSharedPreferences(SYNC_PREFERENCES, 0);
        preferences.edit().putLong(getModelName(), lastSync).apply();
    }

    /**
     * Converts unix time into an Endpoints timestamp
     * @param time milliseconds since the epoch
     * @return a string formatted like this: YYYY-MM-DDTHH:MM:SS.000000
     */
    protected String convertUnixTimeToDate(long time) {
        Date timestamp = new Date(time);
        String rawDatetime = mDateParser.format(timestamp);
        return rawDatetime.replace(' ', 'T');
    }

    /**
     * Converts an Endpoints timestamp string into unix time.
     * @param time a string formatted like this: YYYY-MM-DDTHH:MM:SS.000000
     * @return milliseconds since the epoch
     */
    protected long convertDateToUnixTime(String time) {
        time = time.replace('T', ' ');
        try {
            return mDateParser.parse(time).getTime();
        } catch (ParseException e) {
            Log.e("SRCC", "Error converting DateTime Stamp to unix time.", e);
            return -1L;
        }
    }
}
