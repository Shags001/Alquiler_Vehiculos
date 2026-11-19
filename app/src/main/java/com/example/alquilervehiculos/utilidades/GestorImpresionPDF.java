package com.example.alquilervehiculos.utilidades;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.widget.Toast;

import com.example.alquilervehiculos.fragmentos.InventarioFragment;
import com.example.alquilervehiculos.modelos.Abono;
import com.example.alquilervehiculos.modelos.Renta;
import com.example.alquilervehiculos.modelos.Vehiculo;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class GestorImpresionPDF {

    public static void imprimirContratoRenta(Context context, Renta renta, Vehiculo vehiculo, ArrayList<Abono> abonos) {
        if (context == null || renta == null) return;

        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        String jobName = "Contrato_Alquiler_Orellana_" + renta.getId();

        if (printManager != null) {
            printManager.print(jobName, new PrintDocumentAdapter() {
                private PdfDocument pdfDocument;

                @Override
                public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                    pdfDocument = new PdfDocument();
                    PrintDocumentInfo info = new PrintDocumentInfo.Builder(jobName + ".pdf")
                            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                            .setPageCount(1)
                            .build();
                    callback.onLayoutFinished(info, true);
                }

                @Override
                public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();

                    // Encabezado
                    paint.setColor(Color.parseColor("#29B6F6"));
                    canvas.drawRect(0, 0, 595, 80, paint);

                    paint.setColor(Color.WHITE);
                    paint.setTextSize(22);
                    paint.setFakeBoldText(true);
                    canvas.drawText("ALQUILER ORELLANA", 40, 45, paint);

                    paint.setTextSize(12);
                    canvas.drawText("CONTRATO DE ARRENDAMIENTO Y COMPROBANTE OFICIAL", 40, 65, paint);

                    // Secciones del Contrato
                    paint.setColor(Color.parseColor("#424242"));
                    paint.setTextSize(14);
                    paint.setFakeBoldText(true);

                    int y = 120;
                    canvas.drawText("CONTRATO N°: " + renta.getId() + " | ESTADO: " + renta.getEstadoRenta().toUpperCase(), 40, y, paint);
                    y += 25;

                    paint.setFakeBoldText(false);
                    paint.setTextSize(11);

                    // Datos Cliente
                    canvas.drawText("ARRENDATARIO (CLIENTE): " + renta.getNombreCliente(), 40, y, paint); y += 18;
                    canvas.drawText("DUI / DOC: " + renta.getDocumentoCliente() + " | LICENCIA: " + renta.getLicenciaCliente(), 40, y, paint); y += 18;
                    canvas.drawText("TELÉFONO: " + renta.getTelefonoCliente(), 40, y, paint); y += 25;

                    // Datos Vehículo
                    paint.setFakeBoldText(true);
                    canvas.drawText("DATOS DE LA UNIDAD:", 40, y, paint); y += 18;
                    paint.setFakeBoldText(false);

                    String nombreVehiculo = vehiculo != null ? vehiculo.getMarca() + " " + vehiculo.getModelo() + " (Placa: " + vehiculo.getPlaca() + ")" : "ID #" + renta.getVehiculoId();
                    canvas.drawText("VEHÍCULO: " + nombreVehiculo, 40, y, paint); y += 18;
                    canvas.drawText("PERÍODO: " + renta.getFechaInicio() + " a " + renta.getFechaFin() + " (" + renta.getDiasRenta() + " días)", 40, y, paint); y += 18;

                    int octavosSalida = Math.round((renta.getGasolinaSalida() / 100.0f) * 8.0f);
                    canvas.drawText("SALIDA: Km " + String.format(Locale.getDefault(), "%,.0f", renta.getKmSalida()) + " | Gas: " + InventarioFragment.obtenerTextoOctavos(octavosSalida), 40, y, paint); y += 18;

                    if ("Finalizada".equalsIgnoreCase(renta.getEstadoRenta())) {
                        int octavosRegreso = Math.round((renta.getGasolinaRegreso() / 100.0f) * 8.0f);
                        canvas.drawText("REGRESO: Km " + String.format(Locale.getDefault(), "%,.0f", renta.getKmRegreso()) + " | Gas: " + InventarioFragment.obtenerTextoOctavos(octavosRegreso), 40, y, paint); y += 25;
                    } else {
                        canvas.drawText("REGRESO: En alquiler activo (Pendiente de devolución)", 40, y, paint); y += 25;
                    }

                    // Desglose Financiero
                    paint.setFakeBoldText(true);
                    canvas.drawText("DESGLOSE FINANCIERO Y PAGOS:", 40, y, paint); y += 18;
                    paint.setFakeBoldText(false);

                    double montoBase = renta.getMontoTotal() - renta.getRecargosExtras();
                    canvas.drawText("MONTO BASE: $" + String.format(Locale.getDefault(), "%.2f", montoBase), 40, y, paint); y += 18;
                    canvas.drawText("RECARGOS EXTRAS: $" + String.format(Locale.getDefault(), "%.2f", renta.getRecargosExtras()), 40, y, paint); y += 18;
                    canvas.drawText("MONTO TOTAL FINAL: $" + String.format(Locale.getDefault(), "%.2f", renta.getMontoTotal()), 40, y, paint); y += 18;

                    double pagado = renta.getMontoTotal() - renta.getSaldoPendiente();
                    canvas.drawText("TOTAL ABONADO: $" + String.format(Locale.getDefault(), "%.2f", pagado) + " | SALDO PENDIENTE: $" + String.format(Locale.getDefault(), "%.2f", renta.getSaldoPendiente()), 40, y, paint); y += 35;

                    // Cláusula y Firmas
                    paint.setTextSize(9);
                    canvas.drawText("El arrendatario acepta las condiciones físicas del vehículo e inspecciones detalladas en este contrato.", 40, y, paint); y += 60;

                    paint.setTextSize(10);
                    paint.setFakeBoldText(true);
                    canvas.drawLine(60, y, 220, y, paint);
                    canvas.drawLine(340, y, 500, y, paint); y += 15;
                    canvas.drawText("Firma Arrendador (Empresa)", 80, y, paint);
                    canvas.drawText("Firma Arrendatario (Cliente)", 360, y, paint);

                    pdfDocument.finishPage(page);

                    try {
                        pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                    } catch (Exception e) {
                        callback.onWriteFailed(e.getMessage());
                    } finally {
                        pdfDocument.close();
                    }
                }
            }, null);
        }
    }

    public static void imprimirReporteEstadisticas(Context context, ArrayList<Vehiculo> vehiculos, ArrayList<Renta> rentas, ArrayList<Abono> abonos, String vehiculoFiltroNombre) {
        if (context == null) return;

        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        String jobName = "Reporte_Estadisticas_Alquiler_Orellana";

        if (printManager != null) {
            printManager.print(jobName, new PrintDocumentAdapter() {
                private PdfDocument pdfDocument;

                @Override
                public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                    pdfDocument = new PdfDocument();
                    PrintDocumentInfo info = new PrintDocumentInfo.Builder(jobName + ".pdf")
                            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                            .setPageCount(1)
                            .build();
                    callback.onLayoutFinished(info, true);
                }

                @Override
                public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();

                    // Encabezado Reporte
                    paint.setColor(Color.parseColor("#29B6F6"));
                    canvas.drawRect(0, 0, 595, 80, paint);

                    paint.setColor(Color.WHITE);
                    paint.setTextSize(20);
                    paint.setFakeBoldText(true);
                    canvas.drawText("ALQUILER ORELLANA", 40, 45, paint);

                    paint.setTextSize(12);
                    canvas.drawText("INFORME Y REPORTE GENERAL DE RENDIMIENTO DE FLOTA", 40, 65, paint);

                    paint.setColor(Color.parseColor("#424242"));
                    int y = 120;

                    paint.setTextSize(12);
                    paint.setFakeBoldText(true);
                    canvas.drawText("FILTRO APLICADO: " + (vehiculoFiltroNombre != null ? vehiculoFiltroNombre : "Todos los vehículos"), 40, y, paint); y += 30;

                    double totalRecaudado = 0.0;
                    for (Abono a : abonos) {
                        totalRecaudado += a.getMonto();
                    }

                    double totalContratado = 0.0;
                    double saldoPendienteGlobal = 0.0;
                    double kmRecorridos = 0.0;
                    for (Renta r : rentas) {
                        totalContratado += r.getMontoTotal();
                        saldoPendienteGlobal += r.getSaldoPendiente();
                        if ("Finalizada".equalsIgnoreCase(r.getEstadoRenta()) && r.getKmRegreso() >= r.getKmSalida()) {
                            kmRecorridos += (r.getKmRegreso() - r.getKmSalida());
                        }
                    }

                    paint.setFakeBoldText(false);
                    paint.setTextSize(11);
                    canvas.drawText("TOTAL VEHÍCULOS EN FLOTA: " + vehiculos.size(), 40, y, paint); y += 20;
                    canvas.drawText("CANTIDAD DE RENTAS REGISTRADAS: " + rentas.size(), 40, y, paint); y += 20;
                    canvas.drawText("KILOMETRAJE TOTAL RECORRIDO: " + String.format(Locale.getDefault(), "%,.1f km", kmRecorridos), 40, y, paint); y += 20;
                    canvas.drawText("INGRESOS REALES RECAUDADOS: $" + String.format(Locale.getDefault(), "%.2f", totalRecaudado), 40, y, paint); y += 20;
                    canvas.drawText("TOTAL CONTRATADO GLOBAL: $" + String.format(Locale.getDefault(), "%.2f", totalContratado), 40, y, paint); y += 20;
                    canvas.drawText("SALDO PENDIENTE POR COBRAR: $" + String.format(Locale.getDefault(), "%.2f", saldoPendienteGlobal), 40, y, paint); y += 35;

                    paint.setFakeBoldText(true);
                    canvas.drawText("DESGLOSE DE UNIDADES EN FLOTA:", 40, y, paint); y += 20;
                    paint.setFakeBoldText(false);

                    for (Vehiculo v : vehiculos) {
                        canvas.drawText("• " + v.getMarca() + " " + v.getModelo() + " (" + v.getPlaca() + ") - Estado: " + v.getEstado() + " | Km: " + String.format(Locale.getDefault(), "%,.0f", v.getKilometrajeActual()) + " | Gas: " + v.getNivelCombustibleActual() + "%", 45, y, paint);
                        y += 18;
                    }

                    pdfDocument.finishPage(page);

                    try {
                        pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                    } catch (Exception e) {
                        callback.onWriteFailed(e.getMessage());
                    } finally {
                        pdfDocument.close();
                    }
                }
            }, null);
        } else {
            Toast.makeText(context, "Servicio de impresión no disponible", Toast.LENGTH_SHORT).show();
        }
    }
}
