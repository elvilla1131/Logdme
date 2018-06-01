package com.example.elvilla.logdme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class Alquilador extends AppCompatActivity implements View.OnClickListener {

    private EditText TextEmail;
    private EditText TextPassword;
    private Button btnRegistrar, btnLogin;
    private ProgressDialog progressDialog;


    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alquilador);

        firebaseAuth = FirebaseAuth.getInstance();


        TextEmail = findViewById(R.id.TxtEmail);
        TextPassword = findViewById(R.id.TxtPassword);

        btnRegistrar = findViewById(R.id.botonRegistrar);

        btnLogin = findViewById(R.id.botonLogin);

        progressDialog = new ProgressDialog(this);


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btnRegistrar = new Intent(Alquilador.this,RegistrarAlquilador.class);
                startActivity(btnRegistrar);
            }
        });

        btnLogin.setOnClickListener(this);
    }



    private void loguearUsuario() {

        final String email = TextEmail.getText().toString().trim();
        String password = TextPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Falta ingresar la contraseña", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Realizando consulta en linea...");
        progressDialog.show();


        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            int pos = email.indexOf("@");
                            String user = email.substring(0, pos);
                            Toast.makeText(Alquilador.this, "Bienvenido: " + TextEmail.getText(), Toast.LENGTH_LONG).show();
                            Intent intencion = new Intent(getApplication(), PrincipalAlquilador.class);//cambiar a PrincipalAlquilador
                            //intencion.putExtra(Bienvenido.user, user);
                            startActivity(intencion);


                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Alquilador.this, "Ese usuario ya existe ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Alquilador.this, "No se pudo registrar el usuario, el correo es incorrecto", Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });


    }


    @Override
    public void onClick(View view) {

        loguearUsuario();


    }
    public void borrar(View v){
        TextEmail.setText("");
        TextPassword.setText("");
        TextEmail.requestFocus();
        TextPassword.requestFocus();



    }

}
