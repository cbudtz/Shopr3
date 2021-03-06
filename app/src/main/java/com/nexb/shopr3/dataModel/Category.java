package com.nexb.shopr3.dataModel;

import java.util.ArrayList;

/**
 * Created by Christian on 12-11-2015.
 */
public class Category {
    private String name="new category";
    private ArrayList<ListItem> items = new ArrayList<>();

    public Category() {
        items.add(new ListItem());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ListItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ListItem> items) {
        this.items = items;
    }
}
