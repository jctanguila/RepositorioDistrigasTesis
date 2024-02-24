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
    protected void onCreate(Bundle savedInstanceState) {//se crea la vista
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
        //VALIDA QUE LOS CAMPOS NO ESTEN VACIOS
        if(!cedula.equals("") && !clave.equals("")){
            return true;
        }else{
            return false;
        }
    }

    private void iniciarSesion(String usuario, String clave){//Funcion para login

        // Crea una cola de solicitudes HTTP usando Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        // Construye la URL para la solicitud POST a la API de inicio de sesión
        String url = Api.API_RUTA + "login"; //construye una URL concatenando la constante `API_RUTA` con la cadena "login"
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                // Crea una solicitud de cadena (StringRequest) para una solicitud HTTP POST

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parsea analizar una cadena de texto para identificar su estructura sintáctica y extraer información significativa de ella) la respuesta JSON recibida
                            JSONObject data = new JSONObject(response);//Convertir la respuesta recibida del servidor

                            JSONObject usuarioBD = new JSONObject(String.valueOf(data.getJSONObject("data")));//contiene la respuesta del servidor en data
                            // Comprueba si el usuario no es un administrador y muestra un mensaje si no lo es

                                Toast.makeText(getApplicationContext(), data.getString("mensaje"),Toast.LENGTH_LONG).show();

                            // Llama a la función guardarSesion para guardar los datos de sesión
                            guardarSesion(usuarioBD.getString("cedula"), clave, usuarioBD.getString("nombres"), usuarioBD.getString("apellidos"), usuarioBD.getString("direccion"),usuarioBD.getString("telefono"),usuarioBD.getString("email"), usuario, usuarioBD.getString("idUsuario"), usuarioBD.getString("rol"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode == 401) {
                    // Verifica si el código de respuesta de la red es 401 (error de autenticacion

                    String responseBody = null;
                    // Inicializa una variable para almacenar la respuesta de la red

                    try {

                        responseBody = new String(error.networkResponse.data, "utf-8");
                        // Convierte los datos de la respuesta en una cadena utilizando el conjunto de caracteres UTF-8

                        JSONObject data = new JSONObject(responseBody);
                        // Parsea la respuesta como un objeto JSON

                        String message = data.optString("mensaje");
                        // Muestra un mensaje Toast con el mensaje obtenido del servidor
                        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG).show();

                        //manejo de excepciones
                    } catch (UnsupportedEncodingException e) {// linea 31
                        e.printStackTrace();
                    } catch (JSONException e) {// 134 en adelante
                        e.printStackTrace();
                    }
                }
            }
        })
        {
           protected Map<String, String> getParams() { // Define un método llamado getParams() que devuelve un mapa de pares clave-valor (String, String)
               // Crea un nuevo objeto HashMap para almacenar los parámetros

               Map<String, String> params = new HashMap<>();
               // Agrega pares clave-valor al mapa params

               params.put("usuario", usuario);
               // La clave es "usuario" y el valor es el valor de la variable usuario

               params.put("clave", clave);
               // La clave es "clave" y el valor es el valor de la variable clave

               return params;
               // Devuelve el mapa params con los parámetros configurados
           }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);// realiza la consulta
        // Agrega la solicitud a la cola de solicitudes usando VolleySingleton

        Limpiar();
        // Llama a la función Limpiar() que se describe previamente
    }

    private void guardarSesion(String cedula, String clave, String nombres, String apellidos, String direccion, String celular, String email, String usuario, String idUsuario, String rol){
        if(rol.equals("Cliente")){  // Comprueba si el rol del usuario es "Cliente"
            SharedPreferences session = getSharedPreferences("sesion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = session.edit();
            // Obtiene un editor para las preferencias compartidas "sesion"

            editor.putString("usuario", cedula);
            editor.putString("clave", clave);
            editor.putString("nombres", nombres);editor.putString("apellidos", apellidos);
            editor.putString("email", email);
            editor.putString("celular", celular);
            editor.putString("direccion", direccion);
            editor.putString("usuario", usuario);
            editor.putString("idUsuario", idUsuario);
            editor.putString("rol", rol);
            // Almacena los datos de la sesión del usuario en las preferencias compartidas

            editor.commit();
            // Aplica los cambios en las preferencias compartidas

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            // Inicia una nueva actividad (probablemente la actividad principal) después de guardar la sesión

        } else if(rol.equals("Repartidor")) {
            // Comprueba si el rol del usuario es "Repartidor"

            SharedPreferences session = getSharedPreferences("sesion", Context.MODE_PRIVATE);
            // Obtiene una instancia de SharedPreferences llamada "sesion" con modo privado

            SharedPreferences.Editor editor = session.edit();
            // Obtiene un editor para las preferencias compartidas "sesion"

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
            // Almacena los datos de la sesión del usuario en las preferencias compartidas

            Intent intent = new Intent(getApplicationContext(), MainActivityRepartidor.class);
            startActivity(intent);
            // Inicia una nueva actividad (MainActivityRepartidor) después de guardar la sesión

        } else if(rol.equals("Administrador")) {
            // Comprueba si el rol del usuario es "Administrador"

            SharedPreferences session = getSharedPreferences("sesion", Context.MODE_PRIVATE);
            // Obtiene una instancia de SharedPreferences llamada "sesion" con modo privado

            SharedPreferences.Editor editor = session.edit();
            // Obtiene un editor para las preferencias compartidas "sesion"

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
            // Almacena los datos de la sesión del usuario en las preferencias compartidas

            editor.commit();
            // Aplica los cambios en las preferencias compartidas

            Intent intent = new Intent(getApplicationContext(), MainActivityAdministrador.class);
            startActivity(intent);
            // Inicia una nueva actividad (MainActivityAdministrador) después de guardar la sesión
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