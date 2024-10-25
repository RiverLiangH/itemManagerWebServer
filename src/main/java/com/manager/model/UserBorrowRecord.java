package com.manager.model;

import java.sql.Timestamp;

public class UserBorrowRecord {
    private int id;
    private int userId;
    private int itemId;
    private Timestamp borrowTime;  // 使用 Timestamp 替代 Time
    private Timestamp returnTime;  // 使用 Timestamp 替代 Time

    // 构造函数
    public UserBorrowRecord() {}

    public UserBorrowRecord(int id, int userId, int itemId, Timestamp borrowTime, Timestamp returnTime) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.borrowTime = borrowTime;
        this.returnTime = returnTime;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Timestamp getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(Timestamp borrowTime) {
        this.borrowTime = borrowTime;
    }

    public Timestamp getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Timestamp returnTime) {
        this.returnTime = returnTime;
    }
}
