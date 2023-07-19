package com.utpl.distribuidoragas.administrador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.utpl.distribuidoragas.Login;
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.repartidor.Pedidos;

public class MainActivityAdministrador extends AppCompatActivity {

    Button listaClientes, crearRepartidor, listaRepartidores, reporte, salir;
    String usuario, idUsuario;
    TextView usuarioSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_administrador);

        SharedPreferences session = this.getApplicationContext().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        usuario = session.getString("nombres", "-") + " " + session.getString("apellidos", "-");
        idUsuario = session.getString("idUsuario", "-");
        usuarioSesion = findViewById(R.id.txtUsuarioAdmin);
        usuarioSesion.setText(usuario);

        listaClientes = findViewById(R.id.btnClientesAdmin);
        listaClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListaClientes.class);
                startActivity(intent);
            }
        });

        crearRepartidor = findViewById(R.id.btnNuevoAdmin);
        crearRepartidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NuevoRepartidor.class);
                startActivity(intent);
            }
        });

        listaRepartidores = findViewById(R.id.btnRepartidoresAdmin);
        listaRepartidores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListaRepartidores.class);
                startActivity(intent);
            }
        });

        reporte = findViewById(R.id.btnReporteAdmin);
        reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReporteVentas.class);
                startActivity(intent);
            }
        });

        salir = findViewById(R.id.btnSalirAdmin);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("sesion", MODE_PRIVATE).edit();
                editor.clear().apply();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });


    }
}