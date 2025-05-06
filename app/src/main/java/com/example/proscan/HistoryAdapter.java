package com.example.proscan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proscan.db.HistoryItem;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<HistoryItem> historyList;
    private OnItemClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnItemClickListener {
        void onItemClick(HistoryItem item);
        void onItemLongClick(HistoryItem item);
        void onDeleteClick(HistoryItem item);
    }

    public HistoryAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
        holder.textType.setText(item.getType());
        holder.textTime.setText(dateFormat.format(item.getTimestamp()));

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(item);
            }
            return true;
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textContent;
        TextView textType;
        TextView textTime;
        ImageButton buttonDelete;

        ViewHolder(View view) {
            super(view);
            textContent = view.findViewById(R.id.textContent);
            textType = view.findViewById(R.id.textType);
            textTime = view.findViewById(R.id.textTime);
            buttonDelete = view.findViewById(R.id.buttonDelete);
        }
    }
} 