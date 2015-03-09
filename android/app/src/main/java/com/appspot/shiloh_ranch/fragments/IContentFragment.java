package com.appspot.shiloh_ranch.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by rockwotj on 3/3/2015.
 */
public abstract class IContentFragment extends Fragment {

    public abstract String getTitle();

    public abstract void refresh();
}
