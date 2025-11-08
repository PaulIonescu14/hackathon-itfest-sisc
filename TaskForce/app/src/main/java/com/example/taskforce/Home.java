package com.example.taskforce;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        String email = getIntent().getStringExtra("EMAIL");

        LinearLayout container = findViewById(R.id.containerLayout);

        for(int i = 0; i < 10; i++) {
            TextView tv = new TextView(this);
            tv.setText("Numarul +" + i);
            tv.setTextSize(18);
            tv.setPadding(0, 50, 0, 50);

            container.addView(tv);
        }

    }
}