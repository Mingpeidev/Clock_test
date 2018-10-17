package com.mao.remind_test2.Main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mingpeidev on 2018/10/16.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter{
    private List<Fragment> list=new ArrayList<Fragment>();

    public MyFragmentAdapter(FragmentManager fm,List<Fragment> list) {
        super(fm);
        this.list=list;
    }
    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
