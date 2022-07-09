package com.example.team_5_a8;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String username;
    private String userId;
    private DatabaseReference myDataBase;
    private static final String TAG = "mFirebaseIIDService";
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION = "CHANNEL_DESCRIPTION";

    @SuppressLint("HardwareIds")
    @Override
    public void onCreate() {
        super.onCreate();
        myDataBase = FirebaseDatabase.getInstance().getReference();
        userId = Settings.Secure.getString(
                getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        myDataBase.child("users").child(userId).get().addOnCompleteListener(task -> {
            HashMap tempMap = (HashMap) task.getResult().getValue();
            username = tempMap.get("username").toString();
        });
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.i(TAG, "onTokenRefresh completed with token: " + token);
        System.out.println("onTokenRefresh completed with token: " + token);

        @SuppressLint("HardwareIds") User user = new User(this.username, Settings.Secure.getString(
                getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID), token);
        myDataBase.child("users").child(user.userDeviceID).setValue(user);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        myClassifier(remoteMessage);
        Log.e("msgId", remoteMessage.getMessageId());
        Log.e("senderId", remoteMessage.getSenderId());
    }

    private void myClassifier(RemoteMessage remoteMessage) {
        String identificator = remoteMessage.getFrom();
        if (identificator != null) {
            if (identificator.contains("topic")) {
                if (remoteMessage.getNotification() != null) {
                    RemoteMessage.Notification notification = remoteMessage.getNotification();
                    showNotification(remoteMessage.getNotification());
                    postToastMessage(notification.getTitle(), getApplicationContext());
                }
            } else {
                if (remoteMessage.getData().size() > 0) {
                    RemoteMessage.Notification notification = remoteMessage.getNotification();
                    assert notification != null;
                    showNotification(notification);
                    postToastMessage(remoteMessage.getData().get("title"), getApplicationContext());
                }
            }
        }
    }

    public static void postToastMessage(final String message, final Context context){
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessageNotification FCM message  received.
     */
    private void showNotification(RemoteMessage.Notification remoteMessageNotification) {

        Intent intent = new Intent(this, SendStickerToFriendsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        // Configure the notification channel
        notificationChannel.setDescription(CHANNEL_DESCRIPTION);
        notificationManager.createNotificationChannel(notificationChannel);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);


        notification = builder.setContentTitle(remoteMessageNotification.getTitle())
                .setContentText(remoteMessageNotification.getBody())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0, notification);

    }
}