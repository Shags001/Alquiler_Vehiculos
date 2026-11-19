package com.example.alquilervehiculos.fragmentos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.SeekBar;
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
import com.example.alquilervehiculos.adaptadores.AdaptadorRentas;
import com.example.alquilervehiculos.datos.GestorDatos;
import com.example.alquilervehiculos.modelos.Abono;
import com.example.alquilervehiculos.modelos.Renta;
import com.example.alquilervehiculos.modelos.Vehiculo;
import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RentaFragment extends Fragment implements AdaptadorRentas.OnRentaAccionListener {

    private TabLayout tabLayout;
    private ScrollView layoutTabSalida;
    private ScrollView layoutTabRegreso;
    private View layoutTabHistorial;

    // Componentes de Salida
    private Spinner spinnerVehiculosSalida;
    private TextInputEditText editClienteNombre;
    private TextInputEditText editClienteDoc;
    private TextInputEditText editClienteLicencia;
    private TextInputEditText editClienteTelefono;
    private MaterialButton btnFechaInicioSalida;
    private MaterialButton btnFechaFinSalida;
    private TextInputEditText editKmSalida;
    private TextView textGasolinaSalidaLabel;
    private PointerSpeedometer medidorGasolinaSalida;
    private Slider sliderGasolinaSalida;
    private CheckBox checkLlantaSalida;
    private CheckBox checkHerramientasSalida;
    private TextInputEditText editObservacionesSalida;
    private TextView textCalculoTotalSalida;
    private MaterialButton btnRegistrarSalida;

    // Componentes de Regreso
    private Spinner spinnerRentasActivasRegreso;
    private TextView textResumenSalidaRegreso;
    private TextView textAccesoriosSalidaRegreso;
    private PointerSpeedometer medidorResumenSalidaRegreso;
    private TextInputEditText editKmRegreso;
    private TextView textGasolinaRegresoLabel;
    private PointerSpeedometer medidorGasolinaRegreso;
    private Slider sliderGasolinaRegreso;
    private CheckBox checkLlantaRegreso;
    private CheckBox checkHerramientasRegreso;
    private TextInputEditText editObservacionesRegreso;
    private TextView textRecargosCalculadosRegreso;
    private MaterialButton btnFinalizarRentaRegreso;

    // Componentes de Historial
    private TextInputEditText editBuscadorHistorial;
    private RecyclerView recyclerHistorial;
    private TextView textSinRentasHistorial;
    private AdaptadorRentas adaptadorHistorial;

    private GestorDatos gestorDatos;
    private Calendar calInicio;
    private Calendar calFin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private ArrayList<Vehiculo> vehiculosDisponiblesSalida;
    private ArrayList<Renta> rentasActivasRegreso;
    private ArrayList<Renta> listaCompletaHistorial;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_renta, container, false);

        gestorDatos = GestorDatos.obtenerInstancia(requireContext());

        tabLayout = view.findViewById(R.id.tabLayoutRentas);
        layoutTabSalida = view.findViewById(R.id.layoutTabSalida);
        layoutTabRegreso = view.findViewById(R.id.layoutTabRegreso);
        layoutTabHistorial = view.findViewById(R.id.layoutTabHistorial);

        inicializarComponentesSalida(view);
        inicializarComponentesRegreso(view);
        inicializarComponentesHistorial(view);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                layoutTabSalida.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);
                layoutTabRegreso.setVisibility(pos == 1 ? View.VISIBLE : View.GONE);
                layoutTabHistorial.setVisibility(pos == 2 ? View.VISIBLE : View.GONE);

                if (pos == 0) cargarDatosSalida();
                else if (pos == 1) cargarDatosRegreso();
                else if (pos == 2) cargarDatosHistorial();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        cargarDatosSalida();

        return view;
    }

    private void inicializarComponentesSalida(View view) {
        spinnerVehiculosSalida = view.findViewById(R.id.spinnerVehiculosSalida);
        editClienteNombre = view.findViewById(R.id.editClienteNombreSalida);
        editClienteDoc = view.findViewById(R.id.editClienteDocSalida);
        editClienteLicencia = view.findViewById(R.id.editClienteLicenciaSalida);
        editClienteTelefono = view.findViewById(R.id.editClienteTelefonoSalida);
        btnFechaInicioSalida = view.findViewById(R.id.btnFechaInicioSalida);
        btnFechaFinSalida = view.findViewById(R.id.btnFechaFinSalida);
        editKmSalida = view.findViewById(R.id.editKmSalida);
        textGasolinaSalidaLabel = view.findViewById(R.id.textGasolinaSalidaLabel);
        medidorGasolinaSalida = view.findViewById(R.id.medidorGasolinaSalida);
        sliderGasolinaSalida = view.findViewById(R.id.sliderGasolinaSalida);
        checkLlantaSalida = view.findViewById(R.id.checkLlantaSalida);
        checkHerramientasSalida = view.findViewById(R.id.checkHerramientasSalida);
        editObservacionesSalida = view.findViewById(R.id.editObservacionesSalida);
        textCalculoTotalSalida = view.findViewById(R.id.textCalculoTotalSalida);
        btnRegistrarSalida = view.findViewById(R.id.btnRegistrarSalida);

        medidorGasolinaSalida.setWithTremble(false);
        medidorGasolinaSalida.setUnit("");

        calInicio = Calendar.getInstance();
        calFin = Calendar.getInstance();
        calFin.add(Calendar.DAY_OF_MONTH, 1);

        btnFechaInicioSalida.setText(dateFormat.format(calInicio.getTime()));
        btnFechaFinSalida.setText(dateFormat.format(calFin.getTime()));

        sliderGasolinaSalida.addOnChangeListener((slider, value, fromUser) -> {
            int octavos = Math.round(value);
            medidorGasolinaSalida.speedTo(octavos, 350);
            textGasolinaSalidaLabel.setText("Gasolina de Salida: " + InventarioFragment.obtenerTextoOctavos(octavos));
        });

        btnFechaInicioSalida.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(requireContext(), (view1, year, month, dayOfMonth) -> {
                calInicio.set(year, month, dayOfMonth);
                btnFechaInicioSalida.setText(dateFormat.format(calInicio.getTime()));
                actualizarCalculoTotalSalida();
            }, calInicio.get(Calendar.YEAR), calInicio.get(Calendar.MONTH), calInicio.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        btnFechaFinSalida.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(requireContext(), (view1, year, month, dayOfMonth) -> {
                calFin.set(year, month, dayOfMonth);
                btnFechaFinSalida.setText(dateFormat.format(calFin.getTime()));
                actualizarCalculoTotalSalida();
            }, calFin.get(Calendar.YEAR), calFin.get(Calendar.MONTH), calFin.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        spinnerVehiculosSalida.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (vehiculosDisponiblesSalida != null && position >= 0 && position < vehiculosDisponiblesSalida.size()) {
                    Vehiculo v = vehiculosDisponiblesSalida.get(position);
                    editKmSalida.setText(String.valueOf(v.getKilometrajeActual()));

                    int octavos = Math.round((v.getNivelCombustibleActual() / 100.0f) * 8.0f);
                    sliderGasolinaSalida.setValue(octavos);
                    medidorGasolinaSalida.speedTo(octavos, 0);
                    textGasolinaSalidaLabel.setText("Gasolina de Salida: " + InventarioFragment.obtenerTextoOctavos(octavos));

                    actualizarCalculoTotalSalida();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRegistrarSalida.setOnClickListener(v -> solicitarConfirmacionSalida());
    }

    private void cargarDatosSalida() {
        ArrayList<Vehiculo> todos = gestorDatos.obtenerVehiculos();
        vehiculosDisponiblesSalida = new ArrayList<>();
        for (Vehiculo v : todos) {
            if ("Disponible".equalsIgnoreCase(v.getEstado())) {
                vehiculosDisponiblesSalida.add(v);
            }
        }

        ArrayList<String> nombres = new ArrayList<>();
        for (Vehiculo v : vehiculosDisponiblesSalida) {
            nombres.add(String.format(Locale.getDefault(), "%s %s • Placa: %s ($%.2f/día)", v.getMarca(), v.getModelo(), v.getPlaca(), v.getTarifaDiaria()));
        }

        if (nombres.isEmpty()) {
            nombres.add("No hay vehículos disponibles");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, nombres);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerVehiculosSalida.setAdapter(adapter);

        actualizarCalculoTotalSalida();
    }

    private void actualizarCalculoTotalSalida() {
        int pos = spinnerVehiculosSalida.getSelectedItemPosition();
        if (vehiculosDisponiblesSalida != null && pos >= 0 && pos < vehiculosDisponiblesSalida.size()) {
            Vehiculo v = vehiculosDisponiblesSalida.get(pos);
            long diffMillis = calFin.getTimeInMillis() - calInicio.getTimeInMillis();
            int dias = (int) (diffMillis / (1000 * 60 * 60 * 24));
            if (dias <= 0) dias = 1;

            double total = dias * v.getTarifaDiaria();
            textCalculoTotalSalida.setText(String.format(Locale.getDefault(), "Monto Total Contratado: $%.2f (%d días)", total, dias));
        } else {
            textCalculoTotalSalida.setText("Monto Total Contratado: $0.00 (0 días)");
        }
    }

    private void solicitarConfirmacionSalida() {
        int pos = spinnerVehiculosSalida.getSelectedItemPosition();
        if (vehiculosDisponiblesSalida == null || pos < 0 || pos >= vehiculosDisponiblesSalida.size()) {
            Toast.makeText(requireContext(), "Selecciona un vehículo disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        Vehiculo vElegido = vehiculosDisponiblesSalida.get(pos);
        String nombre = editClienteNombre.getText() != null ? editClienteNombre.getText().toString().trim() : "";
        String doc = editClienteDoc.getText() != null ? editClienteDoc.getText().toString().trim() : "";
        String licencia = editClienteLicencia.getText() != null ? editClienteLicencia.getText().toString().trim() : "";
        String tel = editClienteTelefono.getText() != null ? editClienteTelefono.getText().toString().trim() : "";
        String kmStr = editKmSalida.getText() != null ? editKmSalida.getText().toString().trim() : "";
        String obs = editObservacionesSalida.getText() != null ? editObservacionesSalida.getText().toString().trim() : "";

        if (nombre.isEmpty() || doc.isEmpty() || licencia.isEmpty() || tel.isEmpty() || kmStr.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor completa todos los campos del cliente e inspección", Toast.LENGTH_SHORT).show();
            return;
        }

        double kmSalida = Double.parseDouble(kmStr);
        int octavosSalida = Math.round(sliderGasolinaSalida.getValue());
        int gasSalida = Math.round(octavosSalida * 12.5f);

        boolean llanta = checkLlantaSalida.isChecked();
        boolean herramientas = checkHerramientasSalida.isChecked();

        long diffMillis = calFin.getTimeInMillis() - calInicio.getTimeInMillis();
        final int diasFinal = (int) (diffMillis / (1000 * 60 * 60 * 24)) <= 0 ? 1 : (int) (diffMillis / (1000 * 60 * 60 * 24));
        final double montoTotalFinal = diasFinal * vElegido.getTarifaDiaria();

        String resumen = String.format(Locale.getDefault(),
                "Cliente: %s\nDUI: %s | Licencia: %s\nVehículo: %s %s (Placa: %s)\nFechas: %s a %s (%d días)\nMonto Total: $%.2f\nKilometraje Salida: %,.0f km\nGasolina Salida: %s\nLlanta repuesto: %s\nKit herramientas: %s",
                nombre, doc, licencia, vElegido.getMarca(), vElegido.getModelo(), vElegido.getPlaca(),
                dateFormat.format(calInicio.getTime()), dateFormat.format(calFin.getTime()), diasFinal, montoTotalFinal, kmSalida,
                InventarioFragment.obtenerTextoOctavos(octavosSalida),
                llanta ? "Incluida" : "No incluida",
                herramientas ? "Incluido" : "No incluido");

        View viewDialogo = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_confirmacion_estilizado, null);
        TextView textTitulo = viewDialogo.findViewById(R.id.textTituloConfirmacionDialog);
        TextView textMensaje = viewDialogo.findViewById(R.id.textMensajeConfirmacionDialog);
        MaterialButton btnSi = viewDialogo.findViewById(R.id.btnConfirmarSi);
        MaterialButton btnNo = viewDialogo.findViewById(R.id.btnConfirmarNo);

        textTitulo.setText("Confirmar Salida");
        textMensaje.setText(resumen);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(viewDialogo)
                .create();

        btnSi.setOnClickListener(v -> {
            Renta nuevaRenta = new Renta(0, vElegido.getId(), nombre, doc, licencia, tel,
                    dateFormat.format(calInicio.getTime()), dateFormat.format(calFin.getTime()),
                    diasFinal, montoTotalFinal, montoTotalFinal, "Activa",
                    kmSalida, gasSalida, llanta, herramientas, obs);

            gestorDatos.agregarRenta(nuevaRenta);
            Toast.makeText(requireContext(), "Salida confirmada con éxito", Toast.LENGTH_SHORT).show();

            editClienteNombre.setText("");
            editClienteDoc.setText("");
            editClienteLicencia.setText("");
            editClienteTelefono.setText("");
            editObservacionesSalida.setText("");
            cargarDatosSalida();
            dialog.dismiss();

            mostrarContratoRecibo(nuevaRenta);
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void inicializarComponentesRegreso(View view) {
        spinnerRentasActivasRegreso = view.findViewById(R.id.spinnerRentasActivasRegreso);
        textResumenSalidaRegreso = view.findViewById(R.id.textResumenSalidaRegreso);
        textAccesoriosSalidaRegreso = view.findViewById(R.id.textAccesoriosSalidaRegreso);
        medidorResumenSalidaRegreso = view.findViewById(R.id.medidorResumenSalidaRegreso);

        editKmRegreso = view.findViewById(R.id.editKmRegreso);
        textGasolinaRegresoLabel = view.findViewById(R.id.textGasolinaRegresoLabel);
        medidorGasolinaRegreso = view.findViewById(R.id.medidorGasolinaRegreso);
        sliderGasolinaRegreso = view.findViewById(R.id.sliderGasolinaRegreso);
        checkLlantaRegreso = view.findViewById(R.id.checkLlantaRegreso);
        checkHerramientasRegreso = view.findViewById(R.id.checkHerramientasRegreso);
        editObservacionesRegreso = view.findViewById(R.id.editObservacionesRegreso);
        textRecargosCalculadosRegreso = view.findViewById(R.id.textRecargosCalculadosRegreso);
        btnFinalizarRentaRegreso = view.findViewById(R.id.btnFinalizarRentaRegreso);

        medidorResumenSalidaRegreso.setWithTremble(false);
        medidorResumenSalidaRegreso.setUnit("");

        medidorGasolinaRegreso.setWithTremble(false);
        medidorGasolinaRegreso.setUnit("");

        sliderGasolinaRegreso.addOnChangeListener((slider, value, fromUser) -> {
            int octavos = Math.round(value);
            medidorGasolinaRegreso.speedTo(octavos, 350);
            textGasolinaRegresoLabel.setText("Gasolina de Regreso: " + InventarioFragment.obtenerTextoOctavos(octavos));
            actualizarRecargosExtras();
        });

        checkLlantaRegreso.setOnCheckedChangeListener((buttonView, isChecked) -> actualizarRecargosExtras());
        checkHerramientasRegreso.setOnCheckedChangeListener((buttonView, isChecked) -> actualizarRecargosExtras());

        spinnerRentasActivasRegreso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (rentasActivasRegreso != null && position >= 0 && position < rentasActivasRegreso.size()) {
                    Renta r = rentasActivasRegreso.get(position);
                    int octavosSalida = Math.round((r.getGasolinaSalida() / 100.0f) * 8.0f);

                    textResumenSalidaRegreso.setText(String.format(Locale.getDefault(),
                            "Kilometraje de Salida: %,.0f km | Combustible: %s", r.getKmSalida(), InventarioFragment.obtenerTextoOctavos(octavosSalida)));
                    textAccesoriosSalidaRegreso.setText(String.format(Locale.getDefault(),
                            "Llanta de repuesto: %s | Kit de herramientas: %s",
                            r.isTieneLlantaSalida() ? "Incluida" : "No incluida",
                            r.isTieneHerramientasSalida() ? "Incluido" : "No incluido"));

                    medidorResumenSalidaRegreso.speedTo(octavosSalida, 0);

                    editKmRegreso.setText(String.valueOf(r.getKmSalida()));
                    sliderGasolinaRegreso.setValue(octavosSalida);
                    medidorGasolinaRegreso.speedTo(octavosSalida, 0);
                    textGasolinaRegresoLabel.setText("Gasolina de Regreso: " + InventarioFragment.obtenerTextoOctavos(octavosSalida));

                    checkLlantaRegreso.setChecked(r.isTieneLlantaSalida());
                    checkHerramientasRegreso.setChecked(r.isTieneHerramientasSalida());
                    actualizarRecargosExtras();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnFinalizarRentaRegreso.setOnClickListener(v -> solicitarConfirmacionRegreso());
    }

    private void cargarDatosRegreso() {
        ArrayList<Renta> todas = gestorDatos.obtenerRentas();
        rentasActivasRegreso = new ArrayList<>();
        for (Renta r : todas) {
            if ("Activa".equalsIgnoreCase(r.getEstadoRenta())) {
                rentasActivasRegreso.add(r);
            }
        }

        ArrayList<String> titulos = new ArrayList<>();
        for (Renta r : rentasActivasRegreso) {
            Vehiculo v = gestorDatos.obtenerVehiculoPorId(r.getVehiculoId());
            String nombreVehiculo = v != null ? v.getMarca() + " " + v.getModelo() + " (Placa: " + v.getPlaca() + ")" : "Vehículo #" + r.getVehiculoId();
            titulos.add(String.format(Locale.getDefault(), "%s • %s", r.getNombreCliente(), nombreVehiculo));
        }

        if (titulos.isEmpty()) {
            titulos.add("No hay rentas activas para devolución");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, titulos);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerRentasActivasRegreso.setAdapter(adapter);
    }

    private double calcularRecargosParaRenta(Renta r) {
        if (r == null) return 0.0;
        Vehiculo v = gestorDatos.obtenerVehiculoPorId(r.getVehiculoId());
        double tarifaGas = v != null ? v.getTarifaGasolinaPorcentaje() : 0.65;
        double tarifaLlanta = v != null ? v.getTarifaLlantaFaltante() : 100.0;
        double tarifaHerramientas = v != null ? v.getTarifaHerramientasFaltante() : 50.0;

        double recargos = 0.0;
        int octavosRegreso = Math.round(sliderGasolinaRegreso.getValue());
        int gasRegreso = Math.round(octavosRegreso * 12.5f);

        if (gasRegreso < r.getGasolinaSalida()) {
            int faltanteGas = r.getGasolinaSalida() - gasRegreso;
            recargos += (faltanteGas * tarifaGas);
        }

        if (r.isTieneLlantaSalida() && !checkLlantaRegreso.isChecked()) {
            recargos += tarifaLlanta;
        }

        if (r.isTieneHerramientasSalida() && !checkHerramientasRegreso.isChecked()) {
            recargos += tarifaHerramientas;
        }

        return recargos;
    }

    private void actualizarRecargosExtras() {
        int pos = spinnerRentasActivasRegreso.getSelectedItemPosition();
        if (rentasActivasRegreso != null && pos >= 0 && pos < rentasActivasRegreso.size()) {
            Renta r = rentasActivasRegreso.get(pos);
            double recargos = calcularRecargosParaRenta(r);
            textRecargosCalculadosRegreso.setText(String.format(Locale.getDefault(), "Recargos Extras Calculados: $%.2f", recargos));
        } else {
            textRecargosCalculadosRegreso.setText("Recargos Extras Calculados: $0.00");
        }
    }

    private void solicitarConfirmacionRegreso() {
        int pos = spinnerRentasActivasRegreso.getSelectedItemPosition();
        if (rentasActivasRegreso == null || pos < 0 || pos >= rentasActivasRegreso.size()) {
            Toast.makeText(requireContext(), "Selecciona una renta activa", Toast.LENGTH_SHORT).show();
            return;
        }

        Renta renta = rentasActivasRegreso.get(pos);
        String kmStr = editKmRegreso.getText() != null ? editKmRegreso.getText().toString().trim() : "";
        String obs = editObservacionesRegreso.getText() != null ? editObservacionesRegreso.getText().toString().trim() : "";

        if (kmStr.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor ingresa el kilometraje de regreso", Toast.LENGTH_SHORT).show();
            return;
        }

        double kmRegreso = Double.parseDouble(kmStr);

        // VALIDACIÓN ESTRICTA DE KILOMETRAJE: kmRegreso >= kmSalida
        if (kmRegreso < renta.getKmSalida()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Error de Kilometraje")
                    .setMessage(String.format(Locale.getDefault(),
                            "El kilometraje de regreso (%,.1f km) no puede ser menor al kilometraje de salida (%,.1f km). Por favor verifica el valor ingresado.",
                            kmRegreso, renta.getKmSalida()))
                    .setPositiveButton("Corregir", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        int octavosRegreso = Math.round(sliderGasolinaRegreso.getValue());
        int gasRegreso = Math.round(octavosRegreso * 12.5f);

        boolean llantaRegreso = checkLlantaRegreso.isChecked();
        boolean herramientasRegreso = checkHerramientasRegreso.isChecked();
        double recargos = calcularRecargosParaRenta(renta);

        Vehiculo v = gestorDatos.obtenerVehiculoPorId(renta.getVehiculoId());
        String nombreVehiculo = v != null ? v.getMarca() + " " + v.getModelo() + " (Placa: " + v.getPlaca() + ")" : "Vehículo #" + renta.getVehiculoId();

        String resumen = String.format(Locale.getDefault(),
                "Cliente: %s\nVehículo: %s\nKilometraje Regreso: %,.0f km\nGasolina Regreso: %s\nRecargos Extras: $%.2f\nNuevo Saldo Pendiente: $%.2f",
                renta.getNombreCliente(), nombreVehiculo, kmRegreso, InventarioFragment.obtenerTextoOctavos(octavosRegreso), recargos, (renta.getSaldoPendiente() + recargos));

        View viewDialogo = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_confirmacion_estilizado, null);
        TextView textTitulo = viewDialogo.findViewById(R.id.textTituloConfirmacionDialog);
        TextView textMensaje = viewDialogo.findViewById(R.id.textMensajeConfirmacionDialog);
        MaterialButton btnSi = viewDialogo.findViewById(R.id.btnConfirmarSi);
        MaterialButton btnNo = viewDialogo.findViewById(R.id.btnConfirmarNo);

        textTitulo.setText("Finalizar Revisión");
        textMensaje.setText(resumen);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(viewDialogo)
                .create();

        btnSi.setOnClickListener(v1 -> {
            renta.setKmRegreso(kmRegreso);
            renta.setGasolinaRegreso(gasRegreso);
            renta.setTieneLlantaRegreso(llantaRegreso);
            renta.setTieneHerramientasRegreso(herramientasRegreso);
            renta.setObservacionesRegreso(obs);
            renta.setRecargosExtras(recargos);

            renta.setMontoTotal(renta.getMontoTotal() + recargos);
            renta.setSaldoPendiente(renta.getSaldoPendiente() + recargos);
            renta.setEstadoRenta("Finalizada");

            gestorDatos.actualizarRenta(renta);

            if (v != null) {
                v.setKilometrajeActual(kmRegreso);
                v.setNivelCombustibleActual(gasRegreso);
                v.setEstado("Disponible");
                gestorDatos.actualizarVehiculo(v);
            }

            Toast.makeText(requireContext(), "Revisión finalizada y vehículo recibido", Toast.LENGTH_SHORT).show();
            cargarDatosRegreso();
            dialog.dismiss();

            mostrarContratoRecibo(renta);
        });

        btnNo.setOnClickListener(v1 -> dialog.dismiss());

        dialog.show();
    }

    private void inicializarComponentesHistorial(View view) {
        editBuscadorHistorial = view.findViewById(R.id.editBuscadorRentasHistorial);
        recyclerHistorial = view.findViewById(R.id.recyclerRentasHistorial);
        textSinRentasHistorial = view.findViewById(R.id.textSinRentasHistorial);

        recyclerHistorial.setLayoutManager(new LinearLayoutManager(requireContext()));
        adaptadorHistorial = new AdaptadorRentas(requireContext(), new ArrayList<>(), this);
        recyclerHistorial.setAdapter(adaptadorHistorial);

        editBuscadorHistorial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltroHistorial();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarDatosHistorial() {
        listaCompletaHistorial = gestorDatos.obtenerRentas();
        aplicarFiltroHistorial();
    }

    private void aplicarFiltroHistorial() {
        if (listaCompletaHistorial == null) return;

        String busqueda = editBuscadorHistorial.getText() != null ? editBuscadorHistorial.getText().toString().trim().toLowerCase() : "";

        ArrayList<Renta> listaFiltrada = new ArrayList<>();
        for (Renta r : listaCompletaHistorial) {
            boolean coincideBusqueda = busqueda.isEmpty() ||
                    (r.getNombreCliente() != null && r.getNombreCliente().toLowerCase().contains(busqueda)) ||
                    (r.getDocumentoCliente() != null && r.getDocumentoCliente().toLowerCase().contains(busqueda)) ||
                    (r.getLicenciaCliente() != null && r.getLicenciaCliente().toLowerCase().contains(busqueda));

            if (coincideBusqueda) {
                listaFiltrada.add(r);
            }
        }

        adaptadorHistorial.actualizarLista(listaFiltrada);

        if (listaFiltrada.isEmpty()) {
            textSinRentasHistorial.setVisibility(View.VISIBLE);
        } else {
            textSinRentasHistorial.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFinalizarRenta(Renta renta) {
        mostrarContratoRecibo(renta);
    }

    private void mostrarContratoRecibo(Renta renta) {
        View viewContrato = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_contrato_recibo, null);

        TextView textNumero = viewContrato.findViewById(R.id.textContratoNumero);
        TextView textCliente = viewContrato.findViewById(R.id.textContratoCliente);
        TextView textDocLic = viewContrato.findViewById(R.id.textContratoDocLic);
        TextView textTelefono = viewContrato.findViewById(R.id.textContratoTelefono);

        TextView textVehiculo = viewContrato.findViewById(R.id.textContratoVehiculo);
        TextView textFechas = viewContrato.findViewById(R.id.textContratoFechas);
        TextView textInspeccionSalida = viewContrato.findViewById(R.id.textContratoInspeccionSalida);
        TextView textInspeccionRegreso = viewContrato.findViewById(R.id.textContratoInspeccionRegreso);

        TextView textMontoBase = viewContrato.findViewById(R.id.textContratoMontoBase);
        TextView textRecargos = viewContrato.findViewById(R.id.textContratoRecargos);
        TextView textMontoTotal = viewContrato.findViewById(R.id.textContratoMontoTotal);
        TextView textMontoAbonado = viewContrato.findViewById(R.id.textContratoMontoAbonado);
        TextView textSaldoPendiente = viewContrato.findViewById(R.id.textContratoSaldoPendiente);
        TextView textDetalleEfectivoCambio = viewContrato.findViewById(R.id.textContratoDetalleEfectivoCambio);
        MaterialButton btnImprimir = viewContrato.findViewById(R.id.btnImprimirContrato);

        Vehiculo v = gestorDatos.obtenerVehiculoPorId(renta.getVehiculoId());

        textNumero.setText(String.format(Locale.getDefault(), "Contrato #%d - Estado: %s", renta.getId(), renta.getEstadoRenta().toUpperCase()));
        textCliente.setText(String.format("Cliente: %s", renta.getNombreCliente()));
        textDocLic.setText(String.format("DUI: %s | Licencia: %s", renta.getDocumentoCliente(), renta.getLicenciaCliente()));
        textTelefono.setText(String.format("Teléfono: %s", renta.getTelefonoCliente()));

        if (v != null) {
            textVehiculo.setText(String.format("Vehículo: %s %s (%s)", v.getMarca(), v.getModelo(), v.getPlaca()));
        } else {
            textVehiculo.setText(String.format(Locale.getDefault(), "Vehículo ID: %d", renta.getVehiculoId()));
        }

        textFechas.setText(String.format(Locale.getDefault(), "Fechas: %s a %s (%d días)", renta.getFechaInicio(), renta.getFechaFin(), renta.getDiasRenta()));

        int octavosSalida = Math.round((renta.getGasolinaSalida() / 100.0f) * 8.0f);
        textInspeccionSalida.setText(String.format(Locale.getDefault(), "Salida: Km %,.0f | Gasolina: %s | Llanta: %s | Kit: %s",
                renta.getKmSalida(), InventarioFragment.obtenerTextoOctavos(octavosSalida),
                renta.isTieneLlantaSalida() ? "Incluida" : "No incluida",
                renta.isTieneHerramientasSalida() ? "Incluido" : "No incluido"));

        if ("Finalizada".equalsIgnoreCase(renta.getEstadoRenta())) {
            int octavosRegreso = Math.round((renta.getGasolinaRegreso() / 100.0f) * 8.0f);
            textInspeccionRegreso.setText(String.format(Locale.getDefault(), "Regreso: Km %,.0f | Gasolina: %s | Llanta: %s | Kit: %s",
                    renta.getKmRegreso(), InventarioFragment.obtenerTextoOctavos(octavosRegreso),
                    renta.isTieneLlantaRegreso() ? "Devuelta" : "No entregada",
                    renta.isTieneHerramientasRegreso() ? "Devuelto" : "No entregado"));
        } else {
            textInspeccionRegreso.setText("Regreso: En alquiler activo (Pendiente de recepción)");
        }

        double montoBase = renta.getMontoTotal() - renta.getRecargosExtras();
        textMontoBase.setText(String.format(Locale.getDefault(), "Monto Base Contratado: $%.2f", montoBase));
        textRecargos.setText(String.format(Locale.getDefault(), "Recargos Extras por Faltantes: $%.2f", renta.getRecargosExtras()));
        textMontoTotal.setText(String.format(Locale.getDefault(), "Monto Total Final: $%.2f", renta.getMontoTotal()));

        double pagado = renta.getMontoTotal() - renta.getSaldoPendiente();
        textMontoAbonado.setText(String.format(Locale.getDefault(), "Total Abonado / Recaudado: $%.2f", pagado));
        textSaldoPendiente.setText(String.format(Locale.getDefault(), "Saldo Pendiente Actual: $%.2f", renta.getSaldoPendiente()));

        ArrayList<Abono> abonosRenta = gestorDatos.obtenerAbonosPorRentaId(renta.getId());
        if (!abonosRenta.isEmpty()) {
            Abono ultimoAbono = abonosRenta.get(abonosRenta.size() - 1);
            textDetalleEfectivoCambio.setText(String.format("Última transacción: %s (%s)", ultimoAbono.getMetodoPago(), ultimoAbono.getNota()));
        } else {
            textDetalleEfectivoCambio.setText("Sin pagos registrados aún.");
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(viewContrato)
                .create();

        btnImprimir.setOnClickListener(v1 -> {
            ArrayList<Abono> abonosRentaLista = gestorDatos.obtenerAbonosPorRentaId(renta.getId());
            com.example.alquilervehiculos.utilidades.GestorImpresionPDF.imprimirContratoRenta(requireContext(), renta, v, abonosRentaLista);
            dialog.dismiss();
        });

        dialog.show();
    }
}
