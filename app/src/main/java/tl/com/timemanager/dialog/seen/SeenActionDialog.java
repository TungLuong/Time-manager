package tl.com.timemanager.dialog.seen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import tl.com.timemanager.dialog.insert.InsertActionDialog;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.COUNT_TIME;
import static tl.com.timemanager.Constant.NO_ACTION;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;

public class SeenActionDialog extends BaseSeenDialog implements View.OnClickListener {


    private int idItemData;

    public SeenActionDialog(@NonNull Context context) {
        super(context);
    }

    public void setIdItemData(int idItemData) {
        this.idItemData = idItemData;
    }

    protected void setData() {
        ItemData item = service.getData(idItemData);
        tvAction.setText(item.getTitle());
        int timeStart = item.getTime() - item.getFlag();
        if(timeStart < 0) timeStart =timeStart+COUNT_TIME;
        tvTimeStart.setText(timeStart + " h");
        tvTimeEnd.setText((timeStart + item.getTimeDoIt()) + " h");
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

        if (item.isDoNotDisturb()) {
            ivDoNotDisturb.setVisibility(View.VISIBLE);
        } else ivDoNotDisturb.setVisibility(View.GONE);
    }

    protected void showDialogModifyAction() {
        InsertActionDialog insertActionDialog = new InsertActionDialog(getContext());
        insertActionDialog.setIdItemData(idItemData);
        insertActionDialog.setService(service);
        insertActionDialog.setiListener(iListener);
        insertActionDialog.initView();
        insertActionDialog.show();
    }

    protected void deleteAction() {
        service.deleteAction(idItemData);
        iListener.changedDataItem();
    }

}
