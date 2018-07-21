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

    public RealmResults<ItemDataInTimeTable> getAllItemData() {
        realm.beginTransaction();
        RealmResults<ItemDataInTimeTable> rs = realm.where(ItemDataInTimeTable.class).findAll();
        realm.commitTransaction();
        return rs;
    }

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

    public void setModifyForItemData(boolean bool, ItemDataInTimeTable item){
        realm.beginTransaction();
        item.setModifying(bool);
        realm.commitTransaction();
    }
    public void updateItemData(ItemDataInTimeTable item) {

        realm.beginTransaction();
        realm.insertOrUpdate(item);
        realm.commitTransaction();
    }


    public RealmResults<ItemAction> getActionsInWeek(int weekOfYear, int year) {
        realm.beginTransaction();
        RealmResults<ItemAction> actions = realm.where(ItemAction.class).equalTo("weekOfYear", weekOfYear).equalTo("year", year).findAll();
//        RealmResults<ItemAction> actions = realm.where(ItemAction.class).findAll();
        realm.commitTransaction();
        return actions;
    }

    public void insertItemAction(ItemAction action) {
        realm.beginTransaction();
        if (realm.where(ItemAction.class).findAll().size() > 0) {
            int id_new = realm.where(ItemAction.class).max("id").intValue() + 1;
            action.setId(id_new);
        } else {
            action.setId(1);
        }
        realm.insertOrUpdate(action);
        realm.commitTransaction();
    }

    public void updateItemAction(ItemAction action) {
        realm.beginTransaction();
        realm.insertOrUpdate(action);
        realm.commitTransaction();
    }

    public void deleteItemAction(ItemAction action) {
        realm.beginTransaction();
        try {
            action.deleteFromRealm();
        }catch (Exception e){

        };
        realm.commitTransaction();
    }

    public void deleteAll() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    public void close(){
        realm.close();
    }

    public void setModifyForItemAction(boolean b, ItemAction item) {
        realm.beginTransaction();
        item.setModifying(b);
        realm.commitTransaction();
    }

    public void setDoneForItemAction(ItemAction action, boolean b) {
        realm.beginTransaction();
        action.setDone(b);
        realm.commitTransaction();
    }

    public void setCompleteForAction(ItemAction action) {
        realm.beginTransaction();
        action.setComplete(!action.isComplete());
        realm.commitTransaction();
    }
}
