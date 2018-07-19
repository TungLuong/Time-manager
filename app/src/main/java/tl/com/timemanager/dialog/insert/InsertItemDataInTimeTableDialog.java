package tl.com.timemanager.dialog.insert;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.Item.ItemDataInTimeTable;
import tl.com.timemanager.R;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.COUNT_DAY;
import static tl.com.timemanager.Constant.FREE_TIME;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;
import static tl.com.timemanager.Constant.TIME_MAX;
import static tl.com.timemanager.Constant.TIME_MIN;

public class InsertItemDataInTimeTableDialog extends BaseInsertDialog {

    private static final String TAG = InsertItemDataInTimeTableDialog.class.getSimpleName();

    private int positionItemData;
    private int oldPositionIdItemData;

    public InsertItemDataInTimeTableDialog(@NonNull Context context) {
        super(context);
    }

    public void setPositionItemData(int positionItemData) {
        this.positionItemData = positionItemData;
    }


    protected void setData() {
        oldPositionIdItemData = positionItemData;
        int i = positionItemData - service.getItemDataInTimeTable(positionItemData).getFlag() * COUNT_DAY;
//        if(i < 0) i=  service.getItemDataInTimeTable(positionItemData).getDayOfWeek()  + ((COUNT_TIME-1)*COUNT_DAY) +i;
        ItemDataInTimeTable item = service.getItemDataInTimeTable(i);
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


    }

    protected void setModifyingData(boolean b) {
        ItemDataInTimeTable item = service.getItemDataInTimeTable(oldPositionIdItemData);
        int i = oldPositionIdItemData - item.getFlag() * COUNT_DAY;
        int count = item.getTimeDoIt();
        int j = 0;
        while (j < count && i < service.getCountItemData()) {
            ItemDataInTimeTable itemData  = service.getItemDataInTimeTable(i);
            service.setModifyForItemData(b,itemData);
            i = i + COUNT_DAY;
            j++;
        }
    }


    protected void checkSameTime() {
        int j = 0;
        int i;
        while (j < count) {
            i = positionItemData + COUNT_DAY * j;
            Log.d(TAG,"position" + i);
            if (i >= service.getCountItemData()) {
                tvErrorTime.setVisibility(View.VISIBLE);
                return;
            }
            ItemDataInTimeTable item = service.getItemDataInTimeTable(i);
            if (item.isActive() && !item.isModifying()) {
                tvErrorTime.setVisibility(View.VISIBLE);
                return;
            }
            // Toast.makeText(getActivity(),i+"",Toast.LENGTH_SHORT).show();
//            if (i >= service.getCountItemData()) i = (i-COUNT_DAY * COUNT_TIME ) + 1;
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
                    if (isModify) {
                        service.deleteActionByPositionItemData(oldPositionIdItemData);
                    }
                    updateData();
                    isModify = false;
                    setModifyingData(false);
                    iListener.changedDataItem();
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
//                int hour =service.getItemDataInTimeTable(positionItemData).getHourOfDay();
//                TimePickerDialog dialog = new TimePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog,listener,hour,0,true);
//                dialog.show();
        }
    }


    protected void updateData() {
        if (isModify) {
            positionItemData = positionItemData - service.getItemDataInTimeTable(positionItemData).getFlag() * COUNT_DAY;
        }
        updateItemAction();

        int i = positionItemData;
        int j = 0;
        boolean notifi = swNotification.isChecked();
        boolean doNotDisturb = swDoNotDisturb.isChecked();
        String title = String.valueOf(edtAction.getText());
        while (j < count && i < service.getCountItemData()) {
            ItemDataInTimeTable itemData = service.getItemDataInTimeTable(i);
            if (!itemData.isActive()) {
                ItemDataInTimeTable item = new ItemDataInTimeTable();
                item.setDayOfWeek(itemData.getDayOfWeek());
                item.setHourOfDay(itemData.getHourOfDay());
                item.setId(itemData.getId());
                item.setAction(kindOfAction);
                item.setActive(true);
                item.setNotification(notifi);
                item.setDoNotDisturb(doNotDisturb);
                item.setTitle(title);
                item.setFlag(j);
                item.setTimeDoIt(count);
                service.updateItemData(item);
            }
            j++;
            i = i + COUNT_DAY;
//            if(i >= service.getCountItemData() + COUNT_DAY) break;
//            else if (i >= service.getCountItemData()) i = ( i - COUNT_DAY * COUNT_TIME  ) + 1;
        }
        service.updateTimeTable();
    }

    private void updateItemAction() {
        ItemDataInTimeTable itemDataInTimeTable = service.getItemDataInTimeTable(positionItemData);
        String title = String.valueOf(edtAction.getText());
        ItemAction action = new ItemAction();
        action.setTitle(title);
        action.setAction(kindOfAction);
        action.setDayOfWeek(itemDataInTimeTable.getDayOfWeek());
        action.setHourOfDay(itemDataInTimeTable.getHourOfDay());
        action.setTimeDoIt(count);
        action.setNotification(swNotification.isChecked());
        action.setDoNotDisturb(swDoNotDisturb.isChecked());
        int day = service.getItemDataInTimeTable(positionItemData).getDayOfWeek();
        service.insertItemActionFromTimeTable(day,action);
    }

    protected void checkInvalidTimeStart() {
        if (edtTimeStart.getText().toString().trim().length() > 0) {
            int time = Integer.valueOf(edtTimeStart.getText().toString());
            if (time >= TIME_MIN && time <= TIME_MAX) {
                int day = service.getItemDataInTimeTable(positionItemData).getDayOfWeek();
                int newId = day + COUNT_DAY * (time - TIME_MIN);
//                if (newId > service.getCountItemData()) {
//                    tvErrorTimeStart.setVisibility(View.VISIBLE);
//                    return;
//                }
//                if (service.getItemDataInTimeTable(newId).isActive() && !service.getItemDataInTimeTable(newId).isModifying()) {
//                    tvErrorTimeStart.setVisibility(View.VISIBLE);
//                    return;
//                }
                positionItemData = newId;
                checkSameTime();
                tvErrorTimeStart.setVisibility(View.GONE);
            } else {
                tvErrorTimeStart.setVisibility(View.VISIBLE);
            }
        } else {
            tvErrorTimeStart.setVisibility(View.VISIBLE);
        }
    }


}
