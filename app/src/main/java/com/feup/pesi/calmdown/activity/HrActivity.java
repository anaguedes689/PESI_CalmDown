package com.feup.pesi.calmdown.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.feup.pesi.calmdown.R;
import com.feup.pesi.calmdown.model.User;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.feup.pesi.calmdown.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class HrActivity extends DashBoardActivity {

    //Botao para atualizar
    //visualizar nivel stress
    //emitir notificação que vai à respiration activity

    private Button btnHrv;
    private String loggedUserId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String jacketDocumentId;
    private TextView hrvString;
    private TextView imcString;

    private VelocimeterView  velocimeterView;
    private int height;
    private int weight;
    private int age;

    private ArrayList<Long> pulse;
    private ArrayList<Long> rr;
    private ArrayList<Long> bpm;
    private ArrayList<Long> bpmi;

    private String userSex;

    private String selectedVariable = "pulse";
    private Date selectedDate = new Date();

    //private ArrayList<Integer> rr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get the user ID of the logged user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loggedUserId = currentUser.getUid();
        }

        hrvString = findViewById(R.id.hrvString);
        velocimeterView = findViewById(R.id.velocimeterView);
        btnHrv = findViewById(R.id.btnHrv);
        jacketDocumentId = ReccuperatejacketId();
        selectedVariable = getResources().getStringArray(R.array.variable_options)[0].toLowerCase(); // Pega o primeiro item

        hrvString.setText(String.valueOf(getSDNN()));

        ValueAnimator animator = ValueAnimator.ofFloat((float) getSDNN(), 200);
        animator.setDuration(500);

        animator.start();
        btnHrv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadAndDisplayUserData(loggedUserId);
                obterDadosDaFirebasePeloIdDocumento(jacketDocumentId, selectedDate, selectedVariable);

                double hrvValue = getSDNN();
                hrvString.setText(String.valueOf(hrvValue));
                animateVelocimeter(hrvValue);
            }
        });

    }

    private void animateVelocimeter(double hrvValue) {
        // Animate the VelocimeterView based on the HRV value
        ValueAnimator animator = ValueAnimator.ofFloat(velocimeterView.getCurrentValue(), (float) hrvValue);
        animator.setDuration(1000); // Adjust duration as needed

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                // Update your custom view with the animated value
                velocimeterView.setCurrentValue(animatedValue);
            }
        });

        animator.start();
    }

    private void loadAndDisplayUserData(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userSex = document.getString("sex");
                    // Retrieve the timestamp
                    Timestamp timestamp = document.getTimestamp("birthdaydate");
                    Date userBirthday = timestamp != null ? timestamp.toDate() : null; age = calculateAge(userBirthday);

                    height = document.getLong("height").intValue(); // Assuming "height" is stored as a Long
                    weight = document.getLong("weight").intValue(); // Assuming "weight" is stored as a Long

                    double imc = getIMC(height,weight);

                    imcString.setText(String.valueOf(imc));

                }
            }
        });
    }

    public String ReccuperatejacketId() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("jacketDocumentId", "");
    }

    public void obterDadosDaFirebasePeloIdDocumento(String idDocumento, Date selectedDate, String selectedVariable) {
        db.collection("jacket")
                .document(idDocumento)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String pulse1="pulse";
                                String rr1="rr";
                                String bpm1= "bpm";
                                String bpmi1 ="bpmi";
                                ArrayList<Timestamp> timestampList = (ArrayList<Timestamp>) document.get("dateTimeSpan");
                                ArrayList<Date> dateTimeSpan = new ArrayList<>();

                                for (Timestamp timestamp : timestampList) {
                                    dateTimeSpan.add(timestamp.toDate());
                                }
                                if (selectedVariable.equalsIgnoreCase(pulse1)) {
                                    pulse = (ArrayList<Long>) document.get("pulse");
                                    filterData(selectedDate, selectedVariable, dateTimeSpan, pulse, "Pulse");
                                } else if (selectedVariable.equalsIgnoreCase(rr1)) {
                                    rr = (ArrayList<Long>) document.get("rr");
                                    filterData(selectedDate, selectedVariable, dateTimeSpan, rr, "RR");
                                } else if (selectedVariable.equalsIgnoreCase(bpm1)){
                                    bpm = (ArrayList<Long>) document.get("bpm");
                                    filterData(selectedDate, selectedVariable, dateTimeSpan, bpm, "bpm");
                                }else if (selectedVariable.equalsIgnoreCase(bpmi1)){
                                    bpmi = (ArrayList<Long>) document.get("bpmi");
                                    filterData(selectedDate, selectedVariable, dateTimeSpan, bpmi, "bpmi");

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

    private void filterData(Date selectedDate, String selectedVariable, ArrayList<Date> dateTimeSpan, ArrayList<Long> data, String label) {
        List<Entry> entries = new ArrayList<>();


        boolean dataFound = false;
        // Adiciona os valores ao gráfico apenas se a data e variável coincidirem
        for (int i = 0; i < dateTimeSpan.size(); i++) {
            if (isSameDate(dateTimeSpan.get(i), selectedDate) && selectedVariable.equals(label.toLowerCase())) {
                entries.add(new Entry(dateTimeSpan.get(i).getTime(), ( data.get(i)).floatValue()));
                dataFound = true;
                // Adicione outras verificações de tipo conforme necessário para outros tipos de dados
            }
        }


        if (!dataFound) {
            showToast("No data for that date");
        }
    }

    private void showToast(String message) {
        // Método para exibir um Toast com a mensagem fornecida
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        }
        Calendar today = Calendar.getInstance();
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);
        int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    private double getIMC(int h, int w){
        double imc = 0;
        imc = w*w/h;
    return imc;}

    public double getSDNN(){ //diferença atual e média
        // A high-risk group may be selected by the dichotomy limits of SDNN <50 ms
        ArrayList<Long> rr = this.rr;
        String stress = new String("Normal");
        double SDNN= 0;
        Float sum = (float) 0;
        for (int i = 0; i < this.rr.size(); i++) {
            sum = sum + rr.get(i);
        }
        float media = sum/(rr.size()+1);
        double diff = 0;
        for (int i = 0; i < this.rr.size(); i++) {
            diff = diff + Math.pow((double) rr.get(i) - media, 2);
        }
        SDNN = Math.sqrt((diff/(rr.size()-1)));
        return SDNN;}

    public String getInstantStress(){ //diferença atual e média
        // A high-risk group may be selected by the dichotomy limits of SDNN <50 ms
        ArrayList<Long> rr = this.rr;
        String stress = new String("Normal");
        double SDNN= getSDNN();

        if((50-16)<SDNN){
            stress = "Stress levels high!";
            if (SDNN<32){
                stress = "Stress levels EXTREMELY high!";
            }
        }

        return stress;}

    public double getPNN50(){ //percentagem de intervalos seguidos com diferença maior que 50ms
        ArrayList<Long> rr = this.rr;
        double PNN50 = 0;
        if(rr.size()>0){
            for (int i = 0; i < rr.size(); i++) {
                if ((rr.get(i+1) - rr.get(i)) > 50) {
                    PNN50 += 1;
                }
            }
        }
        PNN50 = PNN50*100/(rr.size()-1);
        return PNN50;}
    public double getRMSSD(){ //diferença entre atual e anterior
        ArrayList<Long> rr = this.rr;
        double RMSSD = 0;
        if(rr.size()>0){
            double diff = 0;
            for (int i = 0; i < (rr.size()); i++) {
                diff = diff + Math.pow((double) rr.get(i+1) - rr.get(i), 2);
            }
            RMSSD = Math.sqrt((diff/(rr.size()-1)));
        }
        return RMSSD;}

    public String getStatus(User user){
        String status = new String("Normal");
        String sex = user.getSex();
        int age = user.getAge();

        return status;}

    /*private void getStress(){
        if(this.rr.get(this.rr.size()-1)<NormalValues10Sec()){
            System.out.printf("STRESSED");
        }
    }*/

    private void NormalValues10Sec(){
        //SDNN: 24.1±16.4
        //RMSSD: 27.3±22.2
        // https://www.ncbi.nlm.nih.gov/pmc/articles/PMC5010946/
    }

    private void NormalValues5min(){
        //SDNN: 141+-39 ms
        //SDANN: 127+-35 ms
        //RMSSD: 27+- 12ms -> WELLTORY: 19 – 48 ms — healthy adults in the age group of 38 – 42 years
        //35 – 107 ms — elite athletes

        /*Freq Domain:
        tp: 3466+-1018 ms^2
        LF: 1170+-416 ms^2
        HF:975+-203 ms^2
        LF/HF: 1.5-2-0

        LOW HRV MORTALITY:
        rmssd<15ms
        pnn50:<0.75%
        SDNN<50ms
         */
        String newage = Integer.toString(this.age);
        char idad = 0;
        double SDNN = 0;
        double RMSSD = 0;
        double PNN50 =0 ;
        if(newage.length()>1){ //valores normais de 10 em 10 anos
            idad = newage.charAt(0);
            if(idad>=6){
                idad = 6;
            }
        }else{
            idad = '0';
        }
        int idade = Character.getNumericValue(idad);
        if(this.userSex.equals('f')){
            switch (idade){
                case(3):
                    SDNN = 138;
                    PNN50 = 13;
                case(4):
                    SDNN = 129;
                    PNN50 = 9;
                case(5):
                    SDNN = 135;
                    PNN50 = 5;
                case(6):
                    SDNN = 115;
                    PNN50 = 5;
                default:
                    SDNN = 188;
                    PNN50 = 23;

            }
        }
        if(this.userSex.equals('m')){
            switch (idade){
                case(3):
                    SDNN = 165;
                    PNN50 = 13;
                case(4):
                    SDNN = 155;
                    PNN50 = 8;
                case(5):
                    SDNN = 152;
                    PNN50 = 7;
                case(6):
                    SDNN = 140;
                    PNN50 = 7;
                default:
                    SDNN = 188;
                    PNN50 = 23;
            }
        }
    }

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
    }

    /*

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        setUpBottomNavigation();

        // Recupera o ID do documento do jacket
        //jacketDocumentId = ReccuperatejacketId();

        // Inicializa o Firestore
        db = FirebaseFirestore.getInstance();


        // Chama o método para obter dados da Firebase
        //obterDadosDaFirebasePeloIdDocumento(jacketDocumentId, selectedDate, selectedVariable);

        // Configura o ouvinte de clique para o botão de atualização
        ImageButton btnUpdateChart = findViewById(R.id.btnUpdateChart);
        btnUpdateChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chama o método para obter dados da Firebase novamente
                //obterDadosDaFirebasePeloIdDocumento(jacketDocumentId, selectedDate, selectedVariable);
            }
        });

        // Configura o ouvinte de seleção de variável
    }


    public void obterDadosDaFirebasePeloIdDocumento(String idDocumento, Date selectedDate, String selectedVariable) {
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
                                    ArrayList<Long> rr = (ArrayList<Long>) document.get("rr");
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
    }*/
}
