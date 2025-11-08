package com.example.taskforce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.UUID;

public class Register extends AppCompatActivity {

    private static final String DB_FILE_NAME = "db.json";
    private Button buttonRegister;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        EditText email = findViewById(R.id.editTextTextEmailAddress2);
        EditText password = findViewById(R.id.editTextTextPassword);
        EditText passwordRetype = findViewById(R.id.editTextTextPassword2);
        EditText group = findViewById(R.id.editTextGroup);
        buttonRegister = findViewById(R.id.buttonRegister);
        TextView goToLogin = findViewById(R.id.textViewGoToLogin);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://taskforce-21df9-default-rtdb.europe-west1.firebasedatabase.app");
        usersRef = database.getReference("Users");

        buttonRegister.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String passwordRetypeText = passwordRetype.getText().toString().trim();
            String groupText = group.getText().toString().trim();

            // Validation
            if (emailText.isEmpty() || passwordText.isEmpty() || passwordRetypeText.isEmpty() || groupText.isEmpty()) {
                Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(emailText)) {
                Toast.makeText(Register.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
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

            // Check if email already exists
            checkEmailExists(emailText, passwordText, groupText);
        });

        goToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void checkEmailExists(String emailText, String passwordText, String groupText) {
        buttonRegister.setEnabled(false);
        buttonRegister.setText("Checking...");

        String userKey = emailText.replace(".", ",");

        usersRef.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Email already registered
                    buttonRegister.setEnabled(true);
                    buttonRegister.setText("Register");
                    Toast.makeText(Register.this, "This email is already registered. Please use a different email or login.", Toast.LENGTH_LONG).show();
                } else {
                    // Email is available, proceed with registration
                    registerNewUser(emailText, passwordText, groupText);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                buttonRegister.setEnabled(true);
                buttonRegister.setText("Register");
                Toast.makeText(Register.this, "Error checking email availability: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerNewUser(String emailText, String passwordText, String groupText) {
        buttonRegister.setEnabled(false);
        buttonRegister.setText("Registering...");

        String hashedPassword = hashPassword(passwordText);
        String userId = UUID.randomUUID().toString();

        // Create User object
        User user = new User(emailText, hashedPassword, userId, groupText);

        // Save to Firebase
        String userKey = emailText.replace(".", ",");
        usersRef.child(userKey).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // Also save to local JSON file
                    if (saveUserToLocalFile(emailText, hashedPassword, userId, groupText)) {
                        Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Register.this, "Registration successful! But local save failed.", Toast.LENGTH_SHORT).show();
                    }

                    // Log the registration
                    logRegistration(emailText, true, null);

                    // Clear fields
                    clearFormFields();

                    // Go to login
                    Intent intent = new Intent(Register.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    buttonRegister.setEnabled(true);
                    buttonRegister.setText("Register");
                    Toast.makeText(Register.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    logRegistration(emailText, false, e.getMessage());
                });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void clearFormFields() {
        EditText email = findViewById(R.id.editTextTextEmailAddress2);
        EditText password = findViewById(R.id.editTextTextPassword);
        EditText passwordRetype = findViewById(R.id.editTextTextPassword2);
        EditText group = findViewById(R.id.editTextGroup);

        email.setText("");
        password.setText("");
        passwordRetype.setText("");
        group.setText("");
    }

    private void logRegistration(String email, boolean success, String errorMessage) {
        try {
            File log = new File(getFilesDir(), "logs.txt");
            FileOutputStream fos_log = new FileOutputStream(log, true);
            OutputStreamWriter osw_log = new OutputStreamWriter(fos_log);
            Date date = new Date();

            String logEntry = success ?
                    "\nUser with email " + email + " registered successfully at " + date :
                    "\nFailed registration attempt with email " + email + " at " + date + " - Error: " + errorMessage;

            osw_log.write(logEntry);
            osw_log.close();
            fos_log.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean saveUserToLocalFile(String email, String password, String id, String groupid) {
        try {
            // Create user JSON object
            JSONObject userJson = new JSONObject();
            userJson.put("mail", email);
            userJson.put("password", password);
            userJson.put("id", id);
            userJson.put("group", groupid);

            // Read existing data from file
            JSONArray usersArray = readExistingUsers();

            // Check if email already exists in local file (for consistency)
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject existingUser = usersArray.getJSONObject(i);
                String existingEmail = existingUser.getString("mail");
                if (existingEmail.equals(email)) {
                    // Remove the existing entry to avoid duplicates
                    usersArray.remove(i);
                    break;
                }
            }

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
}