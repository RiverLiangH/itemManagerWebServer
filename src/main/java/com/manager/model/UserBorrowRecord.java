package com.manager.model;
import java.sql.Time;

public class UserBorrowRecord {
    private int id;
    private int userId;
    private int itemId;
    private Time borrowTime;
    private Time returnTime;

    // 构造函数
    public UserBorrowRecord() {}

    public UserBorrowRecord(int id, int userId, int itemId, Time borrowTime, Time returnTime) {
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

    public Time getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(Time borrowTime) {
        this.borrowTime = borrowTime;
    }

    public Time getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Time returnTime) {
        this.returnTime = returnTime;
    }
}
