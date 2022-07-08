package com.example.team_5_a8;

import java.util.ArrayList;

public class User {
    public String username;
    public String userDeviceID;
    public ArrayList<Sticker> received;
    public ArrayList<Sticker> sent;

    public User(String username, String userDeviceID) {
        this.username = username;
        this.userDeviceID = userDeviceID;
        received = new ArrayList<>();
        sent = new ArrayList<>();
    }
}
