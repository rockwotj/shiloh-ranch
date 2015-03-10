package com.appspot.shiloh_ranch.fragments.news;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appspot.shiloh_ranch.api.model.Category;
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
        mDatabase = Database.getDatabase(context);
        mCategories = mDatabase.getAllCategories();
    }

    @Override
    public Fragment getItem(int position) {
        // This is okay if we parcel it... But we need to think about maybe doing callbacks instead to share data?
        // Or maybe we just parcel the category?
        String categoryKey = null;
        if (position != 0)
            categoryKey = mCategories.get(position - 1).getEntityKey();
        return CategoryFragment.createFragment(categoryKey);
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
        mCategories = mDatabase.getAllCategories();
        notifyDataSetChanged();
    }
}
