package com.example.alquilervehiculos.datos;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.alquilervehiculos.modelos.Abono;
import com.example.alquilervehiculos.modelos.Renta;
import com.example.alquilervehiculos.modelos.Usuario;
import com.example.alquilervehiculos.modelos.Vehiculo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GestorDatos {

    private static final String NOMBRE_PREF = "AlquilerOrellanaPrefs";
    private static final String CLAVE_VEHICULOS = "lista_vehiculos";
    private static final String CLAVE_RENTAS = "lista_rentas";
    private static final String CLAVE_ABONOS = "lista_abonos";
    private static final String CLAVE_USUARIOS = "lista_usuarios";
    private static final String CLAVE_USUARIO_ACTUAL = "usuario_actual";
    private static final String CLAVE_SESION_ACTIVA = "sesion_activa";

    private static GestorDatos instancia;
    private final SharedPreferences preferences;
    private final Gson gson;

    private GestorDatos(Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
        this.gson = new Gson();
        cargarDatosInicialesSiEstaVacio();
    }

    public static synchronized GestorDatos obtenerInstancia(Context context) {
        if (instancia == null) {
            instancia = new GestorDatos(context);
        }
        return instancia;
    }

    private void cargarDatosInicialesSiEstaVacio() {
        // Cargar Usuarios iniciales
        ArrayList<Usuario> usuarios = obtenerUsuarios();
        if (usuarios.isEmpty()) {
            usuarios.add(new Usuario(1, "Administrador General", "admin", "admin123", "admin@alquilerorellana.com", "7700-1122", "Administrador", true));
            usuarios.add(new Usuario(2, "Empleado de Turno", "empleado", "1234", "empleado@alquilerorellana.com", "7111-2233", "Empleado", true));
            guardarUsuarios(usuarios);
        }

        ArrayList<Vehiculo> vehiculos = obtenerVehiculos();
        if (vehiculos.isEmpty()) {
            // Generar vehículos de prueba vinculando imágenes reales de res/drawable
            ArrayList<String> img1 = new ArrayList<>();
            img1.add("hilux4x4");
            vehiculos.add(new Vehiculo(1, "Toyota", "Hilux 4x4", 2023, "P-458921", 65.0, "Rentado", img1, 45200.0, 100));

            ArrayList<String> img2 = new ArrayList<>();
            img2.add("hyundayaccent2022");
            vehiculos.add(new Vehiculo(2, "Hyundai", "Accent", 2022, "P-312784", 35.0, "Disponible", img2, 28100.0, 80));

            ArrayList<String> img3 = new ArrayList<>();
            img3.add("kiasportage");
            vehiculos.add(new Vehiculo(3, "Kia", "Sportage", 2024, "P-984210", 50.0, "Disponible", img3, 12500.0, 100));

            ArrayList<String> img4 = new ArrayList<>();
            img4.add("nissannp300");
            vehiculos.add(new Vehiculo(4, "Nissan", "Frontier NP300", 2023, "P-112345", 60.0, "Disponible", img4, 34000.0, 90));

            guardarVehiculos(vehiculos);

            // Generar renta de prueba
            ArrayList<Renta> rentas = new ArrayList<>();
            Renta rentaPrueba = new Renta(101, 1, "Carlos Orellana", "05123984-7", "LIC-984512", "7845-1234",
                    "2025-02-10", "2025-02-15", 5, 325.0, 125.0, "Activa",
                    45200.0, 100, true, true, "Rayón leve en puerta trasera derecha");
            rentas.add(rentaPrueba);

            Renta rentaFinalizada = new Renta(102, 2, "Maria Fernandez", "04875123-9", "LIC-774411", "7123-9988",
                    "2025-01-01", "2025-01-04", 3, 105.0, 0.0, "Finalizada",
                    27800.0, 80, true, true, "Sin observaciones de entrega");
            rentaFinalizada.setKmRegreso(28100.0);
            rentaFinalizada.setGasolinaRegreso(80);
            rentaFinalizada.setTieneLlantaRegreso(true);
            rentaFinalizada.setTieneHerramientasRegreso(true);
            rentaFinalizada.setObservacionesRegreso("Entregado en perfecto estado");
            rentas.add(rentaFinalizada);

            guardarRentas(rentas);

            // Generar abono de prueba
            ArrayList<Abono> abonos = new ArrayList<>();
            abonos.add(new Abono(201, 101, "2025-02-10", 200.0, "Efectivo", "Anticipo de entrega"));
            abonos.add(new Abono(202, 102, "2025-01-01", 105.0, "Tarjeta", "Pago total"));
            guardarAbonos(abonos);
        }
    }

    // --- MANEJO DE USUARIOS ---
    public ArrayList<Usuario> obtenerUsuarios() {
        String json = preferences.getString(CLAVE_USUARIOS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Usuario>>() {}.getType();
        ArrayList<Usuario> lista = gson.fromJson(json, type);
        return lista != null ? lista : new ArrayList<>();
    }

    public void guardarUsuarios(ArrayList<Usuario> lista) {
        String json = gson.toJson(lista);
        preferences.edit().putString(CLAVE_USUARIOS, json).apply();
    }

    public void agregarUsuario(Usuario u) {
        ArrayList<Usuario> lista = obtenerUsuarios();
        int maxId = 0;
        for (Usuario usr : lista) {
            if (usr.getId() > maxId) maxId = usr.getId();
        }
        u.setId(maxId + 1);
        lista.add(u);
        guardarUsuarios(lista);
    }

    public void actualizarUsuario(Usuario u) {
        ArrayList<Usuario> lista = obtenerUsuarios();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == u.getId()) {
                lista.set(i, u);
                break;
            }
        }
        guardarUsuarios(lista);
    }

    public void eliminarUsuario(int usuarioId) {
        ArrayList<Usuario> lista = obtenerUsuarios();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == usuarioId) {
                lista.remove(i);
                break;
            }
        }
        guardarUsuarios(lista);
    }

    public Usuario obtenerUsuarioPorNombre(String username) {
        ArrayList<Usuario> lista = obtenerUsuarios();
        for (Usuario u : lista) {
            if (u.getUsuario().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public Usuario validarUsuario(String username, String password) {
        ArrayList<Usuario> lista = obtenerUsuarios();
        for (Usuario u : lista) {
            if (u.getUsuario().equalsIgnoreCase(username) && u.getClave().equals(password)) {
                if (u.isActivo()) {
                    return u;
                }
            }
        }
        return null;
    }

    // --- MANEJO DE VEHICULOS ---
    public ArrayList<Vehiculo> obtenerVehiculos() {
        String json = preferences.getString(CLAVE_VEHICULOS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Vehiculo>>() {}.getType();
        ArrayList<Vehiculo> lista = gson.fromJson(json, type);

        if (lista != null) {
            // Migrar automáticamente imágenes heredadas a los drawables reales
            for (Vehiculo v : lista) {
                ArrayList<String> imgs = v.getImagenes();
                if (imgs == null || imgs.isEmpty()) {
                    imgs = new ArrayList<>();
                    v.setImagenes(imgs);
                }
                for (int i = 0; i < imgs.size(); i++) {
                    String img = imgs.get(i);
                    if ("toyota_hilux".equalsIgnoreCase(img)) imgs.set(i, "hilux4x4");
                    else if ("hyundai_accent".equalsIgnoreCase(img)) imgs.set(i, "hyundayaccent2022");
                    else if ("kia_sportage".equalsIgnoreCase(img)) imgs.set(i, "kiasportage");
                    else if ("nissan_frontier".equalsIgnoreCase(img)) imgs.set(i, "nissannp300");
                }
                if (imgs.isEmpty()) {
                    if (v.getId() == 1) imgs.add("hilux4x4");
                    else if (v.getId() == 2) imgs.add("hyundayaccent2022");
                    else if (v.getId() == 3) imgs.add("kiasportage");
                    else if (v.getId() == 4) imgs.add("nissannp300");
                }
            }
        }

        return lista != null ? lista : new ArrayList<>();
    }

    public void guardarVehiculos(ArrayList<Vehiculo> lista) {
        String json = gson.toJson(lista);
        preferences.edit().putString(CLAVE_VEHICULOS, json).apply();
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        ArrayList<Vehiculo> lista = obtenerVehiculos();
        int maxId = 0;
        for (Vehiculo v : lista) {
            if (v.getId() > maxId) maxId = v.getId();
        }
        vehiculo.setId(maxId + 1);
        lista.add(vehiculo);
        guardarVehiculos(lista);
    }

    public void actualizarVehiculo(Vehiculo vehiculo) {
        ArrayList<Vehiculo> lista = obtenerVehiculos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == vehiculo.getId()) {
                lista.set(i, vehiculo);
                break;
            }
        }
        guardarVehiculos(lista);
    }

    public void eliminarVehiculo(int vehiculoId) {
        ArrayList<Vehiculo> lista = obtenerVehiculos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == vehiculoId) {
                lista.remove(i);
                break;
            }
        }
        guardarVehiculos(lista);
    }

    public Vehiculo obtenerVehiculoPorId(int vehiculoId) {
        ArrayList<Vehiculo> lista = obtenerVehiculos();
        for (Vehiculo v : lista) {
            if (v.getId() == vehiculoId) {
                return v;
            }
        }
        return null;
    }

    // --- MANEJO DE RENTAS ---
    public ArrayList<Renta> obtenerRentas() {
        String json = preferences.getString(CLAVE_RENTAS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Renta>>() {}.getType();
        ArrayList<Renta> lista = gson.fromJson(json, type);
        return lista != null ? lista : new ArrayList<>();
    }

    public void guardarRentas(ArrayList<Renta> lista) {
        String json = gson.toJson(lista);
        preferences.edit().putString(CLAVE_RENTAS, json).apply();
    }

    public void agregarRenta(Renta renta) {
        ArrayList<Renta> lista = obtenerRentas();
        int maxId = 100;
        for (Renta r : lista) {
            if (r.getId() > maxId) maxId = r.getId();
        }
        renta.setId(maxId + 1);
        lista.add(renta);
        guardarRentas(lista);

        // Actualizar estado del vehículo a Rentado
        Vehiculo v = obtenerVehiculoPorId(renta.getVehiculoId());
        if (v != null) {
            v.setEstado("Rentado");
            actualizarVehiculo(v);
        }
    }

    public void actualizarRenta(Renta renta) {
        ArrayList<Renta> lista = obtenerRentas();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == renta.getId()) {
                lista.set(i, renta);
                break;
            }
        }
        guardarRentas(lista);
    }

    public Renta obtenerRentaPorId(int rentaId) {
        ArrayList<Renta> lista = obtenerRentas();
        for (Renta r : lista) {
            if (r.getId() == rentaId) {
                return r;
            }
        }
        return null;
    }

    // --- MANEJO DE ABONOS ---
    public ArrayList<Abono> obtenerAbonos() {
        String json = preferences.getString(CLAVE_ABONOS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Abono>>() {}.getType();
        ArrayList<Abono> lista = gson.fromJson(json, type);
        return lista != null ? lista : new ArrayList<>();
    }

    public void guardarAbonos(ArrayList<Abono> lista) {
        String json = gson.toJson(lista);
        preferences.edit().putString(CLAVE_ABONOS, json).apply();
    }

    public void agregarAbono(Abono abono) {
        ArrayList<Abono> lista = obtenerAbonos();
        int maxId = 200;
        for (Abono a : lista) {
            if (a.getId() > maxId) maxId = a.getId();
        }
        abono.setId(maxId + 1);
        lista.add(abono);
        guardarAbonos(lista);

        // Descontar saldo pendiente de la renta
        Renta renta = obtenerRentaPorId(abono.getRentaId());
        if (renta != null) {
            double nuevoSaldo = renta.getSaldoPendiente() - abono.getMonto();
            if (nuevoSaldo < 0) nuevoSaldo = 0;
            renta.setSaldoPendiente(nuevoSaldo);
            actualizarRenta(renta);
        }
    }

    public ArrayList<Abono> obtenerAbonosPorRentaId(int rentaId) {
        ArrayList<Abono> todos = obtenerAbonos();
        ArrayList<Abono> resultado = new ArrayList<>();
        for (Abono a : todos) {
            if (a.getRentaId() == rentaId) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    // --- MANEJO DE SESION ---
    public void guardarSesion(String usuario, boolean mantenerSesion) {
        preferences.edit()
                .putString(CLAVE_USUARIO_ACTUAL, usuario)
                .putBoolean(CLAVE_SESION_ACTIVA, mantenerSesion)
                .apply();
    }

    public String obtenerUsuarioActual() {
        return preferences.getString(CLAVE_USUARIO_ACTUAL, "admin");
    }

    public boolean isSesionIniciada() {
        return preferences.getBoolean(CLAVE_SESION_ACTIVA, false);
    }

    public void cerrarSesion() {
        preferences.edit()
                .remove(CLAVE_USUARIO_ACTUAL)
                .putBoolean(CLAVE_SESION_ACTIVA, false)
                .apply();
    }
}
