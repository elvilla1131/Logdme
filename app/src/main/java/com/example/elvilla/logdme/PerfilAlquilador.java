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
import org.w3c.dom.Text;

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

        //Listar informacion de la Base de datos
        firebaseFirestore.collection("Usuarios").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    DocumentSnapshot usuarioActual = task.getResult();

                    //Si el usuario existe y el nombre no está vacio ( Vuelve obligatorio registrar almenos el nombre)
                    if(usuarioActual.exists() && !task.getResult().getString("nombre").isEmpty()){

                        String name = usuarioActual.getString("nombre");
                        String lastname = usuarioActual.getString("apellido");
                        String telephone = usuarioActual.getString("telefono");
                        String email = usuarioActual.getString("correo");

                        setupName.setText(name);
                        setupLastName.setText(lastname);
                        setupPhone.setText(telephone);
                        setupEmail.setText(email);


                        String image = usuarioActual.getString("imagen");

                        if(!TextUtils.isEmpty(image)){

                            mainImageURI = Uri.parse(image);

                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.default_profile);

                            Glide.with(PerfilAlquilador.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);

                        }

                    // Sino solo muestra el correo
                    }else{
                        String email = usuarioActual.getString("correo");
                        setupEmail.setText(email);
                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(PerfilAlquilador.this, "Firestore Error : " + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });

        //Evento para guardar o actualizar los datos del usuario
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user_name = setupName.getText().toString(),
                            apellido = setupLastName.getText().toString(),
                            telefono = setupPhone.getText().toString();

                //Valida si el campo del nombre esta vacio y si no se ha seleccionado imagen
                if (validarDatos() && mainImageURI != null) {

                    setupProgress.setVisibility(View.VISIBLE);

                    //Valida si la imagen ha sido modificada
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

                        //Guarda imagen en el Storage
                        UploadTask image_path = storageReference.child("imagenes_perfil").child(user_id + ".jpg").putBytes(thumbData);

                        //Guarda todos los datos incluyendo la imagen modificada
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

                    //Guarda todos los datos sin modificaciones en la imagen (Deja la imagen vieja)
                    } else {

                        storeFirestore(null, user_name, apellido, telefono);

                    }

                //Guarda todos los datos sin imagen
                } else if(validarDatos()){

                    setupProgress.setVisibility(View.VISIBLE);
                    storeFirestoreWithouImage(user_name, apellido, telefono);
                }



            }

        });

        // Evento para escoger imagen del dispositivo
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(PerfilAlquilador.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(PerfilAlquilador.this, getResources().getString(R.string.permiso_denegado), Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(PerfilAlquilador.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        //Permite escoger y recortar la imagen
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

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nombre", user_name);
        userMap.put("imagen", download_uri.toString());
        userMap.put("apellido", user_lastName);
        userMap.put("telefono", user_phone);

        firebaseFirestore.collection("Usuarios").document(user_id).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(PerfilAlquilador.this, getResources().getString(R.string.perfil_actualizado), Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(PerfilAlquilador.this, PrincipalAlquilador.class);
                    startActivity(mainIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(PerfilAlquilador.this, "Firestore Error : " + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);

            }
        });


    }

    private void storeFirestoreWithouImage(String user_name, String user_lastName, String user_phone){

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nombre", user_name);
        userMap.put("apellido", user_lastName);
        userMap.put("telefono", user_phone);

        firebaseFirestore.collection("Usuarios").document(user_id).update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(PerfilAlquilador.this, getResources().getString(R.string.perfil_actualizado), Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(PerfilAlquilador.this, PrincipalAlquilador.class);
                    startActivity(mainIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(PerfilAlquilador.this, "Firestore Error : " + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);

            }
        });
    }

    public Boolean validarDatos(){

        String nombre, telefono;

        nombre = setupName.getText().toString();
        telefono = setupPhone.getText().toString();


        if(TextUtils.isEmpty(nombre)){
            setupName.requestFocus();
            setupName.setError(getResources().getString(R.string.error_campo_vacio));
            return false;
        }

        if(TextUtils.isEmpty(telefono)){
            setupPhone.requestFocus();
            setupPhone.setError(getResources().getString(R.string.error_campo_vacio));
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

    //Evento que se despliega al escoger una imagen
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

    @Override
    public void onBackPressed() {
        if(TextUtils.isEmpty(setupName.getText().toString())){

            setupBtn.performClick();

        }else {

            finish();
            Intent i = new Intent(PerfilAlquilador.this, PrincipalAlquilador.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

    }
}

