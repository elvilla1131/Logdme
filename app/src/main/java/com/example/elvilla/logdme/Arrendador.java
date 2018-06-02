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

public class Arrendador extends AppCompatActivity implements View.OnClickListener {

    private EditText TextEmail;
    private EditText TextPassword;
    private Button btnRegistrar, btnLogin;
    private ProgressDialog progressDialog;


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private String id_usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrendador);

        firebaseAuth = FirebaseAuth.getInstance();
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


        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            int pos = email.indexOf("@");
                            String user = email.substring(0, pos);

                            Toast.makeText(Arrendador.this, getResources().getString(R.string.bienvenido) + " " + user, Toast.LENGTH_LONG).show();

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
    protected void onStart() {
        super.onStart();

        FirebaseUser usuario_actual = firebaseAuth.getCurrentUser();

        Log.d("MyActivity", "Usuario actual : " + usuario_actual);

        if(usuario_actual != null){

            id_usuarioActual = firebaseAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Usuarios").document(id_usuarioActual).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){

                        if (task.getResult().getString("tipo_usuario").equals("ARR")){
                            Intent i = new Intent(Arrendador.this, PrincipalArrendador.class);
                            startActivity(i);
                            finish();
                        }

                    }else{

                        String error = task.getException().getMessage();
                        Toast.makeText(Arrendador.this,"Error : " + error,Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Arrendador.this, Principal.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View view) {
        loguearUsuario();
    }

}
