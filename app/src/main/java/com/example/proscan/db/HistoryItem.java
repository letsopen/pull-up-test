package com.example.proscan.db;

public class HistoryItem {
    private long id;
    private String content;
    private long timestamp;
    private String type;

    public HistoryItem(long id, String content, long timestamp, String type) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }
} 