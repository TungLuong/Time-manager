package tl.com.timemanager.dialog;

import android.view.View;

import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.Item.ItemData;
import tl.com.timemanager.R;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.COUNT_TIME;
import static tl.com.timemanager.Constant.NO_ACTION;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;

public class SeenActionsInDayDialog extends SeenActionDialog {
    private InsertActionsInDayDialog.IDataActionChangedListener iDataActionChangedListener;
    private int idItemAction;
    private int day;

    public void setIdItemAction(int idItemAction) {
        this.idItemAction = idItemAction;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setiDataActionChangedListener(InsertActionsInDayDialog.IDataActionChangedListener iDataActionChangedListener) {
        this.iDataActionChangedListener = iDataActionChangedListener;
    }

    protected void setItemData() {
        ItemAction item = service.getActionsInDays().get(day).get(idItemAction);
        tvAction.setText(item.getTitle());
        int timeStart = item.getTime();

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
        InsertActionsInDayDialog dialog = new InsertActionsInDayDialog();
        dialog.setIdItemAction(idItemAction);
        dialog.setService(service);
        dialog.setiDataActionChangedListener(iDataActionChangedListener);
        dialog.show(getActivity().getSupportFragmentManager(), "example dialog");
    }

    @Override
    protected void deleteAction() {
        service.deleteActionByIdItemAction(day,idItemAction);
        iDataActionChangedListener.changedDataAction();
    }
}
