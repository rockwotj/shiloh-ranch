package com.appspot.shiloh_ranch.fragments.events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Event;
import com.appspot.shiloh_ranch.database.Database;

public class EventDetailFragment extends Fragment {

    private Event mEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String key = getArguments().getString("EVENT_KEY");
        mEvent = Database.getDatabase(getActivity()).getEvent(key);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);
        TextView contentView = (TextView) rootView.findViewById(R.id.content);
        contentView.setText(Html.fromHtml(mEvent.getContent().replace("\\,", ",")));
        TextView locationView = (TextView) rootView.findViewById(R.id.location);
        locationView.setText("Event at " + mEvent.getLocation());
        TextView timeView = (TextView) rootView.findViewById(R.id.time);
        String startTime = DateTimeUtils.extractTime(mEvent.getStartTime());
        String endTime = DateTimeUtils.extractTime(mEvent.getEndTime());
        String date = DateTimeUtils.getHumanReadableDateString(mEvent.getStartTime());
        timeView.setText("Goes from " + startTime + " to " + endTime + " on " + date);
        return rootView;
    }

    public void subscribeToEvent() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getArguments().getLong("EVENT_START"))
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getArguments().getLong("EVENT_END"))
                .putExtra(CalendarContract.Events.TITLE, mEvent.getTitle())
                .putExtra(CalendarContract.Events.DESCRIPTION, Html.fromHtml(mEvent.getContent().replace("\\,", ",")).toString())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, mEvent.getLocation())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        startActivity(intent);
    }


}
