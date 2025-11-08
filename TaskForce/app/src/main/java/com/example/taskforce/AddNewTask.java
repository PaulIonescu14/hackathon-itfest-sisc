package com.example.taskforce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNewTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_task);

        EditText taskTitle = (EditText) findViewById(R.id.newTaskTitle);
        EditText taskGroup = (EditText) findViewById(R.id.newTaskGroup);
        EditText taskNotes = (EditText) findViewById(R.id.newTaskDetails);

        String connected_email = getIntent().getStringExtra("EMAIL");

        Button saveTask = (Button) findViewById(R.id.saveTaskBtn);

        saveTask.setOnClickListener(v -> {
            String newTaskTitle = taskTitle.getText().toString();
            String newTaskGroup = taskGroup.getText().toString();
            String newTaskNotes = taskNotes.getText().toString();

            Task newTask = new Task(
                    newTaskTitle,
                    "current",
                    newTaskGroup,
                    "",
                    1,
                    "",
                    newTaskNotes
            );

            FirebaseDatabase database =  FirebaseDatabase.getInstance("https://taskforce-21df9-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference myref = database.getReference("Users").child(connected_email).child("tasks");

            myref.push().setValue(newTask)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Task salvat cu succes!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Eroare la salvare: " + e.getMessage(), Toast.LENGTH_LONG).show());

            Intent intent = new Intent(AddNewTask.this, Home.class);
            startActivity(intent);

        });


    }
}