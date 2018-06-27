package tl.com.timemanager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import tl.com.timemanager.Item.ItemData;
import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.COUNT_DAY;
import static tl.com.timemanager.Constant.COUNT_TIME;
import static tl.com.timemanager.Constant.NO_ACTION;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;

public class InsertActionDiaglog extends android.support.v4.app.DialogFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener, TextWatcher {

    private static final String TAG = InsertActionDiaglog.class.getSimpleName() ;
    private ImageView ivAction;
    private Spinner spin_time;
    private Spinner spin_action;
    private EditText edtAction;
    private ImageView ivClose;
    private Button btnSave;
    private Switch swNotification;
    private Switch swDoNotDisturb;
    private int idItemData;
    private int kindOfAction;
    private int count;
    private TimeService service;
    private IDataChangedListener listener;
    private TextView tvErrorTime;
    private TextView tvErrorTitle;
    private int colorId;
    private  boolean isModify;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_insert_action,null);
        initView(view);
        builder.setView(view);
        return builder.create();
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

    private void initView(View view) {

        ivAction = view.findViewById(R.id.iv_img_action);

        spin_time=(Spinner) view.findViewById(R.id.spinner_time);
        ArrayAdapter<CharSequence> adapter_time= ArrayAdapter.createFromResource(getActivity(),R.array.time,android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_time.setAdapter(adapter_time);
        spin_time.setOnItemSelectedListener(this);

        spin_action=(Spinner) view.findViewById(R.id.spinner_kind_of_action);
        ArrayAdapter<CharSequence> adapter_action= ArrayAdapter.createFromResource(getActivity(),R.array.kind_of_action,android.R.layout.simple_spinner_item);
        adapter_action.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_action.setAdapter(adapter_action);
        spin_action.setOnItemSelectedListener(this);

        ivAction = view.findViewById(R.id.iv_img_action);
        ivClose = view.findViewById(R.id.iv_close);
        btnSave = view.findViewById(R.id.btn_save);
        edtAction = view.findViewById(R.id.edt_name_action);
        swNotification = view.findViewById(R.id.sw_notification);
        swDoNotDisturb = view.findViewById(R.id.sw_do_not_disturb);
        tvErrorTime = view.findViewById(R.id.tv_error_time);
        tvErrorTitle = view.findViewById(R.id.tv_error_title);

        ivClose.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        edtAction.addTextChangedListener(this);
        setItemData();


    }

    private void setItemData() {
        ItemData item = service.getData(idItemData);
        if(item.isActive()) {
            isModify = true;
            edtAction.setText(item.getTitle());
            spin_time.setSelection(item.getTimeDoIt() - 1);
            spin_action.setSelection(item.getAction());

            if (item.isNotification()) {
                swNotification.setChecked(true);
            } else swNotification.setChecked(false);

            if (item.isTurnOffMedia()) {
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

        deleteActive(item.getFlag(),item.getTimeDoIt());

    }

    private void deleteActive(int flag, int timeDoIt) {
        int i = idItemData - flag*COUNT_DAY;
        for(int j =0;j<timeDoIt;j++){
            service.getData(i).setActive(false);
            i=i + COUNT_DAY;
        }
    }

    private void resetActive(int flag, int timeDoIt) {
        int i = idItemData - flag*COUNT_DAY;
        for(int j =0;j<timeDoIt;j++){
            service.getData(i).setActive(true);
            i=i + COUNT_DAY;
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
                        colorId = R.color.colorNoAction;
                        break;
                    case OUTSIDE_ACTION:
                        ivAction.setImageResource(R.drawable.school);
                        colorId = R.color.colorOutSideAction;
                        break;
                    case AT_HOME_ACTION:
                        ivAction.setImageResource(R.drawable.homework);
                        colorId = R.color.colorHomework;
                        break;
                    case AMUSING_ACTION:
                        ivAction.setImageResource(R.drawable.giaitri);
                        colorId = R.color.colorEntertainment;
                        break;
                    case RELAX_ACTION:
                        ivAction.setImageResource(R.drawable.sleep);
                        colorId = R.color.colorRelax;
                        break;
                }
                break;
            case R.id.spinner_time:
                switch (position){
                    case 0: count = 1; break;
                    case 1: count = 2; break;
                    case 2: count = 3; break;
                    case 3: count = 4; break;
                    case 4: count = 5; break;
                    case 5: count = 6; break;
                }
                if(checkSameTime()){
                    tvErrorTime.setVisibility(View.VISIBLE);
                }
                else {
                    tvErrorTime.setVisibility(View.GONE);
                }
                break;
        }

    }

    private boolean checkSameTime() {
        for (int i = 0 ; i< count;i++ ){
            if(service.getData(idItemData+i*COUNT_DAY).isActive()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close: if(isModify){resetActive(service.getData(idItemData).getFlag(),service.getData(idItemData).getTimeDoIt()); }
                                dismiss();
                                break;
            case R.id.btn_save :checkInvalidTitle();
                                if(tvErrorTime.getVisibility() == View.GONE && tvErrorTitle.getVisibility() == View.GONE ){
                                    saveData();
                                    listener.changedData();
                                    dismiss();
                                }
                                break;
        }
    }

    private boolean checkInvalidTitle() {
        String title = String.valueOf(edtAction.getText());
        if(title.trim().length() > 0){
            tvErrorTitle.setVisibility(View.GONE);
            return true;
        }
        else {
            tvErrorTitle.setVisibility(View.VISIBLE);
            return false;
        }
    }


    private void saveData() {
        if(isModify) {
            idItemData = idItemData - service.getData(idItemData).getFlag()*COUNT_DAY;
        }
        int i = idItemData;
        int j=0;
        while (j < count && i < service.getCount()){
            ItemData item = service.getData(i);
            if(!item.isActive()) {
                item.setAction(kindOfAction);
                item.setActive(true);
                item.setNotification(swNotification.isChecked());
                item.setTurnOffMedia(swDoNotDisturb.isChecked());
                item.setTitle(String.valueOf(edtAction.getText()));
                item.setFlag(j);
                item.setTimeDoIt(count);
                j++;
            }
            Log.d(TAG,"XXXXX......" + i);
            i = i + COUNT_DAY;
            if(i > service.getCount()) i =  ( - (COUNT_DAY*COUNT_TIME - i))   + 1;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        checkInvalidTitle();
    }

    public interface IDataChangedListener {
        void changedData();
    }
}
