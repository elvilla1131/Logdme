package com.example.elvilla.logdme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Principal extends AppCompatActivity {


    private Button btnArrendador,btnArrendatario;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        btnArrendador = findViewById(R.id.botonArrendador);
        btnArrendatario = findViewById(R.id.botonArrendatario);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

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
                Intent btnArrendatario = new Intent(Principal.this, Alquilador.class);
                startActivity(btnArrendatario);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuario_actual = mAuth.getCurrentUser();
        final String id_usuarioActual;
        Log.d("MyActivity", "Usuario actual : " + usuario_actual);

        if(usuario_actual != null){

            id_usuarioActual = usuario_actual.getUid();
            Log.d("MyActivity", "Id Usuario actual : " + id_usuarioActual);


            firebaseFirestore.collection("Usuarios").document(id_usuarioActual).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){

                        firebaseFirestore.collection("Sesiones").document(id_usuarioActual).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if(task.getResult().getBoolean("estado")){

                                    if (task.getResult().getString("tipo_usuario").equals("AL")){

                                        Intent i = new Intent(Principal.this, PrincipalAlquilador.class);
                                        startActivity(i);
                                        finish();

                                    }else{

                                        Intent i = new Intent(Principal.this, PrincipalArrendador.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }

                            }
                        });



                    }else{

                        String error = task.getException().getMessage();
                        Toast.makeText(Principal.this,"Error : " + error,Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

    }
}