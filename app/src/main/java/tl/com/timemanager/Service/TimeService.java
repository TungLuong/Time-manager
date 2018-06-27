package tl.com.timemanager.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import tl.com.timemanager.Item.ItemData;
import tl.com.timemanager.MyBinder;

import static tl.com.timemanager.Constant.COUNT_DAY;
import static tl.com.timemanager.Constant.COUNT_TIME;

public class TimeService extends Service {
    String TAG = TimeService.class.getSimpleName();

    private List<ItemData> dataList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    private void initData() {
        for(int i =0;i<COUNT_TIME;i++){
            for(int j =0;j<COUNT_DAY;j++){
                dataList.add(new ItemData(j,i));
            }

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
}
