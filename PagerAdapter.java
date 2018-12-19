package com.example.chirag.librarybooklocator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Chirag on 30-Jul-17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public PagerAdapter(FragmentManager fm, int NumberOfTabs)
    {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch(position)
        {

            case 0:
                Tab1_bookinfo tab1Bookinfo = new Tab1_bookinfo();
                return tab1Bookinfo;
            case 1:
                Tab2_map tab2Map = new Tab2_map();
                return tab2Map;
            case 2:
                Tab3_comment tab3Comment = new Tab3_comment();
                return tab3Comment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
