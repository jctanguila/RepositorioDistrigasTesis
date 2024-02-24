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
import com.utpl.distribuidoragas.entidades.ConfigEntidad;

import java.util.ArrayList;

public class AdaptadorConfigs extends RecyclerView.Adapter<AdaptadorConfigs.MyViewHolder> implements View.OnClickListener{
    private ArrayList<ConfigEntidad> listaConfigs;
    private Context context;
    private View.OnClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView Nombres, Estados;
        public ImageView foto;
        public MyViewHolder(View view) {
            super(view);
            Nombres = view.findViewById(R.id.txtNombres);
            Estados = view.findViewById(R.id.txtEstados);
            foto = view.findViewById(R.id.iv_foto);
        }
    }

    public AdaptadorConfigs(Context context, ArrayList<ConfigEntidad> configs) {
        this.context = context;
        this.listaConfigs = configs;
    }

    @Override
    public AdaptadorConfigs.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_config_lista, parent, false);
        itemView.setOnClickListener(this);
        return new AdaptadorConfigs.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdaptadorConfigs.MyViewHolder holder, int position) {
        ConfigEntidad config = listaConfigs.get(position);
        // Usar placeholder o imagen por defecto
        holder.foto.setImageResource(R.drawable.cilindro);
        holder.Nombres.setText("Nombres: " + config.getNombres());
        holder.Estados.setText("Estados: " + config.getEstados());
    }

    @Override
    public int getItemCount() {
        return listaConfigs.size();
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
