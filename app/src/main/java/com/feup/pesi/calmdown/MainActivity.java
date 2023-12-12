package com.feup.pesi.calmdown;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.feup.pesi.calmdown.activity.DashBoardActivity;
import com.feup.pesi.calmdown.activity.HrActivity;
import com.feup.pesi.calmdown.activity.StatsActivity;
import com.feup.pesi.calmdown.activity.StressActivity;
import com.feup.pesi.calmdown.service.BluetoothService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends DashBoardActivity {

    private Button btnStress, btnStats;
    private ProgressBar stressbar;
    private TextView userNameTextView; // Assuming you have a TextView to display the user name
    private TextView StressLevel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String address = "";

    private String jacketDocumentId;
    private String selectedVariable;

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


        jacketDocumentId = ReccuperatejacketId();
        selectedVariable = getResources().getStringArray(R.array.variable_options)[0].toLowerCase(); // Pega o primeiro item

        obterDadosDaFirebasePeloIdDocumento(jacketDocumentId, selectedDate);
        //fez set do RR

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
        btnStats = findViewById(R.id.btnStats);
        stressbar = findViewById(R.id.stressbar);
        StressLevel = findViewById(R.id.StressLevel);

        double rmssd = getRMSSD(rr);
        float stress = (float) (-1.12359*rmssd + 117.8);

        stressbar.setProgress(Math.round(stress));
        StressLevel.setText(String.valueOf(stress));


    /*
        Speed = (SpeedometerView)findViewById(R.id.speedometer);
        Speed.setLabelConverter(new SpeedometerView.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        // configure value range and ticks
        Speed.setMaxSpeed(100);
        Speed.setMajorTickStep(25);
        Speed.setMinorTicks(0);

// Configure value range colors
        Speed.addColoredRange(0, 50, Color.GREEN);
        Speed.addColoredRange(50, 75, Color.YELLOW);
        Speed.addColoredRange(75, 100, Color.RED);
        Speed.setSpeed(25, 2000, 500);*/



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

    public double getRMSSD(List<Long> rr){ //diferença entre atual e anterior
        double RMSSD = 0;
        if(rr.size()>0){
            double diff = 0;
            for (int i = 0; i < (rr.size()); i++) {
                diff = diff + Math.pow((double) rr.get(i + 1) - rr.get(i), 2);
            }
            RMSSD = Math.sqrt((diff/(rr.size()-1)));
        }
        return RMSSD;}

    public String ReccuperatejacketId() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("jacketDocumentId", "");
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
    public void obterDadosDaFirebasePeloIdDocumento(String idDocumento, Date selectedDate) {
        db.collection("jacket")
                .document(idDocumento)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String rr1="rr";
                                ArrayList<Timestamp> timestampList = (ArrayList<Timestamp>) document.get("dateTimeSpan");
                                ArrayList<Date> dateTimeSpan = new ArrayList<>();

                                for (Timestamp timestamp : timestampList) {
                                    dateTimeSpan.add(timestamp.toDate());
                                }
                                if (selectedVariable.equalsIgnoreCase(rr1)) {
                                    rr = (ArrayList<Long>) document.get("rr");
                                    //filterData(selectedDate, selectedVariable, dateTimeSpan, rr, "RR");

                                }



                            } else {
                                Log.d(TAG, "Documento não encontrado");
                            }
                        } else {
                            Log.w(TAG, "Falha ao obter documento", task.getException());
                        }
                    }
                });
    }


    /*

    public class VelocimeterView extends View {
        private Paint dialPaint;
        private Paint needlePaint;
        private Paint textPaint;

        private float currentValue = 0; // Current speed value

        public VelocimeterView(Context context) {
            super(context);
            init();
        }

        public VelocimeterView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public VelocimeterView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            dialPaint = new Paint();
            dialPaint.setColor(Color.BLACK);
            dialPaint.setStyle(Paint.Style.STROKE);
            dialPaint.setStrokeWidth(8f);
            dialPaint.setAntiAlias(true);

            needlePaint = new Paint();
            needlePaint.setColor(Color.RED);
            needlePaint.setStyle(Paint.Style.STROKE);
            needlePaint.setStrokeWidth(5f);
            needlePaint.setAntiAlias(true);

            textPaint = new Paint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(30f);
            textPaint.setAntiAlias(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();
            int centerX = width / 2;
            int centerY = height / 2;

            // Draw dial
            canvas.drawCircle(centerX, centerY, Math.min(centerX, centerY) - 20, dialPaint);

            // Draw needle
            float angle = (currentValue / 220) * 180 - 90; // Assuming a range of 0 to 220 for demonstration
            float needleLength = Math.min(centerX, centerY) - 40;
            float startX = centerX + needleLength / 2 * (float) Math.cos(Math.toRadians(angle));
            float startY = centerY + needleLength / 2 * (float) Math.sin(Math.toRadians(angle));
            float endX = centerX + needleLength * (float) Math.cos(Math.toRadians(angle));
            float endY = centerY + needleLength * (float) Math.sin(Math.toRadians(angle));
            canvas.drawLine(startX, startY, endX, endY, needlePaint);

            // Draw text
            String speedText = String.format("%.1f", currentValue);
            float textWidth = textPaint.measureText(speedText);
            canvas.drawText(speedText, centerX - textWidth / 2, centerY + 50, textPaint);
        }

        // Method to set the current value and trigger a redraw
        public void setCurrentValue(float value) {
            currentValue = value;
            invalidate(); // Trigger onDraw
        }

        public float getCurrentValue() {
            return currentValue;
        }
    }*/
}
