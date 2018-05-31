package com.example.elvilla.logdme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Principal extends AppCompatActivity {


    private Button btnArrendador,btnArrendatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        btnArrendador = findViewById(R.id.botonArrendador);
        btnArrendatario = findViewById(R.id.botonArrendatario);

        btnArrendador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btnArrendador = new Intent(Principal.this,Arrendador.class);
                startActivity(btnArrendador);


            }
        });
        btnArrendatario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btnArrendatario = new Intent(Principal.this, Arrendador.class);
                startActivity(btnArrendatario);
            }
        });
    }
}