package com.feup.pesi.calmdown.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.feup.pesi.calmdown.R;
import android.os.Handler;


public class RespirationActivity extends DashBoardActivity {
    private float stressLevel;
    private TextView stressTextView;
    private Handler handler;
    private static final int UPDATE_INTERVAL = 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiration);
        setUpBottomNavigation();

        stressTextView = findViewById(R.id.textStress);
        stressLevel = ReccuperateStress();
        stressTextView.setText(String.valueOf(stressLevel));

        ImageView imageView = findViewById(R.id.gifImageView);
        Glide.with(this)
                .load(R.drawable.breathing_orange)
                .into(imageView);

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
