package com.example.taskforce;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddNewTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_task);

        EditText taskTitle = (EditText) findViewById(R.id.newTaskTitle);
        EditText taskGroup = (EditText) findViewById(R.id.newTaskGroup);
        EditText taskNotes = (EditText) findViewById(R.id.newTaskDetails);

        Button saveTask = (Button) findViewById(R.id.saveTaskBtn);

        saveTask.setOnClickListener(v -> {
            String newTaskTitle = taskTitle.getText().toString();
            String newTaskGroup = taskGroup.getText().toString();
            String newTaskNotes = taskNotes.getText().toString();
        });


    }
}