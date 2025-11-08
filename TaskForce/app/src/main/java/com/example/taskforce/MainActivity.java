package com.example.taskforce;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;

    private Switch switchPassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewGoToRegister);
        switchPassView = findViewById(R.id.switchPassView);

        switchPassView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            editTextPassword.setSelection(editTextPassword.getText().length());
        });



        buttonLogin.setOnClickListener(v -> {

            String inputEmail = editTextEmail.getText().toString().trim();
            String inputPassword = editTextPassword.getText().toString().trim();

            if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            String firebaseEmailKey = inputEmail.replace(".", ",");

            FirebaseDatabase database = FirebaseDatabase.getInstance(
                    "https://taskforce-21df9-default-rtdb.europe-west1.firebasedatabase.app"
            );
            DatabaseReference userRef = database.getReference("Users").child(firebaseEmailKey);

            userRef.get().addOnCompleteListener(task -> {
                DataSnapshot snapshot = task.getResult();
                String hashedPassword = hashPassword(inputPassword);

                String storedPassword = snapshot.child("password").getValue(String.class);
                String userId = snapshot.child("id").getValue(String.class);

                if (storedPassword != null && storedPassword.equals(hashedPassword)) {
                    File log = new File(getFilesDir(), "logs.txt");
                    FileOutputStream fos_log = null;
                    try {
                        fos_log = new FileOutputStream(log, true);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    OutputStreamWriter osw_log = new OutputStreamWriter(fos_log);
                    Date date = new Date();
                    String current_log = "\nUser with email " + inputEmail + " logged in successfully at " + date;

                    try {
                        osw_log.write(current_log);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        osw_log.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        fos_log.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    Intent intent = new Intent(MainActivity.this, Home.class);
                    intent.putExtra("USER_EMAIL", inputEmail);
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);
                    finish();
                } else {
                    File log = new File(getFilesDir(), "logs.txt");
                    FileOutputStream fos_log = null;
                    try {
                        fos_log = new FileOutputStream(log, true);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    OutputStreamWriter osw_log = new OutputStreamWriter(fos_log);
                    Date date = new Date();
                    String current_log = "\nSomeone tried to login to " + inputEmail + " but failed - " + date;

                    try {
                        osw_log.write(current_log);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        osw_log.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        fos_log.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }

            });

        });

        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: return a simple hash if SHA-256 fails
            return String.valueOf(password.hashCode());
        }
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
//                System.out.println("User " + i + ": " + mail + " | " + password + " | " + id);

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
                File log = new File(getFilesDir(), "logs.txt");
                FileOutputStream fos_log = new FileOutputStream(log, true);
                OutputStreamWriter osw_log = new OutputStreamWriter(fos_log);
                Date date = new Date();
                String current_log = "\nSomeone tried to login with mail " + inputEmail + "but failed - " + date;

                osw_log.write(current_log);
                osw_log.close();
                fos_log.close();
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