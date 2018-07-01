package tl.com.timemanager.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import tl.com.timemanager.ActionsInDay.ActionsInDayFragment;
import tl.com.timemanager.Service.TimeService;

public class DaysInWeekAdapter extends FragmentPagerAdapter {

    private TimeService timeService;
    public DaysInWeekAdapter(FragmentManager fm,TimeService timeService) {
        super(fm);
        this.timeService = timeService;
    }

    @Override
    public Fragment getItem(int position) {
        ActionsInDayFragment fragment = new ActionsInDayFragment(timeService,position);
        return fragment;
    }

    @Override
    public int getCount() {
        if(timeService == null) return 0;
        return timeService.getActionsInDays().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 6) return "Chủ nhật";
        return "Thứ " +(position + 2);
    }
}
