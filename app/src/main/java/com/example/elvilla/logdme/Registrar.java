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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registrar extends AppCompatActivity implements View.OnClickListener {


    private EditText TextEmail;
    private EditText TextPassword;
    private EditText TextConfirmPass;
    private Button btnRegistrar;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        TextEmail = findViewById(R.id.TxtEmail);
        TextPassword = findViewById(R.id.TxtPassword);
        TextConfirmPass = findViewById(R.id.TxtConfirmPass);

        btnRegistrar = findViewById(R.id.botonRegistrar);


        progressDialog = new ProgressDialog(this);

        btnRegistrar.setOnClickListener(this);
    }

    private void registrarUsuario() {


      final String email = TextEmail.getText().toString().trim();
        String password = TextPassword.getText().toString().trim();
        String confirmPass = TextConfirmPass.getText().toString().trim();


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


        if (!password.equals(confirmPass) ) {

            TextConfirmPass.requestFocus();
            TextConfirmPass.setError(getResources().getString(R.string.error_confirm_pass));
            return;
        }


        progressDialog.setMessage(getResources().getString(R.string.registrando_msg));
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            String tipoUsuario = getIntent().getExtras().getString("tipoUsuario");

                            Map<String, String> userMap = new HashMap<>();
                            userMap.put("correo", email);
                            userMap.put("tipo_usuario", tipoUsuario);
                            userMap.put("nombre", "");
                            userMap.put("imagen", "");
                            userMap.put("apellido", "");
                            userMap.put("telefono", "");

                            firebaseFirestore.collection("Usuarios").document(firebaseAuth.getCurrentUser().getUid()).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(Registrar.this, getResources().getString(R.string.registro_exitoso) + " " + TextEmail.getText(), Toast.LENGTH_LONG).show();

                                    } else {

                                        String error = task.getException().getMessage();
                                        Toast.makeText(Registrar.this, "Firestore Error : " + error, Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                TextEmail.requestFocus();
                                TextEmail.setError(getResources().getString(R.string.correo_ya_existe));

                            } else {

                                Toast.makeText(Registrar.this, getResources().getString(R.string.registro_fallido), Toast.LENGTH_LONG).show();


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
