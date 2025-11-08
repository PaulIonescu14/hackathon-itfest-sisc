package com.example.taskforce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewGoToRegister);

        buttonLogin.setOnClickListener(v -> {
            // Check if db.json exists
            File f = new File(getFilesDir(), "db.json");

            if (!f.exists()) {
                Toast.makeText(MainActivity.this, "Please create an account first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Read and process the JSON file
            readUsersFromJson();
        });

        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });
    }

    private void readUsersFromJson() {
        FileInputStream fis = null;
        BufferedReader br = null;

        try {
            File file = new File(getFilesDir(), "db.json");
            fis = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line);
            }

            JSONArray usersArray = new JSONArray(content.toString());

            // Get login credentials from input fields
            String inputEmail = editTextEmail.getText().toString().trim();
            String inputPassword = editTextPassword.getText().toString().trim();

            boolean loginSuccessful = false;

            // Iterate through all users and check credentials
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);

                String mail = user.getString("mail");
                String password = user.getString("password");
                String id = user.getString("id");

                // Print user info to log (for debugging)
                System.out.println("User " + i + ": " + mail + " | " + password + " | " + id);

                // Check if credentials match
                if (mail.equals(inputEmail) && password.equals(inputPassword)) {
                    loginSuccessful = true;
                    Toast.makeText(this, "Login successful! Welcome " + mail, Toast.LENGTH_SHORT).show();

                    // Proceed to next activity
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    intent.putExtra("USER_EMAIL", mail);
                    intent.putExtra("USER_ID", id);
                    startActivity(intent);
                    finish();
                    break;
                }
            }

            if (!loginSuccessful && !inputEmail.isEmpty() && !inputPassword.isEmpty()) {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }

        } catch (FileNotFoundException e) {
            // This should rarely happen since we check file existence above
            e.printStackTrace();
            Toast.makeText(this, "User database not found", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            // Close resources in finally block
            try {
                if (br != null) {
                    br.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}