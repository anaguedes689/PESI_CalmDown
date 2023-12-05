package com.feup.pesi.calmdown.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.feup.pesi.calmdown.R;

public class RespirationActivity extends DashBoardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiration);

        ImageView imageView = findViewById(R.id.gifImageView);
        Glide.with(this)
                .load(R.drawable.breathing)
                .into(imageView);
    }
}
