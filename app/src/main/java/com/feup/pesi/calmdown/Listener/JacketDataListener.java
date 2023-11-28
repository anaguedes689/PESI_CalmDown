package com.feup.pesi.calmdown.Listener;

import com.feup.pesi.calmdown.model.JacketData;

public interface JacketDataListener {
    // Método chamado quando novos dados do casaco são recebidos
    void onCasacoDataReceived(JacketData casacoData);
}
