package com.example.taskforce;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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

public class AddNewTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_task);

        EditText taskTitle = findViewById(R.id.newTaskTitle);
        EditText taskGroup = findViewById(R.id.newTaskGroup);
        EditText taskNotes = findViewById(R.id.newTaskDetails);
        RadioGroup assignGroup = findViewById(R.id.assignRadioGroup);
        EditText assignedEmail = findViewById(R.id.assignedEmail);
        RadioGroup importanceGroup = findViewById(R.id.importanceRadioGroup);

        assignGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.assignSomeone) {
                assignedEmail.setVisibility(View.VISIBLE);
            } else {
                assignedEmail.setVisibility(View.GONE);
            }
        });



        final String connected_email = getIntent().getStringExtra("USER_EMAIL");


        int index = connected_email.lastIndexOf('.');
        String terminatie = connected_email.substring(index + 1, connected_email.length());

        final String parse = connected_email.substring(0, index) + ',' + terminatie;

        Log.d(TAG, "Connected email: " + parse);

        Button saveTask = findViewById(R.id.saveTaskBtn);

        saveTask.setOnClickListener(v -> {
            String newTaskTitle = taskTitle.getText().toString();
            String newTaskGroup = taskGroup.getText().toString();
            String newTaskNotes = taskNotes.getText().toString();

            if (newTaskTitle.isEmpty()) {
                Toast.makeText(this, "Please enter task title", Toast.LENGTH_SHORT).show();
                return;
            }

            final String assignedToEmailFinal; // variabilă finală pentru lambda
            boolean assignToOther = false;
            if (assignGroup.getCheckedRadioButtonId() == R.id.assignSomeone) {
                assignedToEmailFinal = assignedEmail.getText().toString().trim();
                assignToOther = true;
                if (assignedToEmailFinal.isEmpty()) {
                    Toast.makeText(this, "Please enter the email of the user", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                assignedToEmailFinal = connected_email; // atribuit pentru mine
            }

            int importance = 1;
            int checkedId = importanceGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.importance1) importance = 1;
            else if (checkedId == R.id.importance2) importance = 2;
            else if (checkedId == R.id.importance3) importance = 3;
            else if (checkedId == R.id.importance4) importance = 4;

            Task newTask = new Task(
                    newTaskTitle,
                    assignedToEmailFinal,
                    newTaskGroup,
                    "",
                    importance,
                    "",
                    newTaskNotes
            );

            FirebaseDatabase database = FirebaseDatabase.getInstance("https://taskforce-21df9-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference usersRef = database.getReference("Users");

            if (assignToOther) {
                // Referința către userul curent
                DatabaseReference currentUserRef = usersRef.child(parse);
                // Referința către userul căruia vrem să îi atribuim task-ul
                DatabaseReference assignedUserRef = usersRef.child(assignedToEmailFinal.replace(".", ","));

                // Mai întâi luăm grupul userului curent
                currentUserRef.child("group").get().addOnSuccessListener(currentSnapshot -> {
                    String currentUserGroupId = currentSnapshot.getValue(String.class);

                    if (currentUserGroupId == null) {
                        Toast.makeText(this, "Nu s-a putut determina grupul tău curent.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Apoi luăm grupul userului asignat
                    assignedUserRef.get().addOnSuccessListener(assignedSnapshot -> {
                        if (!assignedSnapshot.exists()) {
                            Toast.makeText(this, "Userul " + assignedToEmailFinal + " nu există.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String assignedGroupId = assignedSnapshot.child("group").getValue(String.class);

                        Log.d(TAG, "AssignedGroupID: " + assignedGroupId);
                        Log.d(TAG, "LoggedUserGroupID: " + currentUserGroupId);

                        if (assignedGroupId == null || !assignedGroupId.equals(currentUserGroupId)) {
                            Toast.makeText(this, "Userul " + assignedToEmailFinal + " nu face parte din grupul tău.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Totul e ok, salvăm task-ul la userul asignat
                        assignedUserRef.child("tasks")
                                .push()
                                .setValue(newTask)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Task atribuit lui " + assignedToEmailFinal, Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Eroare la salvarea task-ului: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });

                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Eroare la citirea userului: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Eroare la citirea grupului curent: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } else {
                // Task pentru mine
                usersRef.child(parse).child("tasks")
                        .push()
                        .setValue(newTask)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Task salvat pentru tine", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Eroare la salvarea task-ului: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });


    }

    private boolean writeTaskToJson(Task task, String userEmail) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;

        try {
            File file = new File(getFilesDir(), "tasks.json");

            JSONObject jsonObject;
            if (file.exists()) {
                // Read existing file
                String existingJson = readExistingJson(file);
                if (existingJson != null && !existingJson.isEmpty()) {
                    jsonObject = new JSONObject(existingJson);
                } else {
                    jsonObject = new JSONObject();
                }
            } else {
                jsonObject = new JSONObject();
            }

            JSONArray tasksArray;
            if (jsonObject.has(userEmail)) {
                tasksArray = jsonObject.getJSONArray(userEmail);
            } else {
                tasksArray = new JSONArray();
            }

            JSONObject taskJson = new JSONObject();
            taskJson.put("title", task.title);
            taskJson.put("author", task.author);
            taskJson.put("group", task.group);
            taskJson.put("deadline", task.deadline);
            taskJson.put("importance", task.importance);
            taskJson.put("pathToImage", task.pathToImage);
            taskJson.put("notes", task.notes);
            taskJson.put("timestamp", System.currentTimeMillis());

            // Add task to array
            tasksArray.put(taskJson);


            jsonObject.put(userEmail, tasksArray);


            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos);
            osw.write(jsonObject.toString(2));
            osw.flush();

            Log.d(TAG, "Task successfully written to JSON file for user: " + userEmail);
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error writing task to JSON: " + e.getMessage());
            return false;
        } finally {
            try {
                if (osw != null) osw.close();
                if (fos != null) fos.close();
            } catch (Exception e) {
                Log.e(TAG, "Error closing streams: " + e.getMessage());
            }
        }
    }

    // Helper method to read existing JSON
    private String readExistingJson(File file) {
        FileInputStream fis = null;
        BufferedReader br = null;

        try {
            fis = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line);
            }

            return content.toString();

        } catch (Exception e) {
            Log.e(TAG, "Error reading existing JSON: " + e.getMessage());
            return null;
        } finally {
            try {
                if (br != null) br.close();
                if (fis != null) fis.close();
            } catch (Exception e) {
                Log.e(TAG, "Error closing streams: " + e.getMessage());
            }
        }
    }
}