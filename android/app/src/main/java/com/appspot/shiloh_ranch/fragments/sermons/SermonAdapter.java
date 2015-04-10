package com.appspot.shiloh_ranch.fragments.sermons;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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
    private final AnimationDrawable[] mAnimations;
    private List<Sermon> mSermons;

    public SermonAdapter(Context context, List<Sermon> sermons) {
        mContext = context;
        mSermons = sermons;
        mAnimations = new AnimationDrawable[2];
        mAnimations[0] = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.play_pause_animation);
        mAnimations[1] = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.pause_play_animation);
        mAnimations[0].setOneShot(true);
        mAnimations[1].setOneShot(true);
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
        View sermonView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            sermonView = inflater.inflate(R.layout.sermon_list_item, parent, false);
        } else {
            sermonView = convertView;
        }
        TextView titleView = (TextView) sermonView.findViewById(R.id.sermon_title);
        TextView dateView = (TextView) sermonView.findViewById(R.id.sermon_date);
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
        final ImageButton playPauseButton = (ImageButton) sermonView.findViewById(R.id.play_pause_button);
        playPauseButton.setTag(1);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int animationNumber = (((int) playPauseButton.getTag()) + 1) % 2;
                playPauseButton.setTag(animationNumber);
                ReplayableAnimationDrawable animation = new ReplayableAnimationDrawable(mAnimations[animationNumber]) {
                    @Override
                    public void onAnimationFinish() {
                        super.onAnimationFinish();
                        int finalDrawable = animationNumber == 0 ? R.drawable.ic_play_pause_8 : R.drawable.ic_play_pause_0;
                        playPauseButton.setImageDrawable(mContext.getResources().getDrawable(finalDrawable));
                    }
                };
                playPauseButton.setImageDrawable(animation);
                animation.start();
            }
        });
        return sermonView;
    }
}
