package com.utpl.distribuidoragas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

// Declaración de la clase PerfilFragment que extiende Fragment
public class PerfilFragment extends Fragment {

    // Declaración de variables de instancia
    View myView;
    private RequestQueue requestQueue;
    EditText nombres, apellidos, cedula, direccion, celular, email;
    Button guardarCambios;
    String usuarioBD, idUsuarioBD;

    // ES Este método es parte del ciclo de vida de un Fragmento en Android
    // y se llama cuando el fragmento necesita crear su interfaz de usuario.
    //Este método se utiliza para inflar y devolver la interfaz de usuario asociada
    // con el fragmento  Sobrescribe el método onCreateView()
          @Override
         public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla  el diseño definido en el archivo fragment_perfil.xml para crear la vista del fragmento
        //(convertir a una vista de Java) un archivo de diseño XML en objetos de vista de Android.
        // El tercer parámetro (false) indica que la vista no se debe adjuntar automáticamente al contenedor
        myView = inflater.inflate(R.layout.fragment_perfil, container, false);
        nombres = myView.findViewById(R.id.txtNombresPerfil);
        apellidos = myView.findViewById(R.id.txtApellidosPerfil);
        cedula = myView.findViewById(R.id.txtCedulaPerfil);
        // Deshabilita la edición del campo de cédula
        cedula.setEnabled(false);
        direccion = myView.findViewById(R.id.txtDireccionPerfil);
        celular = myView.findViewById(R.id.txtCelularPerfil);
        email = myView.findViewById(R.id.txtEmailPerfil);

        // Obtiene una referencia al botón de guardar cambios en el diseño
        guardarCambios = myView.findViewById(R.id.btnGuardarPerfil);

        // Establece un OnClickListener para manejar clics en el botón de guardar cambios
        guardarCambios.setOnClickListener(new View.OnClickListener() {

            // Cuando se hace clic en el botón, se llama al método guardarPerfil
            // con los valores actuales de los campos de entrada
            @Override
            public void onClick(View view)
            {guardarPerfil(nombres.getText().toString(), apellidos.getText().toString(), celular.getText().toString(), direccion.getText().toString(), email.getText().toString(), idUsuarioBD);
            }
        });
        // Obtiene una referencia a SharedPreferences llamado "sesion" con modo privado
        SharedPreferences session = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);

        // Asigna el valor asociado con la clave "nombres" a la vista 'nombres', y si no existe, asigna el valor por defecto "-"
        nombres.setText(session.getString("nombres", "-"));

        // Asigna el valor asociado con la clave "apellidos" a la vista 'apellidos', y si no existe, asigna el valor por defecto "-"
        apellidos.setText(session.getString("apellidos", "-"));

        // Asigna el valor asociado con la clave "usuario" a la vista 'cedula', y si no existe, asigna el valor por defecto "-"
        cedula.setText(session.getString("usuario", "-"));

        // Asigna el valor asociado con la clave "direccion" a la vista 'direccion', y si no existe, asigna el valor por defecto "-"
        direccion.setText(session.getString("direccion", "-"));

       // Asigna el valor asociado con la clave "email" a la vista 'email', y si no existe, asigna el valor por defecto "-"
        celular.setText(session.getString("celular", "-"));

        // Asigna el valor asociado con la clave "email" a la vista 'email', y si no existe, asigna el valor por defecto "-"
        email.setText(session.getString("email", "-"));

        // Asigna el valor asociado con la clave "usuario" a la variable 'usuarioBD', y si no existe, asigna el valor por defecto "-"
        usuarioBD = session.getString("usuario", "-");

        // Asigna el valor asociado con la clave "idUsuario" a la variable 'idUsuarioBD', y si no existe, asigna el valor por defecto "-"
        idUsuarioBD = session.getString("idUsuario", "-");

        // Devuelve la vista inflada para ser mostrada en el fragmento
        return myView;
    }
    // Método para enviar una solicitud POST al servidor para actualizar el perfil del usuario
    private void guardarPerfil(String nombres, String apellidos, String celular, String direccion, String email, String idUsuario) {
        // Crea una cola de solicitudes Volley
        RequestQueue queue = Volley.newRequestQueue(getContext());
        // Construye la URL completa para la solicitud POST
        String url = Api.API_RUTA + "usuarios/actPerfil";
        // Crea una solicitud de cadena POST usando StringReques

        // Implementa un escuchador para manejar la respuesta exitosa del servidor
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            Toast.makeText(getContext(), data.getString("mensaje"),Toast.LENGTH_LONG).show();
                            actualizarUsuario();
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
                        Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
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
                params.put("nombres", nombres);
                params.put("apellidos", apellidos);
                params.put("direccion", direccion);
                params.put("telefono", celular);
                params.put("email", email);
                params.put("idUsuario", idUsuario);
                return params;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        Limpiar();
    }

    private void actualizarUsuario() {
        SharedPreferences session = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        editor.putString("nombres", nombres.getText().toString());
        editor.putString("apellidos", apellidos.getText().toString());
        editor.putString("celular", celular.getText().toString());
        editor.putString("direccion", direccion.getText().toString());
        editor.commit();
    }

    protected void Limpiar () {
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }

}