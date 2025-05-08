package com.example.proscan;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proscan.adapter.HistoryAdapter;
import com.example.proscan.db.HistoryDbHelper;
import com.example.proscan.db.HistoryItem;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyList;
    private HistoryDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new HistoryDbHelper(this);
        historyList = new ArrayList<>();
        
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new HistoryAdapter(historyList, new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistoryItem item) {
                // 复制内容到剪贴板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("URL", item.getContent());
                clipboard.setPrimaryClip(clip);
                
                // 显示提示
                Toast.makeText(HistoryActivity.this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                
                // 返回主界面
                finish();
            }

            @Override
            public void onItemLongClick(HistoryItem item) {
                // 长按复制内容
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("扫描内容", item.getContent());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(HistoryActivity.this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(HistoryItem item) {
                // 点击删除按钮
                new AlertDialog.Builder(HistoryActivity.this)
                    .setTitle("删除记录")
                    .setMessage("确定要删除这条记录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        dbHelper.deleteHistoryItem(item.getId());
                        historyList.remove(item);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(HistoryActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
            }
        });
        recyclerView.setAdapter(adapter);

        // 设置删除全部按钮点击事件
        findViewById(R.id.buttonClearAll).setOnClickListener(v -> {
            if (historyList.isEmpty()) {
                Toast.makeText(this, "没有历史记录", Toast.LENGTH_SHORT).show();
                return;
            }
            
            new AlertDialog.Builder(this)
                .setTitle("删除全部")
                .setMessage("确定要删除所有历史记录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    dbHelper.clearAllHistory();
                    historyList.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(HistoryActivity.this, "已清空历史记录", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
        });

        loadHistory();
    }

    private void loadHistory() {
        historyList.clear();
        historyList.addAll(dbHelper.getAllHistoryItems());
        adapter.notifyDataSetChanged();
    }
} 