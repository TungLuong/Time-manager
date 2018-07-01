package tl.com.timemanager.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.Item.ItemData;
import tl.com.timemanager.MyBinder;

import static tl.com.timemanager.Constant.COUNT_DAY;
import static tl.com.timemanager.Constant.COUNT_TIME;
import static tl.com.timemanager.Constant.TIME_MIN;

public class TimeService extends Service {
    String TAG = TimeService.class.getSimpleName();

    private List<ItemData> dataList = new ArrayList<>();
    private List<List<ItemAction>> actionsInDays;
    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    public List<List<ItemAction>> getActionsInDays() {
        return actionsInDays;
    }

    private void initData() {
        for(int i =0;i<COUNT_TIME;i++){
            for(int j =0;j<COUNT_DAY;j++){
                dataList.add(new ItemData(j,i + TIME_MIN));
            }
        }
        actionsInDays = new ArrayList<>();
        for(int i = 0;i<COUNT_DAY;i++){
            actionsInDays.add(new ArrayList<>());
        }
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

    public int getCount(){
        Log.d(TAG,"size.........."+dataList.size()+"");
        if(dataList == null) return 0;
        else return dataList.size();
    }

    public ItemData getData(int position){
        return dataList.get(position);
    }

    public void resetClickInsert(int position){

    }



    public void sortActionByTime(int day){
        Collections.sort(actionsInDays.get(day));
    }

    public void deleteActionByIdItemAction(int day,int idItemData){
        actionsInDays.get(day).remove(idItemData);
    }

    public void deleteAction(int idItemData) {

        int i = idItemData - getData(idItemData).getFlag() * COUNT_DAY;
        ItemData item = getData(i);
        int count = item.getTimeDoIt();

        List<ItemAction> actions = getActionsInDays().get(getData(i).getDay());
        for(ItemAction action : actions){
            if(action.getTime() == item.getTime() && action.getDay() == item.getDay()){
                actions.remove(action);
                break;
            }
        }

        int j =0;
        while (j < count && i < getCount()){
            item = getData(i);
            item.setActive(false);
            item.setAction(0);
            item.setNotification(false);
            item.setDoNotDisturb(false);
            item.setTitle("");
            item.setFlag(0);
            item.setTimeDoIt(0);
            i = i + COUNT_DAY;
            j++;
        }

    }
}
