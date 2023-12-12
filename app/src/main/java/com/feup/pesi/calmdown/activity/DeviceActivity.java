package com.feup.pesi.calmdown.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.feup.pesi.calmdown.R;
import com.feup.pesi.calmdown.service.BluetoothService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Bio.Library.namespace.BioLib;

public class DeviceActivity extends DashBoardActivity {
    private BioLib lib;

    private String address = "";
    private BluetoothDevice deviceToConnect;
    private Button buttonSearch;
    private BluetoothService bluetoothService;
    private boolean isCon;
    private String jacketId;
    private FirebaseFirestore db;
    private TextView textBatteryLevel;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        //Intent serviceIntent = new Intent(this, BluetoothService.class);
        //startService(serviceIntent);
        //Intent intent = new Intent(this, BluetoothService.class);
        //bindService(intent, connection, Context.BIND_AUTO_CREATE);
        setUpBottomNavigation();
        isCon = getIsConnected();
        jacketId= ReccuperatejacketId();
        // Recupera o endereço MAC da última conexão
        //address = Reccuperateadress();

        db = FirebaseFirestore.getInstance();

        textBatteryLevel = findViewById(R.id.buttonDisconnect);

        if (isCon) {
            obterDadosDaFirebasePeloIdDocumento(jacketId);
        }
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    Intent myIntent = new Intent(view.getContext(), SearchActivity.class);
                    startActivity(myIntent);

                    // Conecta apenas se o endereço MAC estiver disponível
                    /*if (!address.isEmpty()) {
                        Connect();
                    }*/
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Conecta automaticamente se o endereço MAC estiver disponível
        /*if (!address.isEmpty()) {
            Connect();
        }*/
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
                    Intent serviceIntent = new Intent(DeviceActivity.this, BluetoothService.class);
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
    /*private void Connect() {
        try {
            deviceToConnect = lib.mBluetoothAdapter.getRemoteDevice(address);
            lib.Connect(address, 5);
            Log.d("connect device sucess: ", address);

        } catch (Exception e) {
            Log.d("Error to connect device: ", address);
            e.printStackTrace();
            showToast("Falha na conexão. Verifique o dispositivo e tente novamente.");
        }
    }

    private void Disconnect() {
        try {
            lib.Disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    public void obterDadosDaFirebasePeloIdDocumento(String idDocumento) {
        db.collection("jacketdata")
                .document(idDocumento)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                               ArrayList<Long> bat = (ArrayList<Long>) document.get("batteryLevel");

                                // Verifica se há dados de bateria
                                if (bat != null && !bat.isEmpty()) {
                                    // Obtém o último valor da bateria
                                    long lastBatteryLevel = bat.get(bat.size() - 1);

                                    // Atualiza o TextView com o valor da bateria
                                    textBatteryLevel.setText("Battery Level: " + lastBatteryLevel);
                                } else {
                                    textBatteryLevel.setText("Battery Level: N/A");
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
    private boolean getIsConnected() {
        // Recupera o valor de isConnected do SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isConnected", false); // O segundo parâmetro é um valor padrão caso não haja valor salvo
    }
    public String ReccuperatejacketId() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("jacketDocumentId", "");
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    /*public String Reccuperateadress() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("selectedValue", "");
    }*/
}
