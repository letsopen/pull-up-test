package com.example.proscan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proscan.R;
import com.example.proscan.db.HistoryItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<HistoryItem> historyList;
    private OnItemClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnItemClickListener {
        void onItemClick(HistoryItem item);
    }

    public HistoryAdapter(List<HistoryItem> historyList, OnItemClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);
        holder.textContent.setText(item.getContent());
        holder.textType.setText(item.getType().equals("scan") ? "扫码" : "粘贴");
        holder.textTime.setText(dateFormat.format(new Date(item.getTimestamp())));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<HistoryItem> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textContent;
        TextView textType;
        TextView textTime;

        ViewHolder(View view) {
            super(view);
            textContent = view.findViewById(R.id.textContent);
            textType = view.findViewById(R.id.textType);
            textTime = view.findViewById(R.id.textTime);
        }
    }
} 