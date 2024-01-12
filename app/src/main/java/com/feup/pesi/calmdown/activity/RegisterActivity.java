package com.feup.pesi.calmdown.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.feup.pesi.calmdown.R;
import com.feup.pesi.calmdown.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private EditText nameField;
    private EditText birthDateField;
    private EditText weightField;
    private EditText heightField;
    private CheckBox maleCheckBox;
    private CheckBox femaleCheckBox;
    private Button registerButton;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Assuming you have a layout file named activity_register

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        nameField = findViewById(R.id.name);
        birthDateField = findViewById(R.id.birth_date);
        weightField = findViewById(R.id.weight);
        heightField = findViewById(R.id.height);

        maleCheckBox = findViewById(R.id.maleCheckBox);
        femaleCheckBox = findViewById(R.id.femaleCheckBox);

        registerButton = findViewById(R.id.register);
        loginButton = findViewById(R.id.login);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String confirmPassword = confirmPasswordField.getText().toString();
                String name = nameField.getText().toString();
                String sex = getSelectedGender();
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


                if (password.equals(confirmPassword)) {
                    Date finalBirthDate = birthDate;
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    User user = new User(name, finalBirthDate,email, weight, height, sex);
                                    db.collection("users").document(mAuth.getCurrentUser().getUid()).set(user);
                                    Toast.makeText(RegisterActivity.this, "Registration successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> birthDateField.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private String getSelectedGender() {
        if (maleCheckBox.isChecked()) {
            return "Male";
        } else if (femaleCheckBox.isChecked()) {
            return "Female";
        } else {
            return ""; // You may want to handle this case based on your app's requirements
        }
    }
}
