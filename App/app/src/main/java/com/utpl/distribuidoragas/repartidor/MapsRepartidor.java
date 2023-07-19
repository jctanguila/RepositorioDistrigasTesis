package com.utpl.distribuidoragas.repartidor;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.api.Ubicaciones;
import com.utpl.distribuidoragas.databinding.ActivityMapsRepartidorBinding;
import com.utpl.distribuidoragas.entidades.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsRepartidor extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsRepartidorBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1;
    ProgressDialog progressDialog;
    Double latCliente, lngCliente;
    int idPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsRepartidorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapRepartidor);
        mapFragment.getMapAsync(this);

    }

    private void drawPolylines(LatLng origen, LatLng destino) {
        progressDialog = new ProgressDialog(MapsRepartidor.this);
        progressDialog.setMessage("Espere, Mientras se traza la ruta hacia el Cliente.");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = getDirectionsUrl(origen, destino);
        Log.d("url", url + "");
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = new ArrayList<LatLng>();;
            PolylineOptions lineOptions = new PolylineOptions();;
            lineOptions.width(10);
            lineOptions.color(Color.BLUE);
            MarkerOptions markerOptions = new MarkerOptions();
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);

            }
            mMap.addPolyline(lineOptions);
            progressDialog.hide();
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters +"&key=" +"AIzaSyD78RUY7KyrZzZ-aW1bZ2GU0u8HeUedO2Y";
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
            Log.d("data", data);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        obtenerUbicacion();
        idPedido = getIntent().getExtras().getInt("id");
        latCliente = getIntent().getExtras().getDouble("latCliente");
        lngCliente = getIntent().getExtras().getDouble("lngCliente");
        LatLng ubicacionActual = new LatLng(Ubicaciones.latitudeApp, Ubicaciones.longitudeApp);
        LatLng ubicacionCasa = new LatLng(latCliente, lngCliente);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.cilindromapa);
        BitmapDescriptor iconCasa = BitmapDescriptorFactory.fromResource(R.drawable.casa);
        mMap.addMarker(new MarkerOptions().position(ubicacionActual).title("Ubicacion actual").draggable(false).icon(icon));
        mMap.addMarker(new MarkerOptions().position(ubicacionCasa).title("Cliente").draggable(false).icon(iconCasa));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacionActual));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacionActual));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), null);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        drawPolylines(ubicacionActual, ubicacionCasa);
    }

    private void obtenerUbicacion(){
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

}