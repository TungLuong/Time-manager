package tl.com.timemanager.TimeTable;

import android.content.Context;
import android.util.AttributeSet;

import de.codecrafters.tableview.TableView;

public class TableViewCustom<T> extends TableView<T> {
    public TableViewCustom(Context context) {
        super(context);
    }

    public TableViewCustom(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public TableViewCustom(Context context, AttributeSet attributes, int styleAttributes) {
        super(context, attributes, styleAttributes);
    }
}
