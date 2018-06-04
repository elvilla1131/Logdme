package com.example.elvilla.logdme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class PautarPension extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private Toolbar nuevaPensionToolbar;

    private ImageView imagenPension;
    private Uri imagenPensionUri = null;
    private EditText mTitulo, mDescripcion, mNumHuespedes, mPrecio, mBarrio, mRestriciones;
    private TextView mImagenPension;
    private Spinner cmbLavadora;
    private String lavadoraOpcs[];

    String id_pension, url_imagen_pension, thumbnail, titulo, fecha, barrio, no_huespedes, serv_lavadora, descripcion, restricciones, precio;


    private ArrayAdapter<String> adaptadorLavadora;

    private Button mGuardarDatos;

    private ProgressBar pautarPensionProgressBar;

    private String id_usuario_actual;

    private Bitmap compressedImageFile;

    private Intent i ;
    private Bundle bundle;
    private Boolean enModificacion = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pautar_pension);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        id_usuario_actual = firebaseAuth.getCurrentUser().getUid();

        nuevaPensionToolbar = findViewById(R.id.pautarPensionToolbar);
        setSupportActionBar(nuevaPensionToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.nueva_pension));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagenPension = findViewById(R.id.imgPrincipalPension);
        mTitulo = findViewById(R.id.txtNombrePension);
        mDescripcion = findViewById(R.id.txtDescPension);
        mNumHuespedes = findViewById(R.id.txtNumeroCuartosPension);
        mPrecio = findViewById(R.id.txtPrecioPension);
        mBarrio = findViewById(R.id.txtBarrioPension);
        mRestriciones = findViewById(R.id.txtRestricPension);

        cmbLavadora = findViewById(R.id.cmbLavadora);
        lavadoraOpcs = this.getResources().getStringArray(R.array.opciones_s_lavadora);
        adaptadorLavadora = new ArrayAdapter<String>(this,R.layout.custom_spinner,lavadoraOpcs);
        cmbLavadora.setAdapter(adaptadorLavadora);

        pautarPensionProgressBar = findViewById(R.id.prgrsGuardarPension);

        mGuardarDatos = findViewById(R.id.btnGuardarDatosPension);

        mImagenPension = findViewById(R.id.tvImagenPrincPension);


        mGuardarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nombrePension,descripcion,barrio,restricciones, numHuespedes, precio;
                final int servLavadora;

                if(validar()){

                    pautarPensionProgressBar.setVisibility(View.VISIBLE);

                    nombrePension = mTitulo.getText().toString();
                    descripcion= mDescripcion.getText().toString();
                    barrio = mBarrio.getText().toString();
                    restricciones = mRestriciones.getText().toString();
                    numHuespedes = mNumHuespedes.getText().toString();
                    servLavadora = cmbLavadora.getSelectedItemPosition();
                    precio = mPrecio.getText().toString();

                    final String randomName = UUID.randomUUID().toString(),
                                 randomId = UUID.randomUUID().toString();

                    StorageReference filePath = storageReference.child("imagenes_pension").child(randomName + ".jpg");

                    filePath.putFile(imagenPensionUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                           final String downloadUri = task.getResult().getDownloadUrl().toString();

                            if (task.isSuccessful()){

                                File nuevaImagenFile = new File(imagenPensionUri.getPath());
                                try {

                                    compressedImageFile = new Compressor(PautarPension.this)
                                            .setMaxHeight(200)
                                            .setMaxWidth(200)
                                            .setQuality(10)
                                            .compressToBitmap(nuevaImagenFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadTask = storageReference .child("imagenes_pension/thumbs").child(randomName + ".jpg").putBytes(thumbData);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                        Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("id_pension", randomId);
                                        postMap.put("id_usuario", id_usuario_actual);
                                        postMap.put("url_imagen", downloadUri);
                                        postMap.put("titulo", nombrePension);
                                        postMap.put("descripcion", descripcion);
                                        postMap.put("barrio", barrio);
                                        postMap.put("restricciones", restricciones);
                                        postMap.put("no_huespedes", numHuespedes);
                                        postMap.put("serv_lavadora", lavadoraOpcs[servLavadora]);
                                        postMap.put("precio", precio);
                                        postMap.put("timestamp", FieldValue.serverTimestamp());
                                        postMap.put("thumbnail", downloadthumbUri);

                                        /*
                                        firebaseFirestore.collection("Pensiones").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                if (task.isSuccessful()){

                                                    Toast.makeText(PautarPension.this,getResources().getString(R.string.guardar_pension_correcto),Toast.LENGTH_LONG).show();

                                                    Intent intentPrincipal = new Intent(PautarPension.this, PrincipalAlquilador.class);
                                                    startActivity(intentPrincipal);
                                                    finish();

                                                }else{

                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(PautarPension.this,"Firestore Error : " + error,Toast.LENGTH_LONG).show();
                                                }

                                                pautarPensionProgressBar.setVisibility(View.INVISIBLE);

                                            }
                                        });*/

                                        firebaseFirestore.collection("Pensiones").document(randomId).set(postMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Toast.makeText(PautarPension.this,getResources().getString(R.string.guardar_pension_correcto),Toast.LENGTH_LONG).show();

                                                Intent intentPrincipal = new Intent(PautarPension.this, PrincipalAlquilador.class);
                                                startActivity(intentPrincipal);
                                                finish();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                String error = e.getMessage();
                                                Toast.makeText(PautarPension.this,"Firestore Error : " + error,Toast.LENGTH_LONG).show();

                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error handling
                                    }
                                });




                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(PautarPension.this,"Storage Error : " + error,Toast.LENGTH_LONG).show();

                                pautarPensionProgressBar.setVisibility(View.INVISIBLE);
                            }

                        }
                    });
                }
            }
        });

        imagenPension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (ContextCompat.checkSelfPermission(PautarPension.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(PautarPension.this,getResources().getString(R.string.permiso_denegado),Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(PautarPension.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    }else{

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setMinCropResultSize(512,512)
                                .setAspectRatio(1,1)
                                .start(PautarPension.this);
                    }
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        i = getIntent();
        bundle = i.getBundleExtra("datos");

        Log.v("TAG", "Bundle : " + bundle);

        if(bundle != null){
            enModificacion = true;
            // Obteniendo los datos de la pension pasados por DetallePEnsionAlqu (A través del bundle)

            id_pension = bundle.getString("id_pension");
            url_imagen_pension = bundle.getString("url_imagen");
            thumbnail = bundle.getString("thumbnail");
            titulo = bundle.getString("titulo");
            fecha = bundle.getString("timestamp");
            barrio = bundle.getString("barrio");
            no_huespedes = bundle.getString("no_huespedes");
            serv_lavadora = bundle.getString("serv_lavadora");
            descripcion = bundle.getString("descripcion");
            restricciones = bundle.getString("restricciones");
            precio = bundle.getString("precio");


            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.default_principal_image);

            //modifica la imagen de la pension
            Glide.with(this).applyDefaultRequestOptions(placeholderOption).load(url_imagen_pension).into(imagenPension);



            mTitulo.setText(titulo);
            mDescripcion.setText(descripcion);
            mNumHuespedes.setText(no_huespedes);
            mPrecio.setText(precio);
            mBarrio.setText(barrio);
            mRestriciones.setText(restricciones);
            cmbLavadora.setSelection((serv_lavadora.equals("Si"))?1:2);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                imagenPensionUri = result.getUri();
                imagenPension.setImageURI(imagenPensionUri);

                Toast.makeText(PautarPension.this,getResources().getString(R.string.guardar_imagen_correcto),Toast.LENGTH_LONG).show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }

    public Boolean validar(){
        String nombrePension,descripcion,barrio,restricciones, numHuespedes, precio;
        int opcCervLavadora;

        nombrePension = mTitulo.getText().toString();
        descripcion= mDescripcion.getText().toString();
        barrio = mBarrio.getText().toString();
        restricciones = mRestriciones.getText().toString();
        numHuespedes = mNumHuespedes.getText().toString();
        precio = mPrecio.getText().toString();

        opcCervLavadora = cmbLavadora.getSelectedItemPosition();

        if(TextUtils.isEmpty(nombrePension)){
            mTitulo.requestFocus();
            mTitulo.setError(getResources().getString(R.string.error_campo_vacio));

            return false;
        }

        if(imagenPensionUri == null && !enModificacion){
            imagenPension.requestFocus();
            mImagenPension.setError(getResources().getString(R.string.error_imagen_vacia));

            return false;
        }

        if(TextUtils.isEmpty(descripcion)){
            mDescripcion.requestFocus();
            mDescripcion.setError(getResources().getString(R.string.error_campo_vacio));

            return false;
        }

        if(TextUtils.isEmpty(numHuespedes)){
            mNumHuespedes.requestFocus();
            mNumHuespedes.setError(getResources().getString(R.string.error_campo_vacio));

            return false;
        }

        if(opcCervLavadora == 0){
            cmbLavadora.requestFocus();
            TextView errorText = (TextView) cmbLavadora.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText(getResources().getString(R.string.error_combo_vacio));

            return false;
        }

        if(TextUtils.isEmpty(precio)){
            mPrecio.requestFocus();
            mPrecio.setError(getResources().getString(R.string.error_campo_vacio));

            return false;
        }

        if(TextUtils.isEmpty(barrio)){
            mBarrio.requestFocus();
            mBarrio.setError(getResources().getString(R.string.error_campo_vacio));

            return false;
        }

        if(TextUtils.isEmpty(restricciones)){
            mRestriciones.requestFocus();
            mRestriciones.setError(getResources().getString(R.string.error_campo_vacio));

            return false;
        }

        return true;
    }

}
