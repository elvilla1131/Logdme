package com.example.elvilla.logdme;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewParent;
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
    private TextView tvTitulo, tvDueño,tvFecha, tvBarrio, tvHuespedes, tvServLadadora, tvDescripcion, tvRestricciones, tvPrecio;
    String url_imagen_pension, thumbnail, imagen_dueño, titulo, dueño, fecha, barrio, no_huespedes, serv_lavadora, descripcion, restricciones, precio;

    private Intent i ;
    private Bundle bundle;

    private FirebaseFirestore firebaseFirestore;


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

                if(task.isSuccessful()){

                    dueño = task.getResult().getString("nombre");
                    imagen_dueño = task.getResult().getString("imagen");

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


    }

    public void setUserData(String dueño, String url_imagen_dueño){
        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.default_profile);

        //modifica el nombre del dueño
        tvDueño.setText(dueño);
        //modifica la imagen del dueño
        Glide.with(this).applyDefaultRequestOptions(placeholderOption).load(url_imagen_dueño).into(imgDueño);
    }


}
