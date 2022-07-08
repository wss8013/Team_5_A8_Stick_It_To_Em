package com.example.team_5_a8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendStickerToFriendsActivity extends AppCompatActivity {
    private DatabaseReference myDataBase;
    Spinner allFriends;
    ImageView image1, image2, image3,image4,image5,image6;
    Map<ImageView, Boolean> imageViewIsClickedMap = new HashMap<>();

    Map<String,String> userNameToUserIdMap = new HashMap<>();
    Map<String,String> userIdToUserNameMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sticker_to_friends);
        allFriends = findViewById(R.id.friend_spinner);
        myDataBase = FirebaseDatabase.getInstance().getReference();
        initializeAllImageSticker();
        initializeSpinner();
    }

    private void initializeSpinner() {
        myDataBase.child("users").get().addOnCompleteListener((task)->{
            HashMap<String,HashMap<String,String>>tempMap =  (HashMap)task.getResult().getValue();
            List<String> userNames = new ArrayList<>();

            // populate user id and name
            for (String userId : tempMap.keySet()) {
                String userName = tempMap.get(userId).get("username");
                userNames.add(userName);
                userIdToUserNameMap.put(userId,userName);
                userNameToUserIdMap.put(userName,userId);
            }
            ArrayAdapter<String> adapter
                    = new ArrayAdapter<>(getApplicationContext(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    userNames);
            allFriends.setAdapter(adapter);
        });

    }

    private void initializeAllImageSticker() {
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);
        image1.setClickable(true);
        image2.setClickable(true);
        image3.setClickable(true);
        image4.setClickable(true);
        image5.setClickable(true);
        image6.setClickable(true);
        imageViewIsClickedMap.put(image1, false);
        imageViewIsClickedMap.put(image2, false);
        imageViewIsClickedMap.put(image3, false);
        imageViewIsClickedMap.put(image4, false);
        imageViewIsClickedMap.put(image5, false);
        imageViewIsClickedMap.put(image6, false);
        image1.setOnClickListener((v) -> imageViewOnClickListener(v));
        image2.setOnClickListener((v) -> imageViewOnClickListener(v));
        image3.setOnClickListener((v) -> imageViewOnClickListener(v));
        image4.setOnClickListener((v) -> imageViewOnClickListener(v));
        image5.setOnClickListener((v) -> imageViewOnClickListener(v));
        image6.setOnClickListener((v) -> imageViewOnClickListener(v));
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