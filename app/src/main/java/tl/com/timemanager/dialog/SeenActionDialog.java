package tl.com.timemanager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import tl.com.timemanager.Item.ItemData;
import tl.com.timemanager.R;
import tl.com.timemanager.Service.TimeService;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.COUNT_DAY;
import static tl.com.timemanager.Constant.NO_ACTION;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;

public class SeenActionDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = InsertActionDiaglog.class.getSimpleName();
    private ImageView ivAction;
    private TextView tvAction;
    private ImageView ivClose;
    private Button btnModify;
    private Button btnDelete;
    private TextView tvTimeStart;
    private TextView tvTimeEnd;
    private TextView tvKindOfAction;
    private ImageView ivNotification;
    private ImageView ivDoNotDisturb;

    private int idItemData;
    private TimeService service;
    private InsertActionDiaglog.IDataChangedListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_seen_action, null);
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

    public InsertActionDiaglog.IDataChangedListener getListener() {
        return listener;
    }

    public void setListener(InsertActionDiaglog.IDataChangedListener listener) {
        this.listener = listener;
    }

    private void initView(View view) {

        ivAction = view.findViewById(R.id.iv_img_action);
        ivClose = view.findViewById(R.id.iv_close);
        ivNotification = view.findViewById(R.id.iv_notifi);
        ivDoNotDisturb = view.findViewById(R.id.iv_do_not_disturb);
        btnModify = view.findViewById(R.id.btn_modify);
        btnDelete = view.findViewById(R.id.btn_delete);
        tvAction = view.findViewById(R.id.tv_name_action_);
        tvKindOfAction = view.findViewById(R.id.tv_kind_of_action_);
        tvTimeStart = view.findViewById(R.id.tv_time_start);
        tvTimeEnd = view.findViewById(R.id.tv_time_end);

        ivClose.setOnClickListener(this);
        btnModify.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        setItemData();

    }

    private void setItemData() {
        ItemData item = service.getData(idItemData);
        tvAction.setText(item.getTitle());
        int timeStart = item.getTime() - item.getFlag();
        tvTimeStart.setText(timeStart + " h");
        tvTimeEnd.setText(timeStart + item.getTimeDoIt() + " h");
        switch (item.getAction()) {
            case NO_ACTION:
                ivAction.setImageResource(R.drawable.no_action);
                tvKindOfAction.setText("Không xác định");
                break;
            case OUTSIDE_ACTION:
                ivAction.setImageResource(R.drawable.school);
                tvKindOfAction.setText("Hoạt động bên ngoài");
                break;
            case AT_HOME_ACTION:
                ivAction.setImageResource(R.drawable.homework);
                tvKindOfAction.setText("Hoạt động tại nhà");
                break;
            case AMUSING_ACTION:
                ivAction.setImageResource(R.drawable.giaitri);
                tvKindOfAction.setText("Hoạt động giải trí");
                break;
            case RELAX_ACTION:
                ivAction.setImageResource(R.drawable.sleep);
                tvKindOfAction.setText("Hoạt động nghỉ ngơi");
                break;
        }

        if (item.isNotification()) {
            ivNotification.setVisibility(View.VISIBLE);
        } else ivNotification.setVisibility(View.GONE);

        if (item.isTurnOffMedia()) {
            ivDoNotDisturb.setVisibility(View.VISIBLE);
        } else ivDoNotDisturb.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.btn_modify:
                InsertActionDiaglog insertActionDiaglog = new InsertActionDiaglog();
                insertActionDiaglog.setIdItemData(idItemData);
                insertActionDiaglog.setService(service);
                insertActionDiaglog.setListener((InsertActionDiaglog.IDataChangedListener) getActivity());
                insertActionDiaglog.show(getActivity().getSupportFragmentManager(), "example dialog");
                dismiss();
                break;
            case R.id.btn_delete:
                showDialogDelete();
                break;
        }

    }

    private void showDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Xoá hoạt động");
        builder.setMessage("Bạn có chắc chắn xoá hoạt động này đi không");
        builder.setCancelable(false);
        builder.setPositiveButton("Quay lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAction();
                listener.changedData();
                dismiss();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }


    private void deleteAction() {
        int i = idItemData - service.getData(idItemData).getFlag() * COUNT_DAY;
        int count = service.getData(idItemData).getTimeDoIt();
        for (int j = 0; j < count; j++) {
            service.getData(i).setActive(false);
            i = i + COUNT_DAY;
        }
    }

}
