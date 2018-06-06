package com.example.elvilla.logdme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Arrendador extends AppCompatActivity implements View.OnClickListener {

    private EditText TextEmail;
    private EditText TextPassword;
    private Button btnRegistrar, btnLogin;
    private ProgressDialog progressDialog;


    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String id_usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrendador);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        TextEmail = findViewById(R.id.TxtEmailArr);
        TextPassword = findViewById(R.id.TxtPasswordPass);

        btnRegistrar = findViewById(R.id.botonRegistrar);

        btnLogin = findViewById(R.id.botonLogin);

        progressDialog = new ProgressDialog(this);


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btnRegistrar = new Intent(Arrendador.this, Registrar.class);
                btnRegistrar.putExtra("tipoUsuario", "ARR");
                startActivity(btnRegistrar);
            }
        });

        btnLogin.setOnClickListener(this);
    }



    private void loguearUsuario() {

        final String email = TextEmail.getText().toString().trim();
        String password = TextPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            TextEmail.requestFocus();
            TextEmail.setError(getResources().getString(R.string.error_campo_vacio));
            return;
        }

        if (TextUtils.isEmpty(password)) {

            TextPassword.requestFocus();
            TextPassword.setError(getResources().getString(R.string.error_campo_vacio));
            return;
        }

        if (password.length() < 6) {

            TextPassword.requestFocus();
            TextPassword.setError(getResources().getString(R.string.contraseña_invalida));
            return;
        }


        progressDialog.setMessage(getResources().getString(R.string.consultando_msg));
        progressDialog.show();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            int pos = email.indexOf("@");
                            String user = email.substring(0, pos);

                            Toast.makeText(Arrendador.this, getResources().getString(R.string.bienvenido) + " " + user, Toast.LENGTH_LONG).show();

                            Map<String, Object> userSessMap = new HashMap<>();
                            userSessMap.put("id_usuario", mAuth.getCurrentUser().getUid());
                            userSessMap.put("tipo_usuario", "ARR");
                            userSessMap.put("estado", true);

                            // Creacion de Sesion en la base de datos
                            firebaseFirestore.collection("Sesiones").document(mAuth.getCurrentUser().getUid()).set(userSessMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        Log.v("TAG", "Sesion creada/modificada");

                                    } else {

                                        String error = task.getException().getMessage();
                                        Toast.makeText(Arrendador.this, "Firestore Error : " + error, Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                            Intent intencion = new Intent(getApplication(), PrincipalArrendador.class);
                            startActivity(intencion);


                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                TextEmail.requestFocus();
                                TextEmail.setError(getResources().getString(R.string.correo_ya_existe));

                            } else {

                                Toast.makeText(Arrendador.this, getResources().getString(R.string.inicio_fallido), Toast.LENGTH_LONG).show();

                            }
                        }

                        progressDialog.dismiss();
                    }
                });


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Arrendador.this, Principal.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onClick(View view) {
        loguearUsuario();
    }

}
