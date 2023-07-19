package com.utpl.distribuidoragas.cliente;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.adaptadores.AdaptadorPedidos;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.api.SocketServer;
import com.utpl.distribuidoragas.api.Ubicaciones;
import com.utpl.distribuidoragas.entidades.Pedido;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

public class GenerarPedido extends AppCompatActivity {

    private RequestQueue requestQueue;
    private Socket mSocket;
    int idProducto;
    String nombreProducto, detalleProducto, imagenProducto, pvpProducto;
    TextView nombre, detalle, precio;
    ImageView imagen;
    Spinner spinnerCantidad;
    int cantidadEscogida = 0;
    Double pagarTotal = 0.00;
    StringBuilder MyString;
    Button escogerUbicacion, finalizarPedido;
    FrameLayout frameLayout;
    ImageView guardarUbi, cancelarUbi, checkUbi;
    String idUsuario, total;
    int controlUbicacion = 0;
    Double subtotal, iva;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_pedido);
        this.setTitle("Detalle del pedido");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idProducto = getIntent().getExtras().getInt("idProducto");
        nombreProducto = getIntent().getExtras().getString("nombre");
        detalleProducto = getIntent().getExtras().getString("descripcion");
        imagenProducto = getIntent().getExtras().getString("imagen");
        pvpProducto = getIntent().getExtras().getString("pvp");
        MyString = new StringBuilder(pvpProducto);
        MyString = MyString.deleteCharAt(0);

        SharedPreferences session = this.getApplicationContext().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        idUsuario = session.getString("idUsuario", "-");

        nombre = findViewById(R.id.txtNombreProductoDetalle);
        detalle = findViewById(R.id.txtDescripcionProductoDetalle);
        precio = findViewById(R.id.txtPrecioProductoDetalle);
        imagen = findViewById(R.id.imgPedidoDetalle);
        spinnerCantidad = findViewById(R.id.spinnerCantidad);
        escogerUbicacion = findViewById(R.id.btnUbicacionPedido);
        finalizarPedido = findViewById(R.id.btnFinalizarPedido);
        frameLayout = findViewById(R.id.frameMaps);
        guardarUbi = findViewById(R.id.imgGuardarUbi);
        cancelarUbi = findViewById(R.id.imgCancelarUbi);
        checkUbi = findViewById(R.id.imgCheckUbi);

        guardarUbi.setVisibility(View.INVISIBLE);
        cancelarUbi.setVisibility(View.INVISIBLE);
        frameLayout.setVisibility(View.INVISIBLE);

        guardarUbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarUbicacion();
                frameLayout.setVisibility(View.INVISIBLE);
                guardarUbi.setVisibility(View.INVISIBLE);
                cancelarUbi.setVisibility(View.INVISIBLE);
                escogerUbicacion.setVisibility(View.VISIBLE);
                finalizarPedido.setVisibility(View.VISIBLE);
                checkUbi.setImageResource(R.drawable.visto);
            }
        });

        cancelarUbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.INVISIBLE);
                guardarUbi.setVisibility(View.INVISIBLE);
                cancelarUbi.setVisibility(View.INVISIBLE);
                escogerUbicacion.setVisibility(View.VISIBLE);
                finalizarPedido.setVisibility(View.VISIBLE);
            }
        });

        spinnerCantidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                cantidadEscogida = Integer.parseInt(item.toString());
                pagarTotal = 0.00;
                MyString = new StringBuilder(MyString.toString().replace(",", "."));
                pagarTotal = Double.parseDouble(String.valueOf(MyString)) * cantidadEscogida;
                String precioTot = String.format( "%.2f", pagarTotal);
                precio.setText("$ " + precioTot);
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        escogerUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escogerUbicacion.setVisibility(View.INVISIBLE);
                finalizarPedido.setVisibility(View.INVISIBLE);
                reemplazarFragment(new MapsFragment());
                frameLayout.setVisibility(View.VISIBLE);
                guardarUbi.setVisibility(View.VISIBLE);
                cancelarUbi.setVisibility(View.VISIBLE);
            }
        });

        finalizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controlUbicacion != 0){
                    guardarPedido();
                } else {
                    Toast.makeText(getApplicationContext(),"Para continuar debe escoger su ubicación",Toast.LENGTH_LONG).show();
                }
            }
        });

        Bitmap bmp = get_imagen(imagenProducto);
        imagen.setImageBitmap(bmp);
        nombre.setText(nombreProducto);
        detalle.setText(detalleProducto);

    }

    private void guardarUbicacion(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Api.API_RUTA + "clientes/actualizarUbicacion";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), data.getString("mensaje"),Toast.LENGTH_LONG).show();
                            controlUbicacion = 1;
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
                params.put("lat", Ubicaciones.latitudeApp.toString());
                params.put("lng", Ubicaciones.longitudeApp.toString());
                params.put("idUsuario", idUsuario);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        Limpiar();
    }

    protected void Limpiar () {
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }

    private void reemplazarFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameMaps, fragment);
        fragmentTransaction.commit();
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

    private void guardarPedido(){
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String horaActual = new SimpleDateFormat("HH:mm:ss").format(new Date());
        total = precio.getText().toString();
        StringBuilder MyString = new StringBuilder(total);
        MyString = MyString.deleteCharAt(0);
        String myString = MyString.toString().replace(",", ".");
        total = String.valueOf(myString);
        iva = Double.parseDouble(total) - (Double.parseDouble(total) / 1.12);
        subtotal = (Double.parseDouble(total) / 1.12);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Api.API_RUTA + "pedidos/crearPedido";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            int idPedidoBD = data.getInt("data");
                            idPedidoBD = idPedidoBD + 1;
                            Toast.makeText(getApplicationContext(), data.getString("mensaje"),Toast.LENGTH_LONG).show();
                            mSocket = IO.socket(SocketServer.SOCKET_RUTA);
                            mSocket.connect();
                            JSONObject datos = new JSONObject();
                            datos.put("idCliente", idUsuario);
                            datos.put("idProducto", String.valueOf(idProducto));
                            datos.put("cantidad", String.valueOf(cantidadEscogida));
                            mSocket.emit("position", datos);
                            buscarRepartidor(idPedidoBD);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
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
                params.put("idCliente", idUsuario);
                params.put("fecha", fechaActual);
                params.put("hora", horaActual);
                params.put("total", total);
                params.put("subtotal", String.valueOf(subtotal));
                params.put("iva", String.valueOf(iva));
                params.put("idProducto", String.valueOf(idProducto));
                params.put("cantidad", String.valueOf(cantidadEscogida));
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        Limpiar();
    }

    private void buscarRepartidor(int idPedido){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Espere mientras se asigna un repartidor");
        dialog.show();
        String url = Api.API_RUTA + "pedidos/buscarRepartidores";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        float masCorta = 100000;
                        float resultado;
                        int idRepartidor = 0;
                        Double latRepartidor = 0.0;
                        Double lngRepartidor = 0.0;
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray json = data.optJSONArray("data");
                            for(int x=0; x<json.length();x++){
                                JSONObject jsonObject = null;
                                jsonObject = json.getJSONObject(x);
                                if(jsonObject.getDouble("lat") != 0) {
                                    Location locationA = new Location("Cliente");
                                    locationA.setLatitude(Ubicaciones.latitudeApp);
                                    locationA.setLongitude(Ubicaciones.longitudeApp);
                                    Location locationB = new Location("Repartidor");
                                    locationB.setLatitude(jsonObject.getDouble("lat"));
                                    locationB.setLongitude(jsonObject.getDouble("lng"));
                                    resultado = locationA.distanceTo(locationB);
                                    if(resultado < masCorta){
                                        masCorta = resultado;
                                        idRepartidor = jsonObject.getInt("id");
                                        latRepartidor = jsonObject.getDouble("lat");
                                        lngRepartidor = jsonObject.getDouble("lng");
                                    }
                                }
                            }
                            asignarRepartidor(idRepartidor, latRepartidor, lngRepartidor, idPedido);
                            dialog.hide();
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
                        dialog.hide();
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
                params.put("idCliente", idUsuario);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        Limpiar();
    }

    private void asignarRepartidor(int idRepartidor, Double latRepartidor, Double lngRepartidor, int idPedido){
        String url = Api.API_RUTA + "pedidos/asignarRepartidorAutomatico";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), data.getString("mensaje"),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                params.put("idPedido", String.valueOf(idPedido));
                params.put("idRepartidor", String.valueOf(idRepartidor));
                params.put("origenLat", String.valueOf(latRepartidor));
                params.put("origenLng", String.valueOf(lngRepartidor));
                params.put("destinoLat", String.valueOf(Ubicaciones.latitudeApp));
                params.put("destinoLng", String.valueOf(Ubicaciones.longitudeApp));
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        Limpiar();
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