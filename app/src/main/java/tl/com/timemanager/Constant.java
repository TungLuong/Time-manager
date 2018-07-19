package tl.com.timemanager;

public interface Constant {
    int NO_ACTION = 0;
    int OUTSIDE_ACTION = 1;
    int AT_HOME_ACTION = 2;
    int AMUSING_ACTION= 3;
    int RELAX_ACTION = 4;

    int TIME_MIN = 5;
    int TIME_MAX = 22;
    int COUNT_DAY = 7;
    int COUNT_TIME = TIME_MAX - TIME_MIN + 1;
    String NOTIFICATION_BEGIN="action.begin";
    String NOTIFICATION_LATTER="action.latter";
    String NOTIFICATION_OKE="action.oke";
    String NOTIFICATION_COMPLETE="action.complete";
    String MAIN_ACTION="action_main";
    int FOREGROUND_NOTIFICATION_FLAG = 101;
    int FOREGROUND_NOTIFICATION_COMPLETE=100;
    int FOREGROUND_NOTIFICATION_OKE=99;
    String START_ALARM="action.start.alarm";

}
