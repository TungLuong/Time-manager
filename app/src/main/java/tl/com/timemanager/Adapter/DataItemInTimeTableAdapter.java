package tl.com.timemanager.Adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tl.com.timemanager.Item.ItemDataInTimeTable;
import tl.com.timemanager.R;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.NO_ACTION;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;

public class DataItemInTimeTableAdapter extends RecyclerView.Adapter<DataItemInTimeTableAdapter.ViewHolder> {

    private IDataItem iDataItem;
    private int currentFocus = 0;
    public DataItemInTimeTableAdapter(IDataItem iDataItem) {
        this.iDataItem = iDataItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_data_in_time_table, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ll_root:
                        int position = viewHolder.getAdapterPosition();
                        iDataItem.onClickItem(position);
                        break;
                }
            }
        });

        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
       //holder.tvTitle.setText( "( "+iDataItem.getItemDataInTimeTable(position).getHourOfDay()+","+ iDataItem.getItemDataInTimeTable(position).getDayOfWeek() +" )");
//       holder.itemView.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               iDataItem.onClickItem(holder.getAdapterPosition());
//           }
//       });

        ItemDataInTimeTable item = iDataItem.getData(position);
        if(!item.isActive()) {
            if (position == currentFocus) {
                holder.itemView.setBackgroundResource(R.drawable.plus_1);
            } else {
                holder.itemView.setBackgroundResource(R.color.colorDefault);
            }
            holder.tvTitle.setText("");
        }
        else {
            switch (item.getAction()){
                case NO_ACTION : holder.itemView.setBackgroundResource(R.color.colorFreeTime);break;
                case OUTSIDE_ACTION : holder.itemView.setBackgroundResource(R.color.colorOutSideAction); break;
                case AT_HOME_ACTION : holder.itemView.setBackgroundResource(R.color.colorHomework); break;
                case AMUSING_ACTION : holder.itemView.setBackgroundResource(R.color.colorEntertainment); break;
                case RELAX_ACTION : holder.itemView.setBackgroundResource(R.color.colorRelax); break;
                default:
                    break;
            }
            if(item.getFlag() == 0){
                holder.tvTitle.setText(item.getTitle());
            }else {
                holder.tvTitle.setText("");
            }
        }
    }

    public void updatePositionFocus(int position) {
        if ( currentFocus == position ){
            return;
        }
        if(currentFocus == -1)return;
        int old = currentFocus;
        currentFocus = position;
        notifyItemChanged(position);
        notifyItemChanged(old);
    }



    @Override
    public int getItemCount() {
        return iDataItem.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }

    }

    public interface IDataItem{
        int getCount();
        ItemDataInTimeTable getData(int position);
        void onClickItem(int position);
    }

}