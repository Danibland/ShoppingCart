package com.danielblandon.carrito;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

     private Button btnUsuario, btnAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnUsuario = findViewById(R.id.BtnUsuario);
        btnAdmin = findViewById(R.id.BtnAdmin);


        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GoActUs = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(GoActUs);
            }
        });
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GoActAdm = new Intent(HomeActivity.this,LoginAdminActivity.class);
                startActivity(GoActAdm);
            }
        });
    }
}