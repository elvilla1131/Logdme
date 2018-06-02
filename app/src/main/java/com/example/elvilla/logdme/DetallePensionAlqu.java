package com.example.elvilla.logdme;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class DetallePensionAlqu extends AppCompatActivity {

    private ImageView imgPension;
    private TextView tvTitulo,tvFecha, tvBarrio, tvHuespedes, tvServLadadora, tvDescripcion, tvRestricciones, tvPrecio;
    String id_pension, url_imagen_pension, thumbnail, titulo, fecha, barrio, no_huespedes, serv_lavadora, descripcion, restricciones, precio;

    private Intent i ;
    private Bundle bundle;

    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pension_alqu);

        imgPension = findViewById(R.id.imgPensionDetalle);
        tvTitulo = findViewById(R.id.tvTituloPensionDetalle);
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


        // Obteniendo los datos de la pension pasados por PrincipalAlquilador (A través del bundle)

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
        Glide.with(this).applyDefaultRequestOptions(placeholderOption).load(url_imagen_pension).into(imgPension);

        tvTitulo.setText(titulo);
        tvFecha.setText(fecha);
        tvBarrio.setText(barrio);
        tvHuespedes.setText(no_huespedes);
        tvServLadadora.setText(serv_lavadora);
        tvDescripcion.setText(descripcion);
        tvRestricciones.setText(restricciones);
        tvPrecio.setText(precio);

        firebaseFirestore.collection("Pensiones").whereEqualTo("id_pension", id_pension).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

            }
        });
    }

    public void eliminar(View v){
        String positivo, negativo;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.eliminar));
        builder.setMessage(getResources().getString(R.string.pregunta_eliminacion));
        positivo = getResources().getString(R.string.positivo);
        negativo = getResources().getString(R.string.negativo);

        builder.setPositiveButton(positivo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

               /* firebaseFirestore.collection("Pensiones").whereEqualTo("titulo", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            Log.v("TAG", "Resulatado "+ document);
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });*/


                onBackPressed();
            }
        });

        builder.setNegativeButton(negativo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(DetallePensionAlqu.this, PrincipalAlquilador.class);
        startActivity(i);
    }
}
