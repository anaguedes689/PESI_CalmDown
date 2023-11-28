package com.feup.pesi.calmdown;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.feup.pesi.calmdown.R;

public class MainActivity extends AppCompatActivity {

    private Button btnStress, btnHrv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStress = findViewById(R.id.btnStress);
        btnHrv = findViewById(R.id.btnHrv);

        btnStress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir StressActivity
                Intent stressIntent = new Intent(MainActivity.this, StressActivity.class);
                startActivity(stressIntent);
            }
        });

        btnHrv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir HrvActivity
                Intent hrvIntent = new Intent(MainActivity.this, HrActivity.class);
                startActivity(hrvIntent);
            }
        });
    }
}

