package com.utpl.distribuidoragas.cliente;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.util.Log;
import android.widget.Toast;

import com.utpl.distribuidoragas.Login;
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.api.SocketServer;
import com.utpl.distribuidoragas.api.Ubicaciones;
import com.utpl.distribuidoragas.databinding.ActivityMainBinding;
import com.utpl.distribuidoragas.entidades.NotificacionesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Socket mSocket;
    NotificacionesHelper notificacionesHelper;
    String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        reemplazarFragment(new InicioFragment());
        this.setTitle("DistriGAS");

        SharedPreferences session = this.getApplicationContext().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        idUsuario = session.getString("idUsuario", "-");

        try {
            mSocket = IO.socket(SocketServer.SOCKET_RUTA);
            mSocket.connect();
        } catch (URISyntaxException e) {
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.inicio:
                    reemplazarFragment(new InicioFragment());
                    break;
                case R.id.perfil:
                    reemplazarFragment(new PerfilFragment());
                    break;
                case R.id.pedidos:
                    reemplazarFragment(new PedidosFragment());
                    break;
                case R.id.historial:
                    reemplazarFragment(new HistorialFragment());
                    break;
                case R.id.salir:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("Está seguro que desea cerrar la sesión");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Si",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences.Editor editor = getSharedPreferences("sesion", MODE_PRIVATE).edit();
                                    editor.clear().apply();
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
                    break;
            }
            return true;
        });
        obtenerUbicacion();
        buscarNotificaciones();
    }

    private void obtenerUbicacion(){
        if (Build.VERSION.SDK_INT >= 23){
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
        }
        else{
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

    private void reemplazarFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Está seguro que desea cerrar la sesión");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = getSharedPreferences("sesion", MODE_PRIVATE).edit();
                        editor.clear().apply();
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

    //Funcion para revisar permisos de android
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
            Toast.makeText(getApplicationContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
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

    private void buscarNotificaciones(){
        mSocket.on("notiPedidoLlego/" + idUsuario, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showNotificacion("Entrega de Pedido", "Estimado cliente el pedido #" + messageJson.getString("idPedido") + " que solicitó acaba de llegar a su ubicación");
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
        mSocket.on("notiPedidoFinalizo/" + idUsuario, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showNotificacion("Finalizo el Pedido", "Estimado cliente el pedido #" + messageJson.getString("idPedido") + " ha finalizado. Gracias por preferirnos");
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

    private void showNotificacion(String titulo, String mensaje){
        notificacionesHelper = new NotificacionesHelper(this);
        NotificationCompat.Builder builder = notificacionesHelper.getNotification(titulo, mensaje);
        notificacionesHelper.getManager().notify(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

}