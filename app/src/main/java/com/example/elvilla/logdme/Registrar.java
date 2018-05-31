package com.example.elvilla.logdme;

import android.app.ProgressDialog;
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

public class Registrar extends AppCompatActivity implements View.OnClickListener {


    private EditText TextPassword;
    private EditText TextEmail;
    private Button btnRegistrar;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        firebaseAuth = FirebaseAuth.getInstance();
        TextEmail = findViewById(R.id.TxtEmail);
        TextPassword = findViewById(R.id.TxtPassword);

        btnRegistrar = findViewById(R.id.botonRegistrar);


        progressDialog = new ProgressDialog(this);

        btnRegistrar.setOnClickListener(this);
    }

    private void registrarUsuario() {


        String email = TextEmail.getText().toString().trim();
        String password = TextPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Falta ingresar la contraseña", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Realizando registro en linea...");
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(Registrar.this, "Se ha registrado el usuario con el email: " + TextEmail.getText(), Toast.LENGTH_LONG).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Registrar.this, "Ese usuario ya existe ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Registrar.this, "No se pudo registrar el usuario, el correo es incorrecto ", Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });

    }


    @Override
    public void onClick(View v) {
        registrarUsuario();
    }
}
