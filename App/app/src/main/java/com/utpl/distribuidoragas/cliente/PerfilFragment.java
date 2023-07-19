package com.utpl.distribuidoragas.cliente;

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
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PerfilFragment extends Fragment {

    View myView;
    private RequestQueue requestQueue;
    EditText nombres, apellidos, cedula, direccion, celular, email;
    Button guardarCambios;
    String usuarioBD, idUsuarioBD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_perfil, container, false);
        nombres = myView.findViewById(R.id.txtNombresPerfil);
        apellidos = myView.findViewById(R.id.txtApellidosPerfil);
        cedula = myView.findViewById(R.id.txtCedulaPerfil);
        cedula.setEnabled(false);
        direccion = myView.findViewById(R.id.txtDireccionPerfil);
        celular = myView.findViewById(R.id.txtCelularPerfil);
        email = myView.findViewById(R.id.txtEmailPerfil);

        guardarCambios = myView.findViewById(R.id.btnGuardarPerfil);
        guardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarPerfil(nombres.getText().toString(), apellidos.getText().toString(), celular.getText().toString(), direccion.getText().toString(), email.getText().toString(), idUsuarioBD);
            }
        });

        SharedPreferences session = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        nombres.setText(session.getString("nombres", "-"));
        apellidos.setText(session.getString("apellidos", "-"));
        cedula.setText(session.getString("usuario", "-"));
        direccion.setText(session.getString("direccion", "-"));
        celular.setText(session.getString("celular", "-"));
        email.setText(session.getString("email", "-"));
        usuarioBD = session.getString("usuario", "-");
        idUsuarioBD = session.getString("idUsuario", "-");
        return myView;
    }

    private void guardarPerfil(String nombres, String apellidos, String celular, String direccion, String email, String idUsuario) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Api.API_RUTA + "usuarios/actPerfil";
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