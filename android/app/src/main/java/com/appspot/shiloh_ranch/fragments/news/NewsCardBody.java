package com.appspot.shiloh_ranch.fragments.news;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appspot.shiloh_ranch.R;

import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by rockwotj on 3/9/2015.
 */
public class NewsCardBody extends CardExpand {
    private final String mContent;

    public NewsCardBody(Context context, String content) {
        super(context, R.layout.news_card_body);
        mContent = content;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        if (view == null) return;
        TextView tx = (TextView) view.findViewById(R.id.content);
        if (tx != null) {
            tx.setText(Html.fromHtml(mContent));
        }

    }
}
