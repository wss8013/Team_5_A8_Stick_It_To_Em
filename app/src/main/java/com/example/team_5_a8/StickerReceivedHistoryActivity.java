package com.example.team_5_a8;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StickerReceivedHistoryActivity extends AppCompatActivity {
    private List<Sticker> stickers;
    private String myName;
    private DatabaseReference myDataBase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_received_history);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myName = extras.getString("user_name");
        }

        myDataBase = FirebaseDatabase.getInstance().getReference();

        stickers = new ArrayList<>();
        updateReceivedHistoryRV();

        syncStickersFromDB();

    }

    private void updateReceivedHistoryRV() {
        RecyclerView receivedHistoryRV = findViewById(R.id.receivedHistoryRV);
        receivedHistoryRV.setLayoutManager(new LinearLayoutManager(this));
        receivedHistoryRV.setAdapter(new StickerAdapter(stickers));
    }

    private void syncStickersFromDB() {
        stickers = new ArrayList<>();
        myDataBase.child("stickers").get().addOnCompleteListener((task) -> {
            HashMap<String, HashMap<String, String>> tempMap = (HashMap) task.getResult().getValue();
            for (String entryKey : tempMap.keySet()) {
                String fromUser = tempMap.get(entryKey).get("fromUser");
                String id = String.valueOf(tempMap.get(entryKey).get("id"));
                String sendTime = tempMap.get(entryKey).get("sendTime");
                String toUser = tempMap.get(entryKey).get("toUser");
                if (toUser != null && toUser.equals(myName)) {
                    stickers.add(new Sticker(Integer.parseInt(id), fromUser, toUser, sendTime));
                }
            }
            Collections.sort(stickers, Collections.reverseOrder());
            updateReceivedHistoryRV();
        });
    }
}
