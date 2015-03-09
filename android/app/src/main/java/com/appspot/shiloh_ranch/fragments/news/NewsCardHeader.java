package com.appspot.shiloh_ranch.fragments.news;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appspot.shiloh_ranch.DateTimeUtils;
import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Post;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by rockwotj on 3/9/2015.
 */
public class NewsCardHeader extends CardHeader {

    private final Post mPost;

    public NewsCardHeader(Context context, Post post) {
        super(context, R.layout.news_card_header);
        mPost = post;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        if (view != null) {
            TextView t1 = (TextView) view.findViewById(R.id.title);
            if (t1 != null)
                t1.setText(mPost.getTitle());

            TextView t2 = (TextView) view.findViewById(R.id.subtitle);
            if (t2 != null)
                t2.setText("Posted on: " + DateTimeUtils.getHumanReadableDateString(mPost.getDate()));
        }
    }
}
