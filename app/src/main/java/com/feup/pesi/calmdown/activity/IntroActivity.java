package com.feup.pesi.calmdown.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.feup.pesi.calmdown.R;
import com.google.firebase.FirebaseApp;


public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Initialize FirebaseApp
        FirebaseApp.initializeApp(this);

        // Initialize Firebase if not already initialized
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        ImageView imageView = findViewById(R.id.gifImageView1);
        Glide.with(this)
                .load(R.drawable.gifintro)
                .into(imageView);


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);

                // Pass FirebaseApp instance as an extra
                intent.putExtra("firebaseApp", FirebaseApp.getInstance().getName());

                startActivity(intent);
                finish();
            }
        }, 2900); // 3 seconds delay for demonstration purposes
    }
}
