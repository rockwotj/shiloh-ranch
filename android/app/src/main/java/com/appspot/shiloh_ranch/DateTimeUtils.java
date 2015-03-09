package com.appspot.shiloh_ranch;

import android.util.Log;

import com.google.api.client.json.GenericJson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 *
 * A collection of methods that have to do with sorting, and converting endpoint datetime strings.
 *
 * Created by rockwotj on 3/8/2015.
 */
public class DateTimeUtils {

    /**
     * Converts unix time into an Endpoints timestamp
     * @param time milliseconds since the epoch
     * @return a string formatted like this: YYYY-MM-DDTHH:MM:SS.000000
     */
    public static String convertUnixTimeToDate(long time) {
        Date timestamp = new Date(time);

        DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.US);
        String rawDatetime = dateParser.format(timestamp);
        return rawDatetime.replace(' ', 'T');
    }

    /**
     * Converts an Endpoints timestamp string into unix time.
     * @param time a string formatted like this: YYYY-MM-DDTHH:MM:SS.000000
     * @return milliseconds since the epoch
     */
    public static long convertDateToUnixTime(String time) {
        time = time.replace('T', ' ');
        try {
            DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.US);
            return dateParser.parse(time).getTime();
        } catch (ParseException e) {
            Log.e("SRCC", "Error converting DateTime Stamp to unix time.", e);
            return -1L;
        }
    }

    public static Comparator<GenericJson> getModelDateComparator() {
        final DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.US);
        return new Comparator<GenericJson>() {
            @Override
            public int compare(GenericJson genericJson, GenericJson genericJson2) {
                try {
                    Date date1 = dateParser.parse(genericJson.get("timeAdded").toString().replace('T', ' '));
                    Date date2 = dateParser.parse(genericJson2.get("timeAdded").toString().replace('T', ' '));
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    Log.e("SRCC", "Error converting DateTime Stamp to unix time.", e);
                    return 0;
                }

            }
        };
    }

}
