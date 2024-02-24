package com.utpl.distribuidoragas.administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ConfigNuevo extends AppCompatActivity {

    Button btnGuardar, btnVolver, btnLista;

    EditText textViewNombres, textViewEstado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_nuevo);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnVolver = findViewById(R.id.btnVover);
        btnLista = findViewById(R.id.btnlista);

        textViewNombres = findViewById(R.id.txtNombres);
        textViewEstado = findViewById(R.id.txtEstados);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivityAdministrador.class);
                startActivity(intent);
            }

        });

        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConfigLista.class);
                startActivity(intent);
            }

        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(ConfigNuevo.this);
                String url = Api.API_RUTA + "configs/registro";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject data = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), "ha sido creado",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivityAdministrador.class);
                                    startActivity(intent);
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
                        params.put("nombre", textViewNombres.getText().toString());
                        params.put("estado", textViewEstado.getText().toString());
                        return params;
                    }
                };
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


            }

        });
    }
}
