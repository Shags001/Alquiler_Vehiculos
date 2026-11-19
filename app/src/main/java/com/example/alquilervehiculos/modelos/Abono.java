package com.example.alquilervehiculos.modelos;

public class Abono {
    private int id;
    private int rentaId;
    private String fecha;
    private double monto;
    private String metodoPago; // "Efectivo", "Tarjeta", "Transferencia"
    private String nota;

    public Abono() {
    }

    public Abono(int id, int rentaId, String fecha, double monto, String metodoPago, String nota) {
        this.id = id;
        this.rentaId = rentaId;
        this.fecha = fecha;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.nota = nota;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRentaId() {
        return rentaId;
    }

    public void setRentaId(int rentaId) {
        this.rentaId = rentaId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }
}
