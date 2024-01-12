package com.feup.pesi.calmdown.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.feup.pesi.calmdown.R;
import com.feup.pesi.calmdown.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditUserActivity extends DashBoardActivity {

    private EditText nameField;
    private EditText birthDateField;
    private EditText emailField;
    private EditText weightField;
    private EditText heightField;
    private CheckBox maleCheckBox;
    private CheckBox femaleCheckBox;
    private Button saveButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User currentUser;
    private User originalUser;

    private String loggedUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setUpBottomNavigation();


        nameField = findViewById(R.id.edit_name);
        birthDateField = findViewById(R.id.edit_birth_date);
        emailField = findViewById(R.id.edit_email);
        weightField = findViewById(R.id.edit_weight);
        heightField = findViewById(R.id.edit_height);

        maleCheckBox = findViewById(R.id.edit_maleCheckBox);
        femaleCheckBox = findViewById(R.id.edit_femaleCheckBox);

        saveButton = findViewById(R.id.save_changes);

        // Fetch the current user's information from Firebase and populate the fields
        String currentUserId = getIntent().getStringExtra("USER_ID");


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        fetchUserData(currentUserId);
        String finalCurrentUserId = currentUserId;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(finalCurrentUserId);
            }
        });
    }

    private void populateFields(User user) {
        nameField.setText(user.getName());
        emailField.setText(user.getUserEmail());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedBirthDate = sdf.format(user.getBirthdaydate());
        birthDateField.setText(formattedBirthDate);
        weightField.setText(String.valueOf(user.getWeight()));
        heightField.setText(String.valueOf(user.getHeight()));


        // Set checkboxes based on user's gender
        if ("Male".equals(user.getSex())) {
            maleCheckBox.setChecked(true);
            femaleCheckBox.setChecked(false);
        } else if ("Female".equals(user.getSex())) {
            maleCheckBox.setChecked(false);
            femaleCheckBox.setChecked(true);
        }
    }

    private void fetchUserData(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User document found, parse data and populate fields
                        originalUser = documentSnapshot.toObject(User.class);
                        if (originalUser != null) {
                            populateFields(originalUser);
                        }
                    } else {
                        Toast.makeText(EditUserActivity.this, "User not found in Firestore.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch user data
                    Toast.makeText(EditUserActivity.this, "Failed to fetch user data.",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private String getSelectedGender() {
        if (maleCheckBox.isChecked()) {
            return "Male";
        } else if (femaleCheckBox.isChecked()) {
            return "Female";
        } else {
            return "";
        }
    }

    private void updateProfile(String currentUserId) {

        if (originalUser != null && isDataChanged()) {
            String name = nameField.getText().toString();
            String sex = getSelectedGender();
            String email = emailField.getText().toString();
            int weight = Integer.parseInt(weightField.getText().toString());
            int height = Integer.parseInt(heightField.getText().toString());

            String birthDateString = birthDateField.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date birthDate = null;
            try {
                birthDate = sdf.parse(birthDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Update the user's information in Firebase
            User updatedUser = new User(name, birthDate, email, weight, height, sex);

            db.collection("users").document(currentUserId).set(updatedUser)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditUserActivity.this, "Profile updated successfully.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditUserActivity.this, UserActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(EditUserActivity.this, "Failed to update profile.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Nenhum dado foi alterado
            Toast.makeText(EditUserActivity.this, "No changes to save.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isDataChanged() {
        return !nameField.getText().toString().equals(originalUser.getName())
                || !birthDateField.getText().toString().equals(originalUser.getUserEmail())
                || !emailField.getText().toString().equals(originalUser.getUserEmail())
                || Integer.parseInt(weightField.getText().toString()) != originalUser.getWeight()
                || Integer.parseInt(heightField.getText().toString()) != originalUser.getHeight()
                || !getSelectedGender().equals(originalUser.getSex());
    }
}
