package com.utpl.distribuidoragas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InicioFragment extends Fragment {

    View myView;
    String usuario;
    TextView usuarioSesion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_inicio, container, false);
        SharedPreferences session = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        usuario = session.getString("nombres", "-") + " " + session.getString("apellidos", "-");
        usuarioSesion = myView.findViewById(R.id.txtUsuarioSesion);
        usuarioSesion.setText(usuario);
        return myView;
    }
}