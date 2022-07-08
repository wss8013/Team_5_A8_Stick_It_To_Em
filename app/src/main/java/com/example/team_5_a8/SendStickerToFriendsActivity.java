package com.example.team_5_a8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendStickerToFriendsActivity extends AppCompatActivity {
    private DatabaseReference myDataBase;
    Spinner allFriends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sticker_to_friends);
        allFriends = findViewById(R.id.friend_spinner);
        myDataBase = FirebaseDatabase.getInstance().getReference();
        myDataBase.child("user").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    System.out.println("firebase"+ "Error getting data"+task.getException());
                    return;
                }
              String cloudFriend =  String.valueOf( task.getResult().getValue());
                System.out.println(" aaaaa " +cloudFriend );
            }
        });

    }
}