package com.example.alquilervehiculos.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alquilervehiculos.R;
import com.example.alquilervehiculos.modelos.Usuario;

import java.util.ArrayList;

public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.UsuarioViewHolder> {

    public interface OnUsuarioAccionListener {
        void onEditarUsuario(Usuario usuario);
        void onEliminarUsuario(Usuario usuario);
    }

    private ArrayList<Usuario> listaUsuarios;
    private final OnUsuarioAccionListener listener;

    public AdaptadorUsuarios(ArrayList<Usuario> listaUsuarios, OnUsuarioAccionListener listener) {
        this.listaUsuarios = listaUsuarios != null ? listaUsuarios : new ArrayList<>();
        this.listener = listener;
    }

    public void actualizarLista(ArrayList<Usuario> nuevaLista) {
        this.listaUsuarios = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario u = listaUsuarios.get(position);

        holder.textNombreCompletoUsuario.setText(u.getNombreCompleto());
        holder.textUsernameUsuario.setText(String.format("Usuario: %s", u.getUsuario()));
        holder.textEmailTelefonoUsuario.setText(String.format("%s | Tel: %s", u.getEmail(), u.getTelefono()));

        if ("Administrador".equalsIgnoreCase(u.getRol())) {
            holder.textRolUsuarioBadge.setText("Admin");
            holder.textRolUsuarioBadge.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_badge_disponible));
        } else {
            holder.textRolUsuarioBadge.setText("Empleado");
            holder.textRolUsuarioBadge.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_badge_finalizada));
        }

        if (u.isActivo()) {
            holder.textEstadoUsuario.setText("Estado: Activo");
            holder.textEstadoUsuario.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.estado_disponible));
        } else {
            holder.textEstadoUsuario.setText("Estado: Inactivo");
            holder.textEstadoUsuario.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.estado_rentado));
        }

        // Acciones directas mediante botones de Lápiz y Papelera
        holder.btnEditarUsuario.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditarUsuario(u);
            }
        });

        holder.btnEliminarUsuario.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminarUsuario(u);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView textNombreCompletoUsuario;
        TextView textRolUsuarioBadge;
        TextView textUsernameUsuario;
        TextView textEmailTelefonoUsuario;
        TextView textEstadoUsuario;
        ImageButton btnEditarUsuario;
        ImageButton btnEliminarUsuario;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombreCompletoUsuario = itemView.findViewById(R.id.textNombreCompletoUsuario);
            textRolUsuarioBadge = itemView.findViewById(R.id.textRolUsuarioBadge);
            textUsernameUsuario = itemView.findViewById(R.id.textUsernameUsuario);
            textEmailTelefonoUsuario = itemView.findViewById(R.id.textEmailTelefonoUsuario);
            textEstadoUsuario = itemView.findViewById(R.id.textEstadoUsuario);
            btnEditarUsuario = itemView.findViewById(R.id.btnEditarUsuario);
            btnEliminarUsuario = itemView.findViewById(R.id.btnEliminarUsuario);
        }
    }
}
