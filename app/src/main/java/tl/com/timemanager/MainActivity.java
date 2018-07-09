package tl.com.timemanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.util.List;

import tl.com.timemanager.ActionsInDay.ActionsInDayFragment;
import tl.com.timemanager.Base.BaseActivity;
import tl.com.timemanager.DaysInWeek.DaysInWeekFragment;
import tl.com.timemanager.Service.TimeService;
import tl.com.timemanager.TimeTable.TimeTableFragment;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName() ;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle ;
    private ActionBar actionBar;
    private ServiceConnection conn;
    private TimeService timeService;
    private Animation anim;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService();
        connectedService();
        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout
                , R.string.open, R.string.close);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        drawerToggle.syncState();
        NavigationView navigationView = findViewById(R.id.nv_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        anim = AnimationUtils.loadAnimation(this,R.anim.exit_right_to_left);
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
                openDaysInWeekFragment();
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

    public void openDaysInWeekFragment(){
        DaysInWeekFragment fragment = new DaysInWeekFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exit_left_to_right
        );
        transaction.replace(R.id.content,fragment,DaysInWeekFragment.class.getName());
        fragment.setTimeService(timeService);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openTimeTableFragment(){
        TimeTableFragment fragment = new TimeTableFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exit_left_to_right
        );
        transaction.replace(R.id.content,fragment,ActionsInDayFragment.class.getName());
        fragment.setTimeService(timeService);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       // mDrawerLayout.setAnimation(anim);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        boolean fragmentIsVisible = false;
        switch (item.getItemId()){
            case R.id.it_actions_in_day:
                if(fragments != null){
                    for(Fragment fragment : fragments){
                        if(fragment != null && fragment instanceof DaysInWeekFragment && fragment.isVisible()){
                            fragmentIsVisible = true;
                            break;
                        }
                    }
                }
                if(!fragmentIsVisible){
                    openDaysInWeekFragment();
                    mDrawerLayout.closeDrawer(Gravity.LEFT, false);
                }
                else mDrawerLayout.closeDrawer(Gravity.LEFT,true);
                return true;
            case R.id.it_time_table:
                if(fragments != null){
                    for(Fragment fragment : fragments){
                        if(fragment != null && fragment instanceof TimeTableFragment && fragment.isVisible()){
                            fragmentIsVisible = true;
                            break;
                        }
                    }
                }
                if(!fragmentIsVisible) {
                    openTimeTableFragment();
                    mDrawerLayout.closeDrawer(Gravity.LEFT, false);
                }
                else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT, true);
                }
                return true;
            case R.id.it_statistics:
                return true;
        }
        return false;
    }
}
