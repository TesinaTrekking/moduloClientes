package com.example;

import java.time.LocalDate;

public class FichaMedica {
    private int id;
    private int clienteId;
    private String presion;
    private String alergias;
    private String medicacion;
    private LocalDate fechaVencimientoApto;
    private String observaciones;

    public FichaMedica(int id, int clienteId, String presion, String alergias, String medicacion,
            LocalDate fechaVencimientoApto, String observaciones) {
        this.id = id;
        this.clienteId = clienteId;
        this.presion = presion;
        this.alergias = alergias;
        this.medicacion = medicacion;
        this.fechaVencimientoApto = fechaVencimientoApto;
        this.observaciones = observaciones;
    }

    public int getId() { return id; }
    public int getClienteId() { return clienteId; }
    public String getPresion() { return presion; }
    public String getAlergias() { return alergias; }
    public String getMedicacion() { return medicacion; }
    public LocalDate getFechaVencimientoApto() { return fechaVencimientoApto; }
    public String getObservaciones() { return observaciones; }
}