package com.nexb.shopr3.dataModel;

import java.util.List;

/**
 * Created by Christian on 08-11-2015.
 */
public class ListItem {
    private String itemID;
    private double amount;
    private String unit;
    private String name;

    public ListItem(){}

    public ListItem(String itemID, double amount, String unit, String name) {
        this.itemID = itemID;
        this.amount = amount;
        this.unit = unit;
        this.name = name;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
