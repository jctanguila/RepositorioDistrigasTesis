package com.utpl.distribuidoragas.repartidor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.repartidor.MainActivityRepartidor;

public class PerfilRepartidor extends AppCompatActivity {

    Button volver;
    EditText nombres, apellidos, cedula, email, celular, direccion;
    String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_repartidor);
        this.setTitle("Perfil");

        nombres = findViewById(R.id.txtNombresPerfilRepartidor);
        apellidos = findViewById(R.id.txtApellidosPerfilRepartidor);
        cedula = findViewById(R.id.txtCedulaPerfilRepartidor);
        email = findViewById(R.id.txtEmailPerfilRepartidor);
        celular = findViewById(R.id.txtCelularPerfilRepartidor);
        direccion = findViewById(R.id.txtDireccionPerfilRepartidor);

        SharedPreferences session = this.getApplicationContext().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        nombres.setText(session.getString("nombres", "-"));
        apellidos.setText(session.getString("apellidos", "-"));
        cedula.setText(session.getString("usuario", "-"));
        direccion.setText(session.getString("direccion", "-"));
        celular.setText(session.getString("celular", "-"));
        email.setText(session.getString("email", "-"));
        idUsuario = session.getString("idUsuario", "-");

        volver = findViewById(R.id.btnVolverPerfilRepartidor);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivityRepartidor.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {}

}