package tl.com.timemanager.dialog;

import android.view.View;

import java.util.List;

import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.R;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.NO_ACTION;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;
import static tl.com.timemanager.Constant.TIME_MAX;
import static tl.com.timemanager.Constant.TIME_MIN;

public class InsertActionsInDayDialog extends InsertActionDialog {
    private int day;
    private int idItemAction;
    private int oldInItemAction;
    public void setDay(int day) {
        this.day = day;
    }
    private IDataActionChangedListener iDataActionChangedListener;

    public void setIdItemAction(int idItemAction) {
        this.idItemAction = idItemAction;
    }

    public void setiDataActionChangedListener(IDataActionChangedListener iDataActionChangedListener) {
        this.iDataActionChangedListener = iDataActionChangedListener;
    }

    @Override
    protected void setItemData() {
        oldInItemAction = idItemAction;
//        if(i < 0) i=  service.getData(idItemData).getDay()  + ((COUNT_TIME-1)*COUNT_DAY) +i;
        ItemAction item = service.getActionsInDays().get(day).get(idItemAction);
        if (item.getTitle() != null) {
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

    @Override
    protected void setModifyingData(boolean b) {
        ItemAction item = service.getActionsInDays().get(day).get(oldInItemAction);
        item.setModifying(b);
    }

    protected void checkSameTime() {
        int j=0;
        List<ItemAction> actions = service.getActionsInDays().get(day);
        ItemAction item = service.getActionsInDays().get(day).get(idItemAction);

        try {
            int time = Integer.valueOf(String.valueOf(edtTimeStart.getText() + ""));
            while (j < count ) {
                time = time + j;
                for(ItemAction action : actions){
                    if(action.getTime() <= time && action.getTime()+action.getTimeDoIt() >= time && !item.isModifying()){
                        tvErrorTime.setVisibility(View.VISIBLE);
                        return ;
                    }
                }
                j++;
            }
            tvErrorTime.setVisibility(View.GONE);
        }catch (Exception e){
            tvErrorTime.setVisibility(View.VISIBLE);
        }

    }

    protected void createData() {
        String title = String.valueOf(edtAction.getText());
        int time = Integer.valueOf(String.valueOf(edtTimeStart.getText()+""));
        ItemAction action = service.getActionsInDays().get(day).get(idItemAction);
        action.setTitle(title);
        action.setAction(kindOfAction);
        action.setDay(day);
        action.setTime(time);
        action.setTimeDoIt(count);
        action.setNotification(swNotification.isChecked());
        action.setDoNotDisturb(swDoNotDisturb.isChecked());

    }

    protected void checkInvalidTimeStart() {
        if (edtTimeStart.getText().toString().trim().length() > 0) {
            int time = Integer.valueOf(edtTimeStart.getText().toString());
            if (time >= TIME_MIN && time <= TIME_MAX) {
                int j = 0;
                List<ItemAction> actions = service.getActionsInDays().get(day);
                ItemAction item = service.getActionsInDays().get(day).get(idItemAction);
                while (j < count) {
                    time = time + j;
                    for (ItemAction action : actions) {
                        if (action.getTime() <= time && action.getTime() + action.getTimeDoIt() >= time && !item.isModifying()) {
                            tvErrorTimeStart.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    j++;
                }
                checkSameTime();
                tvErrorTimeStart.setVisibility(View.GONE);
            } else {
                tvErrorTimeStart.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                if (isModify) {
                    isModify = false;
                    setModifyingData(false);
                }
                service.deleteActionByIdItemAction(day,oldInItemAction);
                iDataActionChangedListener.changedDataAction();
                dismiss();
                break;
            case R.id.btn_save:
                checkInvalidTitle();
                if (tvErrorTime.getVisibility() == View.GONE
                        && tvErrorTitle.getVisibility() == View.GONE
                        && tvErrorTimeStart.getVisibility() == View.GONE) {
                    createData();
                    isModify = false;
                    setModifyingData(false);
                    iDataActionChangedListener.changedDataAction();
                    dismiss();
                }
                break;
        }
    }

    public interface IDataActionChangedListener{
        void changedDataAction();
    }


}
