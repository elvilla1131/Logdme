package com.example.elvilla.logdme;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrincipalAlquilador extends AppCompatActivity implements AdaptadorPension.OnPensionClickListener{

    private android.support.v7.widget.Toolbar mainAlquiladorToolbar;

    private RecyclerView mMiListaPensiones;
    private List<Pension> listaPensiones;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String id_usuario_actual;

    private FloatingActionButton btnAgregarPension;

    private AdaptadorPension adaptadorPension;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_alquilador);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainAlquiladorToolbar = findViewById(R.id.principalAlquiladorToolbar);
        setSupportActionBar(mainAlquiladorToolbar);

        getSupportActionBar().setTitle(getResources().getString(R.string.mis_pensiones));


        listaPensiones = new ArrayList<>();
        mMiListaPensiones = findViewById(R.id.lst_mis_pensiones);

        adaptadorPension = new AdaptadorPension(listaPensiones, this);
        mMiListaPensiones.setLayoutManager(new LinearLayoutManager(PrincipalAlquilador.this));
        mMiListaPensiones.setAdapter(adaptadorPension);

        if(mAuth.getCurrentUser() != null){

            firebaseFirestore = FirebaseFirestore.getInstance();


            firebaseFirestore.collection("Pensiones").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(PrincipalAlquilador.this,new  EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED  || doc.getType() == DocumentChange.Type.MODIFIED || doc.getType() == DocumentChange.Type.REMOVED) {

                            Pension p = doc.getDocument().toObject(Pension.class);

                            if(p.getId_usuario().equals(id_usuario_actual)){
                                listaPensiones.add(p);
                                adaptadorPension.notifyDataSetChanged();
                            }

                           /* Log.d(TAG, "pensionUid: " + p.getId_usuario());
                            Log.d(TAG, "UidActual: " + id_usuario_actual);*/


                        }
                    }
                }
            });


            btnAgregarPension = findViewById(R.id.btnAgregarPension);
            btnAgregarPension.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(PrincipalAlquilador.this, PautarPension.class);
                    startActivity(i);
                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioActual = mAuth.getCurrentUser();
        if(usuarioActual == null){

           Intent i = new Intent(PrincipalAlquilador.this, Alquilador.class);
           startActivity(i);
           finish();
        }else{

            id_usuario_actual = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Usuarios").document(id_usuario_actual).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){

                        if (task.getResult().getString("nombre").isEmpty()){
                            Intent i = new Intent(PrincipalAlquilador.this, PerfilAlquilador.class);
                            startActivity(i);
                            finish();
                        }

                    }else{

                        String error = task.getException().getMessage();
                        Toast.makeText(PrincipalAlquilador.this,"Error : " + error,Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }


    private void logOut() {

        String positivo, negativo;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.cerrar_sesion_titulo));
        builder.setMessage(getResources().getString(R.string.cerrar_sesion_mensaje));
        positivo = getResources().getString(R.string.positivo);
        negativo = getResources().getString(R.string.negativo);

        builder.setPositiveButton(positivo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                id_usuario_actual = mAuth.getCurrentUser().getUid();

                firebaseFirestore.collection("Sesiones").document(id_usuario_actual).update("estado", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                            Log.d("TAG", "DocumentSnapshot successfully updated!");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.w("TAG", "Error updating document", e);

                    }
                });

                mAuth.signOut();

                Intent i = new Intent(PrincipalAlquilador.this, Alquilador.class);
                startActivity(i);
            }
        });

        builder.setNegativeButton(negativo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_items_alqu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.btnConfigurar:

                Intent i = new Intent(PrincipalAlquilador.this, PerfilAlquilador.class);
                startActivity(i);

                return true;

            case R.id.btnLogOut:

                logOut();

                return true;

            default:
                return false;
        }

    }

    @Override
    public void onPensionClick(Pension p) {

        Intent i = new Intent(PrincipalAlquilador.this, DetallePensionAlqu.class);
        Bundle b = new Bundle();

        b.putString("id_pension", p.getId_pension());
        b.putString("id_usuario", p.getId_usuario());
        b.putString("titulo", p.getTitulo());
        b.putString("url_imagen", p.getUrl_imagen());
        b.putString("descripcion", p.getDescripcion());
        b.putString("precio", p.getPrecio());
        b.putString("no_huespedes", p.getNo_huespedes());
        b.putString("serv_lavadora", p.getServ_lavadora());
        b.putString("barrio", p.getBarrio());
        b.putString("thumbnail", p.getThumbnail());

        long milisecond = p.getTimestamp().getTime();
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String dateString = df.format("MM/dd/yyyy", new Date(milisecond)).toString();
        b.putString("timestamp", dateString);

        b.putString("restricciones", p.getRestricciones());

        i.putExtra("datos", b);
        startActivity(i);

    }


}
