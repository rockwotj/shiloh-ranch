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

    public static final String REGEX = "(.+)- (.+)- \\((\\d+)/(\\d+)/(\\d+)\\)";
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
    public Sermon getItem(int position) {
        return mSermons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mSermons.get(position).getEntityKey().hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View sermonView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            sermonView = inflater.inflate(R.layout.list_item, parent, false);
        } else {
            sermonView = convertView;
        }
        bindSermonToView(position, sermonView);
        return sermonView;
    }

    public void bindSermonToView(int position, View sermonView) {
        TextView titleView = (TextView) sermonView.findViewById(R.id.primary_text);
        TextView dateView = (TextView) sermonView.findViewById(R.id.secondary_text);
        Sermon sermon = mSermons.get(position);
        String sermonTitle = getSermonDisplayTitle(sermon);
        String secondaryText = getSermonDisplaySubtitle(sermon);
        titleView.setText(Html.fromHtml(sermonTitle));
        dateView.setText(secondaryText);
    }

    public String getSermonDisplayTitle(Sermon sermon) {
        String sermonTitle = sermon.getTitle();
        if (sermonTitle.matches(REGEX)) {
            String[] titleParts = sermonTitle.split("- ", 3);
            sermonTitle = titleParts[0];
        } else {
            Log.d("SRCC", "Did not match the regex for sermon titles!");
        }
        return sermonTitle;
    }

    public String getSermonDisplaySubtitle(Sermon sermon) {
        String sermonTitle = sermon.getTitle();
        String secondaryText = DateTimeUtils.reformatDateString(sermon.getDate());
        if (sermonTitle.matches(REGEX)) {
            String[] titleParts = sermonTitle.split("- ", 3);
            secondaryText = titleParts[1] + " - " + titleParts[2];
        }
        return secondaryText;
    }

}
