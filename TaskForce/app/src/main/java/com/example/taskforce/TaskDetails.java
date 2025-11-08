package com.example.taskforce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TaskDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_details);

        Task task = (Task) getIntent().getSerializableExtra("task_object");
        String connected_email = getIntent().getStringExtra("USER_EMAIL");
        String taskKey = getIntent().getStringExtra("task_key");

        EditText title = findViewById(R.id.TaskTitle);
        RadioGroup importanceGroup = findViewById(R.id.TaskImportance);
        EditText notes = findViewById(R.id.TaskNotes);

        // Set values
        title.setText(task.title);
        notes.setText(task.notes);

        // Set the correct RadioButton checked based on task.importance
        switch (task.importance) {
            case 1:
                importanceGroup.check(R.id.importance1);
                break;
            case 2:
                importanceGroup.check(R.id.importance2);
                break;
            case 3:
                importanceGroup.check(R.id.importance3);
                break;
            case 4:
                importanceGroup.check(R.id.importance4);
                break;
        }

        Button goBack = findViewById(R.id.buttonGoBack);
        Button removeTask = findViewById(R.id.buttonRemove);

        goBack.setOnClickListener(v -> {
            Intent intent = new Intent(TaskDetails.this, Home.class);
            intent.putExtra("USER_EMAIL", connected_email);
            startActivity(intent);
            finish();
        });

        removeTask.setOnClickListener(v -> {
            if (taskKey == null || connected_email == null) {
                Toast.makeText(TaskDetails.this, "Cannot remove task", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert email for Firebase path
            int index = connected_email.lastIndexOf('.');
            String terminatie = connected_email.substring(index + 1);
            String parse = connected_email.substring(0, index) + ',' + terminatie;

            FirebaseDatabase database = FirebaseDatabase.getInstance(
                    "https://taskforce-21df9-default-rtdb.europe-west1.firebasedatabase.app"
            );
            DatabaseReference taskRef = database.getReference("Users").child(parse).child("tasks").child(taskKey);

            // Remove task from Firebase
            taskRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(TaskDetails.this, "Task removed successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TaskDetails.this, Home.class);
                        intent.putExtra("USER_EMAIL", connected_email);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(TaskDetails.this, "Error removing task: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}
