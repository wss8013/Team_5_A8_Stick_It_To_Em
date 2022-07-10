package com.example.team_5_a8;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StickerViewHolder extends RecyclerView.ViewHolder {
    private final ImageView receivedStickerIV;
    private final TextView fromUserTV;
    private final TextView sendTimeTV;

    public StickerViewHolder(@NonNull View itemView) {
        super(itemView);

        this.receivedStickerIV = itemView.findViewById(R.id.receivedStickerIV);
        this.fromUserTV = itemView.findViewById(R.id.fromUserTV);
        this.sendTimeTV = itemView.findViewById(R.id.sendTimeTV);
    }

    public void bindThisData(Sticker stickerToBind) {
        int imageResource = getImageResourceById(stickerToBind.getId());
        if (imageResource != -1) {
            receivedStickerIV.setImageResource(imageResource);
        }
        fromUserTV.setText(stickerToBind.getFromUser());
        sendTimeTV.setText(stickerToBind.getSendTime());
    }

    private int getImageResourceById(int id) {
        if (id == 1) {
            return R.drawable.sandwich;
        } else if (id == 2) {
            return R.drawable.goulash;
        } else if (id == 3) {
            return R.drawable.bean_stew;
        } else if (id == 4) {
            return R.drawable.lamb_peka;
        } else if (id == 5) {
            return R.drawable.walnutroll;
        } else if (id == 6) {
            return R.drawable.sardines;
        }
        return -1;
    }
}
