package com.appspot.shiloh_ranch.fragments.news;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appspot.shiloh_ranch.api.model.Category;
import com.appspot.shiloh_ranch.api.model.Post;
import com.appspot.shiloh_ranch.database.Database;

import java.util.List;

/**
 * Created by rockwotj on 3/8/2015.
 */
public class CategoryAdapter extends FragmentPagerAdapter {
    private final Database mDatabase;
    private List<Category> mCategories;

    public CategoryAdapter(FragmentManager fm, Context context) {
        super(fm);
        mDatabase = new Database(context);
        mDatabase.open();
        mCategories = mDatabase.getAllCategories();
        mDatabase.close();
    }

    @Override
    public Fragment getItem(int position) {
        List<Post> posts = null;
        mDatabase.open();
        if (position == 0)
            posts = mDatabase.getAllPosts();
        else
            posts = mDatabase.getAllPosts(mCategories.get(position - 1).getEntityKey());
        mDatabase.close();
        return new PostFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All".toUpperCase();
            default:
                return mCategories.get(position - 1).getTitle().toUpperCase();
        }
    }

    @Override
    public int getCount() {
        return mCategories.size() + 1;
    }

    public void refresh() {
        mDatabase.open();
        mCategories = mDatabase.getAllCategories();
        mDatabase.close();
        notifyDataSetChanged();
    }
}
