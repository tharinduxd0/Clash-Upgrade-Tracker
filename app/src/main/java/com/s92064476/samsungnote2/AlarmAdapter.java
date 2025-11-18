package com.s92064476.samsungnote2;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<AlarmModel> alarmList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public AlarmAdapter(List<AlarmModel> alarmList, OnItemClickListener listener) {
        this.alarmList = alarmList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlarmModel item = alarmList.get(position);

        holder.tvDesc.setText(item.getDescription());
        holder.tvTime.setText(item.getFormattedTime());

        // --- FEATURE 4: PROGRESS BAR CALCULATION ---
        long now = System.currentTimeMillis();
        long totalDuration = item.getTargetTimeInMillis() - item.getStartTimeInMillis();
        long timePassed = now - item.getStartTimeInMillis();

        int progress = 0;
        if (totalDuration > 0) {
            progress = (int) ((timePassed * 100) / totalDuration);
        }

        if (progress < 0) progress = 0;
        if (progress > 100) progress = 100;

        holder.progressBar.setProgress(progress);

        // Color code progress bar (Orange normally, Green if done)
        if (progress >= 100) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        } else {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
        }

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDesc, tvTime;
        Button btnDelete;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tvDescItem);
            tvTime = itemView.findViewById(R.id.tvTimeItem);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}