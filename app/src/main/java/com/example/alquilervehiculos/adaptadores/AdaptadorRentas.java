package com.example.alquilervehiculos.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alquilervehiculos.R;
import com.example.alquilervehiculos.datos.GestorDatos;
import com.example.alquilervehiculos.modelos.Renta;
import com.example.alquilervehiculos.modelos.Vehiculo;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Locale;

public class AdaptadorRentas extends RecyclerView.Adapter<AdaptadorRentas.RentaViewHolder> {

    public interface OnRentaAccionListener {
        void onFinalizarRenta(Renta renta);
    }

    private ArrayList<Renta> listaRentas;
    private final OnRentaAccionListener listener;
    private final GestorDatos gestorDatos;

    public AdaptadorRentas(Context context, ArrayList<Renta> listaRentas, OnRentaAccionListener listener) {
        this.listaRentas = listaRentas != null ? listaRentas : new ArrayList<>();
        this.listener = listener;
        this.gestorDatos = GestorDatos.obtenerInstancia(context);
    }

    public void actualizarLista(ArrayList<Renta> nuevaLista) {
        this.listaRentas = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_renta, parent, false);
        return new RentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RentaViewHolder holder, int position) {
        Renta r = listaRentas.get(position);

        holder.textClienteRenta.setText(r.getNombreCliente());

        // Buscar información del vehículo asociado
        Vehiculo v = gestorDatos.obtenerVehiculoPorId(r.getVehiculoId());
        if (v != null) {
            holder.textVehiculoRenta.setText(String.format("Vehículo: %s %s (%s)", v.getMarca(), v.getModelo(), v.getPlaca()));
        } else {
            holder.textVehiculoRenta.setText(String.format(Locale.getDefault(), "Vehículo ID: %d", r.getVehiculoId()));
        }

        holder.textFechasRenta.setText(String.format(Locale.getDefault(), "Fechas: %s a %s (%d días)", r.getFechaInicio(), r.getFechaFin(), r.getDiasRenta()));
        holder.textMontoTotalRenta.setText(String.format(Locale.getDefault(), "Total: $%.2f", r.getMontoTotal()));
        holder.textSaldoPendienteRenta.setText(String.format(Locale.getDefault(), "Saldo Pendiente: $%.2f", r.getSaldoPendiente()));

        if ("Activa".equalsIgnoreCase(r.getEstadoRenta())) {
            holder.textEstadoRentaBadge.setText("Activa");
            holder.textEstadoRentaBadge.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_badge_rentado));
            holder.botonFinalizarRenta.setVisibility(View.VISIBLE);
        } else {
            holder.textEstadoRentaBadge.setText("Finalizada");
            holder.textEstadoRentaBadge.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_badge_finalizada));
            holder.botonFinalizarRenta.setVisibility(View.GONE);
        }

        holder.botonFinalizarRenta.setOnClickListener(view -> {
            if (listener != null) {
                listener.onFinalizarRenta(r);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaRentas.size();
    }

    public static class RentaViewHolder extends RecyclerView.ViewHolder {
        TextView textClienteRenta;
        TextView textEstadoRentaBadge;
        TextView textVehiculoRenta;
        TextView textFechasRenta;
        TextView textMontoTotalRenta;
        TextView textSaldoPendienteRenta;
        MaterialButton botonFinalizarRenta;

        public RentaViewHolder(@NonNull View itemView) {
            super(itemView);
            textClienteRenta = itemView.findViewById(R.id.textClienteRenta);
            textEstadoRentaBadge = itemView.findViewById(R.id.textEstadoRentaBadge);
            textVehiculoRenta = itemView.findViewById(R.id.textVehiculoRenta);
            textFechasRenta = itemView.findViewById(R.id.textFechasRenta);
            textMontoTotalRenta = itemView.findViewById(R.id.textMontoTotalRenta);
            textSaldoPendienteRenta = itemView.findViewById(R.id.textSaldoPendienteRenta);
            botonFinalizarRenta = itemView.findViewById(R.id.botonFinalizarRenta);
        }
    }
}
