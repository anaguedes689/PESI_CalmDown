package com.feup.pesi.calmdown.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.feup.pesi.calmdown.R;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HrActivity extends DashBoardActivity {

    //Botao para atualizar
    //visualizar nivel stress
    //emitir notificação que vai à respiration activity

    private FirebaseFirestore db;
    private LineChart lineChart;

    private int height;

    private int age;
    private int weight;

    private String feature= "STRESS";

    private String userSex;

    private final String selectedVariable = "rr";
    private String jacketDocumentId;
    private Date selectedDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr);
        db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        lineChart = findViewById(R.id.lineChart1);

        jacketDocumentId = ReccuperatejacketId();
        //selectedVariable = getResources().getStringArray(R.array.variable_options)[0].toLowerCase(); // Pega o primeiro item


        // Get the user ID of the logged user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String loggedUserId = currentUser.getUid();
            loadAndDisplayUserData(loggedUserId);
        }

        ImageButton btnUpdateChart = findViewById(R.id.btnUpdateChart);
        btnUpdateChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chama o método para obter dados da Firebase novamente
                obterDadosDaFirebasePeloIdDocumento(jacketDocumentId, selectedDate);
                updateChart();
            }
        });

        // Configura o ouvinte de seleção de variável
        Spinner spinnerVariable = findViewById(R.id.spinner);
        spinnerVariable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Atualiza a variável selecionada
                feature = getResources().getStringArray(R.array.history_options)[position].toLowerCase();
                // Atualiza o gráfico com base nas novas escolhas do usuário
                updateChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nada a fazer aqui
            }
        });

        // Atualiza o gráfico com base nas escolhas iniciais do usuário
        updateChart();

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
                    Date userBirthday = timestamp != null ? timestamp.toDate() : null; this.age = calculateAge(userBirthday);

                    height = document.getLong("height").intValue();
                    weight = document.getLong("weight").intValue();

                    double imc = getIMC(height,weight);

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

    public void getHistory(String feature, String window){
        List<Float> History = null;
        Date selectedDate = new Date();
        Date begin = null;



        if(window.equals("24H")){
            begin = calculateOneDayBefore(selectedDate);

        }
        if(window.equals("10min")){
            begin = calculateFiveMinutesBefore(selectedDate);
        }

    }

    private static Date calculateOneDayBefore(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Subtract one day
        return calendar.getTime();
    }

    private static Date calculateFiveMinutesBefore(Date date) {
        //mudar para 10 minutos
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -10); // Subtract 5 minutes
        return calendar.getTime();
    }

    public double getRMSSD(List<Long> rr){ //diferença entre atual e anterior

        double diff = 0;
        double RMSSD = 0;

        if(rr!=null){
            for(int i =0;i<(rr.size()-1);i++){
                diff = diff + Math.pow((double) rr.get(i + 1) - rr.get(i), 2);
            }
            RMSSD = Math.sqrt((diff/(rr.size()-1)));
        }

        return RMSSD;}

    public List<Float> defineInterval(){
        //RMSSD para 10 minutos
        String sex = this.userSex;
        int age = this.age;
        double up, down;
        float imc = (float) getIMC(this.height,this.weight);
        up = 0;
        down = 89;
        List<Float> value = null;
        if(imc>30){
            //nao saudaveis
            down = 18;
        }else{
            //saudaveis
            if (age<34){
                down = 40;
            }else{
                down = 27;
            }
        }
        if(sex.equals("Male")){
            up = 0;
            down = down  + 0.08*down;
        }

        value.add((float) up);
        value.add((float) down);
    return value;}

    private void filterData(Date selectedDate, ArrayList<Date> dateTimeSpan, ArrayList<Long> data, String label) {
        List<Entry> entries = new ArrayList<>();
        int intervalSize = 5 * 60 * 1000; // 5 minutes in milliseconds

        // Map to store aggregated data for each 5-minute interval
        Map<Long, List<Long>> aggregatedDataMap = new HashMap<>();

        // Group data into 5-minute intervals
        for (int i = 0; i < dateTimeSpan.size(); i++) {
            if (isSameDate(dateTimeSpan.get(i), selectedDate) && selectedVariable.equals(label.toLowerCase())) {
                long timeInMillis = dateTimeSpan.get(i).getTime();
                long intervalKey = timeInMillis / intervalSize * intervalSize;

                if (!aggregatedDataMap.containsKey(intervalKey)) {
                    aggregatedDataMap.put(intervalKey, new ArrayList<>());
                }

                aggregatedDataMap.get(intervalKey).add(data.get(i));
            }

        }


        // Calculate aggregated values for each 5-minute interval
        for (Map.Entry<Long, List<Long>> entry : aggregatedDataMap.entrySet()) {
            long intervalKey = entry.getKey();
            List<Long> intervalData = entry.getValue();

            double aggregatedValue = calculateAggregatedValue(intervalData);

            // Add the aggregated value to the entries list
            entries.add(new Entry(intervalKey, (float) aggregatedValue));
        }
        if (entries.isEmpty()) {
            // Adicione algum tratamento de erro ou imprima uma mensagem de log

            return;
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
    }

    private double calculateAggregatedValue(List<Long> intervalData) {
        String feature = this.feature;
        double sum = 0;
        double rmssd = getRMSSD(intervalData);
        if(feature.equals("RMSSD")){
            sum = rmssd;
        }
        if(feature.equals("Stress")){
            List<Float> intervalo = defineInterval();
            double amplitude =(double) (intervalo.get(1) -intervalo.get(0));
            if(rmssd<intervalo.get(1)){ //&& frequencia cardiaca>100
                sum = 100-((rmssd)*100/amplitude);
            }
            //sum = (100/(intervalo.get(0)-intervalo.get(1)))*(rmssd)+(100-(100*rmssd/(intervalo.get(0)-intervalo.get(1))));

        }
        return sum;
    }

/*
    private void filterData(Date selectedDate, ArrayList<Date> dateTimeSpan, ArrayList<Long> data, String label) {
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

*/

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
                                    filterData(selectedDate, dateTimeSpan, rr, "RR");

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
