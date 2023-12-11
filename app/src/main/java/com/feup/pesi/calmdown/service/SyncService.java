package com.feup.pesi.calmdown.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.feup.pesi.calmdown.JacketDatabaseHelper;
import com.feup.pesi.calmdown.model.Jacket;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.feup.pesi.calmdown.model.LocalData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SyncService extends JobIntentService {

    public static int JOB_ID = 1001;
    private static final String TAG = "SyncService";

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SyncService.class, JOB_ID, work);
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // Lógica de sincronização aqui
        try {
            syncLocalDataWithFirestore();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void syncLocalDataWithFirestore() throws ParseException {
            // Exemplo: obter dados locais não sincronizados (substitua isso com sua lógica real)
            Jacket unsyncedData = retrieveData();

            // Verificar a disponibilidade de conexão antes de tentar sincronizar
            if (isNetworkAvailable()) {
                sendDataToFirestore(unsyncedData);
            } else {
            showToast("Sem conexão de internet. Os dados serão sincronizados quando a conexão estiver disponível.");
        }
    }



    public Jacket retrieveData() throws ParseException {
        JacketDatabaseHelper dbHelper = new JacketDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ArrayList<Integer> nleadsUnsynced = new ArrayList<>();
        ArrayList<Long> positionUnsynced = new ArrayList<>();
        ArrayList<Integer> rrUnsynced = new ArrayList<>();
        ArrayList<Integer> bpmiUnsynced = new ArrayList<>();
        ArrayList<Integer> bpmUnsynced = new ArrayList<>();
        ArrayList<Date> dateTimeSpanUnsynced = new ArrayList<>();
        ArrayList<Integer> nBytesUnsynced = new ArrayList<>();
        ArrayList< Integer> batteryLevelUnsynced = new ArrayList<>();
        ArrayList< Integer> pulseUnsynced = new ArrayList<>();
        String userIdUnsynced = "";
        String address = "";


        String[] projection = {
                "address",
                "userId",
                "rr",
                "pulse",
                "batteryLevel",
                "position",
                "bpmi",
                "bpm",
                "nleads",
                "nBytes",
                "dateTimeSpan"
        };

        Cursor cursor = db.query(
                "jacket",   // The table to query
                projection, // The array of columns to return (pass null to get all)
                null,       // The columns for the WHERE clause
                null,       // The values for the WHERE clause
                null,       // Don't group the rows
                null,       // Don't filter by row groups
                null        // The sort order
        );

        ArrayList<LocalData> localDataList = new ArrayList<>();
        while(cursor.moveToNext()) {

            int batteryLevel = cursor.getInt(cursor.getColumnIndexOrThrow("batteryLevel"));
            int bpm = cursor.getInt(cursor.getColumnIndexOrThrow("bpm"));
            int bpmi = cursor.getInt(cursor.getColumnIndexOrThrow("bpmi"));
            int nBytes = cursor.getInt(cursor.getColumnIndexOrThrow("nBytes"));
            int nleads = cursor.getInt(cursor.getColumnIndexOrThrow("nleads"));
            long position = cursor.getInt(cursor.getColumnIndexOrThrow("position"));
            int pulse = cursor.getInt(cursor.getColumnIndexOrThrow("pulse"));
            int rr = cursor.getInt(cursor.getColumnIndexOrThrow("rr"));
            String dateTimeSpanStr = cursor.getString(cursor.getColumnIndexOrThrow("dateTimeSpan"));

            // Parse the date from the string
            Date dateTimeSpan = new SimpleDateFormat("dd-MM-yyyy").parse(dateTimeSpanStr);

            // Convert the Date to a Timestamp
            com.google.firebase.Timestamp timestamp = new com.google.firebase.Timestamp(dateTimeSpan);



            // Adjust this line based on your date format
            userIdUnsynced = cursor.getString(cursor.getColumnIndexOrThrow("userId"));
            address = cursor.getString(cursor.getColumnIndexOrThrow("address"));



            nleadsUnsynced.add(nleads);
            positionUnsynced.add(position);
            rrUnsynced.add(rr);
            bpmiUnsynced.add(bpmi);
            bpmUnsynced.add(bpm);
            dateTimeSpanUnsynced.add(dateTimeSpan);
            nBytesUnsynced.add(nBytes);
            batteryLevelUnsynced.add(batteryLevel);
            pulseUnsynced.add(pulse);

        }
        cursor.close();

        Jacket jacket = new Jacket(nleadsUnsynced, positionUnsynced, rrUnsynced, bpmiUnsynced, bpmUnsynced, dateTimeSpanUnsynced, nBytesUnsynced, address, batteryLevelUnsynced, pulseUnsynced, userIdUnsynced); // Adjust this line based on your Jacket constructor
        return jacket;
    }

// Verificar este método
    private void sendDataToFirestore(Jacket jacketDataUnsynced) {
        // Substitua este bloco de código com sua lógica real para enviar dados para a Firestore
        FirebaseFirestore.getInstance().collection("jacketdata").add(jacketDataUnsynced)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Dados sincronizados com sucesso!");
                    } else {
                        Log.e(TAG, "Falha ao sincronizar dados", task.getException());
                        showToast("Falha ao sincronizar dados");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Falha ao sincronizar dados", e);
                        showToast("Falha ao sincronizar dados");
                    }
                });
    }

    private void showToast(final String message) {
        // Exibir um Toast a partir do serviço
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}

