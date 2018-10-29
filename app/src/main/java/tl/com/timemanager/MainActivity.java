package tl.com.timemanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import tl.com.timemanager.ActionStatistics.ActionStatisticsFragment;
import tl.com.timemanager.ActionsInDay.ActionsInDayFragment;
import tl.com.timemanager.Base.BaseActivity;
import tl.com.timemanager.Base.BaseFragment;
import tl.com.timemanager.DaysInWeek.DaysInWeekFragment;
import tl.com.timemanager.Service.TimeService;
import tl.com.timemanager.TimeTable.TimeTableFragment;
import tl.com.timemanager.TimeTable.TimeTableTwoFragment;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private ActionBar actionBar;
    private ServiceConnection conn;
    private TimeService timeService;
    private Animation anim;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm realm = Realm.getDefaultInstance(); // opens "myrealm.realm"
        try {
            setContentView(R.layout.activity_main);
            startService();
            connectedService();
            init();
        } finally {
            realm.close();
        }
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout
                , R.string.open, R.string.close);
        actionBar = getSupportActionBar();
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
        anim = AnimationUtils.loadAnimation(this, R.anim.exit_right_to_left);
    }

    private void startService() {
        Intent intent = new Intent();
        intent.setClass(this, TimeService.class);
        ContextCompat.startForegroundService(this,intent);
//        startService(intent);
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
                Log.d(TAG, "Error");
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

    public void openDaysInWeekFragment() {
        DaysInWeekFragment fragment = new DaysInWeekFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exit_left_to_right
        );
        transaction.replace(R.id.content, fragment, DaysInWeekFragment.class.getName());
        fragment.setTimeService(timeService);
        transaction.commit();
    }

    public void openTimeTableFragment() {
        TimeTableFragment fragment = new TimeTableFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exit_left_to_right
        );
        transaction.replace(R.id.content, fragment, TimeTableFragment.class.getName());
        fragment.setTimeService(timeService);
        transaction.commit();
    }

    public void openActionStatisticsFragment() {
        ActionStatisticsFragment fragment = new ActionStatisticsFragment(timeService);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exit_left_to_right
        );
        transaction.replace(R.id.content, fragment, ActionStatisticsFragment.class.getName());
        transaction.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // mDrawerLayout.setAnimation(anim);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        boolean fragmentIsVisible = false;
        switch (item.getItemId()) {
            case R.id.it_actions_in_day:
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null && fragment instanceof DaysInWeekFragment && fragment.isVisible()) {
                            fragmentIsVisible = true;
                            break;
                        }
                    }
                }
                if (!fragmentIsVisible) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT, false);
                    openDaysInWeekFragment();
                } else mDrawerLayout.closeDrawer(Gravity.LEFT, true);
                return true;
            case R.id.it_time_table:
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null && fragment instanceof TimeTableFragment && fragment.isVisible()) {
                            fragmentIsVisible = true;
                            break;
                        }
                    }
                }
                if (!fragmentIsVisible) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT, false);
                    openTimeTableFragment();
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT, true);
                }
                return true;
            case R.id.it_statistics:
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null && fragment instanceof ActionStatisticsFragment && fragment.isVisible()) {
                            fragmentIsVisible = true;
                            break;
                        }
                    }
                }
                if (!fragmentIsVisible) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT, false);
                    openActionStatisticsFragment();
                } else mDrawerLayout.closeDrawer(Gravity.LEFT, true);
                return true;
            case R.id.it_exit:
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null && fragment.isVisible()) {
                            ((BaseFragment) fragment).onBackPressed();
                            break;
                        }
                    }
                }
                return true;

        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeService.setiUpdateUI(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment instanceof DaysInWeekFragment && fragment.isVisible()) {
                    Calendar cal = Calendar.getInstance();
                    if(((DaysInWeekFragment) fragment).getWeekOfYear() != cal.get(Calendar.WEEK_OF_YEAR)) {
                        ((DaysInWeekFragment) fragment).updateActionsInWeek(cal.get(Calendar.DAY_OF_WEEK)-1,cal.get(Calendar.WEEK_OF_YEAR),cal.get(Calendar.YEAR));
                        timeService.updateActionsInWeekFromTimeTable(cal.get(Calendar.DAY_OF_WEEK) -1 );
                    }
                    ((DaysInWeekFragment) fragment).setCurrentItemFragment(cal.get(Calendar.DAY_OF_WEEK)-1);
                    ((DaysInWeekFragment) fragment).updateUI();
                    break;
                }
                if (fragment != null && fragment instanceof ActionStatisticsFragment && fragment.isVisible()) {
                    Calendar cal = Calendar.getInstance();
                    if(((ActionStatisticsFragment) fragment).getWeekOfYear() != cal.get(Calendar.WEEK_OF_YEAR)) {
                        ((ActionStatisticsFragment) fragment).updateActionsInWeek(cal.get(Calendar.DAY_OF_WEEK)-1,cal.get(Calendar.WEEK_OF_YEAR),cal.get(Calendar.YEAR));
                        timeService.updateActionsInWeekFromTimeTable(cal.get(Calendar.DAY_OF_WEEK) -1 );
                    }
                    ((ActionStatisticsFragment) fragment).updateActionStatisticFragment(cal.get(Calendar.DAY_OF_WEEK)-1);
                }
            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public void openTimeTableFragmentTwo() {
        TimeTableTwoFragment fragment = new TimeTableTwoFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.enter_right_to_left,
                R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,
                R.anim.exit_left_to_right
        );
        transaction.replace(R.id.content, fragment, TimeTableTwoFragment.class.getName());
        fragment.setTimeService(timeService);
        transaction.commit();
    }

}
