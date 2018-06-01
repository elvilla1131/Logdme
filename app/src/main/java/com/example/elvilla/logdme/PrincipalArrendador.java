package com.example.elvilla.logdme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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

public class PrincipalArrendador extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mainArrendadorToolbar;

    private RecyclerView ryclListaPensiones;
    private List<Pension> listaPensiones;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String id_usuario_actual;

    private AdaptadorPensionArrendador adaptadorPension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_arrendador);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainArrendadorToolbar = findViewById(R.id.principalArrendadorToolbar);
        setSupportActionBar(mainArrendadorToolbar);

        getSupportActionBar().setTitle(getResources().getString(R.string.pensiones));


        listaPensiones = new ArrayList<>();
        ryclListaPensiones = findViewById(R.id.lst_pensiones);

        adaptadorPension = new AdaptadorPensionArrendador(listaPensiones);
        ryclListaPensiones.setLayoutManager(new LinearLayoutManager(PrincipalArrendador.this));
        ryclListaPensiones.setAdapter(adaptadorPension);


        if(mAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();


            firebaseFirestore.collection("Pensiones").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(PrincipalArrendador.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            Pension p = doc.getDocument().toObject(Pension.class);

                                listaPensiones.add(p);
                                adaptadorPension.notifyDataSetChanged();

                           /* Log.d(TAG, "pensionUid: " + p.getId_usuario());
                            Log.d(TAG, "UidActual: " + id_usuario_actual);*/


                        }
                    }
                }
            });
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
                            Intent i = new Intent(PrincipalArrendador.this, PerfilAlquilador.class);
                            startActivity(i);
                            finish();
                        }

                    }else{

                        String error = task.getException().getMessage();
                        Toast.makeText(PrincipalArrendador.this,"Error : " + error,Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_items,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.btnConfigurar:

               /* Intent i = new Intent(PrincipalArrendador.this, PerfilArrendador.class);
                startActivity(i);

                return true;

            case R.id.btnLogOut:
                return true;

            default:
                return false;
        }

    }*/
}
