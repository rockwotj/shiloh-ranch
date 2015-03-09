package com.appspot.shiloh_ranch.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity implements INavigationDrawerCallbacks, IFragmentCallbacks {

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private List<Class<? extends IContentFragment>> mFragmentClasses;
    private int mCurrentPosition;
    private IContentFragment[] mFragments;
    private ShilohRanch mService;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position >= mFragmentClasses.size()) {
            String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=XURAEFVZV2CAJ";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            return;
        } else if (mCurrentPosition != -1) {
            getSupportFragmentManager().beginTransaction()
                    .remove(mFragments[mCurrentPosition])
                    .commit();
        }
        mCurrentPosition = position;
        IContentFragment fragment;
        if (mFragments[position] == null) {
            try {
                fragment = mFragmentClasses.get(position).newInstance();
                mFragments[position] = fragment;
            } catch (Exception e) {
                Log.e("SR", "Error switching content fragment", e);
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

    public ShilohRanch getService() {
        return mService;
    }
}
