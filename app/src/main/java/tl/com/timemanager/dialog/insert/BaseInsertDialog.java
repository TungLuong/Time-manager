package tl.com.timemanager.dialog.insert;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.FREE_TIME;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;

public class BaseInsertDialog extends BottomSheetDialog implements AdapterView.OnItemSelectedListener, View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {

    protected ImageView ivAction;
    protected Spinner spin_time;
    protected Spinner spin_action;
    protected EditText edtAction;
    protected ImageView ivClose;
    protected Button btnSave;
    protected Switch swNotification;
    protected Switch swDoNotDisturb;
    protected int kindOfAction;
    protected int count = 1;
    protected TimeService service;
    protected TextView tvErrorTime;
    protected TextView tvErrorTitle;
    protected TextView tvErrorTimeStart;
    protected EditText edtTimeStart;
    protected boolean isModify;
    protected IDataChangedListener iListener;

    public BaseInsertDialog(@NonNull Context context) {
        super(context, R.style.StyleDialogBottom);
//        getWindow().setGravity(Gravity.BOTTOM);
//
//        WindowManager.LayoutParams p = getWindow().getAttributes();
//        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        getWindow().setAttributes(p);
        setContentView(R.layout.dialog_insert_action);
    }

    //    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_insert_action, null);
//        initView(view);
//        builder.setView(view);
//        return builder.create();
//    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        getDialog().getWindow().setGravity(Gravity.BOTTOM);
//        getDialog().setT
//        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
//        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        getDialog().getWindow().setAttributes(p);
//        View view = inflater.inflate(R.layout.dialog_insert_action, container, false);
//        view.setPadding(0, 0, 0, 0);
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initView(view);
//    }

    public void setService(TimeService service) {
        this.service = service;
    }

    public void setiListener(IDataChangedListener iListener) {
        this.iListener = iListener;
    }

    public void initView() {

        ivAction = findViewById(R.id.iv_img_action);

        spin_time = (Spinner) findViewById(R.id.spinner_time);
        ArrayAdapter<CharSequence> adapter_time = ArrayAdapter.createFromResource(getContext(), R.array.hour, android.R.layout.simple_spinner_item);
        adapter_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_time.setAdapter(adapter_time);
        spin_time.setOnItemSelectedListener(this);

        spin_action = (Spinner) findViewById(R.id.spinner_kind_of_action);
        ArrayAdapter<CharSequence> adapter_action = ArrayAdapter.createFromResource(getContext(), R.array.kind_of_action, android.R.layout.simple_spinner_item);
        adapter_action.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_action.setAdapter(adapter_action);
        spin_action.setOnItemSelectedListener(this);

        ivAction = findViewById(R.id.iv_img_action);
        ivClose =findViewById(R.id.iv_close);
        btnSave =findViewById(R.id.btn_save);
        edtAction =findViewById(R.id.edt_name_action);
        edtTimeStart =findViewById(R.id.edt_time_start);

        swNotification =findViewById(R.id.sw_notification);
        swDoNotDisturb =findViewById(R.id.sw_do_not_disturb);
        tvErrorTime =findViewById(R.id.tv_error_time);
        tvErrorTitle =findViewById(R.id.tv_error_title);
        tvErrorTimeStart =findViewById(R.id.tv_error_time_start);

        ivClose.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        edtAction.addTextChangedListener(this);
        edtTimeStart.addTextChangedListener(this);
        setData();


    }

    protected void setData() {
    }

    protected void setModifyingData(boolean b) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_kind_of_action:
                kindOfAction = position;
                switch (position) {
                    case FREE_TIME:
                        ivAction.setImageResource(R.drawable.free_time);
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

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

    }


    protected void checkInvalidTitle() {
        String title = String.valueOf(edtAction.getText());
        if (title.trim().length() > 0) {
            tvErrorTitle.setVisibility(View.GONE);
        } else {
            tvErrorTitle.setVisibility(View.VISIBLE);
        }
    }


    protected void updateData() {

    }

    private void createNewAction() {

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

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            onBackPressed();
            return true;
        }
        return false;
    }

    public interface IDataChangedListener {
        void changedDataItem();
        void changedActionItem();
    }

}
