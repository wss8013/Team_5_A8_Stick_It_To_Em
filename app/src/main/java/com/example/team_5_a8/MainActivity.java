package com.example.team_5_a8;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.Settings.Secure;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference myDataBase;
    private FirebaseAuth mAuth;
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
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerUserAccount();
            }
        });
    }

    public void registerUserAccount() {
        //get the user input username data
        String userName;
        userName = userNameInput.getText().toString();

        //get the current device id
        String android_id = Secure.getString(
                getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

        //validate for the user name input
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter a valid user name",  Toast.LENGTH_LONG).show();
            return;
        }


        //create the new user and upload it to the database
        User user = new User(userName, android_id);
        myDataBase.child("user").setValue(user);


        //after we successfully update the data base and the user name, we direct the user to the next activity
        Intent sendStickerToFriendsIntent = new Intent(this, sendStickerToFriendsActivity.class);
        startActivity(sendStickerToFriendsIntent);
    }
}