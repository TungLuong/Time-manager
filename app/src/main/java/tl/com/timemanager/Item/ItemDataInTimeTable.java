package tl.com.timemanager.Item;

public class ItemDataInTimeTable {
    private boolean isActive = false;
    private boolean isModifying = false;
    private String title;
    private int day;
    private int time;
    private int flag;
    private int action;
    private boolean notification;
    private boolean doNotDisturb;
    private int colorId;
    private int timeDoIt;


    public ItemDataInTimeTable(){

    }

    public ItemDataInTimeTable(int day, int time) {
        this.day = day;
        this.time = time;
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

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public boolean isModifying() {
        return isModifying;
    }

    public void setModifying(boolean modifying) {
        isModifying = modifying;
    }
}
