package com.dcmmoguls.hardinglaw.model;

import com.stfalcon.chatkit.commons.models.IUser;

/*
 * Created by troy379 on 04.04.17.
 */
public class MyUser {

    private String name;
    private String OneSignalId;
    private boolean isAdmin;

    public MyUser() {

    }

    public MyUser(String name, String OneSignalId, boolean isAdmin) {
        this.OneSignalId = OneSignalId;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public String getOneSignalId() {
        return OneSignalId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
