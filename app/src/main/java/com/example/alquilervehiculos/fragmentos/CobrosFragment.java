package com.example.alquilervehiculos.fragmentos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.alquilervehiculos.adaptadores.AdaptadorAbonos;
import com.example.alquilervehiculos.datos.GestorDatos;
import com.example.alquilervehiculos.modelos.Abono;
import com.example.alquilervehiculos.modelos.Renta;
import com.example.alquilervehiculos.modelos.Vehiculo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CobrosFragment extends Fragment implements AdaptadorAbonos.OnAbonoClickListener {

    private TextInputEditText editBuscadorAbonos;
    private RecyclerView recyclerAbonos;
    private TextView textSinAbonos;
    private FloatingActionButton fabRegistrarAbono;

    private GestorDatos gestorDatos;
    private AdaptadorAbonos adaptador;
    private ArrayList<Abono> listaCompletaAbonos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cobros, container, false);

        gestorDatos = GestorDatos.obtenerInstancia(requireContext());

        editBuscadorAbonos = view.findViewById(R.id.editBuscadorAbonos);
        recyclerAbonos = view.findViewById(R.id.recyclerAbonos);
        textSinAbonos = view.findViewById(R.id.textSinAbonos);
        fabRegistrarAbono = view.findViewById(R.id.fabRegistrarAbono);

        recyclerAbonos.setLayoutManager(new LinearLayoutManager(requireContext()));
        adaptador = new AdaptadorAbonos(requireContext(), new ArrayList<>(), this);
        recyclerAbonos.setAdapter(adaptador);

        fabRegistrarAbono.setOnClickListener(v -> mostrarDialogoNuevoAbono());

        configurarBuscadorAbonos();
        cargarAbonos();

        return view;
    }

    private void configurarBuscadorAbonos() {
        editBuscadorAbonos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltroBuscador();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void cargarAbonos() {
        listaCompletaAbonos = gestorDatos.obtenerAbonos();
        aplicarFiltroBuscador();
    }

    private void aplicarFiltroBuscador() {
        if (listaCompletaAbonos == null) return;

        String busqueda = editBuscadorAbonos.getText() != null ? editBuscadorAbonos.getText().toString().trim().toLowerCase() : "";

        ArrayList<Abono> listaFiltrada = new ArrayList<>();
        for (Abono a : listaCompletaAbonos) {
            Renta renta = gestorDatos.obtenerRentaPorId(a.getRentaId());

            boolean coincideCliente = renta != null && renta.getNombreCliente() != null && renta.getNombreCliente().toLowerCase().contains(busqueda);
            boolean coincideDoc = renta != null && renta.getDocumentoCliente() != null && renta.getDocumentoCliente().toLowerCase().contains(busqueda);
            boolean coincideRentaId = String.valueOf(a.getRentaId()).contains(busqueda);
            boolean coincideAbonoId = String.valueOf(a.getId()).contains(busqueda);
            boolean coincideMetodo = a.getMetodoPago() != null && a.getMetodoPago().toLowerCase().contains(busqueda);
            boolean coincideNota = a.getNota() != null && a.getNota().toLowerCase().contains(busqueda);

            if (busqueda.isEmpty() || coincideCliente || coincideDoc || coincideRentaId || coincideAbonoId || coincideMetodo || coincideNota) {
                listaFiltrada.add(a);
            }
        }

        adaptador.actualizarLista(listaFiltrada);

        if (listaFiltrada.isEmpty()) {
            textSinAbonos.setVisibility(View.VISIBLE);
        } else {
            textSinAbonos.setVisibility(View.GONE);
        }
    }

    private void mostrarDialogoNuevoAbono() {
        ArrayList<Renta> rentasTodas = gestorDatos.obtenerRentas();
        ArrayList<Renta> rentasPendientes = new ArrayList<>();

        for (Renta r : rentasTodas) {
            if ("Finalizada".equalsIgnoreCase(r.getEstadoRenta()) && r.getSaldoPendiente() > 0) {
                rentasPendientes.add(r);
            }
        }
        for (Renta r : rentasTodas) {
            if ("Activa".equalsIgnoreCase(r.getEstadoRenta()) && r.getSaldoPendiente() > 0) {
                rentasPendientes.add(r);
            }
        }

        if (rentasPendientes.isEmpty()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Sin Saldos Pendientes")
                    .setMessage("No hay rentas activas o finalizadas con saldo pendiente de pago en este momento.")
                    .setPositiveButton("Aceptar", null)
                    .show();
            return;
        }

        View viewDialogo = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_abono, null);

        Spinner spinnerRentas = viewDialogo.findViewById(R.id.spinnerRentasPendientes);
        RadioGroup radioGroupModalidad = viewDialogo.findViewById(R.id.radioGroupModalidadPago);
        RadioButton radioPagoDirecto = viewDialogo.findViewById(R.id.radioPagoDirecto);
        TextInputEditText editMonto = viewDialogo.findViewById(R.id.editMontoAbono);
        Spinner spinnerMetodos = viewDialogo.findViewById(R.id.spinnerMetodoPago);

        LinearLayout layoutEfectivo = viewDialogo.findViewById(R.id.layoutMetodoEfectivo);
        TextInputEditText editEfectivoRecibido = viewDialogo.findViewById(R.id.editEfectivoRecibido);
        TextView textCambioAEntregar = viewDialogo.findViewById(R.id.textCambioAEntregar);

        TextInputEditText editNota = viewDialogo.findViewById(R.id.editNotaAbono);

        // Llenar Spinner de Rentas
        ArrayList<String> titulosRentas = new ArrayList<>();
        for (Renta r : rentasPendientes) {
            String etiquetaEstado = "Finalizada".equalsIgnoreCase(r.getEstadoRenta()) ? "FINALIZADA" : "ACTIVA";
            titulosRentas.add(String.format(Locale.getDefault(), "[%s] %s (Renta #%d) - Saldo: $%.2f",
                    etiquetaEstado, r.getNombreCliente(), r.getId(), r.getSaldoPendiente()));
        }
        ArrayAdapter<String> adapterRentas = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, titulosRentas);
        adapterRentas.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerRentas.setAdapter(adapterRentas);

        // Métodos de Pago permitidos: Efectivo y Tarjeta únicamente
        String[] metodos = {"Efectivo", "Tarjeta"};
        ArrayAdapter<String> adapterMetodos = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, metodos);
        adapterMetodos.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerMetodos.setAdapter(adapterMetodos);

        Runnable calcularCambioEfectivo = () -> {
            String montoStr = editMonto.getText() != null ? editMonto.getText().toString().trim() : "";
            String efectivoStr = editEfectivoRecibido.getText() != null ? editEfectivoRecibido.getText().toString().trim() : "";

            if (!montoStr.isEmpty() && !efectivoStr.isEmpty()) {
                double montoVal = Double.parseDouble(montoStr);
                double efectivoVal = Double.parseDouble(efectivoStr);
                double cambio = efectivoVal - montoVal;
                if (cambio < 0) cambio = 0;
                textCambioAEntregar.setText(String.format(Locale.getDefault(), "Cambio a Entregar: $%.2f", cambio));
            } else {
                textCambioAEntregar.setText("Cambio a Entregar: $0.00");
            }
        };

        editEfectivoRecibido.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calcularCambioEfectivo.run(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        editMonto.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calcularCambioEfectivo.run(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        spinnerMetodos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Efectivo
                    layoutEfectivo.setVisibility(View.VISIBLE);
                } else { // Tarjeta
                    layoutEfectivo.setVisibility(View.GONE);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        Runnable actualizarMontoModalidad = () -> {
            int pos = spinnerRentas.getSelectedItemPosition();
            if (pos >= 0 && pos < rentasPendientes.size()) {
                Renta rElegida = rentasPendientes.get(pos);
                if (radioPagoDirecto.isChecked()) {
                    editMonto.setText(String.format(Locale.getDefault(), "%.2f", rElegida.getSaldoPendiente()));
                    editMonto.setEnabled(false);
                } else {
                    editMonto.setEnabled(true);
                    if (editMonto.getText() == null || editMonto.getText().toString().isEmpty()) {
                        editMonto.setText(String.format(Locale.getDefault(), "%.2f", rElegida.getSaldoPendiente()));
                    }
                }
                calcularCambioEfectivo.run();
            }
        };

        spinnerRentas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarMontoModalidad.run();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        radioGroupModalidad.setOnCheckedChangeListener((group, checkedId) -> actualizarMontoModalidad.run());
        actualizarMontoModalidad.run();

        new AlertDialog.Builder(requireContext())
                .setView(viewDialogo)
                .setPositiveButton("Registrar Pago", (dialog, which) -> {
                    int posRenta = spinnerRentas.getSelectedItemPosition();
                    String montoStr = editMonto.getText() != null ? editMonto.getText().toString().trim() : "";
                    int posMetodo = spinnerMetodos.getSelectedItemPosition();
                    String notaStr = editNota.getText() != null ? editNota.getText().toString().trim() : "";

                    if (montoStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Por favor ingresa un monto válido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (posRenta < 0 || posRenta >= rentasPendientes.size()) {
                        Toast.makeText(requireContext(), "Selecciona una renta válida", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double monto = Double.parseDouble(montoStr);
                    Renta rentaElegida = rentasPendientes.get(posRenta);

                    if (monto <= 0) {
                        Toast.makeText(requireContext(), "El monto debe ser mayor a cero", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (monto > rentaElegida.getSaldoPendiente() + 0.01) {
                        Toast.makeText(requireContext(), String.format(Locale.getDefault(), "El monto ($%.2f) supera el saldo pendiente ($%.2f)", monto, rentaElegida.getSaldoPendiente()), Toast.LENGTH_LONG).show();
                        return;
                    }

                    String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String metodoPago = metodos[posMetodo];

                    String detalleMetodo = "";
                    if (posMetodo == 0) { // Efectivo
                        String efecStr = editEfectivoRecibido.getText() != null ? editEfectivoRecibido.getText().toString().trim() : "";
                        double efectivoRecibido = efecStr.isEmpty() ? monto : Double.parseDouble(efecStr);
                        double cambio = efectivoRecibido - monto;
                        if (cambio < 0) cambio = 0;
                        detalleMetodo = String.format(Locale.getDefault(), "Efectivo Recibido: $%.2f | Cambio: $%.2f", efectivoRecibido, cambio);
                    }

                    String notaFinal = (notaStr.isEmpty() ? "" : notaStr + (detalleMetodo.isEmpty() ? "" : " | ")) + detalleMetodo;

                    Abono nuevoAbono = new Abono(0, rentaElegida.getId(), fechaHoy, monto, metodoPago, notaFinal);
                    gestorDatos.agregarAbono(nuevoAbono);

                    Toast.makeText(requireContext(), "Pago registrado con éxito", Toast.LENGTH_SHORT).show();
                    cargarAbonos();

                    // Mostrar contrato / recibo imprimible
                    mostrarContratoRecibo(rentaElegida);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onVerComprobante(Abono abono) {
        Renta renta = gestorDatos.obtenerRentaPorId(abono.getRentaId());
        if (renta != null) {
            mostrarContratoRecibo(renta);
        } else {
            Toast.makeText(requireContext(), "Renta no encontrada", Toast.LENGTH_SHORT).show();
        }
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
