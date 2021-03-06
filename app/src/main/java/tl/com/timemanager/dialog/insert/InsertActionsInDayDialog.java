package tl.com.timemanager.dialog.insert;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.R;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.FREE_TIME;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;
import static tl.com.timemanager.Constant.TIME_MAX;
import static tl.com.timemanager.Constant.TIME_MIN;

public class InsertActionsInDayDialog extends BaseInsertDialog {

    private int dayOfWeek;
    private int positionItemAction;
    private int oldPositionItemAction;

    public InsertActionsInDayDialog(@NonNull Context context) {
        super(context);
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setPositionItemAction(int positionItemAction) {
        this.positionItemAction = positionItemAction;
    }


    @Override
    protected void setData() {
        oldPositionItemAction = positionItemAction;
//        if(i < 0) i=  service.getItemDataInTimeTable(idItemData).getDayOfWeek()  + ((COUNT_TIME-1)*COUNT_DAY) +i;
        ItemAction item = service.getActionsInWeek().get(dayOfWeek).get(positionItemAction);
        if (item.getTitle() != null) {
            isModify = true;
            edtTitleAction.setText(item.getTitle());
            spin_time.setSelection(item.getTimeDoIt() - 1);
            spin_action.setSelection(item.getAction());

            if (item.isNotification()) {
                swNotification.setChecked(true);
            } else swNotification.setChecked(false);

            if (item.isDoNotDisturb()) {
                swDoNotDisturb.setChecked(true);
            } else swDoNotDisturb.setChecked(false);

            switch (item.getAction()) {
                case FREE_TIME:
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

        if (isModify) {
            setModifyingData(true);
        } else {
            setModifyingData(false);
        }
        edtTimeStart.setText(item.getHourOfDay() + "");
        checkInvalidTimeStart();

    }

    /**
     * xet hoạt động có phải đang đc chỉnh sửa
     * @param b
     */
    @Override
    protected void setModifyingData(boolean b) {
        ItemAction item = service.getActionsInWeek().get(dayOfWeek).get(oldPositionItemAction);
        service.setModifyForItemAction(b, item);
    }

    /**
     * kiểm tra trùng thời gian
     */
    protected void checkSameTime() {
        List<ItemAction> itemActions = service.getActionsInWeek().get(dayOfWeek);
        List<ItemAction> actions = new ArrayList<>();
        for (ItemAction action : itemActions) {
            actions.add(action);
        }
//        ItemAction item = actions.get(positionItemAction);
        ItemAction item = actions.remove(positionItemAction);
        try {
            int timeStart = Integer.valueOf(String.valueOf(edtTimeStart.getText() + ""));
            int timeEnd = timeStart + count;
            if(timeEnd > TIME_MAX + 1){
                tvErrorTime.setVisibility(View.VISIBLE);
                return;
            }
            if (actions.size() > 0) {
                for (ItemAction action : actions) {
                    int start = action.getHourOfDay();
                    int end = action.getHourOfDay() + action.getTimeDoIt();
                    if (end <= timeStart || start >= timeEnd) {
                        tvErrorTime.setVisibility(View.GONE);
                    } else {
                        tvErrorTime.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }
            tvErrorTime.setVisibility(View.GONE);
        } catch (Exception e) {
            tvErrorTime.setVisibility(View.VISIBLE);
        }

    }

    /**
     * cập nhật dữ liệu
     */
    protected void updateData() {
        String title = String.valueOf(edtTitleAction.getText());
        int time = Integer.valueOf(String.valueOf(edtTimeStart.getText() + ""));
        ItemAction itemAction = service.getActionsInWeek().get(dayOfWeek).remove(positionItemAction);
        ItemAction action = new ItemAction();
        action.setId(itemAction.getId());
        action.setDayOfWeek(itemAction.getDayOfWeek());
        action.setYear(itemAction.getYear());
        action.setModifying(itemAction.isModifying());
        action.setWeekOfYear(itemAction.getWeekOfYear());
        action.setTitle(title);
        action.setAction(kindOfAction);
        action.setHourOfDay(time);
        action.setTimeDoIt(count);
        action.setNotification(swNotification.isChecked());
        action.setDoNotDisturb(swDoNotDisturb.isChecked());
        action.setComplete(itemAction.isComplete());
        service.updateItemAction(action);
        service.getActionsInWeek().get(dayOfWeek).add(action);

    }

    /**
     * kiểm tra thời gian bắt đầu có phù hợp k
     */
    protected void checkInvalidTimeStart() {
        if (edtTimeStart.getText().toString().trim().length() > 0 && edtTimeStart.getText().toString().trim().length() < 3) {
            int time = Integer.valueOf(edtTimeStart.getText().toString());
            if (time >= TIME_MIN && time <= TIME_MAX) {
//                List<ItemAction> itemActions = service.getActionsInWeek().get(dayOfWeek);
//                List<ItemAction> actions = new ArrayList<>();
//                for (ItemAction action : itemActions) {
//                    actions.add(action);
//                }
//                ItemAction item = actions.remove(positionItemAction);
//                try {
//                    int timeStart = time;
//                    int timeEnd = timeStart + count;
//                    if (actions.size() > 0) {
//                        for (ItemAction action : actions) {
//                            int start = action.getHourOfDay();
//                            int end = action.getHourOfDay() + action.getTimeDoIt();
//                            if (end <= timeStart || start >= timeEnd) {
//                                tvErrorTimeStart.setVisibility(View.GONE);
//                            } else {
//                                tvErrorTimeStart.setVisibility(View.VISIBLE);
//                                return;
//                            }
//                        }
//                    }
//                    tvErrorTimeStart.setVisibility(View.GONE);
//                } catch (Exception e) {
//                    tvErrorTimeStart.setVisibility(View.VISIBLE);
//                }
                tvErrorTimeStart.setVisibility(View.GONE);
                checkSameTime();
            } else {
                tvErrorTimeStart.setVisibility(View.VISIBLE);
            }
        } else {
            tvErrorTimeStart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                if (isModify) {
                    isModify = false;
                    setModifyingData(false);
                } else service.deleteActionByPositionItemAction(dayOfWeek, oldPositionItemAction);
                iListener.changedActionItem();
                dismiss();
                break;
            case R.id.btn_save:
                checkInvalidTitle();
                if (tvErrorTime.getVisibility() == View.GONE
                        && tvErrorTitle.getVisibility() == View.GONE
                        && tvErrorTimeStart.getVisibility() == View.GONE) {
                    updateData();
                    isModify = false;
                    setModifyingData(false);
                    service.sortActionByTime(dayOfWeek);
                    iListener.changedActionItem();
                    dismiss();
                }
                break;
        }
    }


}
