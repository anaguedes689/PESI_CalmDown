package com.feup.pesi.calmdown.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.feup.pesi.calmdown.MainActivity;
import com.feup.pesi.calmdown.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Intent;
import com.feup.pesi.calmdown.LoginActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserActivity extends DashBoardActivity {

    private String loggedUserId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView ageTextView;
    private TextView sexTextView;
    private TextView heightTextView;
    private TextView weightTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get the user ID of the logged user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loggedUserId = currentUser.getUid();
        }

        // Obter referências para os elementos da interface do usuário
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        ageTextView = findViewById(R.id.ageTextView);
        sexTextView = findViewById(R.id.sexTextView);
        heightTextView = findViewById(R.id.heightTextView);
        weightTextView = findViewById(R.id.weightTextView);

        // Carregar e exibir dados do usuário logados
        loadAndDisplayUserData(loggedUserId);

        // Botão de edição
        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, EditUserActivity.class);
            intent.putExtra(loggedUserId, "USER_ID");
            startActivity(intent);
        });

        Button editQuizzButton = findViewById(R.id.editQuizzButton);
        editQuizzButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, QuizzActivity.class);
            startActivity(intent);
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        setUpBottomNavigation();
    }

    private void loadAndDisplayUserData(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String userName = document.getString("name");
                    String userEmail = document.getString("userEmail");
                    String userSex = document.getString("sex");
                    // Retrieve the timestamp
                    Timestamp timestamp = document.getTimestamp("birthdaydate");
                    Date userBirthday = timestamp != null ? timestamp.toDate() : null;int userAge = calculateAge(userBirthday);

                    int userHeight = document.getLong("height").intValue(); // Assuming "height" is stored as a Long
                    int userWeight = document.getLong("weight").intValue(); // Assuming "weight" is stored as a Long

                    // Configurar TextViews com os dados do usuário logado
                    nameTextView.setText(userName);
                    emailTextView.setText(userEmail);
                    ageTextView.setText(String.valueOf(userAge));
                    sexTextView.setText(userSex);
                    heightTextView.setText(String.valueOf(userHeight));
                    weightTextView.setText(String.valueOf(userWeight));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.deleteAccountItem) {
            // Lógica para excluir a conta do usuário
            return true;
        } else if (itemId == R.id.settingsItem) {
            // Lógica para navegar para as configurações do usuário
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        }

        Calendar today = Calendar.getInstance();
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);

        int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }
    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}