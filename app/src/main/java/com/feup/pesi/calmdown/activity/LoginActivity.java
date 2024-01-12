package com.feup.pesi.calmdown.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.feup.pesi.calmdown.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private Button registerButton;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);


        // Retrieve FirebaseApp instance from the intent
        String firebaseAppName = getIntent().getStringExtra("firebaseApp");
        FirebaseApp firebaseApp = null;

        if (firebaseAppName != null) {
            firebaseApp = FirebaseApp.getInstance(firebaseAppName);
        }

// Initialize FirebaseAuth using the retrieved FirebaseApp instance
        mAuth = FirebaseAuth.getInstance(firebaseApp);

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Save user's email and password in SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("Email", email);
                                editor.putString("Password", password);
                                editor.apply();

                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    Toast.makeText(LoginActivity.this, "Authentication successful.",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    String userId = user.getUid(); // Get the UID of the current user
                                    editor.putString("userId", userId);
                                    editor.apply();
                                    CollectionReference quizzesCollection = db.collection("quizzes"); // Assuming the collection name is "quizzes"
                                    quizzesCollection.whereEqualTo("userId", userId).get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            if (task1.getResult().isEmpty()) {
                                                // If the user has not yet completed the quiz, redirect to QuizzActivity
                                                Intent intent = new Intent(LoginActivity.this, QuizzActivity.class);
                                                startActivity(intent);
                                            } else {
                                                // If the user has already completed the quiz, redirect to MainActivity
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.putExtra("Email", email);  // Send email to MainActivity
                                                startActivity(intent);
                                            }
                                        }
                                    });

                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
        @Override
        protected void onStart() {
            super.onStart();

            // Check if user is already logged in
            String email = sharedPreferences.getString("Email", null);
            String password = sharedPreferences.getString("Password", null);

            if (email != null && password != null) {
                // User is already logged in, start MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("Email", email);  // Send email to MainActivity
                startActivity(intent);
            }
        }

}
