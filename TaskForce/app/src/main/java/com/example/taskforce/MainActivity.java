package com.example.taskforce;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        EditText email = (EditText) findViewById(R.id.editTextTextEmailAddress);
        EditText password = (EditText) findViewById(R.id.editTextTextPassword);
        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        Switch togglePass = (Switch) findViewById(R.id.switchPassView);
        TextView forgotPass = (TextView) findViewById(R.id.textViewForgotPass);
        TextView goToRegister = (TextView) findViewById(R.id.textViewGoToRegister);

        buttonLogin.setOnClickListener(v -> {
           String emailText = email.getText().toString();
           String passText = password.getText().toString();

            Log.d("MainActivity", "email: " + emailText);
            Log.d("MainActivity", "password: " + passText);

        });

        togglePass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
    }




}