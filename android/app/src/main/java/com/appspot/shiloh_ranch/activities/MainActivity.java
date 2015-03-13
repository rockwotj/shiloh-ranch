package com.appspot.shiloh_ranch.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.ShilohRanch;
import com.appspot.shiloh_ranch.fragments.IContentFragment;
import com.appspot.shiloh_ranch.fragments.IFragmentCallbacks;
import com.appspot.shiloh_ranch.fragments.events.EventsFragment;
import com.appspot.shiloh_ranch.fragments.home.HomeFragment;
import com.appspot.shiloh_ranch.fragments.navigation.INavigationDrawerCallbacks;
import com.appspot.shiloh_ranch.fragments.navigation.NavigationDrawerFragment;
import com.appspot.shiloh_ranch.fragments.news.NewsFragment;
import com.appspot.shiloh_ranch.fragments.sermons.SermonsFragment;
import com.appspot.shiloh_ranch.fragments.settings.SettingsFragment;
import com.appspot.shiloh_ranch.synchronize.CategorySyncTask;
import com.appspot.shiloh_ranch.synchronize.DeletionSyncTask;
import com.appspot.shiloh_ranch.synchronize.EventSyncTask;
import com.appspot.shiloh_ranch.synchronize.ISyncTaskCallback;
import com.appspot.shiloh_ranch.synchronize.NewsSyncTask;
import com.appspot.shiloh_ranch.synchronize.SermonSyncTask;
import com.appspot.shiloh_ranch.synchronize.SyncTask;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity implements INavigationDrawerCallbacks, IFragmentCallbacks, ISyncTaskCallback {

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private List<Class<? extends IContentFragment>> mFragmentClasses;
    private int mCurrentPosition;
    private IContentFragment[] mFragments;
    private ShilohRanch mService;
    private AsyncTask<Void, Void, Void>[] mSyncTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentPosition = -1;
        mFragmentClasses = Arrays.asList(HomeFragment.class, NewsFragment.class, EventsFragment.class, SermonsFragment.class, SettingsFragment.class);
        mFragments = new IContentFragment[6];
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        mService = new ShilohRanch.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null)
                .setApplicationName("com.appspot.shiloh_ranch.android")
                .build();
        mSyncTasks = new SyncTask[5];
        updateData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_refresh:
                updateData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == mCurrentPosition) {
            return;
        } else if (position >= mFragmentClasses.size()) {
            String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=XURAEFVZV2CAJ";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            return;
        } else if (mCurrentPosition != -1) {
            getSupportFragmentManager().beginTransaction()
                    .remove(getCurrentFragment())
                    .commit();
        }
        mCurrentPosition = position;
        IContentFragment fragment;
        if (mFragments[position] == null) {
            try {
                fragment = mFragmentClasses.get(position).newInstance();
                mFragments[position] = fragment;
            } catch (Exception e) {
                Log.e("SRCC", "Error switching content fragment", e);
                return;
            }
        } else {
            fragment = mFragments[position];
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
        if (mToolbar != null)
            mToolbar.setTitle(fragment.getTitle());
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    private IContentFragment getCurrentFragment() {
        return mCurrentPosition >= 0 ? mFragments[mCurrentPosition] : null;
    }

    public ShilohRanch getService() {
        return mService;
    }

    @Override
    public void updateData() {
        updateCategories();
        updateEvents();
        updateNews();
        updateSermons();
        if (mSyncTasks[4] == null || mSyncTasks[4].getStatus() == AsyncTask.Status.FINISHED) {
            mSyncTasks[4] = new DeletionSyncTask(this, mService, null);
            mSyncTasks[4].execute();
        }

    }

    @Override
    public void updateCategories() {
        if (mSyncTasks[0] == null || mSyncTasks[0].getStatus() == AsyncTask.Status.FINISHED) {
            mSyncTasks[0] = new CategorySyncTask(this, mService, this);
            mSyncTasks[0].execute();
        }
    }

    @Override
    public void updateEvents() {
        if (mSyncTasks[1] == null || mSyncTasks[1].getStatus() == AsyncTask.Status.FINISHED) {
            mSyncTasks[1] = new EventSyncTask(this, mService, this);
            mSyncTasks[1].execute();
        }
    }

    @Override
    public void updateNews() {
        if (mSyncTasks[4] == null || mSyncTasks[4].getStatus() == AsyncTask.Status.FINISHED) {
            mSyncTasks[4] = new NewsSyncTask(this, mService, this);
            mSyncTasks[4].execute();

        }
    }

    @Override
    public void updateSermons() {
        if (mSyncTasks[3] == null || mSyncTasks[3].getStatus() == AsyncTask.Status.FINISHED) {
            mSyncTasks[3] = new SermonSyncTask(this, mService, this);
            mSyncTasks[3].execute();
        }

    }

    @Override
    public void onCompletion() {
        if (getCurrentFragment() != null)
            getCurrentFragment().refresh();
    }
}
