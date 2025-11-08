package com.example.taskforce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class Register extends AppCompatActivity {

    private static final String DB_FILE_NAME = "db.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        EditText email = findViewById(R.id.editTextTextEmailAddress2);
        EditText password = findViewById(R.id.editTextTextPassword);
        EditText passwordRetype = findViewById(R.id.editTextTextPassword2);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        TextView goToLogin = findViewById(R.id.textViewGoToLogin);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://taskforce-21df9-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference usersRef = database.getReference("Users");

        buttonRegister.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String passwordRetypeText = passwordRetype.getText().toString().trim();

            // Validation
            if (emailText.isEmpty() || passwordText.isEmpty() || passwordRetypeText.isEmpty()) {
                Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordText.equals(passwordRetypeText)) {
                Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (passwordText.length() < 6) {
                Toast.makeText(Register.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create unique ID
            String userId = UUID.randomUUID().toString();

            // Create User object
            User user = new User(emailText, passwordText, userId);

            // Save to Firebase
            String userKey = emailText.replace(".", ",");
            usersRef.child(userKey).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        // Also save to local JSON file
                        if (saveUserToLocalFile(emailText, passwordText, userId)) {
                            Toast.makeText(Register.this, "Registration successful! Saved locally.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Register.this, "Registration successful! But local save failed.", Toast.LENGTH_SHORT).show();
                        }

                        // Clear fields
                        email.setText("");
                        password.setText("");
                        passwordRetype.setText("");

                        // Go to login
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Register.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        goToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean saveUserToLocalFile(String email, String password, String id) {
        try {
            // Create user JSON object
            JSONObject userJson = new JSONObject();
            userJson.put("mail", email);
            userJson.put("password", password);
            userJson.put("id", id);

            // Read existing data from file
            JSONArray usersArray = readExistingUsers();

            // Add new user to the array
            usersArray.put(userJson);

            // Write back to file
            File file = new File(getFilesDir(), DB_FILE_NAME);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(usersArray.toString(4)); // 4 spaces for indentation
            osw.close();
            fos.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private JSONArray readExistingUsers() {
        try {
            File file = new File(getFilesDir(), DB_FILE_NAME);

            if (!file.exists()) {
                return new JSONArray();
            }

            // Read file content
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line);
            }
            br.close();
            fis.close();

            if (content.length() > 0) {
                return new JSONArray(content.toString());
            } else {
                return new JSONArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private String getDbFilePath() {
        return new File(getFilesDir(), DB_FILE_NAME).getAbsolutePath();
    }
}