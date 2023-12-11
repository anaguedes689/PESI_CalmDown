package com.feup.pesi.calmdown.activity;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.feup.pesi.calmdown.R;
import com.feup.pesi.calmdown.service.BluetoothService;

import Bio.Library.namespace.BioLib;

public class DeviceActivity extends DashBoardActivity {
    private BioLib lib;

    private String address = "";
    private BluetoothDevice deviceToConnect;
    private Button buttonSearch;
    private BluetoothService bluetoothService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        //Intent serviceIntent = new Intent(this, BluetoothService.class);
        //startService(serviceIntent);
        //Intent intent = new Intent(this, BluetoothService.class);
        //bindService(intent, connection, Context.BIND_AUTO_CREATE);
        setUpBottomNavigation();

        // Recupera o endereço MAC da última conexão
        //address = Reccuperateadress();

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

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    /*public String Reccuperateadress() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getString("selectedValue", "");
    }*/
}
