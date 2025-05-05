package com.example.proscan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proscan.adapter.HistoryAdapter;
import com.example.proscan.db.HistoryDbHelper;
import com.example.proscan.db.HistoryItem;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private HistoryDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new HistoryDbHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoryAdapter(dbHelper.getAllHistory(), this::onHistoryItemClick);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.buttonClear).setOnClickListener(v -> showClearHistoryDialog());
    }

    private void onHistoryItemClick(HistoryItem item) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getContent()));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "无法打开链接", Toast.LENGTH_SHORT).show();
        }
    }

    private void showClearHistoryDialog() {
        new AlertDialog.Builder(this)
                .setTitle("清除历史记录")
                .setMessage("确定要清除所有历史记录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    dbHelper.clearAllHistory();
                    adapter.updateData(dbHelper.getAllHistory());
                    Toast.makeText(this, "历史记录已清除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.updateData(dbHelper.getAllHistory());
    }
} 