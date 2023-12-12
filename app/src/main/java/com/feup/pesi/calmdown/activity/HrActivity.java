package com.feup.pesi.calmdown.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HrActivity extends DashBoardActivity {

    //Botao para atualizar
    //visualizar nivel stress
    //emitir notificação que vai à respiration activity

    private String loggedUserId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LineChart lineChart;
    private TextView hrvString;
    private TextView imcString;

    private int height;
    private int weight;
    private int age;

   private ArrayList<Long> pulse;
    private ArrayList<Long> rr;
    private ArrayList<Long> bpm;
    private ArrayList<Long> bpmi;

    private String userSex;

    private String selectedVariable = "rr";
    private String jacketDocumentId;
    private Date selectedDate = new Date();

    //private ArrayList<Integer> rr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr);
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        lineChart = findViewById(R.id.lineChart1);

        jacketDocumentId = ReccuperatejacketId();
        selectedVariable = getResources().getStringArray(R.array.variable_options)[0].toLowerCase(); // Pega o primeiro item


        // Get the user ID of the logged user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loggedUserId = currentUser.getUid();
        }

        hrvString.setText(String.valueOf(getSDNN()));

        ImageButton btnUpdateChart = findViewById(R.id.btnUpdateChart);
        btnUpdateChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chama o método para obter dados da Firebase novamente
                obterDadosDaFirebasePeloIdDocumento(jacketDocumentId, selectedDate);
            }
        });

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

                    height = document.getLong("height").intValue();
                    weight = document.getLong("weight").intValue();

                    double imc = getIMC(height,weight);

                    imcString.setText(String.valueOf(imc));

                }
            }
        });
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

    public List<Float> defineInterval(){
        String sex = this.userSex;
        double up, down;
        float imc = (float) getIMC(this.height,this.weight);
        up = 20;
        down = 89;
        List<Float> value = null;
        if(imc>24.9 ||imc<18.5){
            //nao saudaveis
            up = 22;
            down = 79;
            if(sex.equals("Male")){
                up = 23;
                down = 72;
            }

        }else{
            //saudaveis
            up = 48;
            down = 13;
            if(sex.equals("Male")){
                up = 82;
                down = 53.5;
            }
        }
        value.add((float) up);
        value.add((float) down);
    return value;}

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

        // Configuração do eixo X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DateValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(45f);

        // Configuração do eixo Y
        YAxis yAxis = lineChart.getAxisLeft();

        // Configuração dos dados do gráfico
        lineChart.getDescription().setText(label);
        lineChart.getDescription().setTextSize(16f);
        lineChart.getLegend().setEnabled(false);

        // Criação do conjunto de dados
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);
        dataSet.setLineWidth(2f);

        // Adiciona o conjunto de dados ao gráfico
        lineChart.setData(new LineData(dataSet));

        // Atualiza o gráfico
        lineChart.invalidate();

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

    private class DateValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            // Converte o valor do eixo X (tempo em milissegundos) de volta para uma data formatada
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return dateFormat.format(new Date((long) value));
        }
    }

    public String ReccuperatejacketId() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("jacketDocumentId", "");
    }

    private void updateChart() {
        obterDadosDaFirebasePeloIdDocumento(jacketDocumentId, selectedDate);
    }

    public void toggleDatePicker(View view) {
        DatePicker datePicker = findViewById(R.id.datePicker);
        TextView tvSelectDate = findViewById(R.id.tvSelectDate);

        if (datePicker.getVisibility() == View.VISIBLE) {
            datePicker.setVisibility(View.GONE);
        } else {
            datePicker.setVisibility(View.VISIBLE);
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // Atualiza a data selecionada
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    selectedDate = calendar.getTime();
                    // Atualiza o texto do TextView com a nova data selecionada
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    tvSelectDate.setText(dateFormat.format(selectedDate));
                    // Esconde o DatePicker
                    datePicker.setVisibility(View.GONE);
                    // Atualiza o gráfico com base na nova data selecionada
                    updateChart();
                }
            });
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
    }
}
