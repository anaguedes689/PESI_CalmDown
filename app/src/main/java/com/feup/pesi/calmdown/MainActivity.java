package com.feup.pesi.calmdown;


import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feup.pesi.calmdown.activity.DashBoardActivity;
import com.feup.pesi.calmdown.activity.HrActivity;
import com.feup.pesi.calmdown.activity.StatsActivity;
import com.feup.pesi.calmdown.service.BluetoothService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends DashBoardActivity {

    private Button btnStress, btnStats;
    private ProgressBar stressbar;
    private float stressLevel;
    private TextView stressTextView;
    private TextView userNameTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String address = "";
    private Handler handler;
    private static final int UPDATE_INTERVAL = 60 * 1000;
    private boolean isFirstRun = true;
    private ProgressBar circularProgressBar;

    float maxStressLevel = 100.0f;

    private String jacketDocumentId;
    private String selectedVariable;
    //View circularView;
    private ArrayList<Long> rr;
    private Date selectedDate = new Date();
    private BluetoothService bluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        handler = new Handler();

        //circularView = findViewById(R.id.circularView);
        circularProgressBar = findViewById(R.id.circularProgressBar);

        // Check if the user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If not logged in, redirect to the login page
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Finish the current activity to prevent the user from coming back to it
            return;
        }

        if (isFirstRun) {
            // Adia a execução de startUpdatingStressLevel() por 1 minuto
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startUpdatingStressLevel();
                }
            }, UPDATE_INTERVAL);

            isFirstRun = false;  // Define a flag para false após o atraso inicial
        } else {
            // Se não for a primeira execução, inicia imediatamente
            startUpdatingStressLevel();
        }


       // circularProgressBar = findViewById(R.id.circularProgressBar);

        setContentView(R.layout.activity_main);


        userNameTextView = findViewById(R.id.textViewName); // Replace with your actual TextView ID
        btnStress = findViewById(R.id.btnStress);
        btnStats = findViewById(R.id.btnStats);
        //stressbar = findViewById(R.id.stressbar);

        stressTextView = findViewById(R.id.StressLevel);
        stressLevel = ReccuperateStress();
        stressTextView.setText(String.valueOf(stressLevel));

        btnStress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir StressActivity
                Intent intent = new Intent(MainActivity.this, HrActivity.class);
                startActivity(intent);
            }
        });

        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir StressActivity
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });

        setUpBottomNavigation();

        // Retrieve and display the user's name
        displayUserName();
    }

    public String Reccuperateadress() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("selectedValue", "");
    }
    private void updateCircularProgressBar(float stressLevel) {
        // Atualiza o progresso do círculo com animação
        circularProgressBar.setProgress(Math.round(stressLevel));

    }

    private void startUpdatingStressLevel() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Atualiza o stress level
                stressLevel = ReccuperateStress();
                stressTextView.setText(String.valueOf(stressLevel));
                //updateCircularProgressBar(stressLevel);
                float percentageFilled = stressLevel / maxStressLevel;
                int fullWidth = getResources().getDimensionPixelSize(R.dimen.circular_view_full_width);
                int filledWidth = (int) (fullWidth * percentageFilled);

                int progress = Math.round(((int)stressLevel / maxStressLevel) * 100);
                circularProgressBar.setProgress(progress);
                // Agende a próxima atualização após 1 minuto
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    public float ReccuperateStress() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getFloat("stresslevel", 0.0f); // 0.0f é o valor padrão se a chave não existir
    }


    private void displayUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("name");
                        if (userName != null && !userName.isEmpty()) {
                            userNameTextView.setText("Hello, " + userName + "!");
                        }
                    }
                }
            });
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Vincula a instância do serviço
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();

            // Verifica se há um dispositivo previamente selecionado
            if (bluetoothService != null) {
                // Inicia o BluetoothService, se necessário
                if (!bluetoothService.isServiceRunning()) {
                    Intent serviceIntent = new Intent(MainActivity.this, BluetoothService.class);
                    startService(serviceIntent);
                }

                // Conecta ao dispositivo previamente selecionado
                bluetoothService.startBluetoothConnection();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onBindingDied(ComponentName name) {
            ServiceConnection.super.onBindingDied(name);
        }

        @Override
        public void onNullBinding(ComponentName name) {
            ServiceConnection.super.onNullBinding(name);
        }

    };

}
