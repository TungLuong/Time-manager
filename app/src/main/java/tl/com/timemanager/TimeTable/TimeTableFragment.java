package tl.com.timemanager.TimeTable;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import tl.com.timemanager.Adapter.DataItemAdapter;
import tl.com.timemanager.Adapter.TimeItemAdapter;
import tl.com.timemanager.Base.BaseFragment;
import tl.com.timemanager.Item.ItemData;
import tl.com.timemanager.MainActivity;
import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;
import tl.com.timemanager.dialog.InsertActionDialog;
import tl.com.timemanager.dialog.SeenActionDialog;

public class TimeTableFragment extends BaseFragment implements DataItemAdapter.IDataItem, View.OnClickListener, InsertActionDialog.IDataChangedListener {

    private static final String TAG = TimeTableFragment.class.getSimpleName() ;
    private RecyclerView rcvTime;
    private RecyclerView rcvData;
    private TimeService timeService;
    private DataItemAdapter dataItemAdapter;
    private int currPos;

    private RelativeLayout test ;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_table,container,false);
        initRecyclerView(view);
        return view;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initRecyclerView(View view) {
        rcvTime = view.findViewById(R.id.recycler_view_time);
        rcvTime.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        rcvData = view.findViewById(R.id.recycler_view_data);
        rcvData.setHasFixedSize(false);
        rcvData.setLayoutManager(new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL));
        final HorizontalScrollView scrollViewB = view.findViewById(R.id.horizontal_scroll_view_B);
        final HorizontalScrollView scrollViewD = view.findViewById(R.id.horizontal_scroll_view_D);

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

        test = view.findViewById(R.id.relative_layout_A);
        test.setOnClickListener(this);
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
            displaySeenActionDialog(position);
        }
        else {
            if (currPos == position) {
                displayInsertActionDialog(position);
            } else {
                timeService.resetClickInsert(currPos);
                currPos = position;
            }

            dataItemAdapter.updatePositionFocus(position);
        }

    }

    private void displaySeenActionDialog(int position) {
        SeenActionDialog dialog = new SeenActionDialog();
        dialog.setIdItemData(position);
        dialog.setService(timeService);
        dialog.setListener(this);
        dialog.show(getActivity().getSupportFragmentManager(), "example dialog");
    }

    private void displayInsertActionDialog(int position) {
        InsertActionDialog dialog = new InsertActionDialog();
        dialog.setIdItemData(position);
        dialog.setService(timeService);
        dialog.setListener(this);
        dialog.show(getActivity().getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void changedData() {
        dataItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        ((MainActivity)getActivity()).addDaysInWeekFragment();
    }
}
