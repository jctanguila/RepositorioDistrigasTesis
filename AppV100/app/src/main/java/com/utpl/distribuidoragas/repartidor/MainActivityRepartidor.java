package com.utpl.distribuidoragas.repartidor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.utpl.distribuidoragas.api.Ubicaciones;
import com.utpl.distribuidoragas.cliente.MainActivity;
import com.utpl.distribuidoragas.entidades.NotificacionesHelper;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivityRepartidor extends AppCompatActivity {

    String usuario, idUsuario;
    TextView usuarioSesion;
    ImageView salir, perfil, pedidos, activarMapa;
    private Socket mSocket;
    int banderaCompartirUbicacion = 0;
    Handler handler;
    NotificacionesHelper notificacionesHelper;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_repartidor);
        this.setTitle("Menú Principal");

        SharedPreferences session = this.getApplicationContext().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        usuario = session.getString("nombres", "-") + " " + session.getString("apellidos", "-");
        idUsuario = session.getString("idUsuario", "-");
        usuarioSesion = findViewById(R.id.txtUsuarioRepartidor);
        usuarioSesion.setText(usuario);

        salir = findViewById(R.id.imgSalirRepartidor);
        perfil = findViewById(R.id.imgPerfilRepartidor);
        activarMapa = findViewById(R.id.imgMapaRepartidor);
        pedidos = findViewById(R.id.imgPedidosRepartidor);

        obtenerUbicacionActual();

        try {
            mSocket = IO.socket(SocketServer.SOCKET_RUTA);
            mSocket.connect();
            buscarNotificaciones();
        } catch (URISyntaxException e) {
        }

        activarMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banderaCompartirUbicacion == 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivityRepartidor.this);
                    builder1.setMessage("Desea compartir su ubicación");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Si",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    compartirUbicacionActual();
                                    guardarUbicacionActual();
                                    Toast.makeText(getApplicationContext(), "Se ha compartido la ubicación del repartidor", Toast.LENGTH_LONG).show();
                                    handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            compartirUbicacionActual();
                                            guardarUbicacionActual();
                                            handler.postDelayed(this,10000);
                                        }
                                    },5000);
                                    activarMapa.setImageResource(R.drawable.cancelar);
                                    banderaCompartirUbicacion = 1;
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
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivityRepartidor.this);
                    builder1.setMessage("Desea dejar de compartir su ubicación");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Si",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    handler.removeMessages(0);
                                    banderaCompartirUbicacion = 0;
                                    activarMapa.setImageResource(R.drawable.maps);
                                    JSONObject datos = new JSONObject();
                                    try {
                                        datos.put("id", idUsuario);
                                        datos.put("lat", 0);
                                        datos.put("lng", 0);
                                        mSocket.emit("ubicacionRepartidor", datos);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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
        });

        pedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Pedidos.class);
                startActivity(intent);
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PerfilRepartidor.class);
                startActivity(intent);
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivityRepartidor.this);
                builder1.setMessage("Está seguro que desea cerrar la sesión");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Si",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences.Editor editor = getSharedPreferences("sesion", MODE_PRIVATE).edit();
                                editor.clear().apply();
                                mSocket.disconnect();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
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
    }

    private void obtenerUbicacionActual(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                LocationManager locationManager = (LocationManager)
                        getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if(location != null){
                    Ubicaciones.latitudeApp = location.getLatitude();
                    Ubicaciones.longitudeApp = location.getLongitude();
                }
            } else {
                requestPermission();
            }
        } else {
            LocationManager locationManager = (LocationManager)
                    getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if(location != null){
                Ubicaciones.latitudeApp = location.getLatitude();
                Ubicaciones.longitudeApp = location.getLongitude();
            }
        }
    }

    private void compartirUbicacionActual() {
        obtenerUbicacionActual();
        JSONObject datos = new JSONObject();
        try {
            datos.put("id", idUsuario);
            datos.put("lat", Ubicaciones.latitudeApp);
            datos.put("lng", Ubicaciones.longitudeApp);
            mSocket.emit("ubicacionRepartidor", datos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void guardarUbicacionActual(){
        String url = Api.API_RUTA + "pedidos/actualizarUbicacionRepartidor";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), data.getString("mensaje"),Toast.LENGTH_LONG).show();
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
                params.put("idUsuario", String.valueOf(idUsuario));
                params.put("lat", String.valueOf(Ubicaciones.latitudeApp));
                params.put("lng", String.valueOf(Ubicaciones.longitudeApp));
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        Limpiar();
    }

    private void buscarNotificaciones(){
        mSocket.on("notiPedidoAsignado/" + idUsuario, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showNotificacion("Nuevo Pedido", "El pedido #" + messageJson.getString("id_Pedido") + " te ha sido asignado, revisar la lista de pedidos");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Function for notifications
    private void showNotificacion(String titulo, String mensaje){
        notificacionesHelper = new NotificacionesHelper(this);
        NotificationCompat.Builder builder = notificacionesHelper.getNotification(titulo, mensaje);
        notificacionesHelper.getManager().notify(1, builder.build());
    }

    //Function para revisar permisos de android
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    //*

    //Funcion para requerir el permiso al usuario
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(getApplicationContext(), "Es necesario usar el GPS. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }
    //*

    //Funcion para guardar la desicion del usuario sobre el permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use GPS .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use GPS .");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        handler.removeMessages(0);
    }

    protected void Limpiar () {
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }

}