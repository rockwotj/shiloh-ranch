package com.appspot.shiloh_ranch.fragments.sermons;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Sermon;

import java.util.List;

/**
 * Created by rockwotj on 4/9/15.
 */
public class SermonAdapter extends BaseAdapter {

    private final Context mContext;
    private List<Sermon> mSermons;

    public SermonAdapter(Context context, List<Sermon> sermons) {
        mContext = context;
        mSermons = sermons;
    }

    @Override
    public int getCount() {
        return mSermons.size();
    }

    @Override
    public Object getItem(int position) {
        return mSermons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mSermons.get(position).getEntityKey().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View sermonItem;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            sermonItem = inflater.inflate(R.layout.sermon_list_item, parent, false);
        } else {
            sermonItem = convertView;
        }
        TextView titleView = (TextView) sermonItem.findViewById(R.id.sermon_title);
        TextView dateView = (TextView) sermonItem.findViewById(R.id.sermon_date);
        Sermon sermon = mSermons.get(position);
        String sermonTitle = sermon.getTitle();
        String secondaryText = DateTimeUtils.reformatDateString(sermon.getDate());
        String regex = "(.+)- (.+)- \\((\\d+)/(\\d+)/(\\d+)\\)";
        if (sermonTitle.matches(regex)) {
            String[] titleParts = sermonTitle.split("- ", 3);
            sermonTitle = titleParts[0];
            secondaryText = titleParts[1] + " - " + titleParts[2];
        } else {
            Log.d("SRCC", "Did not match the regex for sermon titles!");
        }
        titleView.setText(Html.fromHtml(sermonTitle));
        dateView.setText(secondaryText);
        return sermonItem;
    }
}
