package tl.com.timemanager.Item;

public class ItemData {
    private boolean isActive = false;
    private String title;
    private int day;
    private int time;
    private int flag;
    private int action;
    private boolean isNotification;
    private boolean isTurnOffMedia;
    private int colorId;
    private int timeDoIt;


    public ItemData(){

    }

    public ItemData(int day, int time) {
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
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }

    public boolean isTurnOffMedia() {
        return isTurnOffMedia;
    }

    public void setTurnOffMedia(boolean turnOffMedia) {
        isTurnOffMedia = turnOffMedia;
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
}
