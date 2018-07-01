package tl.com.timemanager.DaysInWeek;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tl.com.timemanager.Adapter.DaysInWeekAdapter;
import tl.com.timemanager.Base.BaseFragment;
import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;

public class DaysInWeekFragment extends BaseFragment {

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

    }
}
