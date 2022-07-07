package com.example.team_5_a8;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String username;
    public String userDeviceID;
    public ArrayList<sticker> received;
    public ArrayList<sticker> sent;

    public User(String username, String userDeviceID) {
        this.username = username;
        this.userDeviceID = userDeviceID;
        received = new ArrayList<sticker>();
        sent = new ArrayList<sticker>();
    }
}
