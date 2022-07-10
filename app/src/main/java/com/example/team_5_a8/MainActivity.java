package com.example.team_5_a8;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.Settings.Secure;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference myDataBase;
    private EditText userNameInput;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up all the UI view elements
        userNameInput = findViewById(R.id.editTextTextPersonName);
        registerBtn = findViewById(R.id.button_register);

        //set up the database reference
        myDataBase = FirebaseDatabase.getInstance().getReference();


        // Set on Click Listener on register button
        registerBtn.setOnClickListener(v -> registerUserAccount());
    }

    public void registerUserAccount() {
        //get the user input username data
        String userName;
        userName = userNameInput.getText().toString();
        //get the current device id
        @SuppressLint("HardwareIds") String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        //validate for the user name input
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(getApplicationContext(), "Please enter a valid user name",  Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("MainActivity", "onCreate Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    System.out.println("token from add for " + userName +  ":" + token);
                    User user = new User(userName, android_id, token);
                    myDataBase.child("users").child(android_id).setValue(user);
                });

        Intent sendStickerToFriendsIntent = new Intent(this, SendStickerToFriendsActivity.class);
        sendStickerToFriendsIntent.putExtra("user_name", userName);
        startActivity(sendStickerToFriendsIntent);
    }
}