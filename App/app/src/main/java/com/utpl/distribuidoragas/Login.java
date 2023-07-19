package com.utpl.distribuidoragas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.utpl.distribuidoragas.administrador.MainActivityAdministrador;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.cliente.MainActivity;
import com.utpl.distribuidoragas.entidades.VolleySingleton;
import com.utpl.distribuidoragas.repartidor.MainActivityRepartidor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private RequestQueue requestQueue;
    EditText usuario, clave;
    Button ingresar, registrarse;
    TextView recuperar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("Login");

        usuario = findViewById(R.id.txtUsuario);
        clave = findViewById(R.id.txtClave);
        ingresar = findViewById(R.id.btnLogin);
        registrarse = findViewById(R.id.btnRegistro);
        recuperar = findViewById(R.id.btnIrRecuperar);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar(usuario.getText().toString(), clave.getText().toString())){
                    iniciarSesion(usuario.getText().toString(), clave.getText().toString());
                }else{
                    Toast.makeText(getApplicationContext(), "Debe llenar los dos campos para continuar",Toast.LENGTH_LONG).show();
                }
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), registrar.class);
                startActivity(intent);
            }
        });

        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecuperarClave.class);
                startActivity(intent);
            }
        });

    }

    private boolean validar(String cedula, String clave){
        if(!cedula.equals("") && !clave.equals("")){
            return true;
        }else{
            return false;
        }
    }

    private void iniciarSesion(String usuario, String clave){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Api.API_RUTA + "login";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONObject usuarioBD = new JSONObject(String.valueOf(data.getJSONObject("data")));
                            if(!usuarioBD.getString("rol").toString().equals("Administrador")){
                                Toast.makeText(getApplicationContext(), data.getString("mensaje"),Toast.LENGTH_LONG).show();
                            }
                            guardarSesion(usuarioBD.getString("cedula"), clave, usuarioBD.getString("nombres"), usuarioBD.getString("apellidos"), usuarioBD.getString("direccion"),usuarioBD.getString("telefono"),usuarioBD.getString("email"), usuario, usuarioBD.getString("idUsuario"), usuarioBD.getString("rol"));
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
               params.put("usuario", usuario);
               params.put("clave", clave);
               return params;
           }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        Limpiar();
    }

    private void guardarSesion(String cedula, String clave, String nombres, String apellidos, String direccion, String celular, String email, String usuario, String idUsuario, String rol){
        if(rol.equals("Cliente")){
            SharedPreferences session = getSharedPreferences("sesion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = session.edit();
            editor.putString("usuario", cedula);
            editor.putString("clave", clave);
            editor.putString("nombres", nombres);
            editor.putString("apellidos", apellidos);
            editor.putString("email", email);
            editor.putString("celular", celular);
            editor.putString("direccion", direccion);
            editor.putString("usuario", usuario);
            editor.putString("idUsuario", idUsuario);
            editor.putString("rol", rol);
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if(rol.equals("Repartidor")) {
            SharedPreferences session = getSharedPreferences("sesion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = session.edit();
            editor.putString("usuario", cedula);
            editor.putString("clave", clave);
            editor.putString("nombres", nombres);
            editor.putString("apellidos", apellidos);
            editor.putString("email", email);
            editor.putString("celular", celular);
            editor.putString("direccion", direccion);
            editor.putString("usuario", usuario);
            editor.putString("idUsuario", idUsuario);
            editor.putString("rol", rol);
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), MainActivityRepartidor.class);
            startActivity(intent);
        } else if(rol.equals("Administrador")) {
            SharedPreferences session = getSharedPreferences("sesion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = session.edit();
            editor.putString("usuario", cedula);
            editor.putString("clave", clave);
            editor.putString("nombres", nombres);
            editor.putString("apellidos", apellidos);
            editor.putString("email", email);
            editor.putString("celular", celular);
            editor.putString("direccion", direccion);
            editor.putString("usuario", usuario);
            editor.putString("idUsuario", idUsuario);
            editor.putString("rol", rol);
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), MainActivityAdministrador.class);
            startActivity(intent);
        }
    }

    protected void Limpiar () {
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }

    @Override
    public void onBackPressed() {}

}