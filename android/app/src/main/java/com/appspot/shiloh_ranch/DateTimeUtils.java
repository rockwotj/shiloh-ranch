package com.appspot.shiloh_ranch;

import android.util.Log;

import com.appspot.shiloh_ranch.api.model.Event;
import com.appspot.shiloh_ranch.api.model.Post;
import com.appspot.shiloh_ranch.api.model.Sermon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A collection of methods that have to do with sorting and converting endpoint datetime strings.
 * <p/>
 * Created by rockwotj on 3/8/2015.
 */
public class DateTimeUtils {

    public static String getHumanReadableDateString(String timestamp) {
        timestamp = timestamp.replace('T', ' ');
        try {
            DateFormat dateParser = getServerDateFormat();
            Date date = dateParser.parse(timestamp);
            DateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
            dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormatter.format(date);
        } catch (ParseException e) {
            Log.e("SRCC", "Error converting DateTime Stamp to unix time.", e);
            return timestamp;
        }
    }

    /**
     * Converts unix time into an Endpoints timestamp
     *
     * @param time milliseconds since the epoch
     * @return a string formatted like this: YYYY-MM-DDTHH:MM:SS.000000
     */
    public static String convertUnixTimeToDate(long time) {
        Date timestamp = new Date(time);
        DateFormat dateParser = getServerDateFormat();
        String rawDatetime = dateParser.format(timestamp);
        return rawDatetime.replace(' ', 'T');
    }

    /**
     * Converts an Endpoints timestamp string into unix time.
     *
     * @param time a string formatted like this: YYYY-MM-DDTHH:MM:SS.000000
     * @return milliseconds since the epoch
     */
    public static long convertDateToUnixTime(String time) {
        time = time.replace('T', ' ');
        try {
            DateFormat dateParser = getServerDateFormat();
            return dateParser.parse(time).getTime();
        } catch (ParseException e) {
            Log.e("SRCC", "Error converting DateTime Stamp to unix time.", e);
            return -1L;
        }
    }

    public static Comparator<Sermon> getSermonDateComparator() {
        final DateFormat dateParser = getServerDateFormat();
        return new Comparator<Sermon>() {
            @Override
            public int compare(Sermon genericJson, Sermon genericJson2) {
                try {
                    Date date1 = dateParser.parse(genericJson.getTimeAdded().replace('T', ' '));
                    Date date2 = dateParser.parse(genericJson2.getTimeAdded().replace('T', ' '));
                    return -date1.compareTo(date2);
                } catch (ParseException e) {
                    Log.e("SRCC", "Error converting DateTime Stamp to unix time.", e);
                    return 0;
                }

            }
        };
    }

    public static Comparator<Event> getEventDateComparator() {
        final DateFormat dateParser = getServerDateFormat();
        return new Comparator<Event>() {
            @Override
            public int compare(Event genericJson, Event genericJson2) {
                try {
                    Date date1 = dateParser.parse(genericJson.getTimeAdded().replace('T', ' '));
                    Date date2 = dateParser.parse(genericJson2.getTimeAdded().replace('T', ' '));
                    return -date1.compareTo(date2);
                } catch (ParseException e) {
                    Log.e("SRCC", "Error converting DateTime Stamp to unix time.", e);
                    return 0;
                }

            }
        };
    }

    public static Comparator<Post> getPostDateComparator() {
        final DateFormat dateParser = getServerDateFormat();
        return new Comparator<Post>() {
            @Override
            public int compare(Post genericJson, Post genericJson2) {
                try {
                    Date date1 = dateParser.parse(genericJson.getTimeAdded().replace('T', ' '));
                    Date date2 = dateParser.parse(genericJson2.getTimeAdded().replace('T', ' '));
                    return -date1.compareTo(date2);
                } catch (ParseException e) {
                    Log.e("SRCC", "Error converting DateTime Stamp to unix time.", e);
                    return 0;
                }

            }
        };
    }

    private static DateFormat getServerDateFormat() {
        DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.US);
        dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateParser;
    }

}
