package com.feup.pesi.calmdown.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.feup.pesi.calmdown.R;


public class StressActivity extends DashBoardActivity {
    private Button btnStress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stress_main);

        btnStress = findViewById(R.id.btnRespiration);

        btnStress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir StressActivity
                Intent intent = new Intent(StressActivity.this, RespirationActivity.class);
                startActivity(intent);
            }
        });
    }
}

