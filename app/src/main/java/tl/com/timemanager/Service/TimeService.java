package tl.com.timemanager.Service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import tl.com.timemanager.Constant;
import tl.com.timemanager.DataBase.Data;
import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.Item.ItemDataInTimeTable;
import tl.com.timemanager.MainActivity;
import tl.com.timemanager.MyBinder;
import tl.com.timemanager.R;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.COUNT_DAY;
import static tl.com.timemanager.Constant.COUNT_TIME;
import static tl.com.timemanager.Constant.FREE_TIME;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;
import static tl.com.timemanager.Constant.TIME_MAX;
import static tl.com.timemanager.Constant.TIME_MIN;

public class TimeService extends Service {
    String TAG = TimeService.class.getSimpleName();
    private NotificationManager manager;
    private Notification notification;
    private List<ItemDataInTimeTable> itemDatas = new ArrayList<>();
    private List<List<ItemAction>> actionsInWeek;
    private AlarmManager alarmManager;
    private MyBroadcastReceiver broadcast;
    //    private RealmAsyncTask transaction;
    private Data data;
    private IUpdateUI iUpdateUI;

    @Override
    public void onCreate() {
        super.onCreate();
        data = new Data();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        registerBroadcast();
        initData();
    }

    public void setiUpdateUI(IUpdateUI iUpdateUI) {
        this.iUpdateUI = iUpdateUI;
    }

    public List<List<ItemAction>> getActionsInWeek() {
        return actionsInWeek;
    }

    public List<ItemAction> getActionsInDay(int day) {
        return actionsInWeek.get(day);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initData() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        List<ItemDataInTimeTable> list = data.getAllItemData();
        if (list.size() == 0) {
            for (int i = 0; i < COUNT_TIME; i++) {
                for (int j = 0; j < COUNT_DAY; j++) {
                    ItemDataInTimeTable itemData = new ItemDataInTimeTable(j, (i + TIME_MIN));
                    data.insertItemData(itemData);
                    Log.d(TAG, "time =    " + itemData.getHourOfDay());
                }
            }
        }
        list = data.getAllItemData();
        itemDatas.addAll(list);
        setActionsInCurrentWeek();

        Calendar calendar = Calendar.getInstance();
        checkActionDoneAndComplete();
        setAlarm((calendar.get(Calendar.HOUR_OF_DAY) + 1) % 24, 0);
    }

    public void setActionsInCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        updateActionsInWeek(weekOfYear, year);

    }

    public void setModifyForItemData(Boolean isModify, ItemDataInTimeTable item) {
        data.setModifyForItemData(isModify, item);
    }

    public void updateActionsInWeek(int weekOfYear, int year) {
        List<ItemAction> actions = data.getActionsInWeek(weekOfYear, year);
        actionsInWeek = new ArrayList<>();
        for (int i = 0; i < COUNT_DAY; i++) {
            actionsInWeek.add(new ArrayList<ItemAction>());
        }
        if (actions.size() != 0) {
            for (ItemAction action : actions) {
                int dayOfWeek = action.getDayOfWeek();
                actionsInWeek.get(dayOfWeek).add(action);
            }
            for (int i = 0; i < COUNT_DAY; i++) {
                sortActionByTime(i);
            }
        }
        checkActionDoneAndComplete();
    }

    public void insertItemAction(int dayOfWeek, ItemAction action) {
        actionsInWeek.get(dayOfWeek).add(action);
        data.insertItemAction(action);
        checkActionDoneAndComplete();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Constant.START_ALARM.equals(intent.getAction())) {
            showNotificationStart(1);
        }
        if (Constant.NOTIFICATION_BEGIN.equals(intent.getAction())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                manager.cancel(Constant.FOREGROUND_NOTIFICATION_FLAG);
                stopForeground(true);
                stopSelf();
            }
            showProgressingRunTime(0);
        }
        if (Constant.NOTIFICATION_COMPLETE.equals(intent.getAction())) {
            manager.cancel(Constant.FOREGROUND_NOTIFICATION_COMPLETE);
            stopForeground(true);
            stopSelf();
        }
        if (Constant.NOTIFICATION_LATTER.equals(intent.getAction())) {
            manager.cancel(Constant.FOREGROUND_NOTIFICATION_COMPLETE);
            stopForeground(true);
            stopSelf();
            showNotificationOk();
        }
        if (Constant.NOTIFICATION_OKE.equals(intent.getAction())) {
            manager.cancel(Constant.FOREGROUND_NOTIFICATION_OKE);
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Binder binder = new MyBinder(this);
        return binder;
    }

    public int getCountItemData() {
        Log.d(TAG, "size.........." + itemDatas.size() + "");
        if (itemDatas == null) return 0;
        else return itemDatas.size();
    }

    public ItemDataInTimeTable getItemDataInTimeTable(int position) {
        return itemDatas.get(position);
    }

    public int getCountActionsInDay(int day) {
//        if (weekAction == null || weekAction.getActionsInWeek() == null
//                || weekAction.getActionsInWeek().get(day) == null) return 0;
//        return weekAction.getActionsInWeek().get(day).size();
        if (actionsInWeek.get(day) == null) return 0;
        return actionsInWeek.get(day).size();
    }

    public ItemAction getItemAction(int day, int position) {
        return actionsInWeek.get(day).get(position);
    }


    public void sortActionByTime(int day) {
        Collections.sort(actionsInWeek.get(day));
    }

    public void deleteActionByPositionItemAction(int day, int posItemAction) {
        ItemAction action = actionsInWeek.get(day).remove(posItemAction);
        data.deleteItemAction(action);
    }

    public void deleteActionByPositionItemData(int posItemData) {

        int i = posItemData - getItemDataInTimeTable(posItemData).getFlag() * COUNT_DAY;
        ItemDataInTimeTable item = getItemDataInTimeTable(i);
        int count = item.getTimeDoIt();

        int dayOfWeek = getItemDataInTimeTable(i).getDayOfWeek();
        List<ItemAction> actions = actionsInWeek.get(dayOfWeek);
        int size = actions.size();
        for (int k = 0; k < size; k++) {
            ItemAction action = actions.get(k);
            if (action.getHourOfDay() == item.getHourOfDay() && action.getDayOfWeek() == item.getDayOfWeek()) {
                deleteActionByPositionItemAction(dayOfWeek, k);
                break;
            }
        }

        int j = 0;
        while (j < count && i < getCountItemData()) {
            item = getItemDataInTimeTable(i);
            ItemDataInTimeTable newItem = new ItemDataInTimeTable();
            newItem.setId(item.getId());
            newItem.setHourOfDay(item.getHourOfDay());
            newItem.setDayOfWeek(item.getDayOfWeek());
            updateItemData(newItem);
            i = i + COUNT_DAY;
            j++;
        }
    }

    public void updateTimeTable() {
        data.updateTimeTable(itemDatas);
    }

    public int setNewTimeForAction(int day, int position) {
        List<ItemAction> itemActions = actionsInWeek.get(day);
        List<ItemAction> actions = new ArrayList<>();
        actions.addAll(itemActions);
        int timeDoIt = actions.get(position).getTimeDoIt();
        if (actions.size() > 0) {
            int timeStart;
            int timeEnd;
            for (int i = position; i < actions.size() - 1; i++) {
                ItemAction actionOne = actions.get(i);
                ItemAction actionTwo = actions.get(i + 1);
                timeStart = actionOne.getHourOfDay() + actionOne.getTimeDoIt();
                timeEnd = actionTwo.getHourOfDay();
                if (timeEnd - timeStart >= timeDoIt) {
                    return timeStart;
                }
            }
            ItemAction actionOne = actions.get(actions.size() - 1);
            timeStart = actionOne.getHourOfDay() + actionOne.getTimeDoIt();
            timeEnd = TIME_MAX;
            if (timeEnd - timeStart >= timeDoIt) {
                return timeStart;
            }
        }
        return -1;
    }
//
//    public void writeToDB(){
//        realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                for(ItemDataInTimeTable item : itemDatas) {
//                    ItemDataInTimeTable itemData = realm.createObject(ItemDataInTimeTable.class);
//                    itemData.setDayOfWeek(item.getDayOfWeek());
//                    itemData.setHourOfDay(item.getHourOfDay());
//                }
//            }
//        });
//    }

    //    private void modifyDB(){
//        itemDatas.get(0).setHourOfDay(0);
//        itemDatas.get(0).setDayOfWeek(0);
//        realm.beginTransaction();
//        realm.insertOrUpdate(itemDatas);
//        realm.cancelTransaction();
//    }
    public void readDB() {
        String output = "";
        for (ItemDataInTimeTable item : itemDatas) {
            output = output + " (" + item.getHourOfDay() + "," + item.getDayOfWeek() + " )";
        }
        Log.d(TAG, "read DB :" + output);
    }

    public void updateItemData(ItemDataInTimeTable item) {
        data.updateItemData(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
        cancelAlarm();

    }

//    public void onStop () {
//        if (transaction != null && !transaction.isCancelled()) {
//            transaction.cancel();
//        }
//    }


    public void setModifyForItemAction(boolean b, ItemAction item) {
        data.setModifyForItemAction(b, item);
    }

    public void updateItemAction(ItemAction action) {
        data.updateItemAction(action);
    }

    public void insertItemActionFromTimeTable(int dayOfWeek, ItemAction action) {
        if (checkValidInsert(dayOfWeek, action)) {
            Calendar calendar = Calendar.getInstance();
            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
            int year = calendar.get(Calendar.YEAR);
            action.setWeekOfYear(weekOfYear);
            action.setYear(year);
            insertItemAction(dayOfWeek, action);
        }
    }

    public void updateActionsInWeekFromTimeTable() {
        for (ItemDataInTimeTable itemData : itemDatas) {
            if (itemData.isActive() && itemData.getFlag() == 0) {
                ItemAction action = new ItemAction();
                action.setTitle(itemData.getTitle());
                action.setAction(itemData.getAction());
                action.setDayOfWeek(itemData.getDayOfWeek());
                action.setHourOfDay(itemData.getHourOfDay());
                action.setTimeDoIt(itemData.getTimeDoIt());
                action.setNotification(itemData.isNotification());
                action.setDoNotDisturb(itemData.isDoNotDisturb());
                int day = itemData.getDayOfWeek();
                insertItemActionFromTimeTable(day, action);
            }
        }
    }

    public boolean checkValidInsert(int day, ItemAction itemAction) {
        List<ItemAction> actions = getActionsInDay(day);
        int timeStart = itemAction.getHourOfDay();
        int timeEnd = timeStart + itemAction.getTimeDoIt();
        boolean valid = true;
        if (actions.size() > 0) {
            for (ItemAction action : actions) {
                int start = action.getHourOfDay();
                int end = action.getHourOfDay() + action.getTimeDoIt();
                if (end <= timeStart || start >= timeEnd) {

                } else {
                    return false;
                }
            }
        }
        return valid;
    }


    public void setAlarm(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        long x = calendar.getTimeInMillis()/1000;
        checkNotification(calendar.getTimeInMillis());
        broadCastActionDoneAndComplete(calendar.getTimeInMillis());
    }

    private void broadCastActionDoneAndComplete(long timeInMillis) {

        Intent intent = new Intent("CHECK_ACTION_DONE_AND_COMPLETE");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

    }

    private void checkNotification(long timeInMillis) {
        Intent intent = new Intent(this, TimeService.class);
        intent.setAction(Constant.START_ALARM);
        PendingIntent pIntent = PendingIntent.getService(this, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pIntent);
        }
    }

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showNotificationStart(int currentPosition) {
//        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_flag);
//        contentView.setTextViewText(R.id.tv_set_time_start, timeStart + "");
//        contentView.setTextViewText(R.id.tv_set_time_finish, timeFinish + "");
//        contentView.setTextViewText(R.id.tv_title, title);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setSmallIcon(R.drawable.icon_notification);
//        builder.setContent(contentView);
//        Notification notification = builder.build();
//        notification.flags=Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(Constant.ID_NOTIFICATION_START,notification);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_flag);
        views.setTextViewText(R.id.tv_title, itemDatas.get(currentPosition).getTitle());
        views.setTextViewText(R.id.tv_set_time_start, itemDatas.get(currentPosition).getFlag() + "");
        views.setTextViewText(R.id.tv_set_time_finish, itemDatas.get(currentPosition).getTimeDoIt() + "");
        views.setImageViewBitmap(R.id.icon_notification, getBitMap(itemDatas.get(currentPosition).getAction()));

        Intent intentStart = new Intent(this, TimeService.class);
        intentStart.setAction(Constant.NOTIFICATION_BEGIN);
        PendingIntent pendingIntentStart = PendingIntent.getService(this, 0, intentStart, 0);
        views.setOnClickPendingIntent(R.id.btn_start, pendingIntentStart);

        Intent intentLatter = new Intent(this, TimeService.class);
        intentLatter.setAction(Constant.NOTIFICATION_LATTER);
        PendingIntent pendingIntentLatter = PendingIntent.getService(this, 0, intentLatter, 0);
        views.setOnClickPendingIntent(R.id.btn_latter, pendingIntentLatter);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constant.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new Notification.Builder(this).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.bigContentView = views;
        } else {
            notification.contentView = views;

        }
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.icon = R.drawable.icon_notification;
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_INSISTENT;
        notification.contentIntent = pendingIntent;
        startForeground(Constant.FOREGROUND_NOTIFICATION_FLAG, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showProgressingRunTime(int currentPosition) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_start);
        views.setTextViewText(R.id.tv_title, itemDatas.get(currentPosition).getTitle());
        views.setTextViewText(R.id.tv_time_finish, "Time Finish:" + itemDatas.get(currentPosition).getFlag());
        views.setImageViewBitmap(R.id.icon_notification, getBitMap(itemDatas.get(currentPosition).getAction()));
        Intent intentComplete = new Intent(this, TimeService.class);
        intentComplete.setAction(Constant.NOTIFICATION_COMPLETE);
        PendingIntent pendingIntentStart = PendingIntent.getService(this, 0, intentComplete, 0);
        views.setOnClickPendingIntent(R.id.btn_complete, pendingIntentStart);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constant.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new Notification.Builder(this).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.bigContentView = views;
        } else {
            notification.contentView = views;

        }
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.icon = R.drawable.icon_notification;
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_INSISTENT;
        notification.contentIntent = pendingIntent;
        startForeground(Constant.FOREGROUND_NOTIFICATION_COMPLETE, notification);
    }

    @SuppressLint("NewApi")
    private void showNotificationOk() {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_cancel);
        Intent intent = new Intent(this, TimeService.class);
        intent.setAction(Constant.NOTIFICATION_OKE);
        PendingIntent pending = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_ok, pending);
        Intent intent1 = new Intent(this, MainActivity.class);
        intent1.setAction(Constant.MAIN_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new Notification.Builder(this).build();
        notification.icon = R.drawable.icon_notification;
        notification.contentView = views;
        notification.contentIntent = pendingIntent;
        startForeground(Constant.FOREGROUND_NOTIFICATION_OKE, notification);
    }


    private Bitmap getBitMap(int action) {
        Bitmap bm = null;
        switch (action) {
            case FREE_TIME:
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.free_time);
                break;
            case OUTSIDE_ACTION:
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.school);
                break;
            case AT_HOME_ACTION:
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.homework);
                break;
            case AMUSING_ACTION:
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.giaitri);
                break;
            case RELAX_ACTION:
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.sleep);
                break;
            default:
                break;
        }
        return bm;
    }

    public void setCompleteForAction(int dayOfWeek, int adapterPosition) {
        data.setCompleteForAction(actionsInWeek.get(dayOfWeek).get(adapterPosition));
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG,"co vao onRecevie");
            String action = intent.getAction();
            switch (action){
                case "CHECK_ACTION_DONE_AND_COMPLETE" :
                    checkActionDoneAndComplete();
                    break;
            }
            Calendar calendar = Calendar.getInstance();
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            setAlarm((hourOfDay + 1) % 24, 0);
             iUpdateUI.updateUI();
        }
    }

    private void checkActionDoneAndComplete() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) -1 ;
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);

        for (int i = 0; i < COUNT_DAY; i++) {
            List<ItemAction> actionsInDay = actionsInWeek.get(i);
            for (ItemAction action : actionsInDay) {
                boolean done;
                if (year <= action.getYear()) {
                    if (year == action.getYear()) {
                        if (weekOfYear <= action.getWeekOfYear()) {
                            if (weekOfYear == action.getWeekOfYear()) {
                                if (dayOfWeek <= action.getDayOfWeek()) {
                                    if (dayOfWeek == action.getDayOfWeek()) {
                                        if (hourOfDay < action.getHourOfDay()) {
                                            done = false;
                                        } else {
                                            done = true;
                                        }
                                    } else done = false;
                                } else {
                                    done = true;
                                }
                            } else done = false;
                        } else {
                            done = true;
                        }
                    } else done = false;
                } else {
                    done = true;
                }
                data.setDoneForItemAction(action,done);
            }
        }
    }

    private void registerBroadcast() {
        broadcast = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("CHECK_ACTION_DONE_AND_COMPLETE");
        registerReceiver(broadcast, intentFilter);
    }

    private void unRegisterBroadcast() {
        unregisterReceiver(broadcast);
    }

    public interface IUpdateUI {
        void updateUI();
    }


}
