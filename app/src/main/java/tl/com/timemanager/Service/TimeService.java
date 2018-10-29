package tl.com.timemanager.Service;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import tl.com.timemanager.DataBase.Data;
import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.Item.ItemDataInTimeTable;
import tl.com.timemanager.MainActivity;
import tl.com.timemanager.MyBinder;
import tl.com.timemanager.R;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.CHANNEL_ID_NOTIFICATION;
import static tl.com.timemanager.Constant.CHANNEL_ID_RUNNING_IN_BACKGROUND;
import static tl.com.timemanager.Constant.COUNT_DAY;
import static tl.com.timemanager.Constant.COUNT_TIME;
import static tl.com.timemanager.Constant.DELAY_MINUTE;
import static tl.com.timemanager.Constant.FREE_TIME;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;
import static tl.com.timemanager.Constant.START_ALARM;
import static tl.com.timemanager.Constant.TIME_MAX;
import static tl.com.timemanager.Constant.TIME_MIN;

public class TimeService extends Service {
    private static final int NOTIFICATION_ACTION_ID = 3;
    String TAG = TimeService.class.getSimpleName();
    private static final int NOTIFICATION_RUNNING_ID = 4;
    private NotificationManager notificationManager;
    //    private Notification notification;
    // quản lí thông báo
    //private NotificationManager notificationManager;
    // danh sách item data
    private List<ItemDataInTimeTable> itemDatas = new ArrayList<>();
    // danh sách các hoạt động
    private List<List<ItemAction>> actionsInWeek;
    // danh sách hoạt động hiện tại
    private List<ItemAction> currAction;
    // quản lí báo thức
    private AlarmManager alarmManager;
    private int currDay = 0;
    private int weekOfYear = 0;
    private int year = 0;

    private MyBroadcastReceiver broadcast;
    private RemoteViews remoteViews;
    //    private RealmAsyncTask transaction;
    // dữ liệu
    private Data data;
    private IUpdateUI iUpdateUI;

    //private boolean beenRepeated;
    // biến đêm
    private PendingIntent pIntent;
    private PendingIntent pBntStartAction;
    private PendingIntent pBntEndAction;
    private Intent intent;
    private int idCurrAction = -1;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//            builder.setPriority(NotificationCompat.PRIORITY_MIN);
//            builder.setChannelId(CHANNEL_ID_RUNNING_IN_BACKGROUND);
//            startForeground(0, builder.build());
//            RemoteViews remoteViews_two = new RemoteViews(getPackageName(), R.layout.notification_action);
//           NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setSmallIcon(R.drawable.icon_notification)
//                .setChannelId(CHANNEL_ID_NOTIFICATION)
//                .setCustomBigContentView(remoteViews_two);
//            startForeground(0, builder.build());
            // showNotification(new ItemAction());
            createNotificationRunningInBackground();
        }
        data = new Data();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        registerBroadcast();
        initIntent();
        createNotificationManager();
        initData();
        setAlarm((Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) % 24, 0);
        checkNotificationAndDND();
    }

    private void createNotificationRunningInBackground() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_running_background_black_24dp)
                .setContentText("Ứng dụng chạy ngầm")
                .setChannelId(CHANNEL_ID_RUNNING_IN_BACKGROUND);
//                        .setCustomBigContentView(remoteViews);
        startForeground(NOTIFICATION_RUNNING_ID, builder.build());
    }

    private void initIntent() {
        Intent intent = new Intent(this, TimeService.class);
        intent.setAction(START_ALARM);
        pIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        //notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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

        //Calendar calendar = Calendar.getInstance();
        // checkActionsDone();
        setCurrAction();
        //setAlarm((calendar.get(Calendar.HOUR_OF_DAY)) % 24, 0);
    }

    /**
     * xét các danh sách theo tuần hiện tại
     */
    public void setActionsInCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        updateActionsInWeek(weekOfYear, year);

    }

    /**
     * xét thuộc tính modify cho item data
     *
     * @param isModify
     * @param item
     */
    public void setModifyForItemData(Boolean isModify, ItemDataInTimeTable item) {
        data.setModifyForItemData(isModify, item);
    }

    /**
     * cập nhật danh sách các hoạt động theo tuần bất kì
     *
     * @param weekOfYear tuần cập nhật
     * @param year       năm
     */
    public void updateActionsInWeek(int weekOfYear, int year) {
        this.weekOfYear = weekOfYear;
        this.year = year;
        List<ItemAction> actions = data.getActionsInWeek(weekOfYear, year);
        actions.size();
        actionsInWeek = new ArrayList<>();
        for (int i = 0; i < COUNT_DAY; i++) {
            actionsInWeek.add(new ArrayList<ItemAction>());
        }
        if (actions.size() != 0) {
            for (ItemAction action : actions) {
                if (action.getTitle() != null) {
                    int dayOfWeek = action.getDayOfWeek();
                    actionsInWeek.get(dayOfWeek).add(action);
                } else {
                    data.deleteItemAction(action);
                }
            }
            for (int i = 0; i < COUNT_DAY; i++) {
                sortActionByTime(i);
            }
        }
        checkActionsDone();
    }

    /**
     * thêm hoạt động vào lưu trữ
     *
     * @param dayOfWeek
     * @param action
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void insertItemAction(int dayOfWeek, ItemAction action) {
//        actionsInWeek.get(dayOfWeek).add(action);
//        int size =  actionsInWeek.get(dayOfWeek).size();
//        data.insertItemAction( actionsInWeek.get(dayOfWeek).get(size-1))
        int id = data.insertItemAction(action);
        actionsInWeek.get(dayOfWeek).add(data.getActionFromDBById(id));
        setCurrAction();
        checkNotificationAndDND();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
////                builder.setPriority(NotificationCompat.PRIORITY_MIN);
////                startForeground(0,builder.build());
//                showNotification(new ItemAction());
//                stopForeground(true);
//            }
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case START_ALARM:
                        checkActionsDone();
                        setAlarm((Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) % 24, 0);
                        checkNotificationAndDND();
                        if (iUpdateUI != null) {
                            try {
                                iUpdateUI.updateUI();
                                // iUpdateUI.setCurrentItemFragment(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
                            } catch (Exception e) {
                            }
                        }
                        break;
//                case "LATTER_ACTION":
//                    notificationManager.cancel(NOTIFICATION_ACTION_ID);
//                    count = 0;
//                    turnOffDoNotDisturb();
//                    break;
//                case "START_ACTION":
//                    int id = intent.getIntExtra("ID_ACTION", -1);
//                    ItemAction itemAction = data.getActionFromDBById(id);
//                    if (itemAction != null) {
//                        if (!itemAction.isComplete()) {
//                            data.setCompleteForAction(itemAction);
//                            iUpdateUI.updateUI();
//                        }
//                    }
//                    notificationManager.cancel(NOTIFICATION_ACTION_ID);
//                    break;
                    default:
                        break;
                }
            }
//        if (Constant.START_ALARM.equals(intent.getAction())) {
//            showNotificationStart(1);
//        }
//        if (Constant.NOTIFICATION_BEGIN.equals(intent.getAction())) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                notificationManager.cancel(Constant.FOREGROUND_NOTIFICATION_FLAG);
//                stopForeground(true);
//                stopSelf();
//            }
//            showProgressingRunTime(0);
//        }
//        if (Constant.NOTIFICATION_COMPLETE.equals(intent.getAction())) {
//            notificationManager.cancel(Constant.FOREGROUND_NOTIFICATION_COMPLETE);
//            stopForeground(true);
//            stopSelf();
//        }
//        if (Constant.NOTIFICATION_LATTER.equals(intent.getAction())) {
//            notificationManager.cancel(Constant.FOREGROUND_NOTIFICATION_COMPLETE);
//            stopForeground(true);
//            stopSelf();
//            showNotificationCancel();
//        }
//        if (Constant.NOTIFICATION_OKE.equals(intent.getAction())) {
//            notificationManager.cancel(Constant.FOREGROUND_NOTIFICATION_OKE);
//            stopForeground(true);
//            stopSelf();
//        }
        }
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void checkNotificationAndDND() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if (currDay != day) {
            currDay = day;
            if (currDay == 0) {
                updateActionsInWeekFromTimeTable(currDay);
                if(iUpdateUI != null){
                    Calendar cal = Calendar.getInstance();
                    iUpdateUI.updateActionsInWeek(currDay, cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.YEAR));
                }
            }
            setCurrAction();
            if(iUpdateUI != null) {
                iUpdateUI.setCurrentItemFragment(currDay);
            }
        }

        if (currAction != null && currAction.size() > 0) {
            for (int i = 0; i < currAction.size(); i++) {
                ItemAction action = currAction.get(i);
                Calendar cal = Calendar.getInstance();
                int currHour = cal.get(Calendar.HOUR_OF_DAY);
                if (currHour >= action.getHourOfDay() && currHour < action.getHourOfDay() + action.getTimeDoIt()) {
                    if (action.isDoNotDisturb()) {
                        turnOnDoNotDisturb();
                    } else {
                        turnOffDoNotDisturb();
                    }
                    int currMinute = cal.get(Calendar.MINUTE);
                    if (currHour == action.getHourOfDay() && currMinute <= DELAY_MINUTE && action.isNotification()) {
                        showNotification(action);
                    } else {
                        notificationManager.cancel(NOTIFICATION_ACTION_ID);
                    }
                    break;
                }else {
                    turnOffDoNotDisturb();
                }
            }

        }
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


    /**
     * sắp xếp hoạt động trong ngày theo thời gian
     *
     * @param day
     */
    public void sortActionByTime(int day) {
        Collections.sort(actionsInWeek.get(day));
    }

    /**
     * xoá hoạt động trong ngày day ở vị trí pos
     *
     * @param day
     * @param posItemAction
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void deleteActionByPositionItemAction(int day, int posItemAction) {
        ItemAction action = actionsInWeek.get(day).remove(posItemAction);
        ItemAction itemAction = data.getActionFromDBById(action.getId());
        data.deleteItemAction(itemAction);
        setCurrAction();
        checkNotificationAndDND();
    }

    /**
     * xoá item data trong bảng thời gian biểu
     *
     * @param posItemData
     */
    public void deleteItemDataFromTimeTable(int posItemData) {

        int i = posItemData - getItemDataInTimeTable(posItemData).getFlag() * COUNT_DAY;
        ItemDataInTimeTable item = getItemDataInTimeTable(i);
        int count = item.getTimeDoIt();

        // delete item action
//        int dayOfWeek = getItemDataInTimeTable(i).getDayOfWeek();
//        List<ItemAction> actions = actionsInWeek.get(dayOfWeek);
//        int size = actions.size();
//        for (int k = 0; k < size; k++) {
//            ItemAction action = actions.get(k);
//            if (action.getHourOfDay() == item.getHourOfDay() && action.getDayOfWeek() == item.getDayOfWeek()) {
//                deleteActionByPositionItemAction(dayOfWeek, k);
//                break;
//            }
//        }

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

    /**
     * cập nhật lại thời gian biểu
     */
    public void updateTimeTable() {
        data.updateTimeTable(itemDatas);
    }

    /**
     * tạo ra thời gian bắt đầu phù hợp cho hoạt động
     *
     * @param day
     * @param timeDoIt
     * @return
     */
    public int setTimeForAction(int day, int timeDoIt) {
        List<ItemAction> itemActions = actionsInWeek.get(day);
        List<ItemAction> actions = new ArrayList<>();
        actions.addAll(itemActions);
        if (actions.size() > 0) {
            int timeStart;
            int timeEnd;
            for (int i = 0; i < actions.size() - 1; i++) {
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
        return TIME_MAX;
    }

//    public int setNewTimeForAction(int oldTimeStart, int timeDoIt) {
//        List<ItemAction> actions = new ArrayList<>();
//        actions.addAll(currAction);
//        int newTimeStart = oldTimeStart + 1;
//        int newTimeEnd = newTimeStart + timeDoIt;
//        if (actions.size() > 0) {
//            for (ItemAction action : actions) {
//                int start = action.getHourOfDay();
//                int end = action.getHourOfDay() + action.getTimeDoIt();
//                if (end <= newTimeStart || start >= newTimeEnd) {
//
//                } else {
//                    return -1;
//                }
//            }
//        }
//        return newTimeStart;
//    }
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
//    public void readDB() {
//        String output = "";
//        for (ItemDataInTimeTable item : itemDatas) {
//            output = output + " (" + item.getHourOfDay() + "," + item.getDayOfWeek() + " )";
//        }
//        Log.d(TAG, "read DB :" + output);
//    }

    /**
     * cập nhật itemdata
     *
     * @param item
     */
    public void updateItemData(ItemDataInTimeTable item) {
        data.updateItemData(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
        unRegisterBroadcast();
        cancelAlarm();

    }

//    public void onStop () {
//        if (transaction != null && !transaction.isCancelled()) {
//            transaction.cancel();
//        }
//    }


    /**
     * xét thuộc tính modify
     *
     * @param b
     * @param item
     */
    public void setModifyForItemAction(boolean b, ItemAction item) {
        data.setModifyForItemAction(b, item);
    }

    /**
     * cập nhật hoạt động
     *
     * @param action
     */
    public void updateItemAction(ItemAction action) {
        data.updateItemAction(action);
        checkActionDone(action);
    }


    /**
     * thêm hoạt động từ bảng thời gian biểu
     *
     * @param dayOfWeek
     * @param action
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void insertItemActionFromTimeTable(int dayOfWeek, ItemAction action) {
        if (checkValidInsert(dayOfWeek, action)) {
//            Calendar calendar = Calendar.getInstance();
//            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
//            int year = calendar.get(Calendar.YEAR);
            action.setWeekOfYear(weekOfYear);
            action.setYear(year);
            insertItemAction(dayOfWeek, action);
        }
    }

    /**
     * cập nhật các hoạt động trong ngày từ thời gian biểu
     *
     * @param dayOfWeek
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void updateActionsInWeekFromTimeTable(int dayOfWeek) {
        for (int i = dayOfWeek; i < COUNT_DAY; i++) {
            for (int j = 0; j < COUNT_TIME; j++) {
                int index = i + COUNT_DAY * j;
                ItemDataInTimeTable itemData = itemDatas.get(index);
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
            sortActionByTime(i);
        }
        setCurrAction();
    }

    /**
     * kiểm tra xem hoạt động thêm vào có thoả mãn phù hợp không
     *
     * @param day
     * @param itemAction
     * @return
     */
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


    /**
     * Đặt hẹn giờ
     *
     * @param currHour
     * @param currMinute
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setAlarm(int currHour, int currMinute) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, currHour + 1);
        calendar.set(Calendar.MINUTE, currMinute);
        calendar.set(Calendar.SECOND, 0);
        long timeInMillis = calendar.getTimeInMillis();
        Intent intent = new Intent(this, TimeService.class);
        intent.setAction(START_ALARM);
        pIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }


    }

    /**
     * tắt chế độ không làm phiền
     */
    private void turnOffDoNotDisturb() {
        changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_ALL);
    }

    /**
     * bật chế độ k làm phiền
     */
    private void turnOnDoNotDisturb() {
        changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_NONE);
    }

//    private void broadCastActionDoneAndComplete(long timeInMillis) {
//
////        Intent intent = new Intent("CHECK_ACTION_DONE_AND_COMPLETE");
////        PendingIntent pendingIntent = PendingIntent.getBroadcast(
////                this.getApplicationContext(), 234324243, intent, 0);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
//
//    }

//    private void checkNotification(long timeInMillis) {
//        Intent intent = new Intent(this, TimeService.class);
//        intent.setAction(START_ALARM);
//        PendingIntent pIntent = PendingIntent.getService(this, 0, intent, 0);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pIntent);
//        }
//    }

    public void cancelAlarm() {
        alarmManager.cancel(pIntent);
    }

//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    public void showNotificationStart(int currentPosition) {
////        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_action);
////        contentView.setTextViewText(R.id.tv_set_time_start, timeStart + "");
////        contentView.setTextViewText(R.id.tv_set_time_finish, timeFinish + "");
////        contentView.setTextViewText(R.id.tv_title, title);
////        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
////        builder.setSmallIcon(R.drawable.icon_notification);
////        builder.setContent(contentView);
////        Notification notification = builder.build();
////        notification.flags=Notification.FLAG_AUTO_CANCEL;
////        notificationManager.notify(Constant.ID_NOTIFICATION_START,notification);
//        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_action);
//        views.setTextViewText(R.id.tv_title, itemDatas.get(currentPosition).getTitle());
//        views.setTextViewText(R.id.tv_set_time_start, itemDatas.get(currentPosition).getFlag() + " giờ " );
//        views.setTextViewText(R.id.tv_set_time_finish, itemDatas.get(currentPosition).getTimeDoIt() + " giờ ");
//        views.setImageViewBitmap(R.id.iv_notification, getBitMap(itemDatas.get(currentPosition).getAction()));
//
//        Intent intentStart = new Intent(this, TimeService.class);
//        intentStart.setAction(Constant.NOTIFICATION_BEGIN);
//        PendingIntent pendingIntentStart = PendingIntent.getService(this, 0, intentStart, 0);
//        views.setOnClickPendingIntent(R.id.btn_start, pendingIntentStart);
//
//        Intent intentLatter = new Intent(this, TimeService.class);
//        intentLatter.setAction(Constant.NOTIFICATION_LATTER);
//        PendingIntent pendingIntentLatter = PendingIntent.getService(this, 0, intentLatter, 0);
//        views.setOnClickPendingIntent(R.id.btn_latter, pendingIntentLatter);
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.setAction(Constant.MAIN_ACTION);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification = new Notification.Builder(this).build();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            notification.bigContentView = views;
//        } else {
//            notification.contentView = views;
//
//        }
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notification.icon = R.drawable.icon_notification;
//        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_INSISTENT;
//        notification.contentIntent = pendingIntent;
//        startForeground(Constant.FOREGROUND_NOTIFICATION_FLAG, notification);
//    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    private void showProgressingRunTime(int currentPosition) {
//        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_start);
//        views.setTextViewText(R.id.tv_title, itemDatas.get(currentPosition).getTitle());
//        views.setTextViewText(R.id.tv_time_finish, "Time Finish:" + itemDatas.get(currentPosition).getFlag());
//        views.setImageViewBitmap(R.id.icon_notification, getBitMap(itemDatas.get(currentPosition).getAction()));
//        Intent intentComplete = new Intent(this, TimeService.class);
//        intentComplete.setAction(Constant.NOTIFICATION_COMPLETE);
//        PendingIntent pendingIntentStart = PendingIntent.getService(this, 0, intentComplete, 0);
//        views.setOnClickPendingIntent(R.id.btn_complete, pendingIntentStart);
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.setAction(Constant.MAIN_ACTION);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification = new Notification.Builder(this).build();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            notification.bigContentView = views;
//        } else {
//            notification.contentView = views;
//
//        }
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notification.icon = R.drawable.icon_notification;
//        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_INSISTENT;
//        notification.contentIntent = pendingIntent;
//        startForeground(Constant.FOREGROUND_NOTIFICATION_COMPLETE, notification);
//    }
//
//    @SuppressLint("NewApi")
//    private void showNotificationCancel() {
//        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_cancel);
//        Intent intent = new Intent(this, TimeService.class);
//        intent.setAction(Constant.NOTIFICATION_OKE);
//        PendingIntent pending = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        views.setOnClickPendingIntent(R.id.btn_ok, pending);
//        Intent intent1 = new Intent(this, MainActivity.class);
//        intent1.setAction(Constant.MAIN_ACTION);
//        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification = new Notification.Builder(this).build();
//        notification.icon = R.drawable.icon_notification;
//        notification.contentView = views;
//        notification.contentIntent = pendingIntent;
//        startForeground(Constant.FOREGROUND_NOTIFICATION_OKE, notification);
//    }


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
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action != null) {
                ItemAction itemAction = data.getActionFromDBById(idCurrAction);
                switch (action) {
                    case Intent.ACTION_TIME_CHANGED:
                        cancelAlarm();
                        setAlarm((Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) % 24, 0);
                        checkNotificationAndDND();
                        checkActionsDone();
                        break;
                    case Intent.ACTION_TIMEZONE_CHANGED:
                        cancelAlarm();
                        setAlarm((Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) % 24, 0);
                        checkNotificationAndDND();
                        break;
                    case "LATTER_ACTION":
                        if (itemAction != null) {
                            if (itemAction.isComplete()) {
                                data.setCompleteForAction(itemAction);
                                if (iUpdateUI != null) {
                                    iUpdateUI.updateUI();
                                }
                            }
                        }
                        notificationManager.cancel(NOTIFICATION_ACTION_ID);
//                        stopForeground(true);
//                        stopSelf(NOTIFICATION_ACTION_ID);
                        turnOffDoNotDisturb();
                        break;
                    case "START_ACTION":
//                        int id = intent.getIntExtra("ID_ACTION", -1);
                        if (itemAction != null) {
                            if (!itemAction.isComplete()) {
                                data.setCompleteForAction(itemAction);
                                if (iUpdateUI != null) {
                                    iUpdateUI.updateUI();
                                }
                            }
                        }
                        notificationManager.cancel(NOTIFICATION_ACTION_ID);
                        // stopForeground(true);
//                        stopSelf(NOTIFICATION_ACTION_ID);
                        break;
//                case "SET_NEW_TIME_FOR_ACTION":
//                    if(itemAction != null) {
//                        int timeStart = intent.getIntExtra("TIME_START",0);
//                        int timeDoIt = intent.getIntExtra("TIME_DO_IT",0);
//                        int newTime = timeStart;
//                        while (timeStart + timeDoIt <= TIME_MAX + 1) {
//                            newTime = setNewTimeForAction(timeStart, timeDoIt);
//                            if(newTime != -1 ) break;
//                            timeStart++;
//                        }
//                        if(newTime == timeStart){}
//                        else {
//                            notificationManager.cancel(NOTIFICATION_ACTION_ID);
//                            showNotificationSetNewAction(itemAction, newTime, timeDoIt);
//                        }
//                    }
//
//                    break;
                    default:
                        break;
                }
            }
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    private void showNotificationSetNewAction(ItemAction action, int timeStart, int timeDoIt) {
//        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_set_new_action);
//        remoteViews.setTextViewText(R.id.tv_title_notifi, action.getTitle());
//        remoteViews.setTextViewText(R.id.tv_set_time_start, String.valueOf(timeStart) + " giờ");
//        remoteViews.setTextViewText(R.id.tv_set_time_finish, String.valueOf(timeStart+timeDoIt)+" giờ");
//        Bitmap bm = getBitMap(action.getAction());
//        remoteViews.setImageViewBitmap(R.id.iv_notification, bm);
//
//        Intent bntOK = new Intent("OK_SET_NEW_ACTION");
//        bntOK.putExtra("ID_ACTION",action.getId());
//        bntOK.putExtra("TIME_START",timeStart);
//        bntOK.putExtra("TIME_DO_IT",timeDoIt);
//
//        PendingIntent pBntOK = PendingIntent.getBroadcast(this, 123, bntOK, 0);
//        remoteViews.setOnClickPendingIntent(R.id.btn_ok, pBntOK);
//
//        Intent btnCancel = new Intent("CANCEL_SET_NEW_ACTION");
//        PendingIntent pBtnCancel = PendingIntent.getBroadcast(this, 456, btnCancel, 0);
//        remoteViews.setOnClickPendingIntent(R.id.btn_cancel, pBtnCancel);
//
//        Intent btnNewAction = new Intent("SET_NEW_TIME_FOR_ACTION");
//        btnNewAction.putExtra("ID_ACTION",action.getId());
//        btnNewAction.putExtra("TIME_START",timeStart);
//        btnNewAction.putExtra("TIME_DO_IT",timeDoIt);
//        PendingIntent pBtnNewAction = PendingIntent.getBroadcast(this, 789, btnNewAction, 0);
//        remoteViews.setOnClickPendingIntent(R.id.btn_new_time_for_action, pBtnNewAction);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setSmallIcon(R.drawable.icon_notification)
//                .setContentText("Thông báo")
//                .setContentText("Thông báo")
//                .setAutoCancel(false)
//                .setChannelId("my_channel_01")
//                .setCustomBigContentView(remoteViews);
//        notificationManager.notify(NOTIFICATION_NEW_ACTION_ID, builder.build());
//
//    }

    /**
     * kiểm tra xem các hoạt động đã qua thời gian hiện tại chưa
     */
    public void checkActionsDone() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        checkActionsDone(dayOfWeek);
    }

    public void checkActionsDone(int dayOfWeek) {
        for (int i = dayOfWeek; i < COUNT_DAY; i++) {
            List<ItemAction> actionsInDay = actionsInWeek.get(i);
            for (ItemAction action : actionsInDay) {
                if (action.isDone() == false
                        || action.getHourOfDay() >= Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        || action.getWeekOfYear() >= (Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) - 1)) {
                    checkActionDone(action);
                }
            }

        }
    }

    /**
     * kiểm tra hoạt động đã qua thời gian hiện tại chưa
     *
     * @param action
     */
    public void checkActionDone(ItemAction action) {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
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
        data.setDoneForItemAction(action, done);
    }


    /**
     * đăng kí broadcast
     */
    private void registerBroadcast() {
        broadcast = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //      intentFilter.addAction("CHECK_ACTION_DONE_AND_COMPLETE");
        intentFilter.addAction("START_ACTION");
        intentFilter.addAction("LATTER_ACTION");
        //  intentFilter.addAction("SET_NEW_TIME_FOR_ACTION");
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(broadcast, intentFilter);
    }

    /**
     * huỷ đăng ký
     */
    private void unRegisterBroadcast() {
        unregisterReceiver(broadcast);
    }

    public interface IUpdateUI {
        void updateUI();

        void setCurrentItemFragment(int dayOfWeek);

        void updateActionsInWeek(int dayOfWeek, int weekOfYear, int year);
    }

    /**
     * xét hoạt động hiện tại
     */
    public void setCurrAction() {
        currAction = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
//        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        List<ItemAction> list = data.getActionsInDay(dayOfWeek, weekOfYear, year);
        if (list == null) return;
        List<ItemAction> actions = new ArrayList<>();
        actions.addAll(list);
        Collections.sort(actions);
        currAction = new ArrayList<>();
        for (ItemAction item : actions) {
            currAction.add(item);
        }
    }

    /**
     * hiển thị thông báo
     *
     * @param action
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(ItemAction action) {

        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_action);
        remoteViews.setTextViewText(R.id.tv_title_notifi, action.getTitle());
        remoteViews.setTextViewText(R.id.tv_set_time_start, String.valueOf(action.getHourOfDay()) + " giờ");
        remoteViews.setTextViewText(R.id.tv_set_time_finish, String.valueOf(action.getHourOfDay() + action.getTimeDoIt()) + " giờ");
        Bitmap bm = getBitMap(action.getAction());
        remoteViews.setImageViewBitmap(R.id.iv_notification, bm);

        Intent bntStartAction = new Intent("START_ACTION");
        idCurrAction = action.getId();
        pBntStartAction = PendingIntent.getBroadcast(this, 111, bntStartAction, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_start, pBntStartAction);

        Intent bntEndAction = new Intent("LATTER_ACTION");
        pBntEndAction = PendingIntent.getBroadcast(this, 222, bntEndAction, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_latter, pBntEndAction);


//        Intent btnNewAction = new Intent("SET_NEW_TIME_FOR_ACTION");
//        btnNewAction.putExtra("ID_ACTION",action.getId());
//        btnNewAction.putExtra("TIME_START",action.getHourOfDay());
//        btnNewAction.putExtra("TIME_DO_IT",action.getTimeDoIt());
//        PendingIntent pBtnNewAction = PendingIntent.getBroadcast(this, 333, btnNewAction, 0);
//        remoteViews.setOnClickPendingIntent(R.id.btn_new_time_for_action, pBtnNewAction);

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
//        intent.putExtra("POTISION", currPosition);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//          bt_intent.setClass(this,)

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setChannelId(CHANNEL_ID_NOTIFICATION)
                .setContentIntent(pendingIntent)
                .setCustomBigContentView(remoteViews);

        //.setContentIntent(pendingIntent);
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
//        builder.setSmallIcon(R.drawable.ic_nav_favorites);
//        builder.setContentTitle(musicItem.getTitle());
//        builder.setContentText(musicItem.getArtist());
        notificationManager.notify(NOTIFICATION_ACTION_ID, builder.build());
//        startForeground(NOTIFICATION_ACTION_ID, builder.build());

    }

    /**
     * tạo notification manager
     */
    private void createNotificationManager() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * thay đổi trạng thái
     *
     * @param interruptionFilter
     */
    protected void changeInterruptionFiler(int interruptionFilter) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted()) {
                notificationManager.setInterruptionFilter(interruptionFilter);
            } else {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }


}
