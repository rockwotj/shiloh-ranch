package com.shilohranch.apps.fragments.navigation;

import android.graphics.drawable.Drawable;

/**
 * Created by poliveira on 24/10/2014.
 * Modified by rockwotj on 3/3/2015.
 */
public class NavigationItem {
    private String mText;
    private Drawable mDrawable;
    private boolean mLineAbove;

    public NavigationItem(String text, Drawable drawable) {
        mText = text;
        mDrawable = drawable;
    }

    public NavigationItem(String text, Drawable drawable, boolean lineAbove) {
        mText = text;
        mDrawable = drawable;
        mLineAbove = lineAbove;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    public boolean isLineAbove() {
        return mLineAbove;
    }

    public void setLineAbove(boolean mLineAbove) {
        this.mLineAbove = mLineAbove;
    }
}
