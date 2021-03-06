package tl.com.timemanager.dialog.seen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import tl.com.timemanager.Item.ItemDataInTimeTable;
import tl.com.timemanager.R;
import tl.com.timemanager.dialog.insert.InsertItemDataInTimeTableDialog;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.COUNT_TIME;
import static tl.com.timemanager.Constant.FREE_TIME;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;

public class SeenActionInTimeTableDialog extends BaseSeenDialog implements View.OnClickListener {


    private int positionItemData;

    public SeenActionInTimeTableDialog(@NonNull Context context) {
        super(context);
    }

    public void setPositionItemData(int positionItemData) {
        this.positionItemData = positionItemData;
    }

    protected void setData() {
        ItemDataInTimeTable item = service.getItemDataInTimeTable(positionItemData);
        tvAction.setText(item.getTitle());
        int timeStart = item.getHourOfDay() - item.getFlag();
        if(timeStart < 0) timeStart =timeStart+COUNT_TIME;
        tvTimeStart.setText(timeStart + " h");
        tvTimeEnd.setText((timeStart + item.getTimeDoIt()) + " h");
        switch (item.getAction()) {
            case FREE_TIME:
                ivAction.setImageResource(R.drawable.free_time);
                tvKindOfAction.setText("Hoạt động tự do");
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

    /**
     * hiển thị dialog để sửa thông tin
     */
    protected void showDialogModifyAction() {
        InsertItemDataInTimeTableDialog insertItemDataInTimeTableDialog = new InsertItemDataInTimeTableDialog(getContext());
        insertItemDataInTimeTableDialog.setPositionItemData(positionItemData);
        insertItemDataInTimeTableDialog.setService(service);
        insertItemDataInTimeTableDialog.setiListener(iListener);
        insertItemDataInTimeTableDialog.initView();
        insertItemDataInTimeTableDialog.show();
    }

    /**
     * xoá hoạt động
     */
    protected void deleteAction() {
        // delete item data not changed item action
        service.deleteItemDataFromTimeTable(positionItemData);
        iListener.changedDataItem();
    }

}
