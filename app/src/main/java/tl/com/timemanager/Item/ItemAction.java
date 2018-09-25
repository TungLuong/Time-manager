package tl.com.timemanager.Item;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

import javax.annotation.PropertyKey;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static tl.com.timemanager.Constant.TIME_MIN;

public class ItemAction extends RealmObject implements Comparable {

    // id của hoạt động
    @PrimaryKey
    private int id;


    // tiêu đề hoạt động
    private String title;
    // loại hoạt động
    private int action;
    // thông báo
    private boolean notification;
    // không làm phiền
    private boolean doNotDisturb;
    // đang đc chỉnh sửa thuộc tính
    private boolean isModifying = false;
    // thời gian thực hiện
    private int timeDoIt;
    //ngày thực hiện
    private int dayOfWeek;
    // giờ thực hiện
    private int hourOfDay = TIME_MIN;
    // tuần thực hiện
    private int weekOfYear;
    // năm thực hiện
    private int year;

    // hoạt động đã hoàn thành chưa
    private boolean isComplete = false;
    // hoạt động đã đến giờ thực hiện chưa
    private boolean done = false;

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

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


    // so sánh 2 hoạt động
    @Override
    public int compareTo(@NonNull Object o) {
        ItemAction itemAction = (ItemAction) o;
        int a = getHourOfDay();
        int b = itemAction.getHourOfDay();
        return a > b ? +1 : a < b ? -1 : 0;
    }
}
