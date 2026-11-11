package fidebank;

import java.io.Serializable;

import java.math.BigInteger;
import java.util.ArrayList;

public class Banco implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Cliente> clientes = new ArrayList<>();
    private ArrayList<CuentaBancaria> cuentas = new ArrayList<>();
    private GestorTransacciones gestor = new GestorTransacciones();
    private int consecutivoCuenta = 1;

    public synchronized String abrirCuenta(String cedula, String nombre,
            String correo, String telefono, String pin, String tipo) {
        if (cedula.isBlank() || nombre.isBlank() || correo.isBlank() || telefono.isBlank()) {
            throw new IllegalArgumentException("Complete todos los datos.");
        }
        if (!correo.contains("@") || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException("Revise el correo y el PIN de 4 dígitos.");
        }
        for (Cliente c : clientes) {
            if (c.getCedula().equals(cedula)) {
                throw new IllegalArgumentException("Esta cédula ya está registrada.");
            }
        }

        // Genera un IBAN de demostración con dígitos de control.
        String base = "0102" + String.format("%014d", consecutivoCuenta++);
        BigInteger numero = new BigInteger(base + "122700");
        int control = 98 - numero.mod(BigInteger.valueOf(97)).intValue();
        String iban = "CR" + String.format("%02d", control) + base;

        CuentaBancaria cuenta;
        if (tipo.equals("Corriente")) {
            cuenta = new CuentaCorriente(iban, pin, cedula);
        } else {
            cuenta = new CuentaAhorros(iban, pin, cedula);
        }
        Cliente cliente = new Cliente(cedula, nombre, correo, telefono);
        cliente.agregarCuenta(cuenta);
        clientes.add(cliente);
        cuentas.add(cuenta);
        return "Cuenta creada. Su número es: " + iban;
    }

    private CuentaBancaria buscarCuenta(String numero) {
        for (CuentaBancaria cuenta : cuentas) {
            if (cuenta.getNumeroCuenta().equalsIgnoreCase(numero)) {
                return cuenta;
            }
        }
        throw new IllegalArgumentException("No existe esa cuenta.");
    }

    private Cliente buscarCliente(String cedula) {
        for (Cliente cliente : clientes) {
            if (cliente.getCedula().equals(cedula)) return cliente;
        }
        throw new IllegalArgumentException("No existe el cliente.");
    }

    public synchronized String consultar(String numero, String pin) {
        CuentaBancaria cuenta = buscarCuenta(numero);
        cuenta.validarPin(pin);
        Cliente cliente = buscarCliente(cuenta.getIdClientePropietario());
        // Polimorfismo: el tipo depende de si es CuentaAhorros o CuentaCorriente.
        return cliente.getNombre() + "|" + cuenta.getNumeroCuenta() + "|"
                + cuenta.getSaldoDisponible() + "|" + cuenta.getTipoCuenta();
    }

    public synchronized String depositar(String numero, String pin, double monto) {
        CuentaBancaria cuenta = buscarCuenta(numero);
        cuenta.validarPin(pin);
        gestor.ejecutarDeposito(cuenta, monto);
        return "Depósito realizado. Nuevo saldo: ₡" + String.format("%,.0f", cuenta.getSaldoDisponible());
    }

    public synchronized String retirar(String numero, String pin, double monto) {
        CuentaBancaria cuenta = buscarCuenta(numero);
        cuenta.validarPin(pin);
        gestor.ejecutarRetiro(cuenta, monto);
        return "Retiro realizado. Nuevo saldo: ₡" + String.format("%,.0f", cuenta.getSaldoDisponible());
    }

    public synchronized String historial(String numero, String pin) {
        CuentaBancaria cuenta = buscarCuenta(numero);
        cuenta.validarPin(pin);
        return gestor.obtenerHistorialPorCuenta(cuenta.getNumeroCuenta());
    }
}
