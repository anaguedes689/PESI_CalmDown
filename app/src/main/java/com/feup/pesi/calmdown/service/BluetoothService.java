package com.feup.pesi.calmdown.service;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.feup.pesi.calmdown.R;
import com.feup.pesi.calmdown.activity.RespirationActivity;
import com.feup.pesi.calmdown.model.Jacket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Bio.Library.namespace.BioLib;

public class BluetoothService extends Service {
    private final IBinder binder = new LocalBinder();
    private BioLib lib;
    private String address = "";
    private BluetoothDevice deviceToConnect;
    private Context context; // Add this line to store the context
    private boolean isConnected = false;
    public static final String DEVICE_NAME = "device_name";
    private String mConnectedDeviceName = "";
    private Date DATETIME_RTC = null;
    private Date DATETIME_TIMESPAN = null;
    private BioLib.DataACC dataACC = null;
    private byte[][] ecg = null;
    public static final String TOAST = "toast";
    private int nbytes = 0;
    private int battery;
    private int pulse;
    private long pos;
    private int rr;
    private int bpmi;
    private int bpm;
    private int nleads;
    private String accConf = "";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;
    private boolean isServiceRunning;
    private Jacket jacket;
    private final int INTERVALO_EXECUCAO = 1 * 60 * 1000; // 2 minuto em milissegundos
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int COLLECTION_INTERVAL = 1 * 60 * 1000;  // 2 minutos em milissegundos
    private final ArrayList<Integer> accumulatedPulse = new ArrayList<>();
    private final ArrayList<Integer> accumulatedBattery = new ArrayList<>();
    private final ArrayList<Long> accumulatedPosition = new ArrayList<>();
    private final ArrayList<Integer> accumulatedRr = new ArrayList<>();
    private final ArrayList<Integer> accumulatedBpmi = new ArrayList<>();
    private final ArrayList<Integer> accumulatedBpm = new ArrayList<>();
    private final ArrayList<Integer> accumulatedNleads = new ArrayList<>();
    private final ArrayList<Integer> accumulatedNbytes = new ArrayList<>();
    List<Integer> existingRr;
    List<Integer> existingPulse;
    List<Integer> existingBatteryLevel;
    List<Long> existingPosition;
    List<Integer> existingBpmi;
    List<Integer> existingBpm;
    List<Integer> existingNleads;
    List<Integer> existingNbytes;


    private long lastCollectionTime;
    String existingDocumentId;

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceRunning = true;
        if (jacket == null) {
            jacket = new Jacket();
        }
        handler.postDelayed(periodicReadTask, INTERVALO_EXECUCAO);

        return START_STICKY;
    }

    public boolean isServiceRunning() {
        return isServiceRunning;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final Runnable periodicReadTask = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, INTERVALO_EXECUCAO);
            updateArrays(pulse, battery, pos, rr, bpmi, bpm, nleads, nbytes);
            DATETIME_TIMESPAN = new Date();
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCollectionTime >= COLLECTION_INTERVAL) {
                lastCollectionTime = currentTime;

                // média dos valores acumulados
                calculateAndInsertToDatabase();

                DATETIME_TIMESPAN = new Date();
            }
        }
    };

    public void startBluetoothConnection() {
        // Inicializar BioLib e estabelecer a conexão Bluetooth aqui
        address = Reccuperateadress();
        if (!address.isEmpty() && !isConnected) {
            try {
                lib = new BioLib(this, mHandler);
                deviceToConnect = lib.mBluetoothAdapter.getRemoteDevice(address);
                lib.Connect(address, 5);
                isConnected = true;
                Log.d("BluetoothService", "Conectado ao dispositivo: " + address);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void onDestroy() {
        // Desconectar e limpar recursos ao destruir o serviço
        Disconnect();
        // Remover callbacks pendentes para evitar vazamentos
        handler.removeCallbacksAndMessages(periodicReadTask);
        super.onDestroy();
    }

    private void Disconnect() {
        try {
            lib.Disconnect();
            isConnected = false;
            Log.d("BluetoothService", "Desconectado do dispositivo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String Reccuperateadress() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("selectedValue", "");
    }

    private void RetrieveData() {
        if (isConnected) {
            try {
                lib = new BioLib(this, mHandler);
                Log.d("Init BioLib \n", "");
            } catch (Exception e) {
                Log.d("Error to init BioLib \n", "");
                e.printStackTrace();
            }
        }

    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {

        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            readDataAndInsertIntoFirebase(msg);
        }
    };

    public void insertQRS(BioLib.QRS qrs) {
        ArrayList<Long> qrs_pos_temp = new ArrayList<>();
        qrs_pos_temp.add(qrs.position);
        accumulatedPosition.add(qrs.position);
        ArrayList<Integer> qrs_pos_rr = new ArrayList<>();
        qrs_pos_rr.add(qrs.rr);
        accumulatedRr.add(qrs.rr);
        ArrayList<Integer> qrs_pos_bpmi = new ArrayList<>();
        qrs_pos_bpmi.add(qrs.bpmi);
        accumulatedBpmi.add(qrs.bpmi);
        ArrayList<Integer> qrs_pos_bpm = new ArrayList<>();
        qrs_pos_bpm.add(qrs.bpm);
        accumulatedBpm.add(qrs.bpm);
    }

    public void insertOut(BioLib.Output out) {
        ArrayList<Integer> battery = new ArrayList<>();
        battery.add(out.battery);
        accumulatedBattery.add(out.battery);
        ArrayList<Integer> pulse = new ArrayList<>();
        pulse.add(out.pulse);
        accumulatedPulse.add(out.pulse);
    }


    private void readDataAndInsertIntoFirebase(Message msg) {
        switch (msg.what) {
            case BioLib.MESSAGE_TIMESPAN:
                DATETIME_TIMESPAN = (Date) msg.obj;
                ArrayList<Date> timespan = new ArrayList<>();
                timespan.add(DATETIME_TIMESPAN);
                //Log.d("BluetoothService", "SPAN: " + DATETIME_TIMESPAN.toString());
                break;

            case BioLib.MESSAGE_DATA_UPDATED:
                BioLib.Output out = (BioLib.Output) msg.obj;
                insertOut(out);
                battery = out.battery;
                pulse = out.pulse;
                //Log.d("BluetoothService", "BAT: " + out.battery + " %");
                //Log.d("BluetoothService", "HR: " + out.pulse + " bpm     Nb. Leads: " + lib.GetNumberOfChannels());
                break;

            case BioLib.MESSAGE_PEAK_DETECTION:
                BioLib.QRS qrs = (BioLib.QRS) msg.obj;
                insertQRS(qrs);
                pos = qrs.position;
                rr = qrs.rr;
                bpmi = qrs.bpmi;
                bpm = qrs.bpm;
                //Log.d("BluetoothService", "PEAK: " + qrs.position + "  BPMi: " + qrs.bpmi + " bpm  BPM: " + qrs.bpm + " bpm  R-R: " + qrs.rr + " ms");
                break;

            case BioLib.MESSAGE_ECG_STREAM:
                try {
                    //Log.d("BluetoothService", "ECG received");
                    ecg = (byte[][]) msg.obj;
                    ArrayList<Integer> nLeadss = new ArrayList<>();
                    nleads = ecg.length;
                    nbytes = ecg[0].length;
                    //Log.d("BluetoothService", "ECG stream: OK   nBytes: " + nbytes + "   nLeads: " + nleads);
                } catch (Exception ex) {
                    //Log.d("BluetoothService", "ERROR in ecg stream");
                }
                break;

            default:
                // Lida com outros casos, se necessário
                break;
        }
    }

    private void updateArrays(int pulse, int battery, long pos, int rr, int bpmi, int bpm, int nleads, int nbytes) {
        accumulatedPulse.add(pulse);
        accumulatedBattery.add(battery);
        accumulatedPosition.add(pos);
        accumulatedRr.add(rr);
        accumulatedBpmi.add(bpmi);
        accumulatedBpm.add(bpm);
        accumulatedNleads.add(nleads);
        accumulatedNbytes.add(nbytes);
    }

    private void inserirDadosNoFirebase(Jacket jacket) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String macAddress = address;

        if (macAddress != null) {
            Log.d("BluetoothService", macAddress);
        } else {
            Log.d("BluetoothService", "not found");
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            jacket.setUserId(currentUserId);
            jacket.setAddress(macAddress);
        }

        // Recupera todos os documentos da coleção
        db.collection("jacketdata")
                .whereEqualTo("address", macAddress)
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Encontrou um documento existente
                                existingDocumentId = document.getId();
                                setExistingDocumentId(existingDocumentId);
                                // Adiciona os novos valores médios às posições seguintes

                                existingRr = (List<Integer>) document.get("rr");
                                existingPulse = (List<Integer>) document.get("pulse");
                                existingBatteryLevel = (List<Integer>) document.get("batteryLevel");
                                existingPosition = (List<Long>) document.get("position");
                                existingBpmi = (List<Integer>) document.get("bpmi");
                                existingBpm = (List<Integer>) document.get("bpm");
                                existingNleads = (List<Integer>) document.get("nleads");
                                existingNbytes = (List<Integer>) document.get("nBytes");
                                List<Long>rr1 = (List<Long>) document.get("rr");

                                // Adiciona os novos valores médios às posições seguintes, incluindo valores iguais
                                existingRr.add(jacket.getRr().get(0));
                                existingPulse.add(jacket.getPulse().get(0));
                                existingBatteryLevel.add(jacket.getBatteryLevel().get(0));
                                existingPosition.add(jacket.getPosition().get(0));
                                existingBpmi.add(jacket.getBpmi().get(0));
                                existingBpm.add(jacket.getBpm().get(0));
                                existingNleads.add(jacket.getNleads().get(0));
                                existingNbytes.add(jacket.getnBytes().get(0));

                                getInstantStress(rr1);

                                db.collection("jacketdata").document(document.getId())
                                        .update(
                                                "rr", existingRr,
                                                "pulse", existingPulse,
                                                "batteryLevel", existingBatteryLevel,
                                                "position", existingPosition,
                                                "bpmi", existingBpmi,
                                                "bpm", existingBpm,
                                                "nleads", existingNleads,
                                                "nBytes", existingNbytes,
                                                "dateTimeSpan", FieldValue.arrayUnion(jacket.getDateTimeSpan().get(0))
                                        )
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Documento Jacket atualizado com sucesso!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Erro ao atualizar documento Jacket", e);
                                            }
                                        });
                                return; // Termina a execução após encontrar o documento
                            }
                            // Se não encontrou um documento existente, adiciona um novo
                            db.collection("jacketdata").add(jacket)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "Documento Jacket adicionado com ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Erro ao adicionar documento Jacket", e);
                                        }
                                    });
                        } else {
                            Log.w(TAG, "Erro ao obter documentos.", task.getException());
                        }
                    }
                });
    }


    public void getInstantStress(List<Long> rrint) {
        List<Long> rr = null;
        if (rrint.size() >= 5) {
            // Utiliza a função slice para obter as últimas 5 posições
            rr = rrint.subList(rrint.size() - 5, rrint.size());
            // A high-risk group may be selected by the dichotomy limits of SDNN <50 ms
            String stress = new String("Normal");
            double SDNN = 0;
            float sum = 0;
            for (int i = 0; i < rr.size(); i++) {
                sum = sum + (rr.get(i));  // Convertendo Long para int e, em seguida, para float
            }
            float media = sum / (rr.size() + 1);
            double diff = 0;
            for (int i = 0; i < rr.size(); i++) {
                diff = diff + Math.pow((double) rr.get(i) - media, 2);
            }
            SDNN = Math.sqrt((diff / (rr.size() - 1)));

            if ((50 - 16) < SDNN) {
                stress = "Stress levels high!";
                if (SDNN < 32) {
                    stress = "Stress levels EXTREMELY high!";
                }
                exibirNotificacao(stress);
            }

        }
    }

    // Método para exibir a notificação
    private void exibirNotificacao(String mensagem) {
        // Cria um canal de notificação (necessário para versões do Android a partir do Oreo)
        criarCanalNotificacao(getApplicationContext());  // Use getApplicationContext()

        // Cria uma Intent para a RespirationActivity
        Intent intent = new Intent(getApplicationContext(), RespirationActivity.class);  // Use getApplicationContext()
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);  // Use getApplicationContext()

        // Cria a notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "stressnoti")  // Use getApplicationContext()
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Stress Alert")
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Exibe a notificação
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());  // Use getApplicationContext()
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    // Método para criar o canal de notificação (necessário para versões do Android a partir do Oreo)
    private void criarCanalNotificacao(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Stress channel";
            String description = "Descrição do seu canal";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("stressnoti", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }




    private void setExistingDocumentId(String documentId){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jacketDocumentId", documentId);
        editor.apply();
    }

    private int calculateAverage(List<Integer> values) {
        int sum = 0;
        for (int value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    private long calculateAverageLong(List<Long> values) {
        long sum = 0;
        for (long value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    private void calculateAndInsertToDatabase() {
        // Calcule a média dos valores acumulados
        int averagePulse = calculateAverage(accumulatedPulse);
        int averageBattery = calculateAverage(accumulatedBattery);
        long averagePosition = calculateAverageLong(accumulatedPosition);
        int averageRr = calculateAverage(accumulatedRr);
        int averagebpmi = calculateAverage(accumulatedBpmi);
        int averagebpm = calculateAverage(accumulatedBpm);
        int averageNleads = calculateAverage(accumulatedNleads);
        int averageNbytes = calculateAverage(accumulatedNbytes);

        // Atualize o objeto Jacket com os valores médios
        jacket.setPulse(new ArrayList<>(Collections.singletonList(averagePulse)));
        jacket.setBatteryLevel(new ArrayList<>(Collections.singletonList(averageBattery)));
        jacket.setPosition(new ArrayList<>(Collections.singletonList(averagePosition)));
        jacket.setRr(new ArrayList<>(Collections.singletonList(averageRr)));
        jacket.setBpmi(new ArrayList<>(Collections.singletonList(averagebpmi)));
        jacket.setBpm(new ArrayList<>(Collections.singletonList(averagebpm)));
        jacket.setNleads(new ArrayList<>(Collections.singletonList(averageNleads)));
        jacket.setnBytes(new ArrayList<>(Collections.singletonList(averageNbytes)));
        jacket.setDateTimeSpan(new ArrayList<>(Collections.singletonList(DATETIME_TIMESPAN)));

        // Insira na base de dados
        inserirDadosNoFirebase(jacket);
        Log.d("BluetoothService", "Dados inseridos");
        // Limpar os arrays acumulados
        accumulatedPulse.clear();
        accumulatedBattery.clear();
        accumulatedPosition.clear();
        accumulatedRr.clear();
        accumulatedBpmi.clear();
        accumulatedBpm.clear();
        accumulatedNleads.clear();
        accumulatedNbytes.clear();
    }
}