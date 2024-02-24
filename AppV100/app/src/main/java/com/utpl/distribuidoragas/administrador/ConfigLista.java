package com.utpl.distribuidoragas.administrador;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.adaptadores.AdaptadorClientes;
import com.utpl.distribuidoragas.adaptadores.AdaptadorConfigs;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.entidades.Cliente;
import com.utpl.distribuidoragas.entidades.ConfigEntidad;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfigLista extends AppCompatActivity {

    private RequestQueue requestQueue;
    RecyclerView recyclerConfig;
    ArrayList<ConfigEntidad> listaConfig;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_lista);

        getSupportActionBar().setTitle("Lista de Configuraciones");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        listaConfig=new ArrayList<>();
        recyclerConfig = findViewById(R.id.recycler_view_config);
        recyclerConfig.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerConfig.setHasFixedSize(true);
        cargarConfig();
    }
    private void cargarConfig() {
        String url = Api.API_RUTA + "configs/listaConfigs";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final Context context = getApplicationContext();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ConfigEntidad configEntidad = null;
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray json = data.optJSONArray("data");
                            for(int x=0; x<json.length();x++){
                                JSONObject jsonObject = null;
                                jsonObject = json.getJSONObject(x);
                                configEntidad = new ConfigEntidad(
                                        jsonObject.optString("id"),
                                        jsonObject.optString("Nombres"),
                                        jsonObject.optString("Estado")
                                );
                                listaConfig.add(configEntidad);
                            }
                            AdaptadorConfigs adaptadorConfigs = new AdaptadorConfigs(getApplicationContext(), listaConfig);
                            adaptadorConfigs.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    eliminarConfig(listaConfig.get(recyclerConfig.getChildAdapterPosition(view)).getId());
                                }
                            });
                            recyclerConfig.setAdapter(adaptadorConfigs);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
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
        //Limpiar();
    }
    private void eliminarConfig(String idConfig) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ConfigLista.this);
        builder1.setMessage("¿Estás seguro que deseas eliminar esta Configuracion?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url = Api.API_RUTA + "configs/eliConfig";
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
                                params.put("idConfig", idConfig);
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                        //Limpiar();
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
}
