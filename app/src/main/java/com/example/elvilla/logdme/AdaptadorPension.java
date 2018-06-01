package com.example.elvilla.logdme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.DateFormat;

import java.util.Date;
import java.util.List;

public class AdaptadorPension extends RecyclerView.Adapter<AdaptadorPension.PensionViewHolder>{

    public List<Pension> listaPensiones;
    public  Context cntxt;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    //private OnPensionClickListener clickListener;
    public AdaptadorPension(List<Pension> listaPensiones){

        this.listaPensiones = listaPensiones;
     //   this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public PensionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pension,parent,false);
        cntxt = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new PensionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PensionViewHolder holder, int position) {
        final Pension p = listaPensiones.get(position);

        holder.setTitle(p.getTitulo());
        holder.setDescripcion(p.getDescripcion());
        holder.setImgPension(p.getUrl_imagen(), p.getThumbnail());

        long milisecond = p.getTimestamp().getTime();
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String dateString = df.format("MM/dd/yyyy", new Date(milisecond)).toString();
        holder.setDate(dateString);

        String id_usuario = p.getId_usuario();
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
        });



    }

    @Override
    public int getItemCount() {
        return listaPensiones.size();
    }


    public class PensionViewHolder extends RecyclerView.ViewHolder{

        private View v;

        private ImageView imgDueño, imgPension;
        private TextView tvDueño, tvFechaPublicada, tvTitulo, tvDescripcion;

        public PensionViewHolder(View itemView){
            super(itemView);
            v = itemView;

        }



        public void setTitle(String title){
            tvTitulo = v.findViewById(R.id.tvTituloPension);
            tvTitulo.setText(title);
        }

        public void setDescripcion(String descText){
            tvDescripcion = v.findViewById(R.id.tvDescPension);
            tvDescripcion.setText(descText);
        }

        public void setImgPension(String downloadUri, String thumbUri){

            imgPension = v.findViewById(R.id.imgPension);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.default_principal_image);

            Glide.with(cntxt).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(cntxt).load(thumbUri)
            ).into(imgPension);
        }

        public void setDate(String fecha){
            tvFechaPublicada = v.findViewById(R.id.tvFechaPublicaPension);
            tvFechaPublicada.setText(fecha);
        }

        public void setUserData(String name, String image){

            imgDueño = v.findViewById(R.id.imgUsuarioPension);
            tvDueño = v.findViewById(R.id.tvNombreDueño);

            tvDueño.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.default_profile);

            Glide.with(cntxt).applyDefaultRequestOptions(placeholderOption).load(image).into(imgDueño);

        }
    }

    public interface OnPensionClickListener{
        void onPersonaClick(Pension p);
    }

}
