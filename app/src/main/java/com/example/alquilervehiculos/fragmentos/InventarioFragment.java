package com.example.alquilervehiculos.fragmentos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alquilervehiculos.R;
import com.example.alquilervehiculos.adaptadores.AdaptadorVehiculos;
import com.example.alquilervehiculos.datos.GestorDatos;
import com.example.alquilervehiculos.modelos.Vehiculo;
import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class InventarioFragment extends Fragment implements AdaptadorVehiculos.OnVehiculoAccionListener {

    private TextInputEditText editBuscador;
    private ChipGroup chipGroupFiltro;
    private RecyclerView recyclerVehiculos;
    private TextView textSinVehiculos;
    private FloatingActionButton fabAgregar;

    private GestorDatos gestorDatos;
    private AdaptadorVehiculos adaptador;
    private ArrayList<Vehiculo> listaCompleta;

    private ArrayList<String> fotosSeleccionadasTemp;
    private TextView textFotosAgregadasTemp;
    private ActivityResultLauncher<String> selectorGaleriaLauncher;

    public static String obtenerTextoOctavos(int octavos) {
        switch (octavos) {
            case 0: return "E (Vacío - 0%)";
            case 1: return "1/8 tanque (13%)";
            case 2: return "1/4 tanque (25%)";
            case 3: return "3/8 tanque (38%)";
            case 4: return "1/2 tanque (50%)";
            case 5: return "5/8 tanque (63%)";
            case 6: return "3/4 tanque (75%)";
            case 7: return "7/8 tanque (88%)";
            case 8: return "F (Lleno - 100%)";
            default: return octavos + "/8 tanque (" + Math.round(octavos * 12.5f) + "%)";
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Registrar selector de galería que copia la imagen al almacenamiento interno (getFilesDir())
        selectorGaleriaLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                String rutaArchivoGuardado = copiarImagenAAlmacenamientoInterno(requireContext(), uri);
                if (rutaArchivoGuardado != null) {
                    if (fotosSeleccionadasTemp == null) fotosSeleccionadasTemp = new ArrayList<>();
                    fotosSeleccionadasTemp.add(rutaArchivoGuardado);
                    if (textFotosAgregadasTemp != null) {
                        textFotosAgregadasTemp.setText(String.format(Locale.getDefault(), "Fotos agregadas: %d", fotosSeleccionadasTemp.size()));
                    }
                    Toast.makeText(requireContext(), "Foto agregada al catálogo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);

        gestorDatos = GestorDatos.obtenerInstancia(requireContext());

        editBuscador = view.findViewById(R.id.editBuscadorVehiculo);
        chipGroupFiltro = view.findViewById(R.id.chipGroupFiltroEstado);
        recyclerVehiculos = view.findViewById(R.id.recyclerVehiculos);
        textSinVehiculos = view.findViewById(R.id.textSinVehiculos);
        fabAgregar = view.findViewById(R.id.fabAgregarVehiculo);

        recyclerVehiculos.setLayoutManager(new LinearLayoutManager(requireContext()));
        adaptador = new AdaptadorVehiculos(new ArrayList<>(), this);
        recyclerVehiculos.setAdapter(adaptador);

        fabAgregar.setOnClickListener(v -> mostrarDialogoVehiculo(null));

        configurarBuscadorYFiltros();
        cargarDatosVehiculos();

        return view;
    }

    private void cargarDatosVehiculos() {
        listaCompleta = gestorDatos.obtenerVehiculos();
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

        chipGroupFiltro.setOnCheckedChangeListener((group, checkedId) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        if (listaCompleta == null) return;

        String busqueda = editBuscador.getText() != null ? editBuscador.getText().toString().trim().toLowerCase() : "";
        int checkedChipId = chipGroupFiltro.getCheckedChipId();

        ArrayList<Vehiculo> listaFiltrada = new ArrayList<>();

        for (Vehiculo v : listaCompleta) {
            boolean coincideEstado = true;
            if (checkedChipId == R.id.chipFiltroDisponible) {
                coincideEstado = "Disponible".equalsIgnoreCase(v.getEstado());
            } else if (checkedChipId == R.id.chipFiltroRentado) {
                coincideEstado = "Rentado".equalsIgnoreCase(v.getEstado());
            }

            boolean coincideTexto = busqueda.isEmpty() ||
                    v.getMarca().toLowerCase().contains(busqueda) ||
                    v.getModelo().toLowerCase().contains(busqueda) ||
                    v.getPlaca().toLowerCase().contains(busqueda);

            if (coincideEstado && coincideTexto) {
                listaFiltrada.add(v);
            }
        }

        adaptador.actualizarLista(listaFiltrada);

        if (listaFiltrada.isEmpty()) {
            textSinVehiculos.setVisibility(View.VISIBLE);
        } else {
            textSinVehiculos.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEditarVehiculo(Vehiculo vehiculo) {
        mostrarDialogoVehiculo(vehiculo);
    }

    @Override
    public void onEliminarVehiculo(Vehiculo vehiculo) {
        confirmarEliminarVehiculo(vehiculo);
    }

    private void mostrarDialogoVehiculo(@Nullable Vehiculo vehiculoEditar) {
        View viewDialogo = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_vehiculo, null);

        TextView textTitulo = viewDialogo.findViewById(R.id.textTituloDialogoVehiculo);
        TextInputEditText editMarca = viewDialogo.findViewById(R.id.editMarcaVehiculo);
        TextInputEditText editModelo = viewDialogo.findViewById(R.id.editModeloVehiculo);
        TextInputEditText editAnio = viewDialogo.findViewById(R.id.editAnioVehiculo);
        TextInputEditText editPlaca = viewDialogo.findViewById(R.id.editPlacaVehiculo);
        TextInputEditText editKm = viewDialogo.findViewById(R.id.editKmVehiculo);
        TextView textLabelCombustible = viewDialogo.findViewById(R.id.textLabelCombustibleDialog);
        PointerSpeedometer medidorGasolina = viewDialogo.findViewById(R.id.medidorGasolinaVehiculo);
        Slider sliderGasolina = viewDialogo.findViewById(R.id.sliderGasolinaVehiculo);
        TextInputEditText editTarifa = viewDialogo.findViewById(R.id.editTarifaVehiculo);

        TextInputEditText editTarifaLlanta = viewDialogo.findViewById(R.id.editTarifaLlantaVehiculo);
        TextInputEditText editTarifaHerramientas = viewDialogo.findViewById(R.id.editTarifaHerramientasVehiculo);
        TextInputEditText editTarifaGasolina = viewDialogo.findViewById(R.id.editTarifaGasolinaVehiculo);

        MaterialButton btnAgregarFoto = viewDialogo.findViewById(R.id.btnAgregarFotoVehiculo);
        textFotosAgregadasTemp = viewDialogo.findViewById(R.id.textFotosAgregadasVehiculo);

        // Configuración de Medidor de Aguja
        medidorGasolina.setWithTremble(false);
        medidorGasolina.setUnit("");

        fotosSeleccionadasTemp = vehiculoEditar != null && vehiculoEditar.getImagenes() != null
                ? new ArrayList<>(vehiculoEditar.getImagenes()) : new ArrayList<>();

        textFotosAgregadasTemp.setText(String.format(Locale.getDefault(), "Fotos agregadas: %d", fotosSeleccionadasTemp.size()));

        sliderGasolina.addOnChangeListener((slider, value, fromUser) -> {
            int octavos = Math.round(value);
            medidorGasolina.speedTo(octavos, 350);
            textLabelCombustible.setText("Nivel: " + obtenerTextoOctavos(octavos));
        });

        if (vehiculoEditar != null) {
            textTitulo.setText("Editar Vehículo");
            editMarca.setText(vehiculoEditar.getMarca());
            editModelo.setText(vehiculoEditar.getModelo());
            editAnio.setText(String.valueOf(vehiculoEditar.getAnio()));
            editPlaca.setText(vehiculoEditar.getPlaca());
            editKm.setText(String.valueOf(vehiculoEditar.getKilometrajeActual()));

            int octavosInicial = Math.round((vehiculoEditar.getNivelCombustibleActual() / 100.0f) * 8.0f);
            sliderGasolina.setValue(octavosInicial);
            medidorGasolina.speedTo(octavosInicial, 0);
            textLabelCombustible.setText("Nivel: " + obtenerTextoOctavos(octavosInicial));

            editTarifa.setText(String.valueOf(vehiculoEditar.getTarifaDiaria()));
            editTarifaLlanta.setText(String.valueOf(vehiculoEditar.getTarifaLlantaFaltante()));
            editTarifaHerramientas.setText(String.valueOf(vehiculoEditar.getTarifaHerramientasFaltante()));
            editTarifaGasolina.setText(String.valueOf(vehiculoEditar.getTarifaGasolinaPorcentaje()));
        } else {
            editKm.setText("0");
            sliderGasolina.setValue(8);
            medidorGasolina.speedTo(8, 0);
            textLabelCombustible.setText("Nivel: " + obtenerTextoOctavos(8));
            editTarifaLlanta.setText("100.0");
            editTarifaHerramientas.setText("50.0");
            editTarifaGasolina.setText("0.65");
        }

        btnAgregarFoto.setOnClickListener(v -> selectorGaleriaLauncher.launch("image/*"));

        new AlertDialog.Builder(requireContext())
                .setView(viewDialogo)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String marca = editMarca.getText() != null ? editMarca.getText().toString().trim() : "";
                    String modelo = editModelo.getText() != null ? editModelo.getText().toString().trim() : "";
                    String anioStr = editAnio.getText() != null ? editAnio.getText().toString().trim() : "";
                    String placa = editPlaca.getText() != null ? editPlaca.getText().toString().trim() : "";
                    String kmStr = editKm.getText() != null ? editKm.getText().toString().trim() : "";
                    String tarifaStr = editTarifa.getText() != null ? editTarifa.getText().toString().trim() : "";
                    String tarifaLlantaStr = editTarifaLlanta.getText() != null ? editTarifaLlanta.getText().toString().trim() : "100.0";
                    String tarifaHerramientasStr = editTarifaHerramientas.getText() != null ? editTarifaHerramientas.getText().toString().trim() : "50.0";
                    String tarifaGasolinaStr = editTarifaGasolina.getText() != null ? editTarifaGasolina.getText().toString().trim() : "0.65";

                    int octavosVal = Math.round(sliderGasolina.getValue());
                    int gasPorcentaje = Math.round(octavosVal * 12.5f);

                    if (marca.isEmpty() || modelo.isEmpty() || anioStr.isEmpty() || placa.isEmpty() || kmStr.isEmpty() || tarifaStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int anio = Integer.parseInt(anioStr);
                    double km = Double.parseDouble(kmStr);
                    double tarifa = Double.parseDouble(tarifaStr);
                    double tarifaLlanta = tarifaLlantaStr.isEmpty() ? 100.0 : Double.parseDouble(tarifaLlantaStr);
                    double tarifaHerramientas = tarifaHerramientasStr.isEmpty() ? 50.0 : Double.parseDouble(tarifaHerramientasStr);
                    double tarifaGasolina = tarifaGasolinaStr.isEmpty() ? 0.65 : Double.parseDouble(tarifaGasolinaStr);

                    if (vehiculoEditar == null) {
                        Vehiculo nuevo = new Vehiculo(0, marca, modelo, anio, placa, tarifa, "Disponible", fotosSeleccionadasTemp, km, gasPorcentaje, tarifaLlanta, tarifaHerramientas, tarifaGasolina);
                        gestorDatos.agregarVehiculo(nuevo);
                        Toast.makeText(requireContext(), "Vehículo agregado con éxito", Toast.LENGTH_SHORT).show();
                    } else {
                        vehiculoEditar.setMarca(marca);
                        vehiculoEditar.setModelo(modelo);
                        vehiculoEditar.setAnio(anio);
                        vehiculoEditar.setPlaca(placa);
                        vehiculoEditar.setKilometrajeActual(km);
                        vehiculoEditar.setNivelCombustibleActual(gasPorcentaje);
                        vehiculoEditar.setTarifaDiaria(tarifa);
                        vehiculoEditar.setImagenes(fotosSeleccionadasTemp);
                        vehiculoEditar.setTarifaLlantaFaltante(tarifaLlanta);
                        vehiculoEditar.setTarifaHerramientasFaltante(tarifaHerramientas);
                        vehiculoEditar.setTarifaGasolinaPorcentaje(tarifaGasolina);
                        gestorDatos.actualizarVehiculo(vehiculoEditar);
                        Toast.makeText(requireContext(), "Vehículo actualizado con éxito", Toast.LENGTH_SHORT).show();
                    }
                    cargarDatosVehiculos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarVehiculo(Vehiculo vehiculo) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Vehículo")
                .setMessage("¿Deseas eliminar el vehículo " + vehiculo.getMarca() + " " + vehiculo.getModelo() + " (" + vehiculo.getPlaca() + ")?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    gestorDatos.eliminarVehiculo(vehiculo.getId());
                    Toast.makeText(requireContext(), "Vehículo eliminado con éxito", Toast.LENGTH_SHORT).show();
                    cargarDatosVehiculos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String copiarImagenAAlmacenamientoInterno(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File archivoDestino = new File(context.getFilesDir(), "foto_vehiculo_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(archivoDestino);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return archivoDestino.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
