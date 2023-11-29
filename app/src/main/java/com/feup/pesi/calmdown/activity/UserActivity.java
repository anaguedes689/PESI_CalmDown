package com.feup.pesi.calmdown.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.feup.pesi.calmdown.MainActivity;
import com.feup.pesi.calmdown.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserActivity extends DashBoardActivity {

    private String name = "John Doe";
    private String userEmail = "john.doe@example.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Obter referências para os elementos da interface do usuário
        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);

        // Configurar os elementos da interface do usuário com os dados do usuário
        nameTextView.setText(name);
        emailTextView.setText(userEmail);

        // Botão de edição
        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            // Lógica para abrir a tela de edição
            // Você pode usar Intents para abrir uma nova atividade de edição
        });


        setUpBottomNavigation();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

            if (itemId == R.id.deleteAccountItem) {
                // Lógica para excluir a conta do usuário
                return true;
            }
            else if(itemId == R.id.settingsItem) {
                    // Lógica para navegar para as configurações do usuário
                    return true;
            }
            else {
                return super.onOptionsItemSelected(item);
        }
    }
}
