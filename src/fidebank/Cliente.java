package fidebank;

import java.io.Serializable;

import java.util.ArrayList;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cedula;
    private String nombre;
    private String correo;
    private String telefono;
    private ArrayList<CuentaBancaria> cuentas = new ArrayList<>();

    public Cliente(String cedula, String nombre, String correo, String telefono) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
    }

    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public void agregarCuenta(CuentaBancaria cuenta) { cuentas.add(cuenta); }
    public ArrayList<CuentaBancaria> getCuentas() { return cuentas; }
}
