package tl.com.timemanager.ActionStatistics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import tl.com.timemanager.Base.BaseFragment;
import tl.com.timemanager.CustomView.ActionStatisticsView;
import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;
import tl.com.timemanager.dialog.calendar.BaseCalendarDialog;
import tl.com.timemanager.dialog.calendar.CalendarActionStatisticsDialog;

@SuppressLint("ValidFragment")
public class ActionStatisticsFragment extends BaseFragment implements View.OnClickListener,BaseCalendarDialog.IDateChangedListener{
    private ActionStatisticsView statisticsView ;
    private TimeService service;
    private int countHour[] = {0,0,0,0,0};
    private int countHourComplete[] = {0,0,0,0,0};
    private TextView tvCountHour[] = new TextView[5];
    private int dayOfWeek;
    private int year;
    private int weekOfYear;
    private FloatingActionButton btOpenCalendar;

    @SuppressLint("ValidFragment")
    public ActionStatisticsFragment(TimeService timeService) {
        super();
        this.service = timeService;
        service.setActionsInCurrentWeek();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action_statistics,container,false);
        Calendar calendar = Calendar.getInstance();
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        year = calendar.get(Calendar.YEAR);
        initData();
        initView(view);
        return view;
    }


    private void initView(View view) {
        statisticsView = view.findViewById(R.id.action_statistics_view);
        statisticsView.setCountHour(countHour);
        statisticsView.initData();
        initTextView(view);
        btOpenCalendar = view.findViewById(R.id.fab_open_calendar);
        btOpenCalendar.setOnClickListener(this);

    }

    private void initTextView(View view) {
        tvCountHour[0] = view.findViewById(R.id.tv_action_done_1);
        tvCountHour[1] = view.findViewById(R.id.tv_action_done_2);
        tvCountHour[2] = view.findViewById(R.id.tv_action_done_3);
        tvCountHour[3] = view.findViewById(R.id.tv_action_done_4);
        tvCountHour[4] = view.findViewById(R.id.tv_action_done_5);

        for (int i =0;i<5;i++){
            tvCountHour[i].setText(countHourComplete[i] +" / "+countHour[i]);
        }
    }

    private void initData(){

        for (int i =0;i<5;i++){
            countHour[i] = 0;
        }
        List<ItemAction> actions = service.getActionsInDay(dayOfWeek);
        for (ItemAction action : actions) {
            int kindOfAction = action.getAction();
            countHour[kindOfAction] = countHour[kindOfAction] + action.getTimeDoIt();
            if(action.isComplete()){
                countHourComplete[kindOfAction] = countHourComplete[kindOfAction] + action.getTimeDoIt();
            }
        }
    }

    private void displayCalendarDialog() {
        CalendarActionStatisticsDialog dialog = new CalendarActionStatisticsDialog(getActivity());
        dialog.setIDateChangedListener(this);
        dialog.setDayOfWeek(dayOfWeek);
        dialog.setWeekOfYear(weekOfYear);
        dialog.setYear(year);
        dialog.initCalendar();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_open_calendar: displayCalendarDialog(); break;
        }
    }

    @Override
    public void setCurrentItemFragment(int day) {

    }

    @Override
    public void updateActionsInWeek(int dayOfWeek, int weekOfYear, int year) {
        service.updateActionsInWeek(weekOfYear,year);
        this.dayOfWeek = dayOfWeek;
        this.weekOfYear = weekOfYear;
        this.year = year;
    }

    @Override
    public void updateActionStatisticFragment(int day) {
        initData();
        for (int i =0;i<5;i++){
            tvCountHour[i].setText(countHourComplete[i] +" / "+countHour[i]);
        }
        statisticsView.setCountHour(countHour);
        statisticsView.invalidate();
    }
}
