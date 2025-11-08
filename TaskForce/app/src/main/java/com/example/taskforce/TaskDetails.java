package com.example.taskforce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.Button;

public class TaskDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        EditText title = findViewById(R.id.TaskTitle);
        TextView taskId = findViewById(R.id.TaskID);
        TextView author = findViewById(R.id.TaskAuthor);
        TextView group = findViewById(R.id.TaskGroup);
        EditText importance = findViewById(R.id.TaskImportance);
        EditText notes = findViewById(R.id.TaskNotes);

        title.setText("Complete Project Report");
        taskId.setText("Task ID: 98765");
        author.setText("John Doe");
        group.setText("Team Alpha");
        importance.setText("High");
        notes.setText("Initial note here...");

        Button goBack = findViewById(R.id.buttonGoBack);
        goBack.setOnClickListener(v -> {
            Intent intent = new Intent(TaskDetails.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
