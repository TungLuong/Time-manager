package tl.com.timemanager.DaysInWeek;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import tl.com.timemanager.ActionsInDay.ActionsInDayFragment;
import tl.com.timemanager.Adapter.DaysInWeekAdapter;
import tl.com.timemanager.Base.BaseFragment;
import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;
import tl.com.timemanager.dialog.calendar.BaseCalendarDialog;

public class DaysInWeekFragment extends BaseFragment implements BaseCalendarDialog.IDateChangedListener {

    private static final String TITLE_WEEK_OF_YEAR = "Hoạt động trong tuần " ;
    private TabLayout tab;
    private ViewPager pager ;
    private DaysInWeekAdapter adapter;
    private TimeService timeService;
    private TextView tvWeekOfYear;
    private int weekOfYear;
    private int year;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_days_in_week,container,false);
        initView(view);
        return view;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
        this.timeService.updateActionsInWeekFromTimeTable();
    }

    private void initView(View view) {


        tvWeekOfYear = view.findViewById(R.id.tv_weekOfYear);
        tab = view.findViewById(R.id.tab);
        pager = view.findViewById(R.id.viewpager);

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        year = calendar.get(Calendar.YEAR);

        tvWeekOfYear.setText(TITLE_WEEK_OF_YEAR + weekOfYear);


        adapter = new DaysInWeekAdapter(getChildFragmentManager(),timeService,weekOfYear,year);
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);
        setCurrentFragment(dayOfWeek);
    }

    @Override
    public void setCurrentItemFragment(int dayOfWeek){
        setCurrentFragment(dayOfWeek);
    }

    @Override
    public void updateActionsInWeek(int dayOfWeek, int weekOfYear, int year) {
        timeService.updateActionsInWeek(weekOfYear,year);
        tvWeekOfYear.setText(TITLE_WEEK_OF_YEAR + weekOfYear);
        this.year = year;
        this.weekOfYear = weekOfYear;
        adapter.setWeekOfYear(weekOfYear);
        adapter.setYear(year);
        changedDateInChildFragment();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateActionStatisticFragment(int day) {

    }

    private void changedDateInChildFragment() {

       List<Fragment> fragments =  getChildFragmentManager().getFragments();
       if(fragments != null) {
           for (Fragment fragment : fragments) {
               ((ActionsInDayFragment)fragment).setYear(year);
               ((ActionsInDayFragment)fragment).setWeekOfYear(weekOfYear);
               ((ActionsInDayFragment)fragment).changedActionItem();
           }
       }
    }

    public void setCurrentFragment(int dayOfWeek) {
        pager.setCurrentItem(dayOfWeek);
    }

}
