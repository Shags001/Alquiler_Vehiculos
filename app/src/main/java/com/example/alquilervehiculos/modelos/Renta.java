package com.example.alquilervehiculos.modelos;

public class Renta {
    private int id;
    private int vehiculoId;
    private String nombreCliente;
    private String documentoCliente;
    private String licenciaCliente;
    private String telefonoCliente;
    private String fechaInicio;
    private String fechaFin;
    private int diasRenta;
    private double montoTotal;
    private double saldoPendiente;
    private String estadoRenta; // "Activa", "Finalizada", "Cancelada"

    // Campos de Inspección de Salida
    private double kmSalida;
    private int gasolinaSalida;
    private boolean tieneLlantaSalida;
    private boolean tieneHerramientasSalida;
    private String observacionesSalida;

    // Campos de Inspección de Regreso
    private double kmRegreso;
    private int gasolinaRegreso;
    private boolean tieneLlantaRegreso;
    private boolean tieneHerramientasRegreso;
    private String observacionesRegreso;
    private double recargosExtras;

    public Renta() {
    }

    public Renta(int id, int vehiculoId, String nombreCliente, String documentoCliente, String licenciaCliente, String telefonoCliente,
                 String fechaInicio, String fechaFin, int diasRenta, double montoTotal, double saldoPendiente, String estadoRenta,
                 double kmSalida, int gasolinaSalida, boolean tieneLlantaSalida, boolean tieneHerramientasSalida, String observacionesSalida) {
        this.id = id;
        this.vehiculoId = vehiculoId;
        this.nombreCliente = nombreCliente;
        this.documentoCliente = documentoCliente;
        this.licenciaCliente = licenciaCliente;
        this.telefonoCliente = telefonoCliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diasRenta = diasRenta;
        this.montoTotal = montoTotal;
        this.saldoPendiente = saldoPendiente;
        this.estadoRenta = estadoRenta;

        this.kmSalida = kmSalida;
        this.gasolinaSalida = gasolinaSalida;
        this.tieneLlantaSalida = tieneLlantaSalida;
        this.tieneHerramientasSalida = tieneHerramientasSalida;
        this.observacionesSalida = observacionesSalida;

        // Por defecto en salida
        this.kmRegreso = kmSalida;
        this.gasolinaRegreso = gasolinaSalida;
        this.tieneLlantaRegreso = tieneLlantaSalida;
        this.tieneHerramientasRegreso = tieneHerramientasSalida;
        this.observacionesRegreso = "";
        this.recargosExtras = 0.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(int vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getDocumentoCliente() {
        return documentoCliente;
    }

    public void setDocumentoCliente(String documentoCliente) {
        this.documentoCliente = documentoCliente;
    }

    public String getLicenciaCliente() {
        return licenciaCliente;
    }

    public void setLicenciaCliente(String licenciaCliente) {
        this.licenciaCliente = licenciaCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getDiasRenta() {
        return diasRenta;
    }

    public void setDiasRenta(int diasRenta) {
        this.diasRenta = diasRenta;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public String getEstadoRenta() {
        return estadoRenta;
    }

    public void setEstadoRenta(String estadoRenta) {
        this.estadoRenta = estadoRenta;
    }

    public double getKmSalida() {
        return kmSalida;
    }

    public void setKmSalida(double kmSalida) {
        this.kmSalida = kmSalida;
    }

    public int getGasolinaSalida() {
        return gasolinaSalida;
    }

    public void setGasolinaSalida(int gasolinaSalida) {
        this.gasolinaSalida = gasolinaSalida;
    }

    public boolean isTieneLlantaSalida() {
        return tieneLlantaSalida;
    }

    public void setTieneLlantaSalida(boolean tieneLlantaSalida) {
        this.tieneLlantaSalida = tieneLlantaSalida;
    }

    public boolean isTieneHerramientasSalida() {
        return tieneHerramientasSalida;
    }

    public void setTieneHerramientasSalida(boolean tieneHerramientasSalida) {
        this.tieneHerramientasSalida = tieneHerramientasSalida;
    }

    public String getObservacionesSalida() {
        return observacionesSalida;
    }

    public void setObservacionesSalida(String observacionesSalida) {
        this.observacionesSalida = observacionesSalida;
    }

    public double getKmRegreso() {
        return kmRegreso;
    }

    public void setKmRegreso(double kmRegreso) {
        this.kmRegreso = kmRegreso;
    }

    public int getGasolinaRegreso() {
        return gasolinaRegreso;
    }

    public void setGasolinaRegreso(int gasolinaRegreso) {
        this.gasolinaRegreso = gasolinaRegreso;
    }

    public boolean isTieneLlantaRegreso() {
        return tieneLlantaRegreso;
    }

    public void setTieneLlantaRegreso(boolean tieneLlantaRegreso) {
        this.tieneLlantaRegreso = tieneLlantaRegreso;
    }

    public boolean isTieneHerramientasRegreso() {
        return tieneHerramientasRegreso;
    }

    public void setTieneHerramientasRegreso(boolean tieneHerramientasRegreso) {
        this.tieneHerramientasRegreso = tieneHerramientasRegreso;
    }

    public String getObservacionesRegreso() {
        return observacionesRegreso;
    }

    public void setObservacionesRegreso(String observacionesRegreso) {
        this.observacionesRegreso = observacionesRegreso;
    }

    public double getRecargosExtras() {
        return recargosExtras;
    }

    public void setRecargosExtras(double recargosExtras) {
        this.recargosExtras = recargosExtras;
    }
}
