package com.appspot.shiloh_ranch.fragments.news;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Category;
import com.appspot.shiloh_ranch.database.Database;
import com.appspot.shiloh_ranch.fragments.IContentFragment;
import com.appspot.shiloh_ranch.fragments.IFragmentCallbacks;
import com.appspot.shiloh_ranch.synchronize.UpdateCategories;
import com.astuetz.PagerSlidingTabStrip;

import java.io.IOException;
import java.util.List;

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
        new SyncCategoriesTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        mAdapter = new CategoryAdapter(getActivity().getSupportFragmentManager(), getActivity());
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

    public class SyncCategoriesTask extends AsyncTask<Void, Void, List<Category>> {

        @Override
        protected List<Category> doInBackground(Void... voids) {
            try {
                return new UpdateCategories(getActivity(), mCallbacks.getService()).update();
            } catch (IOException e) {
                Log.e("SRCC", "Could not retrieve any Categories", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Category> categories) {
            if (categories != null && categories.size() > 0) {
                Database db = new Database(getActivity());
                db.open();
                for (Category c : categories) {
                    db.insert(c);
                }
                db.close();
                if (mAdapter != null)
                    mAdapter.refresh();
            }
        }
    }
}
