package tl.com.timemanager.dialog.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import tl.com.timemanager.R;
import tl.com.timemanager.dialog.insert.BaseInsertDialog;

public class CalendarDialog extends BottomSheetDialog implements View.OnClickListener, CalendarView.OnDateChangeListener {

    private CalendarView calendarView;
    private ImageView ivClose;
    private IDateChangedListener iDateChangedListener;
    private int dayOfWeek;
    private int weekOfYear;
    private int year;
    public CalendarDialog(@NonNull Context context) {
        super(context,R.style.Theme_Design_Light_BottomSheetDialog);
        setContentView(R.layout.dialog_calender);
        initView();
    }

    public void setIDateChangedListener(IDateChangedListener iDateChangedListener) {
        this.iDateChangedListener = iDateChangedListener;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public void setYear(int year) {
        this.year = year;
    }

    private void initView() {
        calendarView= findViewById(R.id.calendar);
        ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(this);
        calendarView.setOnDateChangeListener(this);

    }

    public void initCalendar() {
        int day = dayOfWeek + 1;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        calendar.set(Calendar.DAY_OF_WEEK, day);
        long milliTime = calendar.getTimeInMillis();
        calendarView.setDate (milliTime, true, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close : dismiss(); break;
        }
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year,month,dayOfMonth);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfWeek = day - 1;
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        iDateChangedListener.setCurrentItemFragment(dayOfWeek);
        iDateChangedListener.updateActionsInWeek(dayOfWeek,weekOfYear,year);

    }

    public interface IDateChangedListener {
        void setCurrentItemFragment(int day);
        void updateActionsInWeek(int dayOfWeek, int weekOfYear, int year);
    }
}
