package com.example.elvilla.logdme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class AdaptadorPensionArrendador extends RecyclerView.Adapter<AdaptadorPensionArrendador.PensionViewHolder>{

    public List<Pension> listaPensiones;
    public Context cntxt;

    private FirebaseFirestore firebaseFirestore;

    private OnPensionClickListener clickListener;

    public AdaptadorPensionArrendador(List<Pension> listaPensiones, OnPensionClickListener clickListener){

        this.listaPensiones = listaPensiones;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public PensionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pension_publico,parent,false);
        cntxt = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new AdaptadorPensionArrendador.PensionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PensionViewHolder holder, int position) {

        final Pension p = listaPensiones.get(position);

        holder.setTitle(p.getTitulo());
        holder.setPrice(p.getPrecio());
        holder.setBarrio(p.getBarrio());
        holder.setGuests(p.getNo_huespedes());
        holder.setLaundry(p.getServ_lavadora());
        holder.setImgPension(p.getUrl_imagen(), p.getThumbnail());


        long milisecond = p.getTimestamp().getTime();
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String dateString = df.format("MM/dd/yyyy", new Date(milisecond)).toString();
        holder.setDate(dateString);

       /* String id_usuario = p.getId_usuario();
        firebaseFirestore.collection("Usuarios").document(id_usuario).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    String nombreDueño = task.getResult().getString("nombre");
                    String imagenDueño = task.getResult().getString("imagen");

                    holder.setUserData(nombreDueño, imagenDueño);


                } else {

                    //Firebase exception

                }
            }
        });*/

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onPensionClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPensiones.size();
    }


    public class PensionViewHolder extends RecyclerView.ViewHolder{

        private View v;

        private ImageView  imgPension;
        private TextView tvFechaPublicada, tvTitulo, tvPrecio, tvBarrio, tvNoHuespedes, tvLavadora;

        public PensionViewHolder(View itemView){
            super(itemView);
            v = itemView;

        }


        public void setTitle(String title){
            tvTitulo = v.findViewById(R.id.tvTituloPensionPub);
            tvTitulo.setText(title);
        }

        public void setPrice(String price){
            tvPrecio = v.findViewById(R.id.tvPrecioPensionPub);
            tvPrecio.setText(price);
        }

        public void setBarrio(String street){
            tvBarrio = v.findViewById(R.id.tvBarrioPensionPub);
            tvBarrio.setText(street);
        }

        public void setGuests(String guests){
            tvNoHuespedes = v.findViewById(R.id.tvNumeroHuespedPension);
            tvNoHuespedes.setText(guests);
        }

        public void setLaundry(String ans){
            tvLavadora = v.findViewById(R.id.tvLavadoPensionPub);
            tvLavadora.setText(ans);
        }


        public void setImgPension(String downloadUri, String thumbUri){

            imgPension = v.findViewById(R.id.imgPensionPub);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.default_principal_image);

            Glide.with(cntxt).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(cntxt).load(thumbUri)
            ).into(imgPension);
        }

        public void setDate(String fecha){
            tvFechaPublicada = v.findViewById(R.id.tvFechaPub);
            tvFechaPublicada.setText(fecha);
        }
/*
        public void setUserData(String name, String image){

            imgDueño = v.findViewById(R.id.imgUsuarioPensionPub);
            tvDueño = v.findViewById(R.id.tvUsuarioPub);

            tvDueño.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.default_profile);

            Glide.with(cntxt).applyDefaultRequestOptions(placeholderOption).load(image).into(imgDueño);

        }*/
    }

    public interface OnPensionClickListener{
        void onPensionClick(Pension p);
    }
}
