package com.feup.pesi.calmdown.service;

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
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.feup.pesi.calmdown.R;
import com.feup.pesi.calmdown.activity.RespirationActivity;
import com.feup.pesi.calmdown.model.Jacket;
import com.feup.pesi.calmdown.model.LocalData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    private String currentUserId = preferences.getString("userId", "");

    private boolean isServiceRunning;
    private Jacket jacket;
    private final int INTERVALO_EXECUCAO = 500; // 1 minuto em milissegundos
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int COLLECTION_INTERVAL = 500;  // 1 minutos em milissegundos
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
            addToLocalData(pulse, battery, pos, rr, bpmi, bpm, nleads, nbytes);
            //updateArrays(pulse, battery, pos, rr, bpmi, bpm, nleads, nbytes);
            DATETIME_TIMESPAN = new Date();
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastCollectionTime >= COLLECTION_INTERVAL) {
                lastCollectionTime = currentTime;

                // média dos valores acumulados
                calculateAndInsertToDatabase();

                DATETIME_TIMESPAN = new Date();
            }
            //saveIsConnected(isConnected);
        }
    };


    private void saveIsConnected(boolean isConnected) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isConnected", isConnected);
        editor.apply();
    }
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
                saveIsConnected(isConnected);
            } catch (Exception e) {
                isConnected=false;
                saveIsConnected(isConnected);
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

    private void addToLocalData(int pulse, int battery, long pos, int rr, int bpmi, int bpm, int nleads, int nbytes) {

        JacketDataDbAdapter localDataDbAdapter = new JacketDataDbAdapter(context);
        localDataDbAdapter.open();

        LocalData localDataTemp = new LocalData();
        localDataTemp.setBatteryLevel(battery);
        localDataTemp.setPulse(pulse);
        localDataTemp.setPosition(pos);
        localDataTemp.setRr(rr);
        localDataTemp.setBpmi(bpmi);
        localDataTemp.setBpm(bpm);
        localDataTemp.setNleads(nleads);
        localDataTemp.setBytes(nbytes);
        localDataTemp.setDateTimeSpan(DATETIME_TIMESPAN);
        localDataTemp.setUserId(currentUserId);
        localDataTemp.setAddress(address);

        // You need to set the data accordingly
        long insertResult = localDataDbAdapter.insertLocalData(localDataTemp);
        if (insertResult != -1) {
            Log.d("BluetoothService", "Dados inseridos na base de dados local");
        } else {
            Log.d("BluetoothService", "Erro ao inserir dados na base de dados local");
        }
    }

    public double getRMSSD(List<Long> rr){ //diferença entre atual e anterior
        double RMSSD = 0;
        if(rr!=null){
            double diff = 0;
            for (int i = 0; i < (rr.size()-1); i++) {
                diff = diff + Math.pow((double) rr.get(i + 1) - rr.get(i), 2);
            }
            RMSSD = Math.sqrt((diff/(rr.size()-1)));
        }
        return RMSSD;}


    public void getInstantStress(List<Long> rrint) {
        List<Long> rr = null;
        if (rrint.size() >= 5) {
            // Utiliza a função slice para obter as últimas 5 posições
            rr = rrint.subList(rrint.size() - 5, rrint.size());
            // A high-risk group may be selected by the dichotomy limits of SDNN <50 ms

            String stress = new String("Normal");
            double rmssd = getRMSSD(rr);
            float up, down;
            up=89;down=20;

            float sstress = (float) (-0.89*rmssd +117.8); //em percentagem, geral
            setLevelStress(sstress);
            if (sstress>70) {
                stress = "Stress levels high!";
                if (sstress> 85) {
                    stress = "Stress levels EXTREMELY high!";
                }
                exibirNotificacao(stress);
            }

        }
    }

    private void setLevelStress(double stresslevel){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("stresslevel", (float) stresslevel);
        editor.apply();
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

        // Insira os valores médios na base de dados
        JacketDataDbAdapter jacketDataDbAdapter = new JacketDataDbAdapter(context);
        jacketDataDbAdapter.open();

        // Calcule a média dos valores acumulados
        int averagePulse = calculateAverage(accumulatedPulse);
        int averageBattery = calculateAverage(accumulatedBattery);
        long averagePosition = calculateAverageLong(accumulatedPosition);
        int averageRr = calculateAverage(accumulatedRr);
        int averageBpmi = calculateAverage(accumulatedBpmi);
        int averageBpm = calculateAverage(accumulatedBpm);
        int averageNleads = calculateAverage(accumulatedNleads);
        int averageNbytes = calculateAverage(accumulatedNbytes);

        // Get the current timestamp
        long dateTime = System.currentTimeMillis();


        long insertResult = jacketDataDbAdapter.insertAverageData( averagePulse, averageBattery, averagePosition,
                averageRr, averageBpmi, averageBpm, averageNleads,
                averageNbytes, currentUserId, dateTime);
        if (insertResult != -1) {
            Log.d("BluetoothService", "Dados inseridos na base de dados local");
        } else {
            Log.d("BluetoothService", "Erro ao inserir dados na base de dados local");
        }



    }
}