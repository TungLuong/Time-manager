package tl.com.timemanager.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tl.com.timemanager.Item.ItemData;
import tl.com.timemanager.R;

import static tl.com.timemanager.Constant.COUNT_TIME;
import static tl.com.timemanager.Constant.TIME_MIN;

public class TimeItemAdapter extends RecyclerView.Adapter<TimeItemAdapter.ViewHolder> {

    private List<Integer> time = new ArrayList<>();
    public TimeItemAdapter() {
        for(int i =0;i<COUNT_TIME;i++){
            time.add(i + TIME_MIN);
        }
    }

    @NonNull
    @Override
    public TimeItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_time, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvTitle.setText(time.get(position)+" h");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    @Override
    public int getItemCount() {
        //return iDataAdapter.countPlayed();
        return time.size();
    }


}