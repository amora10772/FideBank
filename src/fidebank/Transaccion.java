package fidebank;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaccion implements Serializable {
    private static final long serialVersionUID = 1L;
    private String idTransaccion;
    private LocalDateTime fechaHora;
    private String tipoTransaccion;
    private double monto;
    private String cuentaOrigen;
    private String cuentaDestino;
    private String estado;

    public Transaccion(String id, String tipo, double monto, String cuenta) {
        this.idTransaccion = id;
        this.fechaHora = LocalDateTime.now();
        this.tipoTransaccion = tipo;
        this.monto = monto;
        this.cuentaOrigen = cuenta;
        this.cuentaDestino = "";
        this.estado = "EXITOSA";
    }

    public String getCuentaOrigen() { return cuentaOrigen; }
    public String getIdTransaccion() { return idTransaccion; }

    public String obtenerDetalleTransaccion() {
        String fecha = fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return idTransaccion + " | " + fecha + " | " + tipoTransaccion
                + " | ₡" + String.format("%,.0f", monto);
    }
}
