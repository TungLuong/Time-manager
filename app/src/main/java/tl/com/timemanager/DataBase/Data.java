package tl.com.timemanager.DataBase;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmMigrationNeededException;
import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.Item.ItemDataInTimeTable;
import tl.com.timemanager.MainActivity;
import tl.com.timemanager.MyApplication;

public class Data {
    private Realm realm;

    public Data() {

//        try{
            realm = Realm.getDefaultInstance();
//        }catch (Exception e){
//        }
    }

    /**
     * lấy ra các Item Data trong bảng thời gian biểu
     * @return
     */
    public RealmResults<ItemDataInTimeTable> getAllItemData() {
        realm.beginTransaction();
        RealmResults<ItemDataInTimeTable> rs = realm.where(ItemDataInTimeTable.class).findAll();
        realm.commitTransaction();
        return rs;
    }

    /**
     * Thêm item data trong bảng thời gian biểu vào cơ sở dữ liệu
     * @param item
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void insertItemData(ItemDataInTimeTable item) {
        realm.beginTransaction();
        if (realm.where(ItemDataInTimeTable.class).findAll().size() > 0) {
            int id_new = Objects.requireNonNull(realm.where(ItemDataInTimeTable.class).max("id")).intValue() + 1;
            item.setId(id_new);
        } else {
            item.setId(1);
        }
        realm.insertOrUpdate(item);
        realm.commitTransaction();
    }

    /**
     * cập nhật lại dữ liệu về thời gian biểu
     * @param itemDataInTimeTables
     */
    public void updateTimeTable(final List<ItemDataInTimeTable> itemDataInTimeTables) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(itemDataInTimeTables);
            }
        });
//        realm.beginTransaction();
////        for(ItemDataInTimeTable item  : timeTables) {
////            realm.insertOrUpdate(item);
////        }
//        realm.commitTransaction();
    }

    /**
     * xét thuộc tính modify cho item data
     * @param bool
     * @param item
     */
    public void setModifyForItemData(boolean bool, ItemDataInTimeTable item){
        realm.beginTransaction();
        item.setModifying(bool);
        realm.commitTransaction();
    }

    /**
     * cập nhật lại item data
     * @param item
     */
    public void updateItemData(ItemDataInTimeTable item) {
        realm.beginTransaction();
        realm.insertOrUpdate(item);
        realm.commitTransaction();
    }

    /**
     *  lấy ra các hoạt động trong tuần
     * @param weekOfYear tuần
     * @param year năm
     * @return
     */

    public RealmResults<ItemAction> getActionsInWeek(int weekOfYear, int year) {
        realm.beginTransaction();
        RealmResults<ItemAction> actions = realm.where(ItemAction.class).equalTo("weekOfYear", weekOfYear).equalTo("year", year).findAll();
//        RealmResults<ItemAction> actions = realm.where(ItemAction.class).findAll();
        realm.commitTransaction();
        return actions;
    }

    /**
     * lấy ra các hoạt động trong ngày
     * @param dayOfWeek ngày
     * @param weekOfYear tuần
     * @param year năm
     * @return
     */
    public RealmResults<ItemAction> getActionsInDay(int dayOfWeek,int weekOfYear, int year) {
        realm.beginTransaction();
        RealmResults<ItemAction> actions = realm.where(ItemAction.class).equalTo("dayOfWeek",dayOfWeek).equalTo("weekOfYear", weekOfYear).equalTo("year", year).findAll();
//        RealmResults<ItemAction> actions = realm.where(ItemAction.class).findAll();
        realm.commitTransaction();
        return actions;
    }

    /**
     * thêm hoạt động vào trong cơ sở dữ liệu
     * @param action hoạt động
     * @return id của hoạt động
     */
    public int insertItemAction(ItemAction action) {
        realm.beginTransaction();
        int id =0;
        if (realm.where(ItemAction.class).findAll().size() > 0) {
            id = realm.where(ItemAction.class).max("id").intValue() + 1;
        } else {
            id = 1;
        }
        action.setId(id);
        realm.insertOrUpdate(action);
        realm.commitTransaction();
        return id;
    }

    /**
     * cập nhật thông tin của hoạt động
     * @param action hoạt động
     */
    public void updateItemAction(ItemAction action) {
        realm.beginTransaction();
        realm.insertOrUpdate(action);
        realm.commitTransaction();
    }

    /**
     * Xoá hoạt động
     * @param action hoạt động
     */
    public void deleteItemAction(ItemAction action) {
        realm.beginTransaction();
        try {
            action.deleteFromRealm();
        }catch (Exception e){

        };
        realm.commitTransaction();
    }

//    public void deleteAll() {
//        realm.beginTransaction();
//        realm.deleteAll();
//        realm.commitTransaction();
//    }

    public void close(){
        realm.close();
    }

    /**
     * xét thuộc tính modify cho hoạt động
     * @param b giá trị
     * @param action hoạt động
     */
    public void setModifyForItemAction(boolean b, ItemAction action) {
        realm.beginTransaction();
        action.setModifying(b);
        realm.commitTransaction();
    }

    /**
     * xét thuộc tính đã trải qua thời gian thực hiện cho hoạt động
     * @param action
     * @param b
     */
    public void setDoneForItemAction(ItemAction action, boolean b) {
        realm.beginTransaction();
        action.setDone(b);
        realm.commitTransaction();
    }

    /**
     * xét thuộc tính đã hoàn thành cho hoạt động
     * @param action
     */
    public void setCompleteForAction(ItemAction action) {
        realm.beginTransaction();
        action.setComplete(!action.isComplete());
        realm.commitTransaction();
    }

    /**
     * lấy hoạt động
     * @param id id của hoạt động
     * @return hoạt động
     */
    public ItemAction getActionFromDBById(int id) {
        realm.beginTransaction();
      //  RealmResults<ItemAction> actions = realm.where(ItemAction.class).equalTo("id",id).equalTo("dayOfWeek",dayOfWeek).equalTo("weekOfYear", weekOfYear).equalTo("year", year).findAll();
        RealmResults<ItemAction> actions = realm.where(ItemAction.class).equalTo("id",id).findAll();
        realm.commitTransaction();
        if(actions != null) {
            if(actions.size() > 0) {
                return actions.get(0);
            }else return null;
        }else return null;
    }
}
