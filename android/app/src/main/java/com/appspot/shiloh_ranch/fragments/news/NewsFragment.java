package com.appspot.shiloh_ranch.fragments.news;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.fragments.IContentFragment;
import com.appspot.shiloh_ranch.fragments.IFragmentCallbacks;
import com.astuetz.PagerSlidingTabStrip;

/**
 * A fragment representing a list of Categories.
 * <p/>
 * <p/>
 */
public class NewsFragment extends IContentFragment {

    private CategoryAdapter mAdapter;
    private IFragmentCallbacks mCallbacks;

    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        mAdapter = new CategoryAdapter(getChildFragmentManager(), getActivity());
        pager.setAdapter(mAdapter);
        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (IFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public String getTitle() {
        return "News";
    }

    @Override
    public void refresh() {
        if (mAdapter != null)
            mAdapter.refresh();
    }
}
