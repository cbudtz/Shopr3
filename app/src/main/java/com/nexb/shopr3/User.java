package com.nexb.shopr3;

import java.util.ArrayList;

/**
 * Created by Christian on 05-11-2015.
 */
public class User {
    private String UserID;
    private String UserName = "";

    private ArrayList<String> ownLists = null;
    private ArrayList<ForeignUserlist> foreignLists = null;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public ArrayList<String> getOwnLists() {
        return ownLists;
    }

    public void setOwnLists(ArrayList<String> ownLists) {
        this.ownLists = ownLists;
    }

    public ArrayList<ForeignUserlist> getForeignLists() {
        return foreignLists;
    }

    public void setForeignLists(ArrayList<ForeignUserlist> foreignLists) {
        this.foreignLists = foreignLists;
    }
}
