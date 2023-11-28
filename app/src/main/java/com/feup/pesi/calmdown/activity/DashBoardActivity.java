package com.feup.pesi.calmdown.activity;

import android.content.Intent;
import android.os.Bundle;

import com.feup.pesi.calmdown.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;

public class DashBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_home) {
                startActivity(new Intent(DashBoardActivity.this, PrimeActivity.class));
                return true;
            } else if (itemId == R.id.action_profile) {
                startActivity(new Intent(DashBoardActivity.this, UserActivity.class));
                return true;
            } else if (itemId == R.id.action_device) {
                startActivity(new Intent(DashBoardActivity.this, DeviceActivity.class));
                return true;
            }
            return false;
        });
    }
}