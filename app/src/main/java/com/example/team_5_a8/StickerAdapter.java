package com.example.team_5_a8;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerViewHolder> {
    private final List<Sticker> stickers;

    public StickerAdapter(List<Sticker> stickers) {
        this.stickers = stickers;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StickerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_received_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        holder.bindThisData(stickers.get(position));
    }

    @Override
    public int getItemCount() {
        return stickers != null ? stickers.size() : 0;
    }
}
