package com.example.team_5_a8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SendStickerToFriendsActivity extends AppCompatActivity {
    private DatabaseReference myDataBase;
    Spinner allFriends;
    ImageView image1, image2, image3;
    Map<ImageView, Boolean> imageViewIsClickedMap = new HashMap<>();

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
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image1.setClickable(true);
        image2.setClickable(true);
        image3.setClickable(true);
        imageViewIsClickedMap.put(image1, false);
        imageViewIsClickedMap.put(image2, false);
        imageViewIsClickedMap.put(image3, false);

        image1.setOnClickListener((v) -> imageViewOnClickListener(v));
        image2.setOnClickListener((v) -> imageViewOnClickListener(v));
        image3.setOnClickListener((v) -> imageViewOnClickListener(v));

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

    public void imageViewOnClickListener(View v) {
        if (imageViewIsClickedMap.get(v)) {
            unselectImage((ImageView) v);
        } else {
            // reset all images
            for (ImageView imageView : imageViewIsClickedMap.keySet()) {
                unselectImage(imageView);
            }
            // set current image
            ((ImageView) v).setColorFilter(ContextCompat
                            .getColor(this.getApplicationContext()
                                    , R.color.purple_200)
                    , android.graphics.PorterDuff.Mode.MULTIPLY);
            imageViewIsClickedMap.put((ImageView) v, true);
        }
    }

    private void unselectImage(ImageView v) {
        v.setColorFilter(null);
        imageViewIsClickedMap.put(v, false);
    }

    public int getCurrentSelectedId() {
        for (ImageView imageView : imageViewIsClickedMap.keySet()) {
            if (imageViewIsClickedMap.get(imageView)) {
                switch (imageView.getId()) {
                    case R.id.image1:
                        return 1;
                    case R.id.image2:
                        return 2;
                    case R.id.image3:
                        return 3;
                    default:
                        // do nothing;
                }
            }
        }
        return -1;
    }

}