package com.example.alquilervehiculos.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.alquilervehiculos.R;
import com.example.alquilervehiculos.modelos.Vehiculo;

import java.util.ArrayList;
import java.util.Locale;

public class AdaptadorVehiculos extends RecyclerView.Adapter<AdaptadorVehiculos.VehiculoViewHolder> {

    public interface OnVehiculoAccionListener {
        void onEditarVehiculo(Vehiculo vehiculo);
        void onEliminarVehiculo(Vehiculo vehiculo);
    }

    private ArrayList<Vehiculo> listaVehiculos;
    private final OnVehiculoAccionListener listener;

    public AdaptadorVehiculos(ArrayList<Vehiculo> listaVehiculos, OnVehiculoAccionListener listener) {
        this.listaVehiculos = listaVehiculos != null ? listaVehiculos : new ArrayList<>();
        this.listener = listener;
    }

    public void actualizarLista(ArrayList<Vehiculo> nuevaLista) {
        this.listaVehiculos = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VehiculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehiculo, parent, false);
        return new VehiculoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiculoViewHolder holder, int position) {
        Vehiculo v = listaVehiculos.get(position);

        Context context = holder.itemView.getContext();
        String titulo = v.getMarca() + " " + v.getModelo() + " " + v.getAnio();
        holder.textMarcaModeloItem.setText(titulo);
        holder.textPlacaItem.setText(String.format(Locale.getDefault(), "Placa: %s", v.getPlaca()));
        holder.textKmGasItem.setText(String.format(Locale.getDefault(), "Km: %,.0f | Gas: %d%%", v.getKilometrajeActual(), v.getNivelCombustibleActual()));
        holder.textTarifaItem.setText(String.format(Locale.getDefault(), "$%.2f / día", v.getTarifaDiaria()));

        // Configurar ViewPager2 para catálogo de múltiples imágenes
        AdaptadorGaleriaImagenes adapterGaleria = new AdaptadorGaleriaImagenes(context, v.getImagenes());
        holder.viewPagerGaleria.setAdapter(adapterGaleria);

        if ("Disponible".equalsIgnoreCase(v.getEstado())) {
            holder.textEstadoVehiculoBadge.setText("Disponible");
            holder.textEstadoVehiculoBadge.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_badge_disponible));
        } else {
            holder.textEstadoVehiculoBadge.setText("Rentado");
            holder.textEstadoVehiculoBadge.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_badge_rentado));
        }

        // Acciones directas mediante botones de Lápiz (Editar) y Papelera (Borrar)
        holder.btnEditarVehiculo.setOnClickListener(view -> {
            if (listener != null) {
                listener.onEditarVehiculo(v);
            }
        });

        holder.btnEliminarVehiculo.setOnClickListener(view -> {
            if (listener != null) {
                listener.onEliminarVehiculo(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaVehiculos.size();
    }

    public static class VehiculoViewHolder extends RecyclerView.ViewHolder {
        TextView textMarcaModeloItem;
        TextView textPlacaItem;
        TextView textKmGasItem;
        TextView textTarifaItem;
        TextView textEstadoVehiculoBadge;
        ViewPager2 viewPagerGaleria;
        ImageButton btnEditarVehiculo;
        ImageButton btnEliminarVehiculo;

        public VehiculoViewHolder(@NonNull View itemView) {
            super(itemView);
            textMarcaModeloItem = itemView.findViewById(R.id.textMarcaModeloItem);
            textPlacaItem = itemView.findViewById(R.id.textPlacaItem);
            textKmGasItem = itemView.findViewById(R.id.textKmGasItem);
            textTarifaItem = itemView.findViewById(R.id.textTarifaItem);
            textEstadoVehiculoBadge = itemView.findViewById(R.id.textEstadoVehiculoBadge);
            viewPagerGaleria = itemView.findViewById(R.id.viewPagerGaleriaItem);
            btnEditarVehiculo = itemView.findViewById(R.id.btnEditarVehiculo);
            btnEliminarVehiculo = itemView.findViewById(R.id.btnEliminarVehiculo);
        }
    }
}
