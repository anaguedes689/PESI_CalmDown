package com.feup.pesi.calmdown.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.feup.pesi.calmdown.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Handler;


public class RespirationActivity extends DashBoardActivity {
    private float stressLevel;
    private TextView stressTextView;
    private Handler handler;
    private static final int UPDATE_INTERVAL = 60 * 1000;

    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiration);
        setUpBottomNavigation();

        stressTextView = findViewById(R.id.textStress);
        stressLevel = ReccuperateStress();

        stressTextView.setText(String.valueOf(stressLevel));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            Log.d("UserID", currentUserId);

            // Obter o campo "quizz" do documento do usuário na coleção "users"
            db.collection("users")
                    .document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String quizzId = documentSnapshot.getString("quizz");

                            // Usar o campo "quizzId" para obter o documento correspondente na coleção "quizzes"
                            db.collection("quizzes")
                                    .document(quizzId)
                                    .get()
                                    .addOnSuccessListener(quizzDocument -> {
                                        if (quizzDocument.exists()) {
                                            String userColor = quizzDocument.getString("userColor");
                                            Log.d("userColor", userColor);

                                            // Agora que temos a cor do utilizador, podemos continuar com o restante do código
                                            String gifName;
                                            switch (userColor) {
                                                case "#FF643A":
                                                    gifName = "breathing_orange";
                                                    break;
                                                case "#FFACDF":
                                                    gifName = "breathing_pink";
                                                    break;
                                                case "#4FDAC1":
                                                    gifName = "breathing_green";
                                                    break;
                                                case "#3CA9BC":
                                                    gifName = "breathing_blue";
                                                    break;
                                                default:
                                                    gifName = "breathing_orange";
                                                    break;
                                            }

                                            int resourceId = getResources().getIdentifier(gifName, "drawable", getPackageName());

                                            ImageView imageView = findViewById(R.id.gifImageView);
                                            Glide.with(this)
                                                    .load(resourceId)
                                                    .into(imageView);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Lidar com falhas ao buscar o documento quizzes
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Lidar com falhas ao buscar o documento users
                    });
        }


        // Inicializa o Handler e começa a atualizar o stress level
        handler = new Handler();
        startUpdatingStressLevel();
    }

    private void startUpdatingStressLevel() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Atualiza o stress level
                stressLevel = ReccuperateStress();

                stressTextView.setText(String.valueOf(stressLevel));

                // Agende a próxima atualização após 1 minuto
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        // Remove callbacks para evitar vazamentos
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public float ReccuperateStress() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getFloat("stresslevel", 0.0f); // 0.0f é o valor padrão se a chave não existir
    }

}
