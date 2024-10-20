package com.manager.model;

public class Item {
    private int id;
    private String name;
    private String type;
    // private int totalCount;
    // private int currentStock;
    private int current_condition;
    private int location;

    // 构造函数
    public Item() {}

    public Item(int id, String name, String type, int current_condition, int location) {
        this.id = id;
        this.name = name;
        this.type = type;
        // this.totalCount = totalCount;
        // this.currentStock = currentStock;
        this.current_condition = current_condition;
        this.location = location;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    public int getTotalCount() {
//        return totalCount;
//    }
//
//    public void setTotalCount(int totalCount) {
//        this.totalCount = totalCount;
//    }
//
//    public int getCurrentStock() {
//        return currentStock;
//    }
//
//    public void setCurrentStock(int currentStock) {
//        this.currentStock = currentStock;
//    }

    public int getCondition() {
        return current_condition;
    }

    public void setCondition(int condition) {
        this.current_condition = condition;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}

