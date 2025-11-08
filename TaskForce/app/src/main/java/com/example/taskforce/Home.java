package com.example.taskforce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        String email = getIntent().getStringExtra("EMAIL");

        LinearLayout container = findViewById(R.id.containerLayout);

        for(int i = 0; i < 10; i++) {

           View card = getLayoutInflater().inflate(R.layout.task_card, container, false);

            TextView titluCard = (TextView) card.findViewById(R.id.TitluCard);
            TextView deadlineCard = (TextView) card.findViewById(R.id.DeadlineCard);

            titluCard.setText("Asta e cardul :" + i);
            deadlineCard.setText("MAine");

            container.addView(card);

            card.setOnClickListener(v -> {
                Intent intent = new Intent(Home.this, TaskDetails.class);
                startActivity(intent);
            });

        }



    }
}