package com.example.elvilla.logdme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class PerfilAlquilador extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageURI = null;

    private String user_id, user_email;

    private boolean isChanged = false;

    private TextView setupEmail;
    private EditText setupName, setupLastName, setupPhone;
    private Button setupBtn;
    private ProgressBar setupProgress;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_alquilador);

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.perfil));

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        user_email = firebaseAuth.getCurrentUser().getEmail();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupLastName = findViewById(R.id.setup_lastName);
        setupPhone = findViewById(R.id.setup_phone);

        setupEmail = findViewById(R.id.setupEmail);
        setupEmail.setEnabled(false);

        setupBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);

        firebaseFirestore.collection("Usuarios").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot usuarioActual = task.getResult();
                    if(usuarioActual.exists()){

                        String name = usuarioActual.getString("nombre");
                        String image = usuarioActual.getString("imagen");
                        String lastname = usuarioActual.getString("apellido");
                        String telephone = usuarioActual.getString("telefono");

                        mainImageURI = Uri.parse(image);

                        setupName.setText(name);
                        setupEmail.setText(user_email);
                        setupLastName.setText(lastname);
                        setupPhone.setText(telephone);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);

                        Glide.with(PerfilAlquilador.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);


                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(PerfilAlquilador.this, "Firestore Error : " + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });


        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString(),
                            apellido = setupLastName.getText().toString(),
                            telefono = setupPhone.getText().toString();

                if (validarNombre() && mainImageURI != null) {

                    setupProgress.setVisibility(View.VISIBLE);

                    if (isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();

                        File newImageFile = new File(mainImageURI.getPath());
                        try {

                            compressedImageFile = new Compressor(PerfilAlquilador.this)
                                    .setMaxHeight(125)
                                    .setMaxWidth(125)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();

                        UploadTask image_path = storageReference.child("imagenes_perfil").child(user_id + ".jpg").putBytes(thumbData);

                        image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {
                                    storeFirestore(task, user_name,apellido,telefono);

                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(PerfilAlquilador.this, "Image Error : " + error, Toast.LENGTH_LONG).show();

                                    setupProgress.setVisibility(View.INVISIBLE);

                                }
                            }
                        });

                    } else {

                        storeFirestore(null, user_name, apellido, telefono);

                    }

                } else if(!TextUtils.isEmpty(user_name)){

                    storeFirestore(null, user_name, apellido, telefono);
                }



            }

        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(PerfilAlquilador.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(PerfilAlquilador.this, getResources().getString(R.string.permiso_denegado), Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(PerfilAlquilador.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }

        });


    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name, String user_lastName, String user_phone) {

        Uri download_uri;

        if(task != null) {

            download_uri = task.getResult().getDownloadUrl();

        } else {

            download_uri = mainImageURI;

        }

        Map<String, String> userMap = new HashMap<>();
        userMap.put("nombre", user_name);
        userMap.put("imagen", download_uri.toString());
        userMap.put("apellido", user_lastName);
        userMap.put("telefono", user_phone);
        userMap.put("correo", user_email);

        firebaseFirestore.collection("Usuarios").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(PerfilAlquilador.this, getResources().getString(R.string.perfil_actualizado), Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(PerfilAlquilador.this, PrincipalAlquilador.class);
                    startActivity(mainIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(PerfilAlquilador.this, "(Firestore Error) : " + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);

            }
        });


    }

    public Boolean validarNombre(){

        String nombre;

        nombre = setupName.getText().toString();


        if(TextUtils.isEmpty(nombre)){
            setupName.requestFocus();
            setupName.setError(getResources().getString(R.string.error_campo_vacio));
            return false;
        }


        return true;
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(PerfilAlquilador.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}

