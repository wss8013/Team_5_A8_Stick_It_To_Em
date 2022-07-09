package com.example.team_5_a8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendStickerToFriendsActivity extends AppCompatActivity {
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    private static final String CHANNEL_ID = "Team_5_A8_Stick_It_To_Em";
    private MyFirebaseMessagingService firebaseService;

    private DatabaseReference myDataBase;
    Spinner allFriends;
    ImageView image1, image2, image3, image4, image5, image6;
    Map<ImageView, Boolean> imageViewIsClickedMap = new HashMap<>();
    Map<String, String> userNameToUserIdMap = new HashMap<>();
    Map<String, String> userIdToUserNameMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sticker_to_friends);
        createNotificationChannel();
        allFriends = findViewById(R.id.friend_spinner);
        myDataBase = FirebaseDatabase.getInstance().getReference();
        initializeAllImageSticker();
        initializeSpinner();
        firebaseService = new MyFirebaseMessagingService(getCurrentUsername(), myDataBase);
    }

    private void initializeSpinner() {
        myDataBase.child("users").get().addOnCompleteListener((task) -> {
            HashMap<String, HashMap<String, String>> tempMap = (HashMap) task.getResult().getValue();
            List<String> userNames = new ArrayList<>();

            // populate user id and name
            for (String userId : tempMap.keySet()) {
                String userName = tempMap.get(userId).get("username");
                userNames.add(userName);
                userIdToUserNameMap.put(userId, userName);
                userNameToUserIdMap.put(userName, userId);
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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void onSubmitButtonPressed(View v) {
        String selectedUsername = allFriends.getSelectedItem().toString();
        int selectedImageId = getCurrentSelectedId();
        if (selectedImageId == -1) {
            Context context = getApplicationContext();
            CharSequence text = "no sticker is selected for" + selectedUsername;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        Sticker sticker = new Sticker(selectedImageId, getCurrentUsername(), selectedUsername, now());

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.bean_stew);


        myDataBase.child("stickers").child(sticker.getKey()).setValue(sticker);
        Context context = getApplicationContext();
        CharSequence text = "sticker sent to " + selectedUsername;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        //
        String registrationToken = "YOUR_REGISTRATION_TOKEN";

        Message message = Message.Builder
                .putData("score", "850")
                .putData("time", "2:45")
                .setToken(registrationToken)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent message: " + response);
    }

    private String getCurrentUsername() {
        return userIdToUserNameMap.get(Settings.Secure.getString(
                getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    private ImageView getSelectedImage() {
        for (Map.Entry<ImageView, Boolean> entry : imageViewIsClickedMap.entrySet()) {
            if (entry.getValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String now() {
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    @SuppressLint("NonConstantResourceId")
    public int getCurrentSelectedId() {
        for (ImageView imageView : imageViewIsClickedMap.keySet()) {
            if (Boolean.TRUE.equals(imageViewIsClickedMap.get(imageView))) {
                switch (imageView.getId()) {
                    case R.id.image1:
                        return 1;
                    case R.id.image2:
                        return 2;
                    case R.id.image3:
                        return 3;
                    case R.id.image4:
                        return 4;
                    case R.id.image5:
                        return 5;
                    case R.id.image6:
                        return 6;
                    default:
                        return -1;
                }
            }
        }
        return -1;
    }
}