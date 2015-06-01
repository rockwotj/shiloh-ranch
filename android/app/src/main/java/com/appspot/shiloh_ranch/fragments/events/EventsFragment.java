package com.appspot.shiloh_ranch.fragments.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.activities.MainActivity;
import com.appspot.shiloh_ranch.api.model.Event;
import com.appspot.shiloh_ranch.fragments.IContentFragment;

import java.util.Calendar;

/**
 * A fragment representing a list of Events.
 * <p/>
 */
public class EventsFragment extends IContentFragment implements EventScheduleFragment.OnEventSelectedListener, ActionMode.Callback {

    private EventScheduleFragment mScheduleFragment;
    private EventDetailFragment mDetailFragment;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventsFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mScheduleFragment = new EventScheduleFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.content, mScheduleFragment).commit();
    }

    @Override
    public String getTitle() {
        return "Events";
    }

    @Override
    public void refresh() {
        if (mScheduleFragment != null)
            mScheduleFragment.refresh();
    }

    @Override
    public void onEventSelected(Event event, Calendar startTime, Calendar endTime) {
        mDetailFragment = new EventDetailFragment();
        String key = event.getEntityKey();
        Bundle args = new Bundle();
        args.putString("EVENT_KEY", key);
        args.putLong("EVENT_START", startTime.getTimeInMillis());
        args.putLong("EVENT_END", endTime.getTimeInMillis());
        mDetailFragment.setArguments(args);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.content, mDetailFragment).commit();
        ActionMode actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(this);
        actionMode.setTitle(event.getTitle());
        ((MainActivity) getActivity()).startedActionMode();

    }


    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        MenuItem subscribe = menu.add("Subscribe");
        subscribe.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        subscribe.setIcon(R.drawable.ic_insert_invitation_white_24dp);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
    if (mDetailFragment != null)
        mDetailFragment.subscribeToEvent();
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.content, mScheduleFragment).commit();
        ((MainActivity) getActivity()).finishedActionMode();
        mDetailFragment = null;
    }


}
