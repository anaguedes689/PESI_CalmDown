package com.feup.pesi.calmdown;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.feup.pesi.calmdown.activity.DashBoardActivity;
import com.feup.pesi.calmdown.activity.RespirationActivity;
import com.feup.pesi.calmdown.activity.StatsActivity;
import com.feup.pesi.calmdown.activity.StressActivity;
import com.google.common.math.Stats;
import com.feup.pesi.calmdown.service.SyncService;
import com.feup.pesi.calmdown.service.BluetoothService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends DashBoardActivity {

    private Button btnStress, btnHrv, btnStats;
    private TextView userNameTextView; // Assuming you have a TextView to display the user name

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String address = "";
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



        // Agendar o trabalho de sincronização
        //scheduleSyncJob();

        // Check if the user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If not logged in, redirect to the login page
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Finish the current activity to prevent the user from coming back to it
            return;
        }

        setContentView(R.layout.activity_main);

        userNameTextView = findViewById(R.id.textViewName); // Replace with your actual TextView ID
        btnStress = findViewById(R.id.btnStress);
        btnHrv = findViewById(R.id.btnHrv);
        btnStats = findViewById(R.id.btnStats);


        btnStress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir StressActivity
                Intent intent = new Intent(MainActivity.this, StressActivity.class);
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

        // Set up the dashboard UI (assuming this is a common function)
        setUpBottomNavigation();

        // Retrieve and display the user's name
        displayUserName();
    }

    public String Reccuperateadress() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("selectedValue", "");
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

    private void scheduleSyncJob() {
        ComponentName serviceName = new ComponentName(this, SyncService.class);
        JobInfo jobInfo = new JobInfo.Builder(SyncService.JOB_ID, serviceName)
                .setPeriodic(60 * 60 * 1000) // A cada hora
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
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
