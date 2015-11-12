package com.nexb.shopr3.dataModel;

import java.util.ArrayList;

/**
 * @author Christian Created on 08-11-2015.
 */
public class ShopList {
    private String name = "new shoplist";
    private String createdByID ="";
    private ArrayList<Category> categories = new ArrayList<>();

    public ShopList(){
        Category c = new Category();
        categories.add(c);

    }


    //Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedByID() {
        return createdByID;
    }

    public void setCreatedByID(String createdByID) {
        this.createdByID = createdByID;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
}
