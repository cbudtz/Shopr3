package com.nexb.shopr3;

import java.util.ArrayList;

/**
 * Created by Christian on 05-11-2015.
 */
public class ForeignUserlist {
    private String UserName;
    private ArrayList<String> ShopListIDs;

    public ForeignUserlist() {
    }

    public ForeignUserlist(String userName, ArrayList<String> shopListIDs) {
        UserName = userName;
        ShopListIDs = shopListIDs;
    }

    public ArrayList<String> getShopListIDs() {
        return ShopListIDs;
    }

    public void setShopListIDs(ArrayList<String> shopListIDs) {
        ShopListIDs = shopListIDs;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
