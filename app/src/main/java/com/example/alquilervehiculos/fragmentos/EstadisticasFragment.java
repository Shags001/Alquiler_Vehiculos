package com.example.alquilervehiculos.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.alquilervehiculos.R;
import com.example.alquilervehiculos.datos.GestorDatos;
import com.example.alquilervehiculos.modelos.Abono;
import com.example.alquilervehiculos.modelos.Renta;
import com.example.alquilervehiculos.modelos.Vehiculo;
import com.example.alquilervehiculos.utilidades.GestorImpresionPDF;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EstadisticasFragment extends Fragment {

    private Spinner spinnerFiltroVehiculo;
    private TextView textTotalVehiculos;
    private TextView textDetalleFlota;
    private TextView textPorcentajeOcupacion;
    private ProgressBar progressOcupacion;

    private TextView textTotalRecaudado;
    private TextView textKmRecorridos;
    private TextView textSaldoPendienteTotal;
    private TextView textUltimaActividad;
    private MaterialButton btnImprimirReporte;

    private GestorDatos gestorDatos;
    private ArrayList<Vehiculo> listaVehiculos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        gestorDatos = GestorDatos.obtenerInstancia(requireContext());

        spinnerFiltroVehiculo = view.findViewById(R.id.spinnerFiltroVehiculoStat);
        textTotalVehiculos = view.findViewById(R.id.textTotalVehiculosStat);
        textDetalleFlota = view.findViewById(R.id.textDetalleFlotaStat);
        textPorcentajeOcupacion = view.findViewById(R.id.textPorcentajeOcupacionStat);
        progressOcupacion = view.findViewById(R.id.progressOcupacionStat);

        textTotalRecaudado = view.findViewById(R.id.textTotalRecaudadoStat);
        textKmRecorridos = view.findViewById(R.id.textKmRecorridosStat);
        textSaldoPendienteTotal = view.findViewById(R.id.textSaldoPendienteTotalStat);
        textUltimaActividad = view.findViewById(R.id.textUltimaActividadStat);
        btnImprimirReporte = view.findViewById(R.id.btnImprimirReporteStat);

        configurarSpinnerVehiculos();

        btnImprimirReporte.setOnClickListener(v -> {
            int pos = spinnerFiltroVehiculo.getSelectedItemPosition();
            String filtroNombre = pos == 0 ? "Todos los Vehículos (Flota Completa)" : spinnerFiltroVehiculo.getSelectedItem().toString();

            GestorImpresionPDF.imprimirReporteEstadisticas(
                    requireContext(),
                    gestorDatos.obtenerVehiculos(),
                    gestorDatos.obtenerRentas(),
                    gestorDatos.obtenerAbonos(),
                    filtroNombre
            );
        });

        return view;
    }

    private void configurarSpinnerVehiculos() {
        listaVehiculos = gestorDatos.obtenerVehiculos();
        ArrayList<String> nombres = new ArrayList<>();
        nombres.add("Todos los Vehículos (Flota Completa)");

        for (Vehiculo v : listaVehiculos) {
            nombres.add(String.format(Locale.getDefault(), "%s %s (%s)", v.getMarca(), v.getModelo(), v.getPlaca()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, nombres);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerFiltroVehiculo.setAdapter(adapter);

        spinnerFiltroVehiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calcularYMostrarEstadisticas(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void calcularYMostrarEstadisticas(int posicionSeleccionada) {
        ArrayList<Renta> todasRentas = gestorDatos.obtenerRentas();
        ArrayList<Abono> todosAbonos = gestorDatos.obtenerAbonos();

        Map<Integer, Renta> mapaRentas = new HashMap<>();
        for (Renta r : todasRentas) {
            mapaRentas.put(r.getId(), r);
        }

        if (posicionSeleccionada == 0) {
            // MODO FLOTA COMPLETA
            int totalVehiculos = listaVehiculos.size();
            int disponibles = 0;
            int rentados = 0;

            for (Vehiculo v : listaVehiculos) {
                if ("Rentado".equalsIgnoreCase(v.getEstado())) {
                    rentados++;
                } else {
                    disponibles++;
                }
            }

            textTotalVehiculos.setText(String.valueOf(todasRentas.size()));
            textDetalleFlota.setText(String.format(Locale.getDefault(), "Flota: %d Disp. / %d Rent.", disponibles, rentados));

            int porcentajeOcupacion = totalVehiculos > 0 ? (int) Math.round(((double) rentados / totalVehiculos) * 100) : 0;
            textPorcentajeOcupacion.setText(String.format(Locale.getDefault(), "%d%%", porcentajeOcupacion));
            progressOcupacion.setProgress(porcentajeOcupacion);

            double totalRecaudado = 0.0;
            for (Abono a : todosAbonos) {
                totalRecaudado += a.getMonto();
            }

            double kmTotalRecorrido = 0.0;
            double saldoPendienteGlobal = 0.0;
            for (Renta r : todasRentas) {
                saldoPendienteGlobal += r.getSaldoPendiente();
                if ("Finalizada".equalsIgnoreCase(r.getEstadoRenta()) && r.getKmRegreso() >= r.getKmSalida()) {
                    kmTotalRecorrido += (r.getKmRegreso() - r.getKmSalida());
                }
            }

            textTotalRecaudado.setText(String.format(Locale.getDefault(), "$%.2f", totalRecaudado));
            textKmRecorridos.setText(String.format(Locale.getDefault(), "%,.1f km", kmTotalRecorrido));
            textSaldoPendienteTotal.setText(String.format(Locale.getDefault(), "$%.2f", saldoPendienteGlobal));

            if (!todasRentas.isEmpty()) {
                Renta ultima = todasRentas.get(todasRentas.size() - 1);
                Vehiculo v = gestorDatos.obtenerVehiculoPorId(ultima.getVehiculoId());
                String nombreV = v != null ? v.getMarca() + " " + v.getModelo() : "Vehículo #" + ultima.getVehiculoId();
                textUltimaActividad.setText(String.format(Locale.getDefault(), "Última renta: %s (%s) - Renta #%d", ultima.getNombreCliente(), nombreV, ultima.getId()));
            } else {
                textUltimaActividad.setText("No hay rentas registradas aún en la flota.");
            }

        } else {
            // MODO VEHÍCULO ESPECÍFICO
            Vehiculo vElegido = listaVehiculos.get(posicionSeleccionada - 1);

            int cantRentasVehiculo = 0;
            double totalContratadoVehiculo = 0.0;
            double saldoPendienteVehiculo = 0.0;
            double kmRecorridosVehiculo = 0.0;

            for (Renta r : todasRentas) {
                if (r.getVehiculoId() == vElegido.getId()) {
                    cantRentasVehiculo++;
                    totalContratadoVehiculo += r.getMontoTotal();
                    saldoPendienteVehiculo += r.getSaldoPendiente();
                    if ("Finalizada".equalsIgnoreCase(r.getEstadoRenta()) && r.getKmRegreso() >= r.getKmSalida()) {
                        kmRecorridosVehiculo += (r.getKmRegreso() - r.getKmSalida());
                    }
                }
            }

            double recaudadoRealVehiculo = 0.0;
            for (Abono a : todosAbonos) {
                Renta r = mapaRentas.get(a.getRentaId());
                if (r != null && r.getVehiculoId() == vElegido.getId()) {
                    recaudadoRealVehiculo += a.getMonto();
                }
            }

            textTotalVehiculos.setText(String.valueOf(cantRentasVehiculo));
            textDetalleFlota.setText(String.format(Locale.getDefault(), "Estado Actual: %s", vElegido.getEstado()));

            int porcentajeRentabilidad = 0;
            if (totalContratadoVehiculo > 0) {
                porcentajeRentabilidad = (int) Math.round((recaudadoRealVehiculo / totalContratadoVehiculo) * 100);
            }
            if (porcentajeRentabilidad > 100) porcentajeRentabilidad = 100;

            textPorcentajeOcupacion.setText(String.format(Locale.getDefault(), "%d%%", porcentajeRentabilidad));
            progressOcupacion.setProgress(porcentajeRentabilidad);

            textTotalRecaudado.setText(String.format(Locale.getDefault(), "$%.2f", recaudadoRealVehiculo));
            textKmRecorridos.setText(String.format(Locale.getDefault(), "%,.1f km", kmRecorridosVehiculo));
            textSaldoPendienteTotal.setText(String.format(Locale.getDefault(), "$%.2f", saldoPendienteVehiculo));

            textUltimaActividad.setText(String.format(Locale.getDefault(),
                    "Vehículo: %s %s (%d)\nPlaca: %s | Km Actual: %,.1f km | Gasolina: %d%%\nRentas totales: %d | Recaudado: $%.2f",
                    vElegido.getMarca(), vElegido.getModelo(), vElegido.getAnio(),
                    vElegido.getPlaca(), vElegido.getKilometrajeActual(), vElegido.getNivelCombustibleActual(),
                    cantRentasVehiculo, recaudadoRealVehiculo));
        }
    }
}
