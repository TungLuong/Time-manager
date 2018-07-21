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
    private int dayOfWeek;
    private int weekOfYear;
    private int year;
    private FloatingActionButton fabInsert,fabPlus, fabOpenCalendar;
    private Animation animFabClose,animFabOpen,animRotateClose,animRotateOpen;
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

        fabPlus.setOnClickListener(this);
        fabInsert.setOnClickListener(this);
        fabOpenCalendar.setOnClickListener(this);

        animFabOpen = AnimationUtils.loadAnimation(getContext(),R.anim.fab_open);
        animFabClose = AnimationUtils.loadAnimation(getContext(),R.anim.fab_close);
        animRotateOpen = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_open);
        animRotateClose = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_close);

        updateFloatingActionButton();

    }

    public void updateFloatingActionButton(){
        if(fabPlusIsOpen[0]){
            fabPlus.setAnimation(animRotateOpen);
//            fabInsert.setAnimation(animFabOpen);
//            fabOpenCalendar.setAnimation(animFabOpen);
            fabInsert.setClickable(true);
            fabInsert.setVisibility(View.VISIBLE);
            fabOpenCalendar.setClickable(true);
            fabOpenCalendar.setVisibility(View.VISIBLE);
        }
        else {
            fabPlus.setAnimation(animRotateClose);
//            fabInsert.setAnimation(animFabClose);
//            fabOpenCalendar.setAnimation(animFabClose);
            fabInsert.setClickable(false);
            fabInsert.setVisibility(View.INVISIBLE);
            fabOpenCalendar.setClickable(false);
            fabOpenCalendar.setVisibility(View.INVISIBLE);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_insert:
                int size = service.getCountActionsInDay(dayOfWeek);
                ItemAction action = new ItemAction();
                action.setTimeDoIt(1);
                action.setDayOfWeek(dayOfWeek);
                action.setWeekOfYear(weekOfYear);
                action.setYear(year);
                if(size > 0) {
                    int time = service.setNewTimeForAction(dayOfWeek, 0);
                    action.setHourOfDay(time);
                }
                service.insertItemAction(dayOfWeek,action);
                displayInsertActionDialog(dayOfWeek,size);
                break;
            case R.id.fab_open_calendar:
                displayCalendarDialog();
                break;
            case R.id.fab_plus:
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
        }

    }

    private void displayCalendarDialog() {
        CalendarActionInDayDialog dialog = new CalendarActionInDayDialog(getActivity());
        dialog.setIDateChangedListener((BaseCalendarDialog.IDateChangedListener) getParentFragment());
        dialog.setDayOfWeek(dayOfWeek);
        dialog.setWeekOfYear(weekOfYear);
        dialog.setYear(year);
        dialog.initCalendar();
        dialog.show();
    }

    private void displayInsertActionDialog(int day, int position) {
        InsertActionsInDayDialog dialog = new InsertActionsInDayDialog(getActivity());
        dialog.setPositionItemAction(position);
        dialog.setDayOfWeek(day);
        dialog.setService(service);
        dialog.initView();
        dialog.setiListener(this);
        dialog.show();
    }

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

    @Override
    public void changedActionItem() {
        if(actionItemAdapter != null) {
            actionItemAdapter.notifyDataSetChanged();
        }
    }
}
