package com.utpl.distribuidoragas.repartidor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.utpl.distribuidoragas.Login;
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.adaptadores.AdaptadorPedidos;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.entidades.Pedido;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Pedidos extends AppCompatActivity {

    RecyclerView recyclerPedidos;
    ArrayList<Pedido> listaPedidos;
    ProgressDialog dialog;
    private RequestQueue requestQueue;
    String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        listaPedidos = new ArrayList<>();

        recyclerPedidos = findViewById(R.id.recyclerHistorialRepartidor);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(Pedidos.this));
        recyclerPedidos.setHasFixedSize(true);

        SharedPreferences session = this.getApplicationContext().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        idUsuario = session.getString("idUsuario", "-");

        cargarPedidos();

    }

    private void cargarPedidos() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Cargando Pedidos asignados al Repartidor");
        dialog.show();
        String url = Api.API_RUTA + "pedidos/pedidosRepartidor";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Pedido pedido = null;
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray json = data.optJSONArray("data");
                            for(int x=0; x<json.length();x++){
                                pedido = new Pedido();
                                JSONObject jsonObject = null;
                                jsonObject = json.getJSONObject(x);
                                pedido.setId(jsonObject.optInt("id"));
                                pedido.setNombreProducto(jsonObject.optString("producto"));
                                pedido.setDescripcion(jsonObject.optString("descripcion"));
                                String precioPublico = String.format( "%.2f", jsonObject.optDouble("total"));
                                pedido.setTotal("$ " + precioPublico);
                                pedido.setEstado(jsonObject.optString("estado"));
                                pedido.setFecha(jsonObject.optString("fecha") + " : " + jsonObject.optString("hora"));
                                pedido.setImagen(Api.API_RUTA_IMG + jsonObject.optString("imagen"));
                                listaPedidos.add(pedido);
                            }
                            dialog.hide();
                            AdaptadorPedidos adaptadorPedidos = new AdaptadorPedidos(listaPedidos, Pedidos.this);
                            adaptadorPedidos.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getEstado().equals("ASIGNADO")){
                                        Intent intent = new Intent(getApplicationContext(), DetallePedido.class);
                                        intent.putExtra("id", listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getId());
                                        intent.putExtra("nombre", listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getNombreProducto());
                                        intent.putExtra("descripcion", listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getDescripcion());
                                        intent.putExtra("pvp", listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getTotal());
                                        intent.putExtra("imagen", listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getImagen());
                                        startActivity(intent);
                                    } else if(listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getEstado().equals("FINALIZADO")) {
                                        Toast.makeText(getApplicationContext(), "El pedido ya finalizó, ya no se puede acceder a él",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            recyclerPedidos.setAdapter(adaptadorPedidos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode == 401) {
                    String responseBody = null;
                    try {
                        responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject data = new JSONObject(responseBody);
                        String message = data.optString("mensaje");
                        dialog.hide();
                        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        })
        {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idUsuario", idUsuario);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        Limpiar();
    }

    protected void Limpiar () {
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return false;
    }

    @Override
    public void onBackPressed() {

    }

}