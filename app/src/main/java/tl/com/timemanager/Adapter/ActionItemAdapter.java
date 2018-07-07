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
import static tl.com.timemanager.Constant.NO_ACTION;
import static tl.com.timemanager.Constant.OUTSIDE_ACTION;
import static tl.com.timemanager.Constant.RELAX_ACTION;

public class ActionItemAdapter extends RecyclerView.Adapter<ActionItemAdapter.ViewHolder> {

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
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemAction action = iActionItem.getData(position);
        holder.tvTime.setText(action.getHourOfDay() + " h - " + (action.getHourOfDay() + action.getTimeDoIt())  +" h ");
        holder.tvTitle.setText(action.getTitle() + "");
        switch (action.getAction()){
            case NO_ACTION:
                holder.ivAction.setImageResource(R.drawable.no_action);
                holder.background.setBackgroundResource(R.drawable.background_no_action);
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
        public ViewHolder(View itemView) {
            super(itemView);
            ivAction = itemView.findViewById(R.id.iv_img_action);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            background = itemView.findViewById(R.id.background);
        }
    }

    public interface IActionItem{
        int getCount();
        ItemAction getData(int position);
        void onClickItem(int position);

    }
}
