package tl.com.timemanager.dialog.seen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.R;
import tl.com.timemanager.dialog.insert.InsertActionsInDayDialog;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.FREE_TIME;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;

public class SeenActionsInDayDialog extends BaseSeenDialog {

    private int positionItemAction;
    private int dayOfWeek;

    public SeenActionsInDayDialog(@NonNull Context context) {
        super(context);
    }

    public void setPositionItemAction(int positionItemAction) {
        this.positionItemAction = positionItemAction;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


    protected void setData() {
        ItemAction item = service.getActionsInWeek().get(dayOfWeek).get(positionItemAction);
        tvAction.setText(item.getTitle());
        int timeStart = item.getHourOfDay();

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
     * hiển thị dialog sửa hoạt động
     */
    protected void showDialogModifyAction() {
        InsertActionsInDayDialog dialog = new InsertActionsInDayDialog(getContext());
        dialog.setPositionItemAction(positionItemAction);
        dialog.setDayOfWeek(dayOfWeek);
        dialog.setService(service);
        dialog.initView();
        dialog.setiListener(iListener);
        dialog.show();
    }

    /**
     * xoá hoạt động
     */
    @Override
    protected void deleteAction() {
        service.deleteActionByPositionItemAction(dayOfWeek, positionItemAction);
        iListener.changedActionItem();
    }
}
