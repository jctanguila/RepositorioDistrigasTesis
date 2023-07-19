package com.utpl.distribuidoragas.administrador;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
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
import com.utpl.distribuidoragas.adaptadores.AdaptadorClientes;
import com.utpl.distribuidoragas.adaptadores.AdaptadorRepartidores;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.entidades.Cliente;
import com.utpl.distribuidoragas.entidades.Repartidor;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListaRepartidores extends AppCompatActivity {

    private RequestQueue requestQueue;
    RecyclerView recyclerRepartidores;
    ArrayList<Repartidor> listaRepartidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_repartidores);

        getSupportActionBar().setTitle("Lista de Repartidores");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        listaRepartidores = new ArrayList<>();
        recyclerRepartidores = findViewById(R.id.recycler_view_repartidores);
        recyclerRepartidores.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerRepartidores.setHasFixedSize(true);
        cargarRepartidores();

    }

    private void cargarRepartidores() {
        String url = Api.API_RUTA + "usuarios/listaRepartidores";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final Context context = getApplicationContext();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Repartidor repartidor = null;
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray json = data.optJSONArray("data");
                            for(int x=0; x<json.length();x++){
                                JSONObject jsonObject = null;
                                jsonObject = json.getJSONObject(x);
                                repartidor = new Repartidor(
                                        jsonObject.optString("id"),
                                        jsonObject.optString("nombres"),
                                        jsonObject.optString("apellidos"),
                                        jsonObject.optString("cedula"),
                                        jsonObject.optString("telefono"),
                                        jsonObject.optString("direccion"),
                                        jsonObject.optString("email"),
                                        jsonObject.optString("lat"),
                                        jsonObject.optString("lng")
                                );
                                listaRepartidores.add(repartidor);
                            }
                            AdaptadorRepartidores adaptadorRepartidores = new AdaptadorRepartidores(getApplicationContext(), listaRepartidores);
                            adaptadorRepartidores.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    eliminarRepartidor(listaRepartidores.get(recyclerRepartidores.getChildAdapterPosition(view)).getId());
                                }
                            });
                            recyclerRepartidores.setAdapter(adaptadorRepartidores);
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
                        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        Limpiar();
    }

    private void eliminarRepartidor(String idUsuario) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ListaRepartidores.this);
        builder1.setMessage("¿Estás seguro que deseas eliminar al Repartidor?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url = Api.API_RUTA + "usuarios/eliUsuarioRepartidor";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject data = new JSONObject(response);
                                            Toast.makeText(getApplicationContext(), data.getString("mensaje"),Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), MainActivityAdministrador.class);
                                            startActivity(intent);
                                            finish();
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
                        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                        Limpiar();
                    }
                });
        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    protected void Limpiar () {
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}