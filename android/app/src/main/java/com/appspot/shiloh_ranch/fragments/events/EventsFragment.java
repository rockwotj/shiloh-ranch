package com.appspot.shiloh_ranch.fragments.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Event;
import com.appspot.shiloh_ranch.database.Database;
import com.appspot.shiloh_ranch.fragments.IContentFragment;

import java.util.List;

/**
 * A fragment representing a list of Events.
 * <p/>
 * <p/>
 */
public class EventsFragment extends IContentFragment implements AdapterView.OnItemClickListener {

    private ListView mEventList;
    private View mEmptyView;
    private EventAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        mEventList = (ListView) rootView.findViewById(R.id.sermon_list);
        mEventList.setOnItemClickListener(this);
        mEmptyView = rootView.findViewById(R.id.empty);
        refresh();
        return rootView;
    }

    @Override
    public String getTitle() {
        return "Events";
    }

    @Override
    public void refresh() {
        Database db = Database.getDatabase(getActivity());
        List<Event> events = db.getAllEvents();
        mEventList.setVisibility(!events.isEmpty() ? View.VISIBLE : View.GONE);
        mEmptyView.setVisibility(!events.isEmpty() ? View.GONE : View.VISIBLE);
        if (!events.isEmpty()) {
            mAdapter = new EventAdapter(getActivity(), events);
            mEventList.setAdapter(mAdapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent detailIntent = new Intent(getActivity(), EventDetailActivity.class);
        detailIntent.putExtra("ENTITY_KEY", mAdapter.getItem(position).getEntityKey());
        getActivity().startActivity(detailIntent);
    }
}
