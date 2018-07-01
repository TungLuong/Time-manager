package tl.com.timemanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import tl.com.timemanager.ActionsInDay.ActionsInDayFragment;
import tl.com.timemanager.Base.BaseActivity;
import tl.com.timemanager.DaysInWeek.DaysInWeekFragment;
import tl.com.timemanager.Service.TimeService;
import tl.com.timemanager.TimeTable.TimeTableFragment;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName() ;

    private ServiceConnection conn;
    private TimeService timeService;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService();
        connectedService();
    }

    private void startService() {
        Intent intent = new Intent();
        intent.setClass(this, TimeService.class);
        startService(intent);
    }

    private void connectedService() {
        conn = new ServiceConnection() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MyBinder myBinder = (MyBinder) service;
                timeService = myBinder.getTimeService();
                addTimeTableFragment();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG,"Error");
            }
        };
        Intent intent = new Intent();
        intent.setClass(this, TimeService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

//    public void addActionsInDayFragment(){
//        ActionsInDayFragment fragment = new ActionsInDayFragment();
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.content,fragment,ActionsInDayFragment.class.getName());
//        Toast.makeText(this,"AAAAAAAAAAA",Toast.LENGTH_SHORT).show();
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

    public void addDaysInWeekFragment(){
        DaysInWeekFragment fragment = new DaysInWeekFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content,fragment,DaysInWeekFragment.class.getName());
        Toast.makeText(this,"AAAAAAAAAAA",Toast.LENGTH_SHORT).show();
        fragment.setTimeService(timeService);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void addTimeTableFragment(){
        TimeTableFragment fragment = new TimeTableFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content,fragment,ActionsInDayFragment.class.getName());
        fragment.setTimeService(timeService);
        transaction.commit();
    }
}
