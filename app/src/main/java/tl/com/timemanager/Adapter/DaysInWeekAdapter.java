package tl.com.timemanager.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import tl.com.timemanager.ActionsInDay.ActionsInDayFragment;
import tl.com.timemanager.Service.TimeService;

public class DaysInWeekAdapter extends FragmentPagerAdapter {

    private TimeService timeService;
    private int weekOfYear;
    private int year;
    public DaysInWeekAdapter(FragmentManager fm,TimeService timeService,int weekOfYear,int year) {
        super(fm);
        this.timeService = timeService;
        this.weekOfYear = weekOfYear;
        this.year = year;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public Fragment getItem(int position) {
        ActionsInDayFragment fragment = new ActionsInDayFragment(timeService,position,weekOfYear,year);
        return fragment;
    }

    @Override
    public int getCount() {
        if(timeService == null) return 0;
        return timeService.getActionsInWeek().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0) return "CN";
        return "T " +(position + 1);
    }
}
