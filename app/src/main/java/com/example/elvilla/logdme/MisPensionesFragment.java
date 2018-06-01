package com.example.elvilla.logdme;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MisPensionesFragment extends Fragment {

    private RecyclerView mMiListaPensiones;
    private List<Pension> listaPensiones;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private FloatingActionButton btnAgregarPension;

    private String id_usuario_actual;

    private AdaptadorPension adaptadorPension;


    public MisPensionesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mis_pensiones, container, false);

        listaPensiones = new ArrayList<>();
        mMiListaPensiones = view.findViewById(R.id.lst_mis_pensiones);

        firebaseAuth = FirebaseAuth.getInstance();
        id_usuario_actual = firebaseAuth.getCurrentUser().getUid();

        adaptadorPension = new AdaptadorPension(listaPensiones);
        mMiListaPensiones.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMiListaPensiones.setAdapter(adaptadorPension);

        if(firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();


            firebaseFirestore.collection("Pensiones").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(getActivity(),new  EventListener<QuerySnapshot>() {
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


            btnAgregarPension = view.findViewById(R.id.btnAgregarPension);
            btnAgregarPension.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), PautarPension.class);
                    startActivity(i);
                }
            });
        }

        // Inflate the layout for this fragment
        return view;
    }

}
