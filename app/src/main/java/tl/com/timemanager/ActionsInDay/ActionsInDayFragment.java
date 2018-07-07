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
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import tl.com.timemanager.Adapter.ActionItemAdapter;
import tl.com.timemanager.Base.BaseFragment;
import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;
import tl.com.timemanager.dialog.calendar.CalendarDialog;
import tl.com.timemanager.dialog.insert.BaseInsertDialog;
import tl.com.timemanager.dialog.insert.InsertActionsInDayDialog;
import tl.com.timemanager.dialog.seen.SeenActionsInDayDialog;

@SuppressLint("ValidFragment")
public class ActionsInDayFragment extends BaseFragment implements ActionItemAdapter.IActionItem, View.OnClickListener, BaseInsertDialog.IDataChangedListener {

    private ImageView ivInsertAction;
    private ImageView ivOpenCalendar;
    private RecyclerView rcvAction;
    private TimeService service;
    private List<ItemAction> data;
    private ActionItemAdapter actionItemAdapter;
    private int dayOfWeek;
    private FloatingActionButton fabInsert,fabPlus, fabOpenCalendar;
    private Animation animFabClose,animFabOpen,animRotateClose,animRotateOpen;
    private boolean isOpen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actions_in_one_day, container, false);
        init(view);
        return view;
    }

    @SuppressLint("ValidFragment")
    public ActionsInDayFragment(TimeService service, int day) {
        this.service = service;
        this.dayOfWeek = day;
    }

    private void init(View view) {
        data = service.getActionsInWeek().get(dayOfWeek);
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



    }


    @Override
    public int getCount() {
        if (data == null) return 0;
        return data.size();
    }

    @Override
    public ItemAction getData(int position) {
        return data.get(position);
    }

    @Override
    public void onClickItem(int position) {
        displaySeenActionsInDayDialog(dayOfWeek, position);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(),"@@@",Toast.LENGTH_SHORT).show();
        switch (v.getId()){
            case R.id.fab_insert:
                int size = service.getCountActionsInDay(dayOfWeek);
                ItemAction action = new ItemAction();
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
                if(isOpen){
                    fabPlus.setAnimation(animRotateClose);
                    fabInsert.setAnimation(animFabClose);
                    fabOpenCalendar.setAnimation(animFabClose);
                    fabInsert.setClickable(false);
                    fabInsert.setVisibility(View.INVISIBLE);
                    fabOpenCalendar.setClickable(false);
                    fabOpenCalendar.setVisibility(View.INVISIBLE);
                    isOpen = false;
                }else {
                    fabPlus.setAnimation(animRotateOpen);
                    fabInsert.setAnimation(animFabOpen);
                    fabOpenCalendar.setAnimation(animFabOpen);
                    fabInsert.setClickable(true);
                    fabInsert.setVisibility(View.VISIBLE);
                    fabOpenCalendar.setClickable(true);
                    fabOpenCalendar.setVisibility(View.VISIBLE);
                    isOpen = true;
                }
                break;
        }

    }

    private void displayCalendarDialog() {
        CalendarDialog dialog = new CalendarDialog(getActivity());
        dialog.setIDateChangedListener((CalendarDialog.IDateChangedListener) getParentFragment());
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
        actionItemAdapter.notifyDataSetChanged();
    }
}
