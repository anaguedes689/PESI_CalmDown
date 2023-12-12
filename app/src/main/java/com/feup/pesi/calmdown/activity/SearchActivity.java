package com.feup.pesi.calmdown.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.feup.pesi.calmdown.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class SearchActivity extends DashBoardActivity {
    public static String SELECT_DEVICE_ADDRESS = "device_address";
    public static final int CHANGE_MACADDRESS = 100;
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private String selectedValue = "";
    private BluetoothAdapter mBluetoothAdapter = null;
    private Button buttonOK;
    private boolean isCon;
    private String jacketId;

    private TextView textBatteryLevel;  // Adicione esta linha

    public String GetMacAddress() {
        return selectedValue;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        setUpBottomNavigation();


        try {
            mainListView = (ListView) findViewById(R.id.lstDevices);

            ArrayList<String> lstDevices = new ArrayList<String>();

            // Create ArrayAdapter using the planet list.
            listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, lstDevices);

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null) {
                if (mBluetoothAdapter.isEnabled()) {
                    // Listing paired devices
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Fazer o pedido das permissoes do bluetooth
                        return;
                    }
                    Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice device : devices)
                    {
                        listAdapter.add(device.getAddress() + "   " + device.getName());
                    }
                }
            }
            mainListView.setAdapter( listAdapter );
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick( AdapterView<?> parent, View item, int position, long id)
                {
                    selectedValue = (String) listAdapter.getItem(position);

                    String[] aux = selectedValue.split("   ");
                    selectedValue = aux[0];
                    SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("selectedValue", selectedValue);
                    editor.apply();
                    Intent intent = new Intent();
                    intent.putExtra(SELECT_DEVICE_ADDRESS, selectedValue);
                    setResult(CHANGE_MACADDRESS, intent);
                    finish();
                }
            });
        }
        catch (Exception ex)
        {

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_search_device, menu);
        return true;
    }


}
