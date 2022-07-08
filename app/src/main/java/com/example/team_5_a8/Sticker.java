package com.example.team_5_a8;

public class Sticker {
    public String sticker_image_url;
    public int sticker_ID;
    public String time; //format "09-23-2022"
    //in the list of received stickers, this is the received time
    //in the list of sent stickers, this is the sent time

   public Sticker(String url, int id, String time) {
        this.sticker_image_url = url;
        this.sticker_ID = id;
        this.time = time;
    }
}
