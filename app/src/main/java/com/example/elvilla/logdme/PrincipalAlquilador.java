package com.example.elvilla.logdme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.List;

public class PrincipalAlquilador extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mainAlquiladorToolbar;

    private RecyclerView mMiListaPensiones;
    private List<Pension> listaPensiones;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String id_usuario_actual;

    private FloatingActionButton btnAgregarPension;

    private AdaptadorPension adaptadorPension;
/*
    private BottomNavigationView mainNavAlquilador;

    private MisPensionesFragment misPensionesFragment;
    private PerfilFragment perfilFragment;

    private BottomNavigationView mNavPrincipal;
    private FrameLayout mFramePrincipal;

    private MisPensionesFragment fragmentoMisPensiones;
    private PerfilFragment fragmentoPerfil;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_alquilador);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainAlquiladorToolbar = findViewById(R.id.principalAlquiladorToolbar);
        setSupportActionBar(mainAlquiladorToolbar);

//        getSupportActionBar().setTitle("cual");

        listaPensiones = new ArrayList<>();
        mMiListaPensiones = findViewById(R.id.lst_mis_pensiones);

        adaptadorPension = new AdaptadorPension(listaPensiones);
        mMiListaPensiones.setLayoutManager(new LinearLayoutManager(PrincipalAlquilador.this));
        mMiListaPensiones.setAdapter(adaptadorPension);

        if(mAuth.getCurrentUser() != null){

            firebaseFirestore = FirebaseFirestore.getInstance();


            firebaseFirestore.collection("Pensiones").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(PrincipalAlquilador.this,new  EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

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


           /* mainNavAlquilador = findViewById(R.id.mainNavAlquilador);

            //Fragments
            misPensionesFragment = new MisPensionesFragment();
            perfilFragment = new PerfilFragment();


            replaceFragment(misPensionesFragment);
            mainNavAlquilador.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    switch (item.getItemId()) {

                        case R.id.nav_misPensiones:
                            replaceFragment(misPensionesFragment);
                            return true;

                        case R.id.nav_Perfil:
                            replaceFragment(perfilFragment);
                            return true;

                        default:
                            return false;
                    }
                }
            });*/

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if(usuarioActual == null){
            //Enviar al login
        }else{

            id_usuario_actual = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Usuarios").document(id_usuario_actual).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){

                        if (!task.getResult().exists()){
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

   /* private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.contenedor_alquilador, misPensionesFragment);
        fragmentTransaction.add(R.id.contenedor_alquilador, perfilFragment);

        fragmentTransaction.hide(perfilFragment);

        fragmentTransaction.commit();

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_items,menu);
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
                return true;

            default:
                return false;
        }

    }
/*
    private void replaceFragment(android.support.v4.app.Fragment fragment){
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contenedor_alquilador,fragment);
        fragmentTransaction.commit();
    }*/
}
