package com.feup.pesi.calmdown.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.feup.pesi.calmdown.R;
import com.feup.pesi.calmdown.model.Quizz;

import java.util.Arrays;

public class QuizzActivity extends AppCompatActivity {

    private SeekBar seekBarStressLevel;
    private RadioGroup radioGroupRelaxPreference;
    private ImageView colorOption1, colorOption2, colorOption3, colorOption4, colorDisplay;
    private Switch switchNotification;
    TextView textViewSelectedValue;
    private static final int PERMISSION_REQUEST_CODE = 123;


    private int selectedColor = Color.BLUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        seekBarStressLevel = findViewById(R.id.seekBarStressLevel);
        radioGroupRelaxPreference = findViewById(R.id.radioGroupRelaxPreference);
        switchNotification = findViewById(R.id.switchNotification);

        colorOption1 = findViewById(R.id.colorOption1);
        colorOption2 = findViewById(R.id.colorOption2);
        colorOption3 = findViewById(R.id.colorOption3);
        colorOption4 = findViewById(R.id.colorOption4);

        textViewSelectedValue = findViewById(R.id.textViewSelectedValue);

        seekBarStressLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Atualize o TextView com o valor selecionado
                textViewSelectedValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Não é necessário implementar neste caso
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Não é necessário implementar neste caso
            }
        });
        colorOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = Color.parseColor("#bfdbd5");
            }
        });

        colorOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = Color.parseColor("#d2b3b3");
            }
        });

        colorOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = Color.parseColor("#bcd2b0");
            }
        });

        colorOption4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = Color.parseColor("#dfdfdf");
            }
        });

        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Switch", "Switch state changed: " + isChecked);

            if (isChecked) {

                // Se o switch estiver ativado, solicitar permissão para notificações
                requestNotificationPermission();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Button submitButton = findViewById(R.id.btSubmit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int stressLevel = seekBarStressLevel.getProgress();

                // Get selected radio button
                int selectedRadioButtonId = radioGroupRelaxPreference.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                String relaxPreference = selectedRadioButton.getText().toString();
                boolean notificationPreference = switchNotification.isChecked();



                // Create a Quizz object with the collected data
                Quizz quizz = new Quizz("user_id_placeholder", stressLevel, relaxPreference, colorToHexString(selectedColor), notificationPreference);

                // Display quizz information (for testing purposes)
                quizz.displayQuizzInfo();

                // You can save the Quizz object to Firebase or perform other actions here
            }
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crie um canal de notificação para Android 8.0 e versões posteriores
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Solicite permissões
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            // Se a permissão já foi concedida, envie uma notificação de exemplo
            sendNotification();
        } else {
            Log.d("Permission", "Requesting notification permission");

            // Se a permissão não foi concedida, solicite-a
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.VIBRATE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void sendNotification() {
        // Crie uma notificação de exemplo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notificação de Exemplo")
                .setContentText("Esta é uma notificação de exemplo.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(123, builder.build());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Permission", "Request Code: " + requestCode);
        Log.d("Permission", "Permissions: " + Arrays.toString(permissions));
        Log.d("Permission", "Grant Results: " + Arrays.toString(grantResults));
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Se a permissão foi concedida, envie a notificação
                sendNotification();
            } else {
                // Se a permissão foi negada, exibir uma mensagem  e diricionar o usuário às configurações
                showPermissionDeniedDialog();
            }
        }
    }
    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão Necessária")
                .setMessage("Para receber notificações, é necessário conceder permissão.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Abra as configurações do aplicativo para que o usuário possa conceder a permissão manualmente
                        openAppSettings();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Exiba uma mensagem indicando que as notificações não serão enviadas
                        Toast.makeText(QuizzActivity.this, "Permissão negada. Notificações desativadas.", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void openAppSettings() {
        startActivity(
                new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null)
                )
        );
    }
    private String colorToHexString(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }
}
