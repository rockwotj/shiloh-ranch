package com.appspot.shiloh_ranch.fragments.sermons;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Sermon;
import com.appspot.shiloh_ranch.database.Database;
import com.appspot.shiloh_ranch.fragments.IContentFragment;

import java.util.List;

/**
 * A fragment representing a list of Sermons.
 * <p/>
 * <p/>
 */
public class SermonsFragment extends IContentFragment {


    private List<Sermon> mSermons;
    private View mEmptyView;
    private ListView mPostList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SermonsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sermons, container, false);
        mPostList = (ListView) rootView.findViewById(R.id.sermon_list);
        mEmptyView = rootView.findViewById(R.id.empty);
        refresh();
        return rootView;
    }

    @Override
    public String getTitle() {
        return "Sermons";
    }

    @Override
    public void refresh() {
        Database db = Database.getDatabase(getActivity());
        mSermons = db.getAllSermons();
        mPostList.setVisibility(!mSermons.isEmpty() ? View.VISIBLE : View.GONE);
        mEmptyView.setVisibility(!mSermons.isEmpty() ? View.GONE : View.VISIBLE);
        if (!mSermons.isEmpty()) {
            mPostList.setAdapter(new SermonAdapter(getActivity(), mSermons));
        }
    }
}
