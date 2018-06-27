package tl.com.timemanager;

import android.os.Binder;

import tl.com.timemanager.Service.TimeService;

public class MyBinder extends Binder {
    private TimeService timeService;
    public MyBinder(TimeService timeService) {
        this.timeService = timeService;
    }

    public TimeService getTimeService() {
        return timeService;
    }
}
