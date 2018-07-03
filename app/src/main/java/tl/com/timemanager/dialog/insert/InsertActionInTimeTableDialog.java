package tl.com.timemanager.dialog.insert;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.Item.ItemDataInTimeTable;
import tl.com.timemanager.R;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.COUNT_DAY;
import static tl.com.timemanager.Constant.NO_ACTION;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;
import static tl.com.timemanager.Constant.TIME_MAX;
import static tl.com.timemanager.Constant.TIME_MIN;

public class InsertActionInTimeTableDialog extends BaseInsertDialog {

    private static final String TAG = InsertActionInTimeTableDialog.class.getSimpleName();

    private int idItemData;
    private int oldIdItemData;

    public InsertActionInTimeTableDialog(@NonNull Context context) {
        super(context);
    }

    public void setIdItemData(int idItemData) {
        this.idItemData = idItemData;
    }


    protected void setData() {
        oldIdItemData = idItemData;
        int i = idItemData - service.getItemDataInTimeTable(idItemData).getFlag() * COUNT_DAY;
//        if(i < 0) i=  service.getItemDataInTimeTable(idItemData).getDay()  + ((COUNT_TIME-1)*COUNT_DAY) +i;
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

        if (isModify) {
            setModifyingData(true);
        } else {
            setModifyingData(false);
        }

        edtTimeStart.setText(item.getTime() + "");


    }

    protected void setModifyingData(boolean b) {
        ItemDataInTimeTable item = service.getItemDataInTimeTable(oldIdItemData);
        int i = oldIdItemData - item.getFlag() * COUNT_DAY;
        int count = item.getTimeDoIt();
        int j = 0;
        while (j < count && i < service.getCountItemData()) {
            service.getItemDataInTimeTable(i).setModifying(b);
            i = i + COUNT_DAY;
            j++;
        }
    }


    protected void checkSameTime() {
        int j = 0;
        int i;
        while (j < count) {
            i = idItemData + COUNT_DAY * j;
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
                        service.deleteAction(oldIdItemData);
                    }
                    createData();
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
//                int hour =service.getItemDataInTimeTable(idItemData).getTime();
//                TimePickerDialog dialog = new TimePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog,listener,hour,0,true);
//                dialog.show();
        }
    }


    protected void createData() {
        if (isModify) {
            idItemData = idItemData - service.getItemDataInTimeTable(idItemData).getFlag() * COUNT_DAY;
        }
        createNewAction();

        int i = idItemData;
        int j = 0;
        boolean notifi = swNotification.isChecked();
        boolean doNotDisturb = swDoNotDisturb.isChecked();
        String title = String.valueOf(edtAction.getText());
        while (j < count && i < service.getCountItemData()) {
            ItemDataInTimeTable item = service.getItemDataInTimeTable(i);
            if (!item.isActive()) {
                item.setAction(kindOfAction);
                item.setActive(true);
                item.setNotification(notifi);
                item.setDoNotDisturb(doNotDisturb);
                item.setTitle(title);
                item.setFlag(j);
                item.setTimeDoIt(count);
                j++;
            }
            i = i + COUNT_DAY;
//            if(i >= service.getCountItemData() + COUNT_DAY) break;
//            else if (i >= service.getCountItemData()) i = ( i - COUNT_DAY * COUNT_TIME  ) + 1;
        }
    }

    private void createNewAction() {
        ItemDataInTimeTable itemDataInTimeTable = service.getItemDataInTimeTable(idItemData);
        String title = String.valueOf(edtAction.getText());
        ItemAction action = new ItemAction();
        action.setTitle(title);
        action.setAction(kindOfAction);
        action.setDay(itemDataInTimeTable.getDay());
        action.setTime(itemDataInTimeTable.getTime());
        action.setTimeDoIt(count);
        action.setNotification(swNotification.isChecked());
        action.setDoNotDisturb(swDoNotDisturb.isChecked());
        service.getActionsInDays().get(service.getItemDataInTimeTable(idItemData).getDay()).add(action);
    }

    protected void checkInvalidTimeStart() {
        if (edtTimeStart.getText().toString().trim().length() > 0) {
            int time = Integer.valueOf(edtTimeStart.getText().toString());
            if (time >= TIME_MIN && time <= TIME_MAX) {
                int day = service.getItemDataInTimeTable(idItemData).getDay();
                int newId = day + COUNT_DAY * (time - TIME_MIN);
                if (newId > service.getCountItemData()) {
                    tvErrorTimeStart.setVisibility(View.VISIBLE);
                    return;
                }
                if (service.getItemDataInTimeTable(newId).isActive() && !service.getItemDataInTimeTable(newId).isModifying()) {
                    tvErrorTimeStart.setVisibility(View.VISIBLE);
                    return;
                }
                idItemData = newId;
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
