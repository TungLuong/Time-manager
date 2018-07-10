package tl.com.timemanager.Service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import tl.com.timemanager.DataBase.Data;
import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.Item.ItemDataInTimeTable;
import tl.com.timemanager.MyBinder;

import static tl.com.timemanager.Constant.COUNT_DAY;
import static tl.com.timemanager.Constant.COUNT_TIME;
import static tl.com.timemanager.Constant.TIME_MAX;
import static tl.com.timemanager.Constant.TIME_MIN;

public class TimeService extends Service {
    String TAG = TimeService.class.getSimpleName();

    private List<ItemDataInTimeTable> itemDatas = new ArrayList<>();
    private  List<List<ItemAction>> actionsInWeek;
//    private RealmAsyncTask transaction;
    private Data data;
    @Override
    public void onCreate() {
        super.onCreate();
        data = new Data();
        initData();
    }

    public List<List<ItemAction>> getActionsInWeek() {
        return actionsInWeek;
    }

    public List<ItemAction> getActionsInDay(int day){
        return actionsInWeek.get(day);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initData() {
        List<ItemDataInTimeTable> list = data.getAllItemData();
        if( list.size() == 0 ) {
            for (int i = 0; i < COUNT_TIME; i++) {
                for (int j = 0; j < COUNT_DAY; j++) {
                    ItemDataInTimeTable itemData = new ItemDataInTimeTable(j,(i+TIME_MIN));
                    data.insertItemData(itemData);
                    Log.d(TAG,"time =    " + itemData.getHourOfDay());
                }
            }
        }
        list = data.getAllItemData();
        itemDatas.addAll(list);
        setActionsInCurrentWeek();
    }

    public void setActionsInCurrentWeek(){
        Calendar calendar = Calendar.getInstance();
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        updateActionsInWeek(weekOfYear,year);

    }

    public void setModifyForItemData(Boolean isModify, ItemDataInTimeTable item ){
        data.setModifyForItemData(isModify,item);
    }

    public void updateActionsInWeek(int weekOfYear, int year){
        List<ItemAction> actions = data.getActionsInWeek(weekOfYear,year);
        actionsInWeek = new ArrayList<>();
        for(int i = 0;i<COUNT_DAY;i++){
            actionsInWeek.add(new ArrayList<ItemAction>());
        }
        if (actions.size() != 0){
            for (ItemAction action : actions){
                int dayOfWeek = action.getDayOfWeek();
                actionsInWeek.get(dayOfWeek).add(action);
            }
            for(int i = 0;i<COUNT_DAY;i++){
                sortActionByTime(i);
            }
        }
    }

    public void insertItemAction(int dayOfWeek,ItemAction action){
        actionsInWeek.get(dayOfWeek).add(action);
        data.insertItemAction(action);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Binder binder = new MyBinder(this);
        return binder;
    }

    public int getCountItemData(){
        Log.d(TAG,"size.........."+ itemDatas.size()+"");
        if(itemDatas == null) return 0;
        else return itemDatas.size();
    }

    public ItemDataInTimeTable getItemDataInTimeTable(int position){
        return itemDatas.get(position);
    }

    public int getCountActionsInDay(int day){
//        if (weekAction == null || weekAction.getActionsInWeek() == null
//                || weekAction.getActionsInWeek().get(day) == null) return 0;
//        return weekAction.getActionsInWeek().get(day).size();
        if(actionsInWeek.get(day) == null) return 0;
        return actionsInWeek.get(day).size();
    }

    public ItemAction getItemAction(int day,int position){
        return actionsInWeek.get(day).get(position);
    }


    public void sortActionByTime(int day){
        Collections.sort(actionsInWeek.get(day));
    }

    public void deleteActionByPositionItemAction(int day, int posItemAction){
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
        for(int k =0; k < size;k++){
            ItemAction action = actions.get(k);
            if(action.getHourOfDay() == item.getHourOfDay() && action.getDayOfWeek() == item.getDayOfWeek()){
                deleteActionByPositionItemAction(dayOfWeek,k);
                break;
            }
        }

        int j =0;
        while (j < count && i < getCountItemData()){
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

    public int setNewTimeForAction(int day,int position){
        List<ItemAction> itemActions = actionsInWeek.get(day);
        List<ItemAction> actions = new ArrayList<>();
        actions.addAll(itemActions);
        int timeDoIt  = actions.get(position).getTimeDoIt();
        if (actions.size() > 0){
            int timeStart;
            int timeEnd;
            for(int i = position; i < actions.size() - 1;i++){
                ItemAction actionOne = actions.get(i);
                ItemAction actionTwo = actions.get(i+1);
                 timeStart = actionOne.getHourOfDay() + actionOne.getTimeDoIt();
                 timeEnd = actionTwo.getHourOfDay();
                if(timeEnd-timeStart >= timeDoIt){
                    return timeStart;
                }
            }
            ItemAction actionOne = actions.get(actions.size() - 1);
            timeStart = actionOne.getHourOfDay() + actionOne.getTimeDoIt();
            timeEnd = TIME_MAX;
            if(timeEnd - timeStart >= timeDoIt){
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
    public void readDB(){
        String output = "";
        for(ItemDataInTimeTable item : itemDatas){
            output = output +" ("+item.getHourOfDay()+"," +item.getDayOfWeek() + " )";
        }
        Log.d(TAG,"read DB :" + output);
    }

    public void updateItemData(ItemDataInTimeTable item) {
        data.updateItemData(item);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        realm.close();
//    }

//    public void onStop () {
//        if (transaction != null && !transaction.isCancelled()) {
//            transaction.cancel();
//        }
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        data.close();
    }

    public void setModifyForItemAction(boolean b, ItemAction item) {
        data.setModifyForItemAction(b,item);
    }

    public void updateItemAction(ItemAction action) {
        data.updateItemAction(action);
    }

    public void insertItemActionFromTimeTable(int dayOfWeek, ItemAction action) {
        if(checkValidInsert(dayOfWeek,action)) {
            Calendar calendar = Calendar.getInstance();
            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
            int year = calendar.get(Calendar.YEAR);
            action.setWeekOfYear(weekOfYear);
            action.setYear(year);
            insertItemAction(dayOfWeek, action);
        }
    }

    public void updateActionsInWeekFromTimeTable(){
        for(ItemDataInTimeTable itemData : itemDatas){
            if(itemData.isActive() && itemData.getFlag() == 0){
                ItemAction action = new ItemAction();
                action.setTitle(itemData.getTitle());
                action.setAction(itemData.getAction());
                action.setDayOfWeek(itemData.getDayOfWeek());
                action.setHourOfDay(itemData.getHourOfDay());
                action.setTimeDoIt(itemData.getTimeDoIt());
                action.setNotification(itemData.isNotification());
                action.setDoNotDisturb(itemData.isDoNotDisturb());
                int day = itemData.getDayOfWeek();
                insertItemActionFromTimeTable(day,action);
            }
        }
    }

    public boolean checkValidInsert(int day , ItemAction itemAction){
        List<ItemAction> actions = getActionsInDay(day);
        int timeStart = itemAction.getHourOfDay();
        int timeEnd = timeStart + itemAction.getTimeDoIt();
        boolean valid = true;
        if (actions.size() > 0) {
            for (ItemAction action : actions) {
                int start = action.getHourOfDay();
                int end = action.getHourOfDay() + action.getTimeDoIt();
                if(end <= timeStart || start >= timeEnd){

                }
                else {
                   return false;
                }
            }
        }
        return valid;
    }


}
