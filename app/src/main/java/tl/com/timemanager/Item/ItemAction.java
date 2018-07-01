package tl.com.timemanager.Item;

import android.support.annotation.NonNull;

import static tl.com.timemanager.Constant.TIME_MIN;

public class ItemAction implements Comparable{
    private String title;
    private int day;
    private int time = TIME_MIN;
    private int action;
    private boolean notification;
    private boolean doNotDisturb;
    private boolean isModifying = false;
    private int timeDoIt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isDoNotDisturb() {
        return doNotDisturb;
    }

    public void setDoNotDisturb(boolean doNotDisturb) {
        this.doNotDisturb = doNotDisturb;
    }

    public int getTimeDoIt() {
        return timeDoIt;
    }

    public void setTimeDoIt(int timeDoIt) {
        this.timeDoIt = timeDoIt;
    }

    public boolean isModifying() {
        return isModifying;
    }

    public void setModifying(boolean modifying) {
        isModifying = modifying;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        ItemAction itemAction = (ItemAction) o;
        int a = getTime();
        int b = itemAction.getTime();
        return a > b ? +1 : a < b ? -1 : 0;
    }
}
