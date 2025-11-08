package com.example.taskforce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        String email = getIntent().getStringExtra("USER_EMAIL");

        Button addTask = (Button) findViewById(R.id.addTaskBtn);

        LinearLayout container = findViewById(R.id.containerLayout);


        FirebaseDatabase database =  FirebaseDatabase.getInstance("https://taskforce-21df9-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myref = database.getReference("Users").child("user1").child("tasks");




        Task[] tasks = new Task[3];

        tasks[0] = new Task("ABCBASBDSA", "Eu", "323CA", "Azi", 1, "", "Detalii");
        tasks[1] = new Task("dsadsadada", "Tu", "313CA", "Maine", 2, "", "Alte detalii");
        tasks[2] = new Task("dslkdaslkfnslkd", "El", "999CC", "grdg", 7, "", "dsasdadsa");

        for(int i = 0; i < tasks.length; i++) {

            View card = getLayoutInflater().inflate(R.layout.task_card, container, false);

            TextView titluCard = (TextView) card.findViewById(R.id.TitluCard);
            TextView deadlineCard = (TextView) card.findViewById(R.id.DeadlineCard);

            titluCard.setText(tasks[i].title);
            deadlineCard.setText(tasks[i].deadline);

            container.addView(card);

            final Task currentTask = tasks[i];

            card.setOnClickListener(v -> {
                Intent intent = new Intent(Home.this, TaskDetails.class);
                intent.putExtra("task_object", currentTask);
                startActivity(intent);
            });

        }

        addTask.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, AddNewTask.class);
            intent.putExtra("USER_EMAIL", email);
            startActivity(intent);
        });



    }
}