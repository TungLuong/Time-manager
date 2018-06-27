package tl.com.timemanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import tl.com.timemanager.Adapter.DataItemAdapter;
import tl.com.timemanager.Adapter.TimeItemAdapter;
import tl.com.timemanager.Base.BaseActivity;
import tl.com.timemanager.Item.ItemData;
import tl.com.timemanager.Service.TimeService;
import tl.com.timemanager.dialog.InsertActionDiaglog;
import tl.com.timemanager.dialog.SeenActionDialog;

public class MainActivity extends BaseActivity implements DataItemAdapter.IDataItem, InsertActionDiaglog.IDataChangedListener {
    private static final String TAG = MainActivity.class.getSimpleName() ;
    private RecyclerView rcvTime;
    private RecyclerView rcvData;
    private ServiceConnection conn;
    private TimeService timeService;
    private DataItemAdapter dataItemAdapter;
    private InsertActionDiaglog insertActionDiaglog;
    private int currPos;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_time_table);
        startService();
        connectedService();
        initRecyclerView();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initRecyclerView() {
        rcvTime = findViewById(R.id.recycler_view_time);
        rcvTime.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rcvData = findViewById(R.id.recycler_view_data);
        rcvData.setHasFixedSize(false);
        rcvData.setLayoutManager(new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL));
        final HorizontalScrollView scrollViewB = findViewById(R.id.horizontal_scroll_view_B);
        final HorizontalScrollView scrollViewD = findViewById(R.id.horizontal_scroll_view_D);

        scrollViewB.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollViewD.scrollTo(scrollX,scrollY);
            }
        });

        scrollViewD.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollViewB.scrollTo(scrollX,scrollY);
            }
        });


        final boolean[] firstIsTouched = {false};
        final boolean[] secondIsTouched = {false};

        dataItemAdapter = new DataItemAdapter(this);
        rcvData.setAdapter(dataItemAdapter);

        rcvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(firstIsTouched[0]) {
                    secondIsTouched[0] = false;
                    super.onScrolled(recyclerView, dx, dy);
                    rcvTime.scrollBy(dx, dy);
                }
            }
        });

        rcvData.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                firstIsTouched[0] = true;
                return false;
            }
        });



        TimeItemAdapter timeItemAdapter = new TimeItemAdapter();
        rcvTime.setAdapter(timeItemAdapter);
        rcvTime.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(secondIsTouched[0]) {
                    firstIsTouched[0] = false;
                    super.onScrolled(recyclerView, dx, dy);
                    rcvData.scrollBy(dx, dy);
                }
            }
        });

        rcvTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                secondIsTouched[0] = true;
                return false;
            }
        });
//        rcvTime.scrollToPosition(START_TIME);
//        rcvData.scrollToPosition(START_TIME*COUNT_DAY);
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
                dataItemAdapter.notifyDataSetChanged();
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

    @Override
    public int getCount() {
        if(timeService == null) return 0;
        return timeService.getCount();
    }

    @Override
    public ItemData getData(int position) {
        return timeService.getData(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClickItem(int position) {
        ItemData itemData = timeService.getData(position);
        if(itemData.isActive()){
            SeenActionDialog dialog = new SeenActionDialog();
            dialog.setIdItemData(position);
            dialog.setService(timeService);
            dialog.setListener(this);
            dialog.show(getSupportFragmentManager(), "example dialog");
        }
        else {
            if (currPos == position) {
                insertActionDiaglog = new InsertActionDiaglog();
                insertActionDiaglog.setIdItemData(position);
                insertActionDiaglog.setService(timeService);
                insertActionDiaglog.setListener(this);
                insertActionDiaglog.show(getSupportFragmentManager(), "example dialog");
            } else {
                timeService.resetClickInsert(currPos);
                currPos = position;
            }

            dataItemAdapter.updatePositionFocus(position);
        }

    }

    @Override
    public void changedData() {
        dataItemAdapter.notifyDataSetChanged();
    }

}
