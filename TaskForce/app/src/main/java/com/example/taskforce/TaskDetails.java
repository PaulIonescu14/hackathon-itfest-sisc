package com.example.taskforce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TaskDetails extends AppCompatActivity {

    private Task task;
    private String connected_email;
    private String taskKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_details);

        // Get data from intent
        task = (Task) getIntent().getSerializableExtra("task_object");
        connected_email = getIntent().getStringExtra("USER_EMAIL");
        taskKey = getIntent().getStringExtra("task_key");

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
        Button finishTask = findViewById(R.id.buttonRemove);
        Button updateTask = findViewById(R.id.buttonUpdate);

        goBack.setOnClickListener(v -> {
            goBackToHome();
        });

        // Finish Task button - delete task from database
        finishTask.setOnClickListener(v -> {
            if (taskKey == null || connected_email == null) {
                Toast.makeText(TaskDetails.this, "Cannot finish task", Toast.LENGTH_SHORT).show();
                return;
            }
            finishAndDeleteTask();
        });

        // Update Task button - save changes
        updateTask.setOnClickListener(v -> {
            if (taskKey == null || connected_email == null) {
                Toast.makeText(TaskDetails.this, "Cannot update task", Toast.LENGTH_SHORT).show();
                return;
            }
            updateTaskInFirebase();
        });
    }

    private void goBackToHome() {
        Intent intent = new Intent(TaskDetails.this, Home.class);
        intent.putExtra("USER_EMAIL", connected_email);
        startActivity(intent);
        finish();
    }

    private void finishAndDeleteTask() {
        String parse = convertEmailForFirebase(connected_email);

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://taskforce-21df9-default-rtdb.europe-west1.firebasedatabase.app"
        );
        DatabaseReference taskRef = database.getReference("Users").child(parse).child("tasks").child(taskKey);

        // Remove task from Firebase (delete it completely)
        taskRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TaskDetails.this, "Task finished and removed successfully!", Toast.LENGTH_SHORT).show();
                    goBackToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TaskDetails.this, "Error finishing task: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updateTaskInFirebase() {
        String parse = convertEmailForFirebase(connected_email);

        // Get updated values from UI
        EditText title = findViewById(R.id.TaskTitle);
        RadioGroup importanceGroup = findViewById(R.id.TaskImportance);
        EditText notes = findViewById(R.id.TaskNotes);

        String updatedTitle = title.getText().toString().trim();
        String updatedNotes = notes.getText().toString().trim();

        // Get selected importance
        int updatedImportance = 1; // default
        int selectedId = importanceGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.importance1) updatedImportance = 1;
        else if (selectedId == R.id.importance2) updatedImportance = 2;
        else if (selectedId == R.id.importance3) updatedImportance = 3;
        else if (selectedId == R.id.importance4) updatedImportance = 4;

        // Validate input
        if (updatedTitle.isEmpty()) {
            Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://taskforce-21df9-default-rtdb.europe-west1.firebasedatabase.app"
        );
        DatabaseReference taskRef = database.getReference("Users").child(parse).child("tasks").child(taskKey);

        // Update task in Firebase
        taskRef.child("title").setValue(updatedTitle);
        taskRef.child("importance").setValue(updatedImportance);
        taskRef.child("notes").setValue(updatedNotes)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TaskDetails.this, "Task updated successfully!", Toast.LENGTH_SHORT).show();
                    goBackToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TaskDetails.this, "Error updating task: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String convertEmailForFirebase(String email) {
        int index = email.lastIndexOf('.');
        if (index != -1) {
            String terminatie = email.substring(index + 1);
            String parse = email.substring(0, index) + ',' + terminatie;
            return parse;
        }
        return email.replace('.', ',');
    }
}