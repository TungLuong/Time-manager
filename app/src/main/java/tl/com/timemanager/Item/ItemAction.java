package tl.com.timemanager.Item;

import android.support.annotation.NonNull;

import javax.annotation.PropertyKey;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static tl.com.timemanager.Constant.TIME_MIN;

public class ItemAction extends RealmObject implements Comparable {

    @PrimaryKey
    private int id;


    private String title;
    private int action;
    private boolean notification;
    private boolean doNotDisturb;
    private boolean isModifying = false;
    private int timeDoIt;
    private int dayOfWeek;
    private int hourOfDay = TIME_MIN;
    private int weekOfYear;
    private int year;

    public int getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ItemAction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
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
        int a = getHourOfDay();
        int b = itemAction.getHourOfDay();
        return a > b ? +1 : a < b ? -1 : 0;
    }

}
