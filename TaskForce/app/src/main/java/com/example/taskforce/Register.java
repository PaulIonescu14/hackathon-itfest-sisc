package com.example.taskforce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        TextView goToLogin = (TextView) findViewById(R.id.textViewGoToLogin);

        FirebaseDatabase database =  FirebaseDatabase.getInstance("https://taskforce-21df9-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myref = database.getReference("Users");
        myref.child("user1").setValue("pparola1");

        goToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
        });


    }
}