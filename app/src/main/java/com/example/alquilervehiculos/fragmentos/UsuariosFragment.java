package com.example.alquilervehiculos.fragmentos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alquilervehiculos.R;
import com.example.alquilervehiculos.adaptadores.AdaptadorUsuarios;
import com.example.alquilervehiculos.datos.GestorDatos;
import com.example.alquilervehiculos.modelos.Usuario;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class UsuariosFragment extends Fragment implements AdaptadorUsuarios.OnUsuarioAccionListener {

    private TextInputEditText editBuscador;
    private ChipGroup chipGroupFiltroRol;
    private RecyclerView recyclerUsuarios;
    private TextView textSinUsuarios;
    private FloatingActionButton fabAgregar;

    private GestorDatos gestorDatos;
    private AdaptadorUsuarios adaptador;
    private ArrayList<Usuario> listaCompletaUsuarios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios, container, false);

        gestorDatos = GestorDatos.obtenerInstancia(requireContext());

        editBuscador = view.findViewById(R.id.editBuscadorUsuario);
        chipGroupFiltroRol = view.findViewById(R.id.chipGroupFiltroRol);
        recyclerUsuarios = view.findViewById(R.id.recyclerUsuarios);
        textSinUsuarios = view.findViewById(R.id.textSinUsuarios);
        fabAgregar = view.findViewById(R.id.fabAgregarUsuario);

        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        adaptador = new AdaptadorUsuarios(new ArrayList<>(), this);
        recyclerUsuarios.setAdapter(adaptador);

        fabAgregar.setOnClickListener(v -> mostrarDialogoUsuario(null));

        configurarBuscadorYFiltros();
        cargarUsuarios();

        return view;
    }

    private void cargarUsuarios() {
        listaCompletaUsuarios = gestorDatos.obtenerUsuarios();
        aplicarFiltros();
    }

    private void configurarBuscadorYFiltros() {
        editBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltros();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroupFiltroRol.setOnCheckedChangeListener((group, checkedId) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        if (listaCompletaUsuarios == null) return;

        String busqueda = editBuscador.getText() != null ? editBuscador.getText().toString().trim().toLowerCase() : "";
        int checkedChipId = chipGroupFiltroRol.getCheckedChipId();

        ArrayList<Usuario> listaFiltrada = new ArrayList<>();

        for (Usuario u : listaCompletaUsuarios) {
            boolean coincideRol = true;
            if (checkedChipId == R.id.chipFiltroRolAdmin) {
                coincideRol = "Administrador".equalsIgnoreCase(u.getRol());
            } else if (checkedChipId == R.id.chipFiltroRolEmpleado) {
                coincideRol = "Empleado".equalsIgnoreCase(u.getRol());
            }

            boolean coincideTexto = busqueda.isEmpty() ||
                    u.getNombreCompleto().toLowerCase().contains(busqueda) ||
                    u.getUsuario().toLowerCase().contains(busqueda) ||
                    u.getEmail().toLowerCase().contains(busqueda);

            if (coincideRol && coincideTexto) {
                listaFiltrada.add(u);
            }
        }

        adaptador.actualizarLista(listaFiltrada);

        if (listaFiltrada.isEmpty()) {
            textSinUsuarios.setVisibility(View.VISIBLE);
        } else {
            textSinUsuarios.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEditarUsuario(Usuario usuario) {
        mostrarDialogoUsuario(usuario);
    }

    @Override
    public void onEliminarUsuario(Usuario usuario) {
        confirmarEliminarUsuario(usuario);
    }

    private void mostrarDialogoUsuario(@Nullable Usuario usuarioEditar) {
        View viewDialogo = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_usuario, null);

        TextView textTitulo = viewDialogo.findViewById(R.id.textTituloDialogoUsuario);
        TextInputEditText editNombre = viewDialogo.findViewById(R.id.editNombreCompletoUsuario);
        TextInputEditText editUsername = viewDialogo.findViewById(R.id.editUsernameUsuario);
        TextInputEditText editClave = viewDialogo.findViewById(R.id.editClaveUsuario);
        TextInputEditText editEmail = viewDialogo.findViewById(R.id.editEmailUsuario);
        TextInputEditText editTelefono = viewDialogo.findViewById(R.id.editTelefonoUsuario);
        Spinner spinnerRol = viewDialogo.findViewById(R.id.spinnerRolUsuario);
        SwitchMaterial switchActivo = viewDialogo.findViewById(R.id.switchActivoUsuario);

        String[] roles = {"Administrador", "Empleado"};
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roles);
        adapterRoles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapterRoles);

        if (usuarioEditar != null) {
            textTitulo.setText("Editar Usuario / Perfil");
            editNombre.setText(usuarioEditar.getNombreCompleto());
            editUsername.setText(usuarioEditar.getUsuario());
            editClave.setText(usuarioEditar.getClave());
            editEmail.setText(usuarioEditar.getEmail());
            editTelefono.setText(usuarioEditar.getTelefono());
            spinnerRol.setSelection("Administrador".equalsIgnoreCase(usuarioEditar.getRol()) ? 0 : 1);
            switchActivo.setChecked(usuarioEditar.isActivo());
        }

        new AlertDialog.Builder(requireContext())
                .setView(viewDialogo)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = editNombre.getText() != null ? editNombre.getText().toString().trim() : "";
                    String username = editUsername.getText() != null ? editUsername.getText().toString().trim() : "";
                    String clave = editClave.getText() != null ? editClave.getText().toString().trim() : "";
                    String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
                    String tel = editTelefono.getText() != null ? editTelefono.getText().toString().trim() : "";
                    String rol = roles[spinnerRol.getSelectedItemPosition()];
                    boolean activo = switchActivo.isChecked();

                    if (nombre.isEmpty() || username.isEmpty() || clave.isEmpty() || email.isEmpty() || tel.isEmpty()) {
                        Toast.makeText(requireContext(), "Por favor completa todos los campos del usuario", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (usuarioEditar == null) {
                        // Verificar si el username ya existe
                        Usuario uExistente = gestorDatos.obtenerUsuarioPorNombre(username);
                        if (uExistente != null) {
                            Toast.makeText(requireContext(), "El nombre de usuario '" + username + "' ya está registrado.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Usuario nuevo = new Usuario(0, nombre, username, clave, email, tel, rol, activo);
                        gestorDatos.agregarUsuario(nuevo);
                        Toast.makeText(requireContext(), "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                    } else {
                        usuarioEditar.setNombreCompleto(nombre);
                        usuarioEditar.setUsuario(username);
                        usuarioEditar.setClave(clave);
                        usuarioEditar.setEmail(email);
                        usuarioEditar.setTelefono(tel);
                        usuarioEditar.setRol(rol);
                        usuarioEditar.setActivo(activo);
                        gestorDatos.actualizarUsuario(usuarioEditar);
                        Toast.makeText(requireContext(), "Usuario actualizado con éxito", Toast.LENGTH_SHORT).show();
                    }
                    cargarUsuarios();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarUsuario(Usuario usuario) {
        String usuarioActual = gestorDatos.obtenerUsuarioActual();
        if (usuario.getUsuario().equalsIgnoreCase(usuarioActual)) {
            Toast.makeText(requireContext(), "No puedes eliminar tu propia cuenta en sesión activa.", Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Usuario")
                .setMessage("¿Deseas eliminar la cuenta de usuario " + usuario.getNombreCompleto() + " (@" + usuario.getUsuario() + ")?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    gestorDatos.eliminarUsuario(usuario.getId());
                    Toast.makeText(requireContext(), "Usuario eliminado con éxito", Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
