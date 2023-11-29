package com.feup.pesi.calmdown;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;


public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Initialize FirebaseApp
        FirebaseApp.initializeApp(this);

        // Other code...

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);

                // Pass FirebaseApp instance as an extra
                intent.putExtra("firebaseApp", FirebaseApp.getInstance().getName());

                startActivity(intent);
                finish();
            }
        }, 1000); // 3 seconds delay for demonstration purposes
    }
}
