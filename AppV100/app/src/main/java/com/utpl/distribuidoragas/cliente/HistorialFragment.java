package com.utpl.distribuidoragas.cliente;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.utpl.distribuidoragas.Login;
import com.utpl.distribuidoragas.R;
import com.utpl.distribuidoragas.adaptadores.AdaptadorPedidos;
import com.utpl.distribuidoragas.api.Api;
import com.utpl.distribuidoragas.api.SocketServer;
import com.utpl.distribuidoragas.entidades.Pedido;
import com.utpl.distribuidoragas.entidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

public class HistorialFragment extends Fragment {

    RecyclerView recyclerPedidos;
    ArrayList<Pedido> listaPedidos;
    ProgressDialog dialog;
    private RequestQueue requestQueue;
    String idUsuario;
    Double latRepartidor, lngRepartidor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View vista = inflater.inflate(R.layout.fragment_historial, container, false);

        listaPedidos = new ArrayList<>();

        recyclerPedidos = vista.findViewById(R.id.recyclerHistorial);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerPedidos.setHasFixedSize(true);

        SharedPreferences session = this.getActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        idUsuario = session.getString("idUsuario", "-");

        cargarPedidos();

        return vista;
    }

    private void cargarPedidos() {
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Cargando Historial");
        dialog.show();
        String url = Api.API_RUTA + "pedidos/pedidosCliente";
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Pedido pedido = null;
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray json = data.optJSONArray("data");
                            for(int x=0; x<json.length();x++){
                                pedido = new Pedido();
                                JSONObject jsonObject = null;
                                jsonObject = json.getJSONObject(x);
                                pedido.setId(jsonObject.optInt("id"));
                                pedido.setIdRepartidor(jsonObject.optString("idRepartidor"));
                                pedido.setNombreProducto(jsonObject.optString("producto"));
                                pedido.setDescripcion(jsonObject.optString("descripcion"));
                                String precioPublico = String.format( "%.2f", jsonObject.optDouble("total"));
                                pedido.setTotal("$ " + precioPublico);
                                pedido.setEstado(jsonObject.optString("estado"));
                                pedido.setFecha(jsonObject.optString("fecha") + " : " + jsonObject.optString("hora"));
                                pedido.setImagen(Api.API_RUTA_IMG + jsonObject.optString("imagen"));
                                listaPedidos.add(pedido);
                            }
                            dialog.hide();
                            AdaptadorPedidos adaptadorPedidos = new AdaptadorPedidos(listaPedidos, getContext());
                            adaptadorPedidos.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getEstado().equals("GENERADO")){
                                        Toast.makeText(getContext(), "Espere mientras se le asigna un repartidor",Toast.LENGTH_LONG).show();
                                    } else if(listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getEstado().equals("ASIGNADO")){
                                        buscarUbicacionRepartidor(listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getIdRepartidor());
                                    } else if(listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getEstado().equals("FINALIZADO")){
                                        final EditText comentario = new EditText(getContext());
                                        comentario.setHint("Escriba su comentario");
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                        builder1.setMessage("El Pedido ya finalizó, Desea agregar un comentario del servicio?");
                                        builder1.setCancelable(true);
                                        builder1.setView(comentario);
                                        builder1.setPositiveButton(
                                                "Enviar comentario",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        enviarComentarioPedido(comentario.getText().toString(), listaPedidos.get(recyclerPedidos.getChildAdapterPosition(view)).getId());
                                                    }
                                                });
                                        builder1.setNegativeButton(
                                                "No",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Toast.makeText(getContext(), "Gracias por preferirnos",Toast.LENGTH_LONG).show();
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                    }
                                }
                            });
                            recyclerPedidos.setAdapter(adaptadorPedidos);
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
                        dialog.hide();
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
                params.put("idCliente", idUsuario);
                return params;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        Limpiar();
    }

    private void enviarComentarioPedido(String comentario, int idPedido){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Api.API_RUTA + "pedidos/nuevoComentario";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            Toast.makeText(getContext(), data.getString("mensaje"),Toast.LENGTH_LONG).show();
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
        })
        {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idPedido", String.valueOf(idPedido));
                params.put("comentario", comentario);
                return params;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        Limpiar();
    }

    private void buscarUbicacionRepartidor(String idRepartidor) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = Api.API_RUTA + "pedidos/buscarUbicacionesRepartidor";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONObject ubicacionesRepartidor = new JSONObject(String.valueOf(data.getJSONObject("data")));
                            latRepartidor = ubicacionesRepartidor.getDouble("lat");
                            lngRepartidor = ubicacionesRepartidor.getDouble("lng");
                            Intent intent = new Intent(getContext(), MapsCliente.class);
                            intent.putExtra("lat", latRepartidor);
                            intent.putExtra("lng", lngRepartidor);
                            startActivity(intent);
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
        })
        {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idRepartidor", idRepartidor);
                return params;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        Limpiar();
    }

    protected void Limpiar () {
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }

}