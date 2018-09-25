package tl.com.timemanager.CustomView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.R;
import tl.com.timemanager.dialog.calendar.BaseCalendarDialog;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.COUNT_TIME;
import static tl.com.timemanager.Constant.FREE_TIME;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;

public class ActionStatisticsView extends View {

    private int countHour[] = {0,0,0,0,0};
    private int color[] = {0, 0, 0, 0, 0, 0};
    private int totalHour = COUNT_TIME;
    private Paint paint = new Paint();

    public ActionStatisticsView(Context context) {
        super(context);
    }

    public ActionStatisticsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionStatisticsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCountHour(int[] countHour) {
        this.countHour = countHour;
    }

    public void initData() {
        color[0] = ContextCompat.getColor(getContext(), R.color.colorFreeTime);
        color[1] = ContextCompat.getColor(getContext(), R.color.colorOutSideAction);
        color[2] = ContextCompat.getColor(getContext(), R.color.colorHomework);
        color[3] = ContextCompat.getColor(getContext(), R.color.colorEntertainment);
        color[4] = ContextCompat.getColor(getContext(), R.color.colorRelax);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorDefault));
        canvas.drawOval(0, 0, getWidth(), getHeight(), paint);
        int degree = 0;
        int startAngle = -90;
        for(int i =0 ;i < 5 ;i++) {
            paint.setColor(color[i]);
            degree = (int) (Double.valueOf(countHour[i]) / Double.valueOf(totalHour) * 360);
            RectF oval = new RectF();
            oval.set(0, 0, getWidth(), getHeight());
            canvas.drawArc(oval, startAngle, degree, true, paint);
            startAngle = startAngle + degree;
        }

    }
}
