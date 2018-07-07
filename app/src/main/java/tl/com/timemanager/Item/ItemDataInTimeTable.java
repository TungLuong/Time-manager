package tl.com.timemanager.Item;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ItemDataInTimeTable extends RealmObject{

    @PrimaryKey
    private int id;


    private boolean isActive = false;
    private boolean isModifying = false;
    private String title;
    private int flag;
    private int action;
    private boolean notification;
    private boolean doNotDisturb;
    private int timeDoIt;
    private int dayOfWeek;
    private int hourOfDay;

    public ItemDataInTimeTable() {
    }

    public ItemDataInTimeTable(int dayOfWeek, int hourOfDay) {
        this.dayOfWeek = dayOfWeek;
        this.hourOfDay = hourOfDay;
    }


    public int getTimeDoIt() {
        return timeDoIt;
    }

    public void setTimeDoIt(int timeDoIt) {
        this.timeDoIt = timeDoIt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
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


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public boolean isModifying() {
        return isModifying;
    }

    public void setModifying(boolean modifying) {
        isModifying = modifying;
    }

    public void setId(int id) {
        this.id = id;
    }
}
