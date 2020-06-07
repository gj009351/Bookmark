package com.duke.bookmark.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.duke.bookmark.fragments.PrivateBookmarksFragment;
import com.duke.bookmark.fragments.PublicBookmarksFragment;

/**
 * Created by Matteo on 13/07/2015.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final static int NUM_PAGES = 2;

    private PrivateBookmarksFragment mPrivateBookmarksFragment;
    private PublicBookmarksFragment mPublicBookmarksFragment;

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PrivateBookmarksFragment();
            case 1:
                return new PublicBookmarksFragment();
            default:
                return null;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        switch (position) {
            case 0:
                mPrivateBookmarksFragment = (PrivateBookmarksFragment) createdFragment;
                break;
            case 1:
                mPublicBookmarksFragment = (PublicBookmarksFragment) createdFragment;
                break;
        }
        return createdFragment;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "My Bookmarks";
            case 1:
                return "Public Bookmarks";
            default:
                return null;
        }
    }

    public PrivateBookmarksFragment getPrivateBookmarksFragment() {
        return mPrivateBookmarksFragment;
    }

    public PublicBookmarksFragment getPublicBookmarksFragment() {
        return mPublicBookmarksFragment;
    }
}