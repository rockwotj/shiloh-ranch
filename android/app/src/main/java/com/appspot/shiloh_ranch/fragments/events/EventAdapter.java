package com.appspot.shiloh_ranch.fragments.events;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Event;

import java.util.List;

/**
 * Created by rockwotj on 4/27/2015.
 */
public class EventAdapter extends BaseAdapter {

    private final List<Event> mEvents;
    private final LayoutInflater mInflater;

    public EventAdapter(Context context, List<Event> events) {
        mInflater = LayoutInflater.from(context);
        mEvents = events;
    }

    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public Event getItem(int i) {
        return mEvents.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mEvents.get(i).getEntityKey().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View sermonView;
        if (convertView == null) {
            sermonView = mInflater.inflate(R.layout.list_item, parent, false);
        } else {
            sermonView = convertView;
        }
        bindEventToView(position, sermonView);
        return sermonView;
    }

    private void bindEventToView(int position, View eventView) {
        TextView titleView = (TextView) eventView.findViewById(R.id.primary_text);
        TextView subtitleView = (TextView) eventView.findViewById(R.id.secondary_text);
        Event event = getItem(position);
        titleView.setText(event.getTitle());
        String subtitle = event.getExcerpt();
        if (subtitle.equals("")) {
            subtitle = DateTimeUtils.getHumanReadableDateString(event.getDatePublished());
        }
        subtitleView.setText(Html.fromHtml(subtitle));
    }
}
