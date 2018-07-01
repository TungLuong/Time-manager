package tl.com.timemanager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.Item.ItemData;
import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.COUNT_DAY;
import static tl.com.timemanager.Constant.NO_ACTION;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;
import static tl.com.timemanager.Constant.TIME_MAX;
import static tl.com.timemanager.Constant.TIME_MIN;

public class InsertActionDialog extends android.support.v4.app.DialogFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener, TextWatcher{

    private static final String TAG = InsertActionDialog.class.getSimpleName();
    protected ImageView ivAction;
    protected Spinner spin_time;
    protected Spinner spin_action;
    protected EditText edtAction;
    protected ImageView ivClose;
    protected Button btnSave;
    protected Switch swNotification;
    protected Switch swDoNotDisturb;
    private int idItemData;
    private int oldIdItemData;
    protected int kindOfAction;
    protected int count;
    protected TimeService service;
    private IDataChangedListener listener;
    protected TextView tvErrorTime;
    protected TextView tvErrorTitle;
    protected TextView tvErrorTimeStart;
    protected EditText edtTimeStart;
    protected boolean isModify;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_insert_action, null);
        initView(view);
        builder.setView(view);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(p);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setIdItemData(int idItemData) {
        this.idItemData = idItemData;
    }

    public void setService(TimeService service) {
        this.service = service;
    }

    public void setListener(IDataChangedListener listener) {
        this.listener = listener;
    }

    public void initView(View view) {

        ivAction = view.findViewById(R.id.iv_img_action);

        spin_time = (Spinner) view.findViewById(R.id.spinner_time);
        ArrayAdapter<CharSequence> adapter_time = ArrayAdapter.createFromResource(getActivity(), R.array.time, android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_time.setAdapter(adapter_time);
        spin_time.setOnItemSelectedListener(this);

        spin_action = (Spinner) view.findViewById(R.id.spinner_kind_of_action);
        ArrayAdapter<CharSequence> adapter_action = ArrayAdapter.createFromResource(getActivity(), R.array.kind_of_action, android.R.layout.simple_spinner_item);
        adapter_action.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_action.setAdapter(adapter_action);
        spin_action.setOnItemSelectedListener(this);

        ivAction = view.findViewById(R.id.iv_img_action);
        ivClose = view.findViewById(R.id.iv_close);
        btnSave = view.findViewById(R.id.btn_save);
        edtAction = view.findViewById(R.id.edt_name_action);
        edtTimeStart = view.findViewById(R.id.edt_time_start);

        swNotification = view.findViewById(R.id.sw_notification);
        swDoNotDisturb = view.findViewById(R.id.sw_do_not_disturb);
        tvErrorTime = view.findViewById(R.id.tv_error_time);
        tvErrorTitle = view.findViewById(R.id.tv_error_title);
        tvErrorTimeStart = view.findViewById(R.id.tv_error_time_start);

        ivClose.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        edtAction.addTextChangedListener(this);
        edtTimeStart.addTextChangedListener(this);
        edtTimeStart.setOnClickListener(this);
        setItemData();


    }

    protected void setItemData() {
        oldIdItemData = idItemData;
        int i = idItemData - service.getData(idItemData).getFlag() * COUNT_DAY;
//        if(i < 0) i=  service.getData(idItemData).getDay()  + ((COUNT_TIME-1)*COUNT_DAY) +i;
        ItemData item = service.getData(i);
        if (item.isActive()) {
            isModify = true;
            edtAction.setText(item.getTitle());
            spin_time.setSelection(item.getTimeDoIt() - 1);
            spin_action.setSelection(item.getAction());

            if (item.isNotification()) {
                swNotification.setChecked(true);
            } else swNotification.setChecked(false);

            if (item.isDoNotDisturb()) {
                swDoNotDisturb.setChecked(true);
            } else swDoNotDisturb.setChecked(false);

            switch (item.getAction()) {
                case NO_ACTION:
                    ivAction.setImageResource(R.drawable.no_action);
                    break;
                case OUTSIDE_ACTION:
                    ivAction.setImageResource(R.drawable.school);
                    break;
                case AT_HOME_ACTION:
                    ivAction.setImageResource(R.drawable.homework);
                    break;
                case AMUSING_ACTION:
                    ivAction.setImageResource(R.drawable.giaitri);
                    break;
                case RELAX_ACTION:
                    ivAction.setImageResource(R.drawable.sleep);
                    break;
            }
        }

        if(isModify){
            setModifyingData(true);
        }else {
            setModifyingData(false);
        }

        edtTimeStart.setText(item.getTime()+"");


    }

    protected void setModifyingData(boolean b) {
        ItemData item = service.getData(oldIdItemData);
        int i = oldIdItemData - item.getFlag() * COUNT_DAY;
        int count = item.getTimeDoIt();
        int j =0;
        while (j < count && i < service.getCount()){
            service.getData(i).setModifying(b);
            i = i + COUNT_DAY;
            j++;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_kind_of_action:
                kindOfAction = position;
                switch (position) {
                    case NO_ACTION:
                        ivAction.setImageResource(R.drawable.no_action);
                        //colorId = R.color.colorNoAction;
                        break;
                    case OUTSIDE_ACTION:
                        ivAction.setImageResource(R.drawable.school);
                        //colorId = R.color.colorOutSideAction;
                        break;
                    case AT_HOME_ACTION:
                        ivAction.setImageResource(R.drawable.homework);
                        //colorId = R.color.colorHomework;
                        break;
                    case AMUSING_ACTION:
                        ivAction.setImageResource(R.drawable.giaitri);
                        //colorId = R.color.colorEntertainment;
                        break;
                    case RELAX_ACTION:
                        ivAction.setImageResource(R.drawable.sleep);
                        //colorId = R.color.colorRelax;
                        break;
                }
                break;
            case R.id.spinner_time:
                switch (position) {
                    case 0:
                        count = 1;
                        break;
                    case 1:
                        count = 2;
                        break;
                    case 2:
                        count = 3;
                        break;
                    case 3:
                        count = 4;
                        break;
                    case 4:
                        count = 5;
                        break;
                    case 5:
                        count = 6;
                        break;
                }
                checkSameTime();
                break;
        }

    }

    protected void checkSameTime() {
        int j=0;
        int i ;
        while (j < count ) {
            i = idItemData + COUNT_DAY*j;
            if(i >= service.getCount() ) {
                tvErrorTime.setVisibility(View.VISIBLE);
                return ;
            }
            ItemData item = service.getData(i);
            if (item.isActive() && !item.isModifying()) {
                tvErrorTime.setVisibility(View.VISIBLE);
                return ;
            }
           // Toast.makeText(getActivity(),i+"",Toast.LENGTH_SHORT).show();
//            if (i >= service.getCount()) i = (i-COUNT_DAY * COUNT_TIME ) + 1;
            j++;
        }
        tvErrorTime.setVisibility(View.GONE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                if (isModify) {
                    isModify = false;
                    setModifyingData(false);
                }
                dismiss();
                break;
            case R.id.btn_save:
                checkInvalidTitle();
                if (tvErrorTime.getVisibility() == View.GONE 
                        && tvErrorTitle.getVisibility() == View.GONE 
                        && tvErrorTimeStart.getVisibility() == View.GONE) {
                    if(isModify){
                        service.deleteAction(oldIdItemData);
                    }
                    createData();
                    isModify = false;
                    setModifyingData(false);
                    listener.changedData();
                    dismiss();
                }
                break;
//            case R.id.edt_time_start:
//                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        edtTimeStart.setText(hourOfDay+"");
//                    }
//                };
//                int hour =service.getData(idItemData).getTime();
//                TimePickerDialog dialog = new TimePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog,listener,hour,0,true);
//                dialog.show();
        }
    }


    protected void checkInvalidTitle() {
        String title = String.valueOf(edtAction.getText());
        if (title.trim().length() > 0) {
            tvErrorTitle.setVisibility(View.GONE);
        } else {
            tvErrorTitle.setVisibility(View.VISIBLE);
        }
    }


    protected void createData() {
        if (isModify) {
            idItemData = idItemData - service.getData(idItemData).getFlag() * COUNT_DAY;
        }
        createNewAction();

        int i = idItemData;
        int j = 0;
        boolean notifi = swNotification.isChecked();
        boolean doNotDisturb = swDoNotDisturb.isChecked();
        String title = String.valueOf(edtAction.getText());
        while (j < count && i < service.getCount() ) {
            ItemData item = service.getData(i);
            if (!item.isActive()) {
                item.setAction(kindOfAction);
                item.setActive(true);
                item.setNotification(notifi);
                item.setDoNotDisturb(doNotDisturb);
                item.setTitle(title);
                item.setFlag(j);
                item.setTimeDoIt(count);
                j++;
            }
            i = i + COUNT_DAY;
//            if(i >= service.getCount() + COUNT_DAY) break;
//            else if (i >= service.getCount()) i = ( i - COUNT_DAY * COUNT_TIME  ) + 1;
        }
    }

    private void createNewAction() {
        ItemData itemData = service.getData(idItemData);
        String title = String.valueOf(edtAction.getText());
        ItemAction action = new ItemAction();
        action.setTitle(title);
        action.setAction(kindOfAction);
        action.setDay(itemData.getDay());
        action.setTime(itemData.getTime());
        action.setTimeDoIt(count);
        action.setNotification(swNotification.isChecked());
        action.setDoNotDisturb(swDoNotDisturb.isChecked());
        service.getActionsInDays().get(service.getData(idItemData).getDay()).add(action);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (edtAction.getText().hashCode() == s.hashCode()) {
            checkInvalidTitle();
        } else if (edtTimeStart.getText().hashCode() == s.hashCode()) {
            checkInvalidTimeStart();
        }
    }

    protected void checkInvalidTimeStart() {
        if (edtTimeStart.getText().toString().trim().length() > 0) {
            int time = Integer.valueOf(edtTimeStart.getText().toString());
            if (time >= TIME_MIN && time <= TIME_MAX) {
                int day = service.getData(idItemData).getDay();
                int newId = day + COUNT_DAY * (time - TIME_MIN);
                if (newId > service.getCount()) {
                    tvErrorTimeStart.setVisibility(View.VISIBLE);
                    return;
                }
                if (service.getData(newId).isActive() && !service.getData(newId).isModifying() ) {
                    tvErrorTimeStart.setVisibility(View.VISIBLE);
                    return;
                }
                idItemData = newId;
                checkSameTime();
                tvErrorTimeStart.setVisibility(View.GONE);
            } else {
                tvErrorTimeStart.setVisibility(View.VISIBLE);
            }
        } else {
            tvErrorTimeStart.setVisibility(View.VISIBLE);
        }
    }

    public interface IDataChangedListener {
        void changedData();
    }

}
