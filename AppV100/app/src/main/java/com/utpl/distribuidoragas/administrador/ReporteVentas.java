package com.utpl.distribuidoragas.administrador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.adaptadores.AdaptadorRepartidores;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.entidades.Repartidor;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ReporteVentas extends AppCompatActivity {

    private RequestQueue requestQueue;
    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_ventas);

        getSupportActionBar().setTitle("Reporte de Ventas");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        table = findViewById(R.id.table);

        cargarReporte();

    }

    private void cargarReporte() {
        String url = Api.API_RUTA + "reporte/reporteVentas";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final Context context = getApplicationContext();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("datos", response);
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray json = data.optJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject venta = json.getJSONObject(i);

                                TableRow row = new TableRow(ReporteVentas.this);
                                row.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                                String[] campos = new String[]{
                                        venta.getString("cedula"),
                                        venta.getString("apellidos"),
                                        venta.getString("nombres"),
                                        venta.getString("fecha"),
                                        venta.getString("hora"),
                                        String.valueOf(venta.getDouble("subtotal")),
                                        String.valueOf(venta.getDouble("iva")),
                                        String.valueOf(venta.getDouble("total"))
                                };

                                for (String campo : campos) {
                                    TextView tv = new TextView(ReporteVentas.this);
                                    tv.setLayoutParams(new TableRow.LayoutParams(
                                            TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    tv.setPadding(5, 5, 5, 5);
                                    tv.setText(campo);
                                    row.addView(tv);
                                }

                                table.addView(row);
                            }
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
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        Limpiar();
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