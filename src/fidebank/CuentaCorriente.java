package fidebank;

public class CuentaCorriente extends CuentaBancaria {
    public CuentaCorriente(String numero, String pin, String cedula) {
        super(numero, pin, cedula);
    }

    @Override
    public String getTipoCuenta() { return "Corriente"; }
}
