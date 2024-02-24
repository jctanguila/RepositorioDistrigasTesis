package com.utpl.distribuidoragas.repartidor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.utpl.distribuidoragas.api.SocketServer;
import com.utpl.distribuidoragas.entidades.NotificacionesHelper;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

public class DetallePedido extends AppCompatActivity {

    int idPedido;
    String nombreProducto, detalleProducto, imagenProducto, pvpProducto, idUsuario, idUsuarioCliente;
    Double latCliente, lngCliente;
    StringBuilder MyString;
    ImageView imagen;
    TextView nombre, detalle, total;
    Button mapaCliente, finalizarPedido, notificarCliente;
    NotificacionesHelper notificacionesHelper;
    private Socket mSocket;
    private RequestQueue requestQueue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idPedido = getIntent().getExtras().getInt("id");
        nombreProducto = getIntent().getExtras().getString("nombre");
        detalleProducto = getIntent().getExtras().getString("descripcion");
        imagenProducto = getIntent().getExtras().getString("imagen");
        pvpProducto = getIntent().getExtras().getString("pvp");
        MyString = new StringBuilder(pvpProducto);
        MyString = MyString.deleteCharAt(0);

        buscarPedido(String.valueOf(idPedido));

        SharedPreferences session = this.getApplicationContext().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        idUsuario = session.getString("idUsuario", "-");

        imagen = findViewById(R.id.imgDetallePedidoRepartidor);
        nombre = findViewById(R.id.txtNombreDetalleRepartidor);
        detalle = findViewById(R.id.txtDescripcionDetalleRepartidor);
        total = findViewById(R.id.txtTotalDetalleRepartidor);
        mapaCliente = findViewById(R.id.btnUbicacionDetalleRepartidor);
        finalizarPedido = findViewById(R.id.btnFinalizarDetalleRepartidor);
        notificarCliente = findViewById(R.id.btnNotificarDetalleRepartidor);

        Bitmap bmp = get_imagen(imagenProducto);
        imagen.setImageBitmap(bmp);
        nombre.setText(nombreProducto);
        detalle.setText(detalleProducto);
        total.setText("Total a pagar: $ " + MyString);

        try {
            mSocket = IO.socket(SocketServer.SOCKET_RUTA);
            mSocket.connect();
        } catch (URISyntaxException e) {
        }

        mapaCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsRepartidor.class);
                intent.putExtra("id", idPedido);
                intent.putExtra("latCliente", latCliente);
                intent.putExtra("lngCliente", lngCliente);
                startActivity(intent);
            }
        });

        finalizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DetallePedido.this);
                builder1.setMessage("Está seguro que desea finalizar el Pedido");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Si",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finalizarPedido(String.valueOf(idPedido));
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
        });

        notificarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject datos = new JSONObject();
                try {
                    datos.put("idCliente", idUsuarioCliente);
                    datos.put("idPedido", idPedido);
                    mSocket.emit("pedidoFinalizado", datos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void finalizarPedidoSocket(){
        JSONObject datos = new JSONObject();
        try {
            datos.put("idCliente", idUsuarioCliente);
            datos.put("idPedido", idPedido);
            mSocket.emit("pedidoFinalizado", datos);
            mSocket.disconnect();
            Intent intent = new Intent(getApplicationContext(), MainActivityRepartidor.class);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void buscarPedido(String idPedido) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Api.API_RUTA + "pedidos/buscarUbicacionesPedido";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONObject ubicacionesCliente = new JSONObject(String.valueOf(data.getJSONObject("data")));
                            latCliente = ubicacionesCliente.getDouble("lat");
                            lngCliente = ubicacionesCliente.getDouble("lng");
                            idUsuarioCliente = ubicacionesCliente.getString("idUsuario");
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
                params.put("idPedido", idPedido);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        Limpiar();
    }

    private void finalizarPedido(String idPedido) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Api.API_RUTA + "pedidos/finalizarPedido";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), data.getString("mensaje"),Toast.LENGTH_LONG).show();
                            finalizarPedidoSocket();
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
                params.put("idPedido", idPedido);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        Limpiar();
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