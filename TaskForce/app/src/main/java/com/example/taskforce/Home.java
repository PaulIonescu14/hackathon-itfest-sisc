package com.example.taskforce;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        String email = getIntent().getStringExtra("USER_EMAIL");

        int index = email.lastIndexOf('.');
        String terminatie = email.substring(index + 1, email.length());
        final String parse = email.substring(0, index) + ',' + terminatie;

        Button addTask = (Button) findViewById(R.id.addTaskBtn);

        LinearLayout container = findViewById(R.id.containerLayout);


        FirebaseDatabase database =  FirebaseDatabase.getInstance("https://taskforce-21df9-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myref = database.getReference("Users").child(parse).child("tasks");

        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                container.removeAllViews();

                if (!dataSnapshot.exists()) {
                    Toast.makeText(Home.this, "No tasks found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task == null) continue;
                    String taskKey = snapshot.getKey();


                    View card = getLayoutInflater().inflate(R.layout.task_card, container, false);

                    TextView titluCard = card.findViewById(R.id.TitluCard);
                    TextView importanceCard = card.findViewById(R.id.ImportanceCard);
                    ImageView imageCard = card.findViewById(R.id.imageViewCard);

                    titluCard.setText(task.title);
                    importanceCard.setText("Importance: " + task.importance);
                    if(task.importance == 1) {
                        imageCard.setBackgroundColor(Color.parseColor("#56B894"));
                    } else if(task.importance == 2) {
                        imageCard.setBackgroundColor(Color.parseColor("#3B82F6"));
                    } else if(task.importance == 3) {
                        imageCard.setBackgroundColor(Color.parseColor("#FBBF24"));
                    } else if(task.importance == 4) {
                        imageCard.setBackgroundColor(Color.parseColor("#EF4444"));
                    } else {
                        imageCard.setBackgroundColor(Color.parseColor("#D1D5DB"));

                    }

                    container.addView(card);

                    card.setOnClickListener(v -> {
                        Intent intent = new Intent(Home.this, TaskDetails.class);
                        intent.putExtra("task_object", task);
                        intent.putExtra("task_key", taskKey);
                        intent.putExtra("USER_EMAIL", email);
                        startActivity(intent);
                    });
                }
            }

            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error reading tasks: " + error.getMessage());
                Toast.makeText(Home.this, "Error loading tasks.", Toast.LENGTH_SHORT).show();
            }
        });


        addTask.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, AddNewTask.class);
            intent.putExtra("USER_EMAIL", email);
            startActivity(intent);
        });



    }
}