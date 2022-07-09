package com.example.team_5_a8;

public class Sticker {
    public int id;
    public String fromUser;
    public String toUser;
    public String sendTime; //format "09-23-2022"
    public String receivedTime;
    //in the list of received stickers, this is the received time
    //in the list of sent stickers, this is the sent time
   public Sticker(int id, String fromUser, String toUser, String sendTime) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.sendTime = sendTime;
    }

    public String getKey() {
       return id + "|" + fromUser + "|" + toUser + "|" + sendTime;
    }
}
