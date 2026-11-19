package com.example.alquilervehiculos.modelos;

import java.util.ArrayList;

public class Vehiculo {
    private int id;
    private String marca;
    private String modelo;
    private int anio;
    private String placa;
    private double tarifaDiaria;
    private String estado; // "Disponible" o "Rentado"
    private ArrayList<String> imagenes;
    private double kilometrajeActual;
    private int nivelCombustibleActual; // 0 - 100%

    // Tarifas de extras configurables por vehículo
    private double tarifaLlantaFaltante;
    private double tarifaHerramientasFaltante;
    private double tarifaGasolinaPorcentaje;

    public Vehiculo() {
        this.imagenes = new ArrayList<>();
        this.estado = "Disponible";
        this.kilometrajeActual = 0.0;
        this.nivelCombustibleActual = 100;
        this.tarifaLlantaFaltante = 100.0;
        this.tarifaHerramientasFaltante = 50.0;
        this.tarifaGasolinaPorcentaje = 0.65;
    }

    public Vehiculo(int id, String marca, String modelo, int anio, String placa, double tarifaDiaria, String estado,
                    ArrayList<String> imagenes, double kilometrajeActual, int nivelCombustibleActual) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.placa = placa;
        this.tarifaDiaria = tarifaDiaria;
        this.estado = estado;
        this.imagenes = imagenes != null ? imagenes : new ArrayList<>();
        this.kilometrajeActual = kilometrajeActual;
        this.nivelCombustibleActual = nivelCombustibleActual;
        this.tarifaLlantaFaltante = 100.0;
        this.tarifaHerramientasFaltante = 50.0;
        this.tarifaGasolinaPorcentaje = 0.65;
    }

    public Vehiculo(int id, String marca, String modelo, int anio, String placa, double tarifaDiaria, String estado,
                    ArrayList<String> imagenes, double kilometrajeActual, int nivelCombustibleActual,
                    double tarifaLlantaFaltante, double tarifaHerramientasFaltante, double tarifaGasolinaPorcentaje) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.placa = placa;
        this.tarifaDiaria = tarifaDiaria;
        this.estado = estado;
        this.imagenes = imagenes != null ? imagenes : new ArrayList<>();
        this.kilometrajeActual = kilometrajeActual;
        this.nivelCombustibleActual = nivelCombustibleActual;
        this.tarifaLlantaFaltante = tarifaLlantaFaltante;
        this.tarifaHerramientasFaltante = tarifaHerramientasFaltante;
        this.tarifaGasolinaPorcentaje = tarifaGasolinaPorcentaje;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public double getTarifaDiaria() {
        return tarifaDiaria;
    }

    public void setTarifaDiaria(double tarifaDiaria) {
        this.tarifaDiaria = tarifaDiaria;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ArrayList<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(ArrayList<String> imagenes) {
        this.imagenes = imagenes;
    }

    public double getKilometrajeActual() {
        return kilometrajeActual;
    }

    public void setKilometrajeActual(double kilometrajeActual) {
        this.kilometrajeActual = kilometrajeActual;
    }

    public int getNivelCombustibleActual() {
        return nivelCombustibleActual;
    }

    public void setNivelCombustibleActual(int nivelCombustibleActual) {
        this.nivelCombustibleActual = nivelCombustibleActual;
    }

    public double getTarifaLlantaFaltante() {
        return tarifaLlantaFaltante > 0 ? tarifaLlantaFaltante : 100.0;
    }

    public void setTarifaLlantaFaltante(double tarifaLlantaFaltante) {
        this.tarifaLlantaFaltante = tarifaLlantaFaltante;
    }

    public double getTarifaHerramientasFaltante() {
        return tarifaHerramientasFaltante > 0 ? tarifaHerramientasFaltante : 50.0;
    }

    public void setTarifaHerramientasFaltante(double tarifaHerramientasFaltante) {
        this.tarifaHerramientasFaltante = tarifaHerramientasFaltante;
    }

    public double getTarifaGasolinaPorcentaje() {
        return tarifaGasolinaPorcentaje > 0 ? tarifaGasolinaPorcentaje : 0.65;
    }

    public void setTarifaGasolinaPorcentaje(double tarifaGasolinaPorcentaje) {
        this.tarifaGasolinaPorcentaje = tarifaGasolinaPorcentaje;
    }
}
