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

import java.util.ArrayList;

public class AdaptadorClientes extends RecyclerView.Adapter<AdaptadorClientes.MyViewHolder> implements View.OnClickListener {
    private ArrayList<Cliente> listaClientes;
    private Context context;
    private View.OnClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nombres, apellidos, cedula, telefono, direccion, email;
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
        }
    }

    public AdaptadorClientes(Context context, ArrayList<Cliente> clientes) {
        this.context = context;
        this.listaClientes = clientes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        itemView.setOnClickListener(this);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);
        // Usar placeholder o imagen por defecto
        holder.foto.setImageResource(R.drawable.cilindro);
        holder.nombres.setText("Nombres: " + cliente.getNombres());
        holder.apellidos.setText("Apellidos: " + cliente.getApellidos());
        holder.cedula.setText("Cédula: " + cliente.getCedula());
        holder.telefono.setText("Teléfono: " + cliente.getTelefono());
        holder.direccion.setText("Dirección: " + cliente.getDireccion());
        holder.email.setText("Email: " + cliente.getEmail());
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
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