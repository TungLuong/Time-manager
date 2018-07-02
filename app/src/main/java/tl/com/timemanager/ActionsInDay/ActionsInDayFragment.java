package tl.com.timemanager.ActionsInDay;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import tl.com.timemanager.Adapter.ActionItemAdapter;
import tl.com.timemanager.Base.BaseFragment;
import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;
import tl.com.timemanager.dialog.insert.BaseInsertDialog;
import tl.com.timemanager.dialog.insert.InsertActionsInDayDialog;
import tl.com.timemanager.dialog.seen.SeenActionsInDayDialog;

@SuppressLint("ValidFragment")
public class ActionsInDayFragment extends BaseFragment implements ActionItemAdapter.IActionItem, View.OnClickListener, BaseInsertDialog.IDataChangedListener {

    private ImageView ivInsertAction;
    private RecyclerView rcvAction;
    private TimeService service;
    private List<ItemAction> data;
    private ActionItemAdapter actionItemAdapter;
    private int day;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actions_in_one_day,container,false);
        init(view);
        return view;
    }

    @SuppressLint("ValidFragment")
    public ActionsInDayFragment(TimeService service,int day){
        this.service = service;
        this.day = day;
    }

    private void init(View view) {
        data = service.getActionsInDays().get(day);
        ivInsertAction = view.findViewById(R.id.iv_insert_action);
        rcvAction = view.findViewById(R.id.rcv_actions);
        rcvAction.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rcvAction.setHasFixedSize(true);
        actionItemAdapter = new ActionItemAdapter(this);
        rcvAction.setAdapter(actionItemAdapter);

        ivInsertAction.setOnClickListener(this);

    }

    @Override
    public int getCount() {
        if(data == null) return 0;
        return data.size();
    }

    @Override
    public ItemAction getData(int position) {
        return data.get(position);
    }

    @Override
    public void onClickItem(int position) {
            displaySeenActionsInDayDialog(day,position);
    }

    @Override
    public void onClick(View v) {
        int size = service.getActionsInDays().get(day).size();
        service.getActionsInDays().get(day).add(new ItemAction());
        displayInsertActionDialog(day,size);
    }

    private void displayInsertActionDialog(int day,int position) {
        InsertActionsInDayDialog dialog = new InsertActionsInDayDialog(getActivity());
        dialog.setIdItemAction(position);
        dialog.setDay(day);
        dialog.setService(service);
        dialog.initView();
        dialog.setiListener(this);
        dialog.show();
    }

    private void displaySeenActionsInDayDialog(int day,int position){
        SeenActionsInDayDialog dialog = new SeenActionsInDayDialog(getActivity());
        dialog.setIdItemAction(position);
        dialog.setDay(day);
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
