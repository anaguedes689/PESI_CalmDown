package com.feup.pesi.calmdown;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.feup.pesi.calmdown.activity.DashBoardActivity;
import com.feup.pesi.calmdown.activity.StressActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends DashBoardActivity {

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

    }
}
