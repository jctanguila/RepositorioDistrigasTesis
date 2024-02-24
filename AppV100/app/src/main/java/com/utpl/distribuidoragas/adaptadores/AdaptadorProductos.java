package com.utpl.distribuidoragas.adaptadores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.entidades.Producto;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ViewHolderProductos> implements View.OnClickListener {

    ArrayList<Producto> listaProductos;
    private Context context;
    private View.OnClickListener listener;

    public AdaptadorProductos(ArrayList<Producto> listaProductos, Context context) {
        this.listaProductos = listaProductos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderProductos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, null, false);
        view.setOnClickListener(this);
        return new ViewHolderProductos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProductos holder, int position) {
        holder.nombreProducto.setText(listaProductos.get(position).getProducto());
        holder.descripcion.setText(listaProductos.get(position).getDescripcion());
        holder.pvp.setText(listaProductos.get(position).getPvp());
        holder.nombreProducto.setText(listaProductos.get(position).getProducto());
        if(listaProductos.get(position).getImagen() != null){
            Bitmap bmp = get_imagen(listaProductos.get(position).getImagen().toString());
            holder.foto.setImageBitmap(bmp);
        } else {
            holder.foto.setImageResource(R.drawable.cilindro);
        }
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
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

    public class ViewHolderProductos extends RecyclerView.ViewHolder {

        TextView nombreProducto, descripcion, pvp;
        ImageView foto;

        public ViewHolderProductos(@NonNull View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.idNombre);
            descripcion = itemView.findViewById(R.id.idDescripcion);
            pvp = itemView.findViewById(R.id.idPrecio);
            foto = itemView.findViewById(R.id.idImagen);
        }
    }

    private Bitmap get_imagen(String url) {
        Bitmap bm = null;
        try {
            URL _url = new URL(url);
            URLConnection con = _url.openConnection();
            con.connect();
            InputStream is = con.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {

        }
        return bm;
    }

}


