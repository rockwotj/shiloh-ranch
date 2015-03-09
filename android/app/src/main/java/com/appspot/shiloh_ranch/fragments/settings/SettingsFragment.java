package com.appspot.shiloh_ranch.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.fragments.IContentFragment;

/**
 * A fragment representing a list of Sermons.
 * <p/>
 * <p/>
 */
public class SettingsFragment extends IContentFragment {


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        return rootView;
    }

    @Override
    public String getTitle() {
        return "Settings";
    }

    @Override
    public void refresh() {

    }
}
