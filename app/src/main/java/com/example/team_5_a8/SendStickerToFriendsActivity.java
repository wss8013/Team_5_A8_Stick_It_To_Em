package com.example.team_5_a8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendStickerToFriendsActivity extends AppCompatActivity {
    private DatabaseReference myDataBase;
    Spinner allFriends;
    ImageView image1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sticker_to_friends);
        allFriends = findViewById(R.id.friend_spinner);
        myDataBase = FirebaseDatabase.getInstance().getReference();
        ArrayAdapter<CharSequence> friendNameAdapter = ArrayAdapter.createFromResource(this,
                R.array.friend_names, android.R.layout.simple_spinner_item);
        friendNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        allFriends.setAdapter(friendNameAdapter);
        image1 = findViewById(R.id.image1);
        image1.setClickable(true);
        image1.setOnClickListener((v) -> {
            ((ImageView) v).setColorFilter(ContextCompat
                            .getColor(this.getApplicationContext()
                                    , R.color.purple_200)
                    , android.graphics.PorterDuff.Mode.MULTIPLY);
        });

//        myDataBase.child("user").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    System.out.println("firebase"+ "Error getting data"+task.getException());
//                    return;
//                }
//              String cloudFriend =  String.valueOf( task.getResult().getValue());
//            }
//        });

    }
}