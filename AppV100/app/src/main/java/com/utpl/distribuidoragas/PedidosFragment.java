package com.utpl.distribuidoragas;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.utpl.distribuidoragas.adaptadores.AdaptadorProductos;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.entidades.Producto;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class PedidosFragment extends Fragment {

    RecyclerView recyclerProductos;
    ArrayList<Producto> listaProductos;
    ProgressDialog dialog;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View vista = inflater.inflate(R.layout.fragment_pedidos, container, false);

        listaProductos = new ArrayList<>();

        recyclerProductos = vista.findViewById(R.id.recyclerProductos);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerProductos.setHasFixedSize(true);
        cargarProductos();

        return vista;
    }

    private void cargarProductos() {
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Cargando Productos");
        dialog.show();
        String url = Api.API_RUTA + "productos/listaProductos";
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Producto producto = null;
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray json = data.optJSONArray("data");
                            for(int x=0; x<json.length();x++){
                                producto = new Producto();
                                JSONObject jsonObject = null;
                                jsonObject = json.getJSONObject(x);
                                producto.setId(jsonObject.optInt("id"));
                                producto.setProducto(jsonObject.optString("producto"));
                                producto.setDescripcion(jsonObject.optString("descripcion"));
                                producto.setCosto(jsonObject.optString("costo"));
                                producto.setPvp("$ " + jsonObject.optString("pvp"));
                                producto.setImagen(Api.API_RUTA_IMG + jsonObject.optString("imagen"));
                                listaProductos.add(producto);
                            }
                            dialog.hide();
                            AdaptadorProductos adaptadorProductos = new AdaptadorProductos(listaProductos, getContext());
                            recyclerProductos.setAdapter(adaptadorProductos);
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
                        Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        Limpiar();
    }

    protected void Limpiar () {
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }

}