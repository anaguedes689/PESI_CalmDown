package com.feup.pesi.calmdown;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.feup.pesi.calmdown.activity.DashBoardActivity;
import com.feup.pesi.calmdown.activity.StressActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends DashBoardActivity {

    private Button btnStress, btnHrv;
    private TextView userNameTextView; // Assuming you have a TextView to display the user name

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check if the user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If not logged in, redirect to the login page
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Finish the current activity to prevent the user from coming back to it
            return;
        }

        setContentView(R.layout.activity_main);

        userNameTextView = findViewById(R.id.textViewName); // Replace with your actual TextView ID
        btnStress = findViewById(R.id.btnStress);
        btnHrv = findViewById(R.id.btnHrv);

        btnStress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir StressActivity
                Intent intent = new Intent(MainActivity.this, StressActivity.class);
                startActivity(intent);
            }
        });

        // Set up the dashboard UI (assuming this is a common function)
        setUpBottomNavigation();

        // Retrieve and display the user's name
        displayUserName();
    }

    private void displayUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("name");
                        if (userName != null && !userName.isEmpty()) {
                            userNameTextView.setText("Hello, " + userName + "!");
                        }
                    }
                }
            });
        }
    }
}
