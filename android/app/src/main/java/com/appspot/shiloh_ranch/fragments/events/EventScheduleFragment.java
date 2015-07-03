package com.appspot.shiloh_ranch.fragments.events;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Event;
import com.appspot.shiloh_ranch.database.Database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventScheduleFragment extends Fragment implements WeekView.MonthChangeListener, WeekView.EventClickListener {

    private OnEventSelectedListener mListener;
    private WeekView mWeekView;
    private List<Event> mEvents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events_schedule, container, false);
        mWeekView = (WeekView) rootView.findViewById(R.id.weekView);
        mWeekView.setNumberOfVisibleDays(3);
        Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"), Locale.US);
        mWeekView.goToHour(currentTime.get(Calendar.HOUR));
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        refresh();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            mListener = (OnEventSelectedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new RuntimeException("EventScheduleFragment must have a parent fragment " +
                    "that implements OnEventSelectedListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    public void refresh() {
        Database db = Database.getDatabase(getActivity());
        mEvents = db.getAllEvents();
        mWeekView.notifyDatasetChanged();
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int year, int month) {
        ArrayList<WeekViewEvent> eventList = new ArrayList<>();
        for (int i = 0; i < mEvents.size(); i++) {
            Event event = mEvents.get(i);
            Calendar startTime = DateTimeUtils.convertDateToCalendar(event.getStartTime());
            if (isWeeklyRecurring(event.getRepeat())) {
                Calendar endTime = DateTimeUtils.convertDateToCalendar(event.getEndTime());
                long untilTime = Long.MAX_VALUE;
                if (event.getRepeat().contains(":")) {
                    String until = event.getRepeat().split(":")[1];
                    untilTime = parseICSDateToUnixTime(until);
                }
                for (int weekInMonth = 0; weekInMonth < 4; weekInMonth++) {
                    Calendar weeklyStartTime = getNthOfMonth(weekInMonth, year, month, startTime);
                    if (weeklyStartTime.getTimeInMillis() > untilTime) {
                        break;
                    }
                    WeekViewEvent e = new WeekViewEvent(i, event.getTitle(),
                            weeklyStartTime,
                            getNthOfMonth(weekInMonth, year, month, endTime));
                    eventList.add(e);
                }
            } else if(startTime.get(Calendar.YEAR) == year && startTime.get(Calendar.MONTH) == (month - 1)) {
                WeekViewEvent e = new WeekViewEvent(i, event.getTitle(), startTime,
                        DateTimeUtils.convertDateToCalendar(event.getEndTime()));
                eventList.add(e);
            }
        }
        return eventList;
    }

    private boolean isWeeklyRecurring(String repeat) {
        if (repeat == null) {
            return false;
        }
        return repeat.contains("WEEKLY");
    }

    private long parseICSDateToUnixTime(String timestamp) {
        timestamp = timestamp.replace("T", " ");
        timestamp = timestamp.replace("Z", "");
        DateFormat icsFormat = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.US);
        try {
            return icsFormat.parse(timestamp).getTime();
        } catch (ParseException e) {
            Log.e("SRCC", "Unable to convert date to unix time");
            return -1L;
        }
    }

    private Calendar getNthOfMonth(int n, int year, int month, Calendar time) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
        calendar.set(Calendar.DAY_OF_WEEK, time.get(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, n);
        return calendar;
    }

    @Override
    public void onEventClick(WeekViewEvent weekViewEvent, RectF rectF) {
        Event e = mEvents.get((int) weekViewEvent.getId());
        if (mListener != null)
            mListener.onEventSelected(e, weekViewEvent.getStartTime(), weekViewEvent.getEndTime());
    }

    public interface OnEventSelectedListener {
        void onEventSelected(Event event, Calendar startTime, Calendar endTime);
    }

}
