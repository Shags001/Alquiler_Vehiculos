package com.example.alquilervehiculos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.alquilervehiculos.datos.GestorDatos;
import com.example.alquilervehiculos.modelos.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editUsuario;
    private TextInputEditText editClave;
    private SwitchMaterial switchRecordarSesion;
    private MaterialButton botonIniciarSesion;
    private GestorDatos gestorDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Fuerza el modo claro en toda la aplicación
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        gestorDatos = GestorDatos.obtenerInstancia(this);

        // Si la sesión ya está guardada, se redirige directamente a la pantalla principal
        if (gestorDatos.isSesionIniciada()) {
            abrirPantallaPrincipal();
            return;
        }

        setContentView(R.layout.activity_login);

        // Vinculación tradicional con findViewById()
        editUsuario = findViewById(R.id.editUsuario);
        editClave = findViewById(R.id.editClave);
        switchRecordarSesion = findViewById(R.id.switchRecordarSesion);
        botonIniciarSesion = findViewById(R.id.botonIniciarSesion);

        botonIniciarSesion.setOnClickListener(v -> validarEIniciarSesion());
    }

    private void validarEIniciarSesion() {
        String usuarioStr = editUsuario.getText() != null ? editUsuario.getText().toString().trim() : "";
        String claveStr = editClave.getText() != null ? editClave.getText().toString().trim() : "";

        // Validar campos vacíos
        if (usuarioStr.isEmpty() || claveStr.isEmpty()) {
            mostrarAlertaError("Campos Requeridos", "Por favor ingresa tu usuario y contraseña.");
            return;
        }

        // Validación dinámica contra usuarios registrados en GestorDatos
        Usuario usuarioValidado = gestorDatos.validarUsuario(usuarioStr, claveStr);

        if (usuarioValidado != null) {
            boolean mantenerSesion = switchRecordarSesion.isChecked();
            gestorDatos.guardarSesion(usuarioValidado.getUsuario(), mantenerSesion);
            abrirPantallaPrincipal();
        } else {
            // Verificar si el usuario existe pero está inactivo
            Usuario uExistente = gestorDatos.obtenerUsuarioPorNombre(usuarioStr);
            if (uExistente != null && !uExistente.isActivo()) {
                mostrarAlertaError("Cuenta Inactiva", "Tu cuenta de usuario se encuentra inactiva. Contacta al administrador.");
            } else {
                mostrarAlertaError("Credenciales Inválidas", "El usuario o la contraseña son incorrectos.");
            }
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void abrirPantallaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
