package com.example.alquilervehiculos.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alquilervehiculos.R;
import com.example.alquilervehiculos.datos.GestorDatos;
import com.example.alquilervehiculos.modelos.Abono;
import com.example.alquilervehiculos.modelos.Renta;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Locale;

public class AdaptadorAbonos extends RecyclerView.Adapter<AdaptadorAbonos.AbonoViewHolder> {

    public interface OnAbonoClickListener {
        void onVerComprobante(Abono abono);
    }

    private ArrayList<Abono> listaAbonos;
    private final OnAbonoClickListener listener;
    private final GestorDatos gestorDatos;

    public AdaptadorAbonos(Context context, ArrayList<Abono> listaAbonos, OnAbonoClickListener listener) {
        this.listaAbonos = listaAbonos != null ? listaAbonos : new ArrayList<>();
        this.listener = listener;
        this.gestorDatos = GestorDatos.obtenerInstancia(context);
    }

    public void actualizarLista(ArrayList<Abono> nuevaLista) {
        this.listaAbonos = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AbonoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_abono, parent, false);
        return new AbonoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbonoViewHolder holder, int position) {
        Abono a = listaAbonos.get(position);

        Renta r = gestorDatos.obtenerRentaPorId(a.getRentaId());
        String nombreCliente = r != null ? r.getNombreCliente() : "Cliente Desconocido";

        holder.textClienteAbono.setText(String.format(Locale.getDefault(), "%s (Renta #%d)", nombreCliente, a.getRentaId()));
        holder.textMontoAbono.setText(String.format(Locale.getDefault(), "+$%.2f", a.getMonto()));
        holder.textDetalleAbono.setText(String.format(Locale.getDefault(), "Método: %s | Fecha: %s", a.getMetodoPago(), a.getFecha()));

        if (a.getNota() != null && !a.getNota().isEmpty()) {
            holder.textNotaAbono.setText(String.format("Nota: %s", a.getNota()));
            holder.textNotaAbono.setVisibility(View.VISIBLE);
        } else {
            holder.textNotaAbono.setVisibility(View.GONE);
        }

        holder.btnVerComprobante.setOnClickListener(view -> {
            if (listener != null) {
                listener.onVerComprobante(a);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaAbonos.size();
    }

    public static class AbonoViewHolder extends RecyclerView.ViewHolder {
        TextView textClienteAbono;
        TextView textMontoAbono;
        TextView textDetalleAbono;
        TextView textNotaAbono;
        MaterialButton btnVerComprobante;

        public AbonoViewHolder(@NonNull View itemView) {
            super(itemView);
            textClienteAbono = itemView.findViewById(R.id.textClienteAbono);
            textMontoAbono = itemView.findViewById(R.id.textMontoAbono);
            textDetalleAbono = itemView.findViewById(R.id.textDetalleAbono);
            textNotaAbono = itemView.findViewById(R.id.textNotaAbono);
            btnVerComprobante = itemView.findViewById(R.id.btnVerComprobante);
        }
    }
}
