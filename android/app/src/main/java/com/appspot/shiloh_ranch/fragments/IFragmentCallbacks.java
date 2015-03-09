package com.appspot.shiloh_ranch.fragments;

import com.appspot.shiloh_ranch.api.ShilohRanch;

/**
 * Created by rockwotj on 3/7/2015.
 */
public interface IFragmentCallbacks {

    public ShilohRanch getService();

    public void updateData();

    public void updateCategories();

    public void updateEvents();

    public void updateNews();

    public void updateSermons();

}
