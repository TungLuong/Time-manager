package tl.com.timemanager.Adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tl.com.timemanager.Item.ItemAction;
import tl.com.timemanager.R;

import static tl.com.timemanager.Constant.AMUSING_ACTION;
import static tl.com.timemanager.Constant.AT_HOME_ACTION;
import static tl.com.timemanager.Constant.FREE_TIME;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;

public class ActionItemAdapter extends RecyclerView.Adapter<ActionItemAdapter.ViewHolder> {

    private static final float ALPHA_DEFAULT = 0.3f;
    private static final float ALPHA_ACTION_DONE = 0.99f ;
    private IActionItem iActionItem;


    public ActionItemAdapter(IActionItem iActionItem) {
        this.iActionItem = iActionItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_action, parent, false);
        final ActionItemAdapter.ViewHolder viewHolder = new ActionItemAdapter.ViewHolder(itemView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActionItem.onClickItem(viewHolder.getAdapterPosition());
            }
        });

        viewHolder.ivActionDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActionItem.setCompleteForAction(viewHolder.getAdapterPosition());
                notifyItemChanged(viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemAction action = iActionItem.getItemAction(position);
        if(action.getTitle() != null){
            holder.tvTime.setText(action.getHourOfDay() + " h - " + (action.getHourOfDay() + action.getTimeDoIt())  +" h ");
            holder.tvTitle.setText(action.getTitle() + "");
            if(action.isDone()){
                holder.background.setAlpha( ALPHA_ACTION_DONE );
                holder.ivActionDone.setVisibility(View.VISIBLE);
                if(action.isComplete()){
                    holder.ivActionDone.setImageResource(R.drawable.ic_complete_24dp);
                }
                else {
                    holder.ivActionDone.setImageResource(R.drawable.ic_action_not_complete_24dp);
                }
            }
            else {
                holder.ivActionDone.setVisibility(View.GONE);
                holder.background.setAlpha( ALPHA_DEFAULT );
            }
            switch (action.getAction()){
                case FREE_TIME:
                    holder.ivAction.setImageResource(R.drawable.free_time);
                    holder.background.setBackgroundResource(R.drawable.background_free_time);
                    break;
                case OUTSIDE_ACTION:
                    holder.ivAction.setImageResource(R.drawable.school);
                    holder.background.setBackgroundResource(R.drawable.background_action_outside);
                    break;
                case AT_HOME_ACTION:
                    holder.ivAction.setImageResource(R.drawable.homework);
                    holder.background.setBackgroundResource(R.drawable.background_action_at_home);
                    break;
                case AMUSING_ACTION:
                    holder.ivAction.setImageResource(R.drawable.giaitri);
                    holder.background.setBackgroundResource(R.drawable.background_action_entertainment);
                    break;
                case RELAX_ACTION:
                    holder.ivAction.setImageResource(R.drawable.sleep);
                    holder.background.setBackgroundResource(R.drawable.background_action_relax);
                    break;
            }
        }else {
//            iActionItem.removeItemAction(position);
//            notifyDataSetChanged();
        }

    }

    @Override
    public int getItemCount() {
        return iActionItem.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivAction ;
        private TextView tvTitle;
        private TextView tvTime;
        private RelativeLayout background;
        private ImageView ivActionDone;
        public ViewHolder(View itemView) {
            super(itemView);
            ivAction = itemView.findViewById(R.id.iv_img_action);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            background = itemView.findViewById(R.id.background);
            ivActionDone = itemView.findViewById(R.id.iv_action_done);
        }
    }

    public interface IActionItem{
        int getCount();
        ItemAction getItemAction(int position);
        void onClickItem(int position);
        void setCompleteForAction(int adapterPosition);

      //  void removeItemAction(int position);
        //  void deleteAction(int day,int position);
    }
}
