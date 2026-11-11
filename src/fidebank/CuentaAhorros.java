package fidebank;

public class CuentaAhorros extends CuentaBancaria {
    public CuentaAhorros(String numero, String pin, String cedula) {
        super(numero, pin, cedula);
    }

    @Override
    public String getTipoCuenta() { return "Ahorros"; }
}
