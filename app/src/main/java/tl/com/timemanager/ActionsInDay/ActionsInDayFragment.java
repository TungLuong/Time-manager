package tl.com.timemanager.ActionsInDay;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import tl.com.timemanager.Adapter.ActionItemAdapter;
import tl.com.timemanager.Base.BaseFragment;
import tl.com.timemanager.DaysInWeek.DaysInWeekFragment;
import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;
import tl.com.timemanager.dialog.calendar.BaseCalendarDialog;
import tl.com.timemanager.dialog.calendar.CalendarActionInDayDialog;
import tl.com.timemanager.dialog.insert.BaseInsertDialog;
import tl.com.timemanager.dialog.insert.InsertActionsInDayDialog;
import tl.com.timemanager.dialog.seen.SeenActionsInDayDialog;

@SuppressLint("ValidFragment")
public class ActionsInDayFragment extends BaseFragment implements ActionItemAdapter.IActionItem, View.OnClickListener, BaseInsertDialog.IDataChangedListener {

    private RecyclerView rcvAction;
    private TimeService service;
    private ActionItemAdapter actionItemAdapter;
    // ngày tháng năm của các hoạt động hiển thị
    private int dayOfWeek;
    private int weekOfYear;
    private int year;
    // các nút thêm,+, đồng bộ, mở lịch
    private FloatingActionButton fabInsert,fabPlus, fabSync, fabOpenCalendar;
    private Animation animFabClose,animFabOpen,animRotateClose,animRotateOpen;
    // các nút khác có đc hiển thị không
    private boolean fabPlusIsOpen[];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actions_in_one_day, container, false);
        init(view);
        return view;
    }

    @SuppressLint("ValidFragment")
    public ActionsInDayFragment(TimeService service, int dayOfWeek, int weekOfYear, int year, boolean[] fabPlusIsOpen) {
        this.service = service;
        this.dayOfWeek = dayOfWeek;
        this.weekOfYear = weekOfYear;
        this.year = year;
        this.fabPlusIsOpen = fabPlusIsOpen;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public void setYear(int year) {
        this.year = year;
    }


    private void init(View view) {
        //  ivInsertAction = view.findViewById(R.id.iv_insert_action);
        //  ivOpenCalendar = view.findViewById(R.id.iv_open_calender);
        rcvAction = view.findViewById(R.id.rcv_actions);
        rcvAction.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rcvAction.setHasFixedSize(true);
        actionItemAdapter = new ActionItemAdapter(this);
        rcvAction.setAdapter(actionItemAdapter);

        // ivInsertAction.setOnClickListener(this);
        // ivOpenCalendar.setOnClickListener(this);


        fabInsert = view.findViewById(R.id.fab_insert);
        fabOpenCalendar = view.findViewById(R.id.fab_open_calendar);
        fabPlus = view.findViewById(R.id.fab_plus);
        fabSync = view.findViewById(R.id.fab_sync);

        fabPlus.setOnClickListener(this);
        fabInsert.setOnClickListener(this);
        fabOpenCalendar.setOnClickListener(this);
        fabSync.setOnClickListener(this);

        animFabOpen = AnimationUtils.loadAnimation(getContext(),R.anim.fab_open);
        animFabClose = AnimationUtils.loadAnimation(getContext(),R.anim.fab_close);
        animRotateOpen = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_open);
        animRotateClose = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_close);

        updateFloatingActionButton();

    }

    /*
    Cập nhật lại tình trạng các nút
     */
    public void updateFloatingActionButton(){
        if(fabPlusIsOpen[0]){
            fabPlus.setAnimation(animRotateOpen);
//            fabInsert.setAnimation(animFabOpen);
//            fabOpenCalendar.setAnimation(animFabOpen);
            fabInsert.setClickable(true);
            fabInsert.setVisibility(View.VISIBLE);
            fabOpenCalendar.setClickable(true);
            fabOpenCalendar.setVisibility(View.VISIBLE);
            fabSync.setClickable(true);
            fabSync.setVisibility(View.VISIBLE);
        }
        else {
            fabPlus.setAnimation(animRotateClose);
//            fabInsert.setAnimation(animFabClose);
//            fabOpenCalendar.setAnimation(animFabClose);
            fabInsert.setClickable(false);
            fabInsert.setVisibility(View.INVISIBLE);
            fabOpenCalendar.setClickable(false);
            fabOpenCalendar.setVisibility(View.INVISIBLE);
            fabSync.setClickable(false);
            fabSync.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getCount() {
        if (service.getActionsInDay(dayOfWeek) == null) return 0;
        return service.getActionsInDay(dayOfWeek).size();
    }

    @Override
    public ItemAction getItemAction(int position) {
        return service.getActionsInDay(dayOfWeek).get(position);
    }

    @Override
    public void onClickItem(int position) {
        displaySeenActionsInDayDialog(dayOfWeek, position);
    }

    @Override
    public void setCompleteForAction(int adapterPosition) {
        service.setCompleteForAction(dayOfWeek,adapterPosition);
    }

//    @Override
//    public void removeItemAction(int position) {
//        service.getActionsInDay(dayOfWeek).remove(position);
//    }

//    @Override
//    public void deleteAction(int day,int position) {
//
//        service.deleteActionByPositionItemAction(day,position);
//    }

    /*
    khi nhấn vào nút
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_insert:
                // thêm hoạt động
                int size = service.getCountActionsInDay(dayOfWeek);
                ItemAction action = new ItemAction();
                action.setTimeDoIt(1);
                action.setDayOfWeek(dayOfWeek);
                action.setWeekOfYear(weekOfYear);
                action.setYear(year);
                if(size > 0) {
                    int time = service.setTimeForAction(dayOfWeek, 1);
                    action.setHourOfDay(time);
                }
                service.insertItemAction(dayOfWeek,action);
                displayInsertActionDialog(dayOfWeek,size);
                break;
            case R.id.fab_open_calendar:
                // mở lịch
                displayCalendarDialog();
                break;
            case R.id.fab_plus:
                // mở hoặc đóng hiển thị các nút
                if(fabPlusIsOpen[0]){
                    fabPlus.setAnimation(animRotateClose);
//                    fabInsert.setAnimation(animFabClose);
//                    fabOpenCalendar.setAnimation(animFabClose);
                    fabInsert.setClickable(false);
                    fabInsert.setVisibility(View.INVISIBLE);
                    fabOpenCalendar.setClickable(false);
                    fabOpenCalendar.setVisibility(View.INVISIBLE);
                    fabPlusIsOpen[0] = false;
                    ((DaysInWeekFragment)getParentFragment()).updateUI();
                }else {
                    fabPlus.setAnimation(animRotateOpen);
//                    fabInsert.setAnimation(animFabOpen);
//                    fabOpenCalendar.setAnimation(animFabOpen);
                    fabInsert.setClickable(true);
                    fabInsert.setVisibility(View.VISIBLE);
                    fabOpenCalendar.setClickable(true);
                    fabOpenCalendar.setVisibility(View.VISIBLE);
                    fabPlusIsOpen[0] = true;
                    ((DaysInWeekFragment)getParentFragment()).updateUI();
                }
                break;
            case R.id.fab_sync:
                // đồng bộ dữ liệu hiển thị
                service.updateActionsInWeekFromTimeTable(dayOfWeek);
                service.checkActionsDone(dayOfWeek);
                service.updateActionsInWeek(weekOfYear,year);
                actionItemAdapter.notifyDataSetChanged();
                ((DaysInWeekFragment)getParentFragment()).updateUI();
                Toast.makeText(getActivity(),"Đồng bộ thành công",Toast.LENGTH_LONG).show();
                break;
        }

    }

    /**
     * Mở dialog lịch để xem hoạt động các ngày khác nhau trong lịch
     */
    private void displayCalendarDialog() {
        CalendarActionInDayDialog dialog = new CalendarActionInDayDialog(getActivity());
        dialog.setIDateChangedListener((BaseCalendarDialog.IDateChangedListener) getParentFragment());
        dialog.setDayOfWeek(dayOfWeek);
        dialog.setWeekOfYear(weekOfYear);
        dialog.setYear(year);
        dialog.initCalendar();
        dialog.show();
    }

    /**
     * Mở dialog để thêm hoạt động
     * @param day ngày thêm hoạt động
     * @param position vị trí của hoạt động ý
     */
    private void displayInsertActionDialog(int day, int position) {
        InsertActionsInDayDialog dialog = new InsertActionsInDayDialog(getActivity());
        dialog.setPositionItemAction(position);
        dialog.setDayOfWeek(day);
        dialog.setService(service);
        dialog.initView();
        dialog.setiListener(this);
        dialog.show();
    }

    /**
     * mở ra dialog để xem hoạt động
     * @param day ngày
     * @param position vị trí của hoạt động
     */
    private void displaySeenActionsInDayDialog(int day, int position) {
        SeenActionsInDayDialog dialog = new SeenActionsInDayDialog(getActivity());
        dialog.setPositionItemAction(position);
        dialog.setDayOfWeek(day);
        dialog.setService(service);
        dialog.initView();
        dialog.setiListener(this);
        dialog.show();
    }


    @Override
    public void changedDataItem() {

    }

    /**
     * cập nhật lại hoạt động trong ngày
     */
    @Override
    public void changedActionItem() {
        if(actionItemAdapter != null) {
            actionItemAdapter.notifyDataSetChanged();
        }
    }
}
