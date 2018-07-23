package tl.com.timemanager.dialog.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import tl.com.timemanager.Base.BaseFragment;

public class CalendarActionInDayDialog extends BaseCalendarDialog {

    public CalendarActionInDayDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        super.onSelectedDayChange(view, year, month, dayOfMonth);
        Calendar calendar = new GregorianCalendar();
        calendar.set(year,month,dayOfMonth);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        dayOfWeek = day - 1;
        iDateChangedListener.setCurrentItemFragment(dayOfWeek);
        if(weekOfYear != calendar.get(Calendar.WEEK_OF_YEAR)) {
            weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
            iDateChangedListener.updateActionsInWeek(dayOfWeek, weekOfYear, year);
        }
    }
}
