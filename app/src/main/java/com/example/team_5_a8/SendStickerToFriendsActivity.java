package com.example.team_5_a8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SendStickerToFriendsActivity extends AppCompatActivity {
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    private static String SERVER_KEY;

    private String myName;
    private TextView myNameTV;
    private DatabaseReference myDataBase;
    private Spinner allFriends;
    private ImageView image1, image2, image3, image4, image5, image6;
    private TextView image1TV, image2TV, image3TV, image4TV, image5TV, image6TV;
    private Map<ImageView, Integer> imageViewSentCountMap = new HashMap<>();
    private Map<ImageView, Boolean> imageViewIsClickedMap = new HashMap<>();
    private Map<ImageView, TextView> imageViewToTextViewMap = new HashMap<>();
    private Map<String, String> userNameToUserIdMap = new HashMap<>();
    private Map<String, String> userIdToUserNameMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myName = extras.getString("user_name");
        }
        SERVER_KEY = "key=" + getProperties(getApplicationContext()).getProperty("SERVER_KEY");
        setContentView(R.layout.activity_send_sticker_to_friends);
        createNotificationChannel();
        allFriends = findViewById(R.id.friend_spinner);
        myDataBase = FirebaseDatabase.getInstance().getReference();

        myNameTV = (TextView) findViewById(R.id.my_name);
        myNameTV.setText("My name: " + myName);

        initializeAllImageSticker();
        syncImageViewSentCountWithDB();
        initializeSpinner();
    }

    public static Properties getProperties(Context context)  {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("firebase.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    private void displayCount() {
        for (ImageView imageView : imageViewToTextViewMap.keySet()) {
            TextView textView = imageViewToTextViewMap.get(imageView);
            textView.setText("Times sent: " + imageViewSentCountMap.get(imageView));
        }
    }

    private void syncImageViewSentCountWithDB() {
        myDataBase.child("stickers").get().addOnCompleteListener((task) -> {
            HashMap<String, HashMap<String, String>> tempMap = (HashMap) task.getResult().getValue();
            for (String entryKey : tempMap.keySet()) {
                String fromUser = tempMap.get(entryKey).get("fromUser");
                if (fromUser != null && fromUser.equals(myName)) {
                    String id = String.valueOf(tempMap.get(entryKey).get("id"));
                    if (id == null) {
                        Log.e("DB-SYNC", "ID not found...");
                        continue;
                    }
                    ImageView imageViewRef = getImageViewById(Integer.parseInt(id));
                    if (imageViewRef == null) {
                        Log.e("DB-SYNC", "ID not supported in this app version...");
                        continue;
                    }
                    imageViewSentCountMap.merge(imageViewRef, 1, Integer::sum);
                }
            }
            displayCount();
        });
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
        imageViewSentCountMap.put(image1, 0);
        imageViewSentCountMap.put(image2, 0);
        imageViewSentCountMap.put(image3, 0);
        imageViewSentCountMap.put(image4, 0);
        imageViewSentCountMap.put(image5, 0);
        imageViewSentCountMap.put(image6, 0);
        image1TV = findViewById(R.id.image1SentTimesTV);
        image2TV = findViewById(R.id.image2SentTimesTV);
        image3TV = findViewById(R.id.image3SentTimesTV);
        image4TV = findViewById(R.id.image4SentTimesTV);
        image5TV = findViewById(R.id.image5SentTimesTV);
        image6TV = findViewById(R.id.image6SentTimesTV);
        imageViewToTextViewMap.put(image1, image1TV);
        imageViewToTextViewMap.put(image2, image2TV);
        imageViewToTextViewMap.put(image3, image3TV);
        imageViewToTextViewMap.put(image4, image4TV);
        imageViewToTextViewMap.put(image5, image5TV);
        imageViewToTextViewMap.put(image6, image6TV);
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

    public void createNotificationChannel() {
        // This must be called early because it must be called before a notification is sent.
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void onHistoryButtonPressed(View v) {
        Intent intent = new Intent(this, StickerReceivedHistoryActivity.class);
        startActivity(intent);
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

        // Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.bean_stew);
        myDataBase.child("stickers").child(sticker.getKey()).setValue(sticker).addOnSuccessListener(
                (task) -> {
                    Context context_success = getApplicationContext();
                    CharSequence text_success = "Sticker successfully send to " + selectedUsername;
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context_success, text_success, duration);
                    toast.show();

                    ImageView imageViewById = getImageViewById(selectedImageId);
                    if (imageViewById == null) {
                        Log.e("UI", "ID not supported in this app version...");
                        return;
                    }
                    imageViewSentCountMap.merge(getImageViewById(selectedImageId), 1, Integer::sum);
                    displayCount();
                });
        myDataBase.child("users").child(userNameToUserIdMap.get(selectedUsername)).get().addOnCompleteListener((task) -> {
            HashMap tempMap = (HashMap) task.getResult().getValue();
            String token = tempMap.get("token").toString();
            new Thread(() -> sendMessageToDevice(token, sticker)).start();
        });
    }

    private void sendMessageToDevice(String targetToken, Sticker sticker) {
        System.out.println("client token:" + targetToken);
        // Prepare data
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            String title = "sticker from " + sticker.fromUser;
            String msg = "this is a sticker " + sticker.id;
            jNotification.put("title", title);
            jNotification.put("body", msg);
            jdata.put("title", "data:" + title);
            jdata.put("content", "data:" + msg);
            jdata.put("image_id", sticker.id);

            jPayload.put("to", targetToken);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String resp = fcmHttpConnection(SERVER_KEY, jPayload);
        // postToastMessage("Status from Server: " + resp, getApplicationContext());

    }

    private static void postToastMessage(final String message, final Context context){
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static String fcmHttpConnection(String serverToken, JSONObject jsonObject) {
        try {

            // Open the HTTP connection and send the payload
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", serverToken);
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            return convertStreamToString(inputStream);
        } catch (IOException e) {
            return "NULL";
        }

    }

    private static String convertStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String len;
            while ((len = bufferedReader.readLine()) != null) {
                stringBuilder.append(len);
            }
            bufferedReader.close();
            return stringBuilder.toString().replace(",", ",\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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

    private ImageView getImageViewById(int id) {
        if (id == 1) {
            return image1;
        } else if (id == 2) {
            return image2;
        } else if (id == 3) {
            return image3;
        } else if (id == 4) {
            return image4;
        } else if (id == 5) {
            return image5;
        } else if (id == 6) {
            return image6;
        }
        return null;
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