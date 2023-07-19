package com.utpl.distribuidoragas.administrador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.utpl.distribuidoragas.Login;
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class NuevoRepartidor extends AppCompatActivity {

    private RequestQueue requestQueue;
    Button registrar;
    EditText nombres, apellidos, cedula, direccion, celular, email, clave;
    Spinner spinnerRol;
    String seleccionRol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_repartidor);

        getSupportActionBar().setTitle("Nuevo Repartidor");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        nombres = findViewById(R.id.txtNombresRepartidor);
        apellidos = findViewById(R.id.txtApellidosRepartidor);
        cedula = findViewById(R.id.txtCedulaRepartidor);
        direccion = findViewById(R.id.txtDireccionRepartidor);
        celular = findViewById(R.id.txtTelefonoRepartidor);
        email = findViewById(R.id.txtEmailRepartidor);
        clave = findViewById(R.id.txtClaveRepartidor);
        spinnerRol = findViewById(R.id.spinnerRole);

        registrar = findViewById(R.id.btnRegistrarRepartidor);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar(nombres.getText().toString(), apellidos.getText().toString(), cedula.getText().toString(), direccion.getText().toString(), celular.getText().toString(), email.getText().toString(), clave.getText().toString())){
                    if(validadorDeCedula(cedula.getText().toString())){
                        registrarse(nombres.getText().toString(), apellidos.getText().toString(), cedula.getText().toString(), direccion.getText().toString(), celular.getText().toString(), email.getText().toString(), clave.getText().toString(), seleccionRol);
                    } else {
                        Toast.makeText(getApplicationContext(), "La cédula ingresada no es válida",Toast.LENGTH_LONG).show();
                        cedula.setText("");
                    }
                    //registrarse(nombres.getText().toString(), apellidos.getText().toString(), cedula.getText().toString(), direccion.getText().toString(), celular.getText().toString(), email.getText().toString(), clave.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Debe llenar todos los campos para continuar",Toast.LENGTH_LONG).show();
                }
            }
        });

        spinnerRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seleccionRol = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private boolean validar(String nombres, String apellidos, String cedula, String direccion, String celular, String email, String clave){
        if(!cedula.equals("") && !clave.equals("") && !nombres.equals("") && !apellidos.equals("") && !direccion.equals("") && !celular.equals("") && !email.equals("")){
            return true;
        }else{
            return false;
        }
    }

    private void registrarse(String nombres, String apellidos, String cedula, String direccion, String celular, String email, String clave, String rol){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Api.API_RUTA + "usuarios/registro";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), "El Usuario ha sido creado",Toast.LENGTH_LONG).show();
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
                params.put("nombres", nombres);
                params.put("apellidos", apellidos);
                params.put("cedula", cedula);
                params.put("direccion", direccion);
                params.put("telefono", celular);
                params.put("email", email);
                params.put("usuario", cedula);
                params.put("clave", clave);
                params.put("rol", rol);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        Limpiar();
    }

    public boolean validadorDeCedula(String cedula) {
        boolean cedulaCorrecta = false;
        try {
            if (cedula.length() == 10) // ConstantesApp.LongitudCedula
            {
                int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
                if (tercerDigito < 6) {
                    // Coeficientes de validación cédula
                    // El decimo digito se lo considera dígito verificador
                    int[] coefValCedula = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };
                    int verificador = Integer.parseInt(cedula.substring(9,10));
                    int suma = 0;
                    int digito = 0;
                    for (int i = 0; i < (cedula.length() - 1); i++) {
                        digito = Integer.parseInt(cedula.substring(i, i + 1))* coefValCedula[i];
                        suma += ((digito % 10) + (digito / 10));
                    }

                    if ((suma % 10 == 0) && (suma % 10 == verificador)) {
                        cedulaCorrecta = true;
                    }
                    else if ((10 - (suma % 10)) == verificador) {
                        cedulaCorrecta = true;
                    } else {
                        cedulaCorrecta = false;
                    }
                } else {
                    cedulaCorrecta = false;
                }
            } else {
                cedulaCorrecta = false;
            }
        } catch (NumberFormatException nfe) {
            cedulaCorrecta = false;
        } catch (Exception err) {
            System.out.println("Una excepcion ocurrio en el proceso de validadcion");
            cedulaCorrecta = false;
        }
        if (!cedulaCorrecta) {
            System.out.println("La Cédula ingresada es Incorrecta");
        }
        return cedulaCorrecta;
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