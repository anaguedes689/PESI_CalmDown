package com.feup.pesi.calmdown.handler;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.feup.pesi.calmdown.Listener.JacketDataListener;
import com.feup.pesi.calmdown.model.JacketData;

import Bio.Library.namespace.BioLib;


public class BluetoothCommunication {
    /*private BioLib bioLib;
    private JacketDataListener casacoDataListener;

    public BluetoothCommunication(JacketDataListener listener) {
        this.bioLib = new BioLib( handler);
        this.casacoDataListener = listener;
    }

    public void connectToDevice(String address) {
        // Lógica para conectar ao dispositivo usando o BioLib
        // Exemplo: bioLib.Connect(address, 5);
    }

    public void disconnectDevice() {
        // Lógica para desconectar do dispositivo usando o BioLib
        // Exemplo: bioLib.Disconnect();
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BioLib.MESSAGE_DATA_UPDATED:
                    BioLib.Output output = (BioLib.Output) msg.obj;
                    JacketData casacoData = new JacketData(output.battery, output.pulse);
                    casacoDataListener.onCasacoDataReceived(casacoData);
                    break;
                // Adicione tratamento para outras mensagens conforme necessário
            }
        }
    };*/
}
