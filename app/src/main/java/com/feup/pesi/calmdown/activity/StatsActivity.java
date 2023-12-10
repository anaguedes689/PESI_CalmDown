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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatsActivity extends DashBoardActivity {

    private FirebaseFirestore db;
    private LineChart lineChart;
    private String jacketDocumentId;
    private String selectedVariable = "pulse";
    private Date selectedDate = new Date(); // Inicializa com a data atual

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        setUpBottomNavigation();


        lineChart = findViewById(R.id.lineChart);

        // Recupera o ID do documento do jacket
        jacketDocumentId = ReccuperatejacketId();

        // Inicializa o Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializa com a data atual
        selectedVariable = getResources().getStringArray(R.array.variable_options)[0].toLowerCase(); // Pega o primeiro item

        // Chama o método para obter dados da Firebase
        obterDadosDaFirebasePeloIdDocumento(jacketDocumentId, selectedDate, selectedVariable);

        // Configura o ouvinte de clique para o botão de atualização
        ImageButton btnUpdateChart = findViewById(R.id.btnUpdateChart);
        btnUpdateChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chama o método para obter dados da Firebase novamente
                obterDadosDaFirebasePeloIdDocumento(jacketDocumentId, selectedDate, selectedVariable);
            }
        });

        // Configura o ouvinte de seleção de variável
        Spinner spinnerVariable = findViewById(R.id.spinnerVariable);
        spinnerVariable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Atualiza a variável selecionada
                selectedVariable = getResources().getStringArray(R.array.variable_options)[position].toLowerCase();
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
                                    ArrayList<Long> pulse = (ArrayList<Long>) document.get("pulse");
                                    filterData(selectedDate, selectedVariable, dateTimeSpan, pulse, "Pulse");
                                } else if (selectedVariable.equalsIgnoreCase(rr1)) {
                                    ArrayList<Long> rr = (ArrayList<Long>) document.get("rr");
                                    filterData(selectedDate, selectedVariable, dateTimeSpan, rr, "RR");
                                } else if (selectedVariable.equalsIgnoreCase(bpm1)){
                                    ArrayList<Long> bpm = (ArrayList<Long>) document.get("bpm");
                                    filterData(selectedDate, selectedVariable, dateTimeSpan, bpm, "bpm");
                                }else if (selectedVariable.equalsIgnoreCase(bpmi1)){
                                    ArrayList<Long> bpmi = (ArrayList<Long>) document.get("bpmi");
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
        obterDadosDaFirebasePeloIdDocumento(jacketDocumentId, selectedDate, selectedVariable);
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
}
