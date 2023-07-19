package com.utpl.distribuidoragas.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.entidades.Cliente;
import com.utpl.distribuidoragas.entidades.Repartidor;

import java.util.ArrayList;

public class AdaptadorRepartidores extends RecyclerView.Adapter<AdaptadorRepartidores.MyViewHolder> implements View.OnClickListener {
    private ArrayList<Repartidor> listaRepartidores;
    private Context context;
    private View.OnClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nombres, apellidos, cedula, telefono, direccion, email, lat, lng;
        public ImageView foto;

        public MyViewHolder(View view) {
            super(view);
            foto = view.findViewById(R.id.iv_foto);
            nombres = view.findViewById(R.id.tv_nombres);
            apellidos = view.findViewById(R.id.tv_apellidos);
            cedula = view.findViewById(R.id.tv_cedula);
            telefono = view.findViewById(R.id.tv_telefono);
            direccion = view.findViewById(R.id.tv_direccion);
            email = view.findViewById(R.id.tv_email);
            lat = view.findViewById(R.id.tv_lat);
            lng = view.findViewById(R.id.tv_lng);
        }
    }

    public AdaptadorRepartidores(Context context, ArrayList<Repartidor> repartidores) {
        this.context = context;
        this.listaRepartidores = repartidores;
    }

    @Override
    public AdaptadorRepartidores.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repartidor, parent, false);
        itemView.setOnClickListener(this);
        return new AdaptadorRepartidores.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdaptadorRepartidores.MyViewHolder holder, int position) {
        Repartidor repartidor = listaRepartidores.get(position);
        // Usar placeholder o imagen por defecto
        holder.foto.setImageResource(R.drawable.cilindro);
        holder.nombres.setText("Nombres: " + repartidor.getNombres());
        holder.apellidos.setText("Apellidos: " + repartidor.getApellidos());
        holder.cedula.setText("Cédula: " + repartidor.getCedula());
        holder.telefono.setText("Teléfono: " + repartidor.getTelefono());
        holder.direccion.setText("Dirección: " + repartidor.getDireccion());
        holder.email.setText("Email: " + repartidor.getEmail());
        holder.lat.setText("Latitud: " + repartidor.getLat());
        holder.lng.setText("Longitud: " + repartidor.getLng());
    }

    @Override
    public int getItemCount() {
        return listaRepartidores.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }
}