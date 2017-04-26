package com.dcmmoguls.hardinglaw.model;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ChannelItem {

    public String key;
    public Object message;
    public String name;

    public ChannelItem() {
    }

    ChannelItem(Object message, String name) {
        this.message = message;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
