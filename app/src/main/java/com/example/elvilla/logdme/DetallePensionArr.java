package com.example.elvilla.logdme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetallePensionArr extends AppCompatActivity {

    private ImageView imgPension, imgDueño;
    private TextView tvTitulo, tvDueño, tvFecha, tvBarrio, tvHuespedes, tvServLadadora, tvDescripcion, tvRestricciones, tvPrecio;
    String url_imagen_pension, thumbnail, imagen_dueño, titulo, dueño, fecha, barrio, no_huespedes, serv_lavadora, descripcion, restricciones, precio, telefono;
    private Button btnContactar;

    private Intent i;
    private Bundle bundle;

    private FirebaseFirestore firebaseFirestore;

    private static final int REQUEST_PHONE_CALL = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pension_arr);

        imgPension = findViewById(R.id.imgPensionDetalle);
        imgDueño = findViewById(R.id.imgUsuarioPensionDetalle);
        tvTitulo = findViewById(R.id.tvTituloPensionDetalle);
        tvDueño = findViewById(R.id.tvDueñoPensionDetalle);
        tvFecha = findViewById(R.id.tvFechaPubPensionDetalle);
        tvBarrio = findViewById(R.id.tvBarrioPensionDetalle);
        tvHuespedes = findViewById(R.id.tvHuespedPensionDetalle);
        tvServLadadora = findViewById(R.id.tvLavadoraPensionDetalle);
        tvDescripcion = findViewById(R.id.tvDescripcionPensionDetalle);
        tvRestricciones = findViewById(R.id.tvRestriccionesPensionDetalle);
        tvPrecio = findViewById(R.id.tvPrecioPensionDetalle);

        btnContactar = findViewById(R.id.btnContactarDetalle);

        firebaseFirestore = FirebaseFirestore.getInstance();
        i = getIntent();
        bundle = i.getBundleExtra("datos");

        // Obteniendo los datos pasados por PrincipalArrendador (A través del bundle)
        url_imagen_pension = bundle.getString("url_imagen");
        thumbnail = bundle.getString("thumbnail");


        String id_dueño = bundle.getString("id_usuario");
        firebaseFirestore.collection("Usuarios").document(id_dueño).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    dueño = task.getResult().getString("nombre");
                    imagen_dueño = task.getResult().getString("imagen");
                    telefono = task.getResult().getString("telefono");

                    setUserData(dueño, imagen_dueño);

                } else {

                    //Firebase exception

                }
            }
        });

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
        Glide.with(this).applyDefaultRequestOptions(placeholderOption).load(url_imagen_pension).into(imgPension);


        //modificacion de los demás elementos
        tvTitulo.setText(titulo);
        tvFecha.setText(fecha);
        tvBarrio.setText(barrio);
        tvHuespedes.setText(no_huespedes);
        tvServLadadora.setText(serv_lavadora);
        tvDescripcion.setText(descripcion);
        tvRestricciones.setText(restricciones);
        tvPrecio.setText(precio);


        btnContactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + telefono));


                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(DetallePensionArr.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DetallePensionArr.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    } else {
                        startActivity(callIntent);
                    }
                } else {
                    startActivity(callIntent);
                }
            }
        });

    }

    public void setUserData(String dueño, String url_imagen_dueño) {
        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.default_profile);

        //modifica el nombre del dueño
        tvDueño.setText(dueño);
        //modifica la imagen del dueño
        Glide.with(this).applyDefaultRequestOptions(placeholderOption).load(url_imagen_dueño).into(imgDueño);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:0377778888"));

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DetallePensionArr.this, PrincipalArrendador.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

}
