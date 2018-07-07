package tl.com.timemanager.DaysInWeek;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import tl.com.timemanager.Adapter.DaysInWeekAdapter;
import tl.com.timemanager.Base.BaseFragment;
import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;
import tl.com.timemanager.dialog.calendar.CalendarDialog;

public class DaysInWeekFragment extends BaseFragment implements CalendarDialog.IDateChangedListener {

    private TabLayout tab;
    private ViewPager pager ;
    private DaysInWeekAdapter adapter;
    private TimeService timeService;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_days_in_week,container,false);
        initView(view);
        return view;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    private void initView(View view) {
        tab = view.findViewById(R.id.tab);
        pager = view.findViewById(R.id.viewpager);
        adapter = new DaysInWeekAdapter(getChildFragmentManager(),timeService);
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        setCurrentFragment(day);
    }

    @Override
    public void setCurrentItemFragment(int dayOfWeek){
        setCurrentFragment(dayOfWeek);
    }

    @Override
    public void updateActionsInWeek(int weekOfYear, int year) {
        timeService.updateActionsInWeek(weekOfYear,year);
    }

    public void setCurrentFragment(int day) {
        int position = 0;
        switch (day){
            case 1 : position = 6; break;
            case 2 : position = 0; break;
            case 3 : position = 1; break;
            case 4 : position = 2; break;
            case 5 : position = 3; break;
            case 6 : position = 4; break;
            case 7 : position = 5; break;
        }
        pager.setCurrentItem(position);
    }
}
