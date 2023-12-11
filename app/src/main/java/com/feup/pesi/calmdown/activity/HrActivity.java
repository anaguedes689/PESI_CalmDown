package com.feup.pesi.calmdown.activity;

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

    private FirebaseFirestore db;

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
    }
}
