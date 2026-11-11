package fidebank;

import java.io.Serializable;

import java.util.ArrayList;

public class GestorTransacciones implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Transaccion> historialTransacciones = new ArrayList<>();
    private int consecutivo = 1;

    public synchronized void ejecutarDeposito(CuentaBancaria cuenta, double monto) {
        cuenta.acreditarMonto(monto);
        registrar("DEPOSITO", cuenta, monto);
    }

    public synchronized void ejecutarRetiro(CuentaBancaria cuenta, double monto) {
        cuenta.debitarMonto(monto);
        registrar("RETIRO", cuenta, monto);
    }

    private void registrar(String tipo, CuentaBancaria cuenta, double monto) {
        String codigo = "TR-" + String.format("%04d", consecutivo++);
        historialTransacciones.add(
            new Transaccion(codigo, tipo, monto, cuenta.getNumeroCuenta()));
    }

    public synchronized String obtenerHistorialPorCuenta(String numeroCuenta) {
        StringBuilder texto = new StringBuilder();
        for (int i = historialTransacciones.size() - 1; i >= 0; i--) {
            Transaccion t = historialTransacciones.get(i);
            if (t.getCuentaOrigen().equals(numeroCuenta)) {
                texto.append(t.obtenerDetalleTransaccion()).append("\n");
            }
        }
        return texto.length() == 0 ? "No tiene movimientos todavía." : texto.toString();
    }
}
