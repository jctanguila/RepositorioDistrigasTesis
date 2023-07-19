package com.utpl.distribuidoragas.cliente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.api.Ubicaciones;

public class MapsFragment extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng ubicacionActual = new LatLng(Ubicaciones.latitudeApp, Ubicaciones.longitudeApp);
            Log.e("Mapa", ubicacionActual.toString());
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.casa);
            googleMap.addMarker(new MarkerOptions().position(ubicacionActual).title("Ubicacion actual").draggable(true).icon(icon));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacionActual));
            float zoomLevel = 18.0f; // Nivel de zoom deseado
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual, zoomLevel));
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDrag(@NonNull Marker marker) {}

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    LatLng punto = marker.getPosition();
                    double lati = marker.getPosition().latitude;
                    double longi = marker.getPosition().longitude;
                    Ubicaciones.latitudeApp = lati;
                    Ubicaciones.longitudeApp = longi;
                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {}

            });
        }

    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}