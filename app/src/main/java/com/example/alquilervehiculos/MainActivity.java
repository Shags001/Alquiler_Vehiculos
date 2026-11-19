package com.example.alquilervehiculos;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.alquilervehiculos.datos.GestorDatos;
import com.example.alquilervehiculos.fragmentos.CobrosFragment;
import com.example.alquilervehiculos.fragmentos.EstadisticasFragment;
import com.example.alquilervehiculos.fragmentos.InventarioFragment;
import com.example.alquilervehiculos.fragmentos.RentaFragment;
import com.example.alquilervehiculos.fragmentos.UsuariosFragment;
import com.example.alquilervehiculos.modelos.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;
    private NavigationView navigationView;
    private GestorDatos gestorDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestorDatos = GestorDatos.obtenerInstancia(this);

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);

        // Configurar Toolbar como Action Bar e ícono Hamburguesa
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.menu_inventario, R.string.menu_inventario);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Actualizar datos del header del Drawer
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView textHeaderUsuario = headerView.findViewById(R.id.textHeaderUsuario);
            if (textHeaderUsuario != null) {
                String username = gestorDatos.obtenerUsuarioActual();
                Usuario userObj = gestorDatos.obtenerUsuarioPorNombre(username);
                if (userObj != null) {
                    textHeaderUsuario.setText("Usuario: " + userObj.getUsuario() + " (" + userObj.getRol() + ")");
                } else {
                    textHeaderUsuario.setText("Usuario: " + (username != null ? username : "admin"));
                }
            }
        }

        // Cargar fragmento inicial (Inventario) si es la primera creación
        if (savedInstanceState == null) {
            remplazarFragmento(new InventarioFragment(), getString(R.string.menu_inventario));
            navigationView.setCheckedItem(R.id.nav_inventario);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_inventario) {
            remplazarFragmento(new InventarioFragment(), getString(R.string.menu_inventario));
        } else if (id == R.id.nav_rentas) {
            remplazarFragmento(new RentaFragment(), getString(R.string.menu_rentas));
        } else if (id == R.id.nav_cobros) {
            remplazarFragmento(new CobrosFragment(), getString(R.string.menu_cobros));
        } else if (id == R.id.nav_estadisticas) {
            remplazarFragmento(new EstadisticasFragment(), getString(R.string.menu_estadisticas));
        } else if (id == R.id.nav_usuarios) {
            remplazarFragmento(new UsuariosFragment(), "Gestión de Usuarios / Perfil");
        } else if (id == R.id.nav_cerrar_sesion) {
            confirmarCierreSesion();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void remplazarFragmento(Fragment fragmento, String titulo) {
        if (toolbar != null) {
            toolbar.setTitle(titulo);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmento)
                .commit();
    }

    private void confirmarCierreSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas salir de la aplicación?")
                .setPositiveButton("Sí, salir", (dialog, which) -> {
                    gestorDatos.cerrarSesion();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
