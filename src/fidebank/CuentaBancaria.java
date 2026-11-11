package fidebank;

import java.io.Serializable;

public class CuentaBancaria implements Serializable {
    private static final long serialVersionUID = 1L;
    private String numeroCuenta;
    private String pinSeguridad;
    private double saldoDisponible;
    private String estadoCuenta = "ACTIVA";
    private String idClientePropietario;
    private int intentosFallidos = 0;

    public CuentaBancaria(String numeroCuenta, String pin, String cedula) {
        this.numeroCuenta = numeroCuenta;
        this.pinSeguridad = pin;
        this.idClientePropietario = cedula;
    }

    public synchronized boolean validarPin(String pin) {
        if (estadoCuenta.equals("BLOQUEADA")) {
            throw new IllegalArgumentException("La cuenta está bloqueada.");
        }
        if (pinSeguridad.equals(pin)) {
            intentosFallidos = 0;
            return true;
        }
        intentosFallidos++;
        if (intentosFallidos >= 3) {
            estadoCuenta = "BLOQUEADA";
            throw new IllegalArgumentException("Cuenta bloqueada por 3 intentos incorrectos.");
        }
        throw new IllegalArgumentException("PIN incorrecto. Intentos restantes: " + (3 - intentosFallidos));
    }

    public synchronized void acreditarMonto(double monto) {
        if (!Double.isFinite(monto) || monto <= 0 || monto != Math.floor(monto)) {
            throw new IllegalArgumentException("Ingrese un monto válido en colones.");
        }
        saldoDisponible += monto;
    }

    public synchronized void debitarMonto(double monto) {
        if (!Double.isFinite(monto) || monto <= 0 || monto != Math.floor(monto)
                || monto % 5000 != 0) {
            throw new IllegalArgumentException("El retiro debe ser múltiplo de ₡5.000.");
        }
        if (monto > saldoDisponible) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }
        saldoDisponible -= monto;
    }

    // Las clases hijas cambian este método: ejemplo de polimorfismo.
    public String getTipoCuenta() { return "Bancaria"; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public synchronized double getSaldoDisponible() { return saldoDisponible; }
    public String getEstadoCuenta() { return estadoCuenta; }
    public String getIdClientePropietario() { return idClientePropietario; }
}
