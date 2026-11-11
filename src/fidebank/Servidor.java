package fidebank;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private static Banco banco;
    private static final String ARCHIVO = "fidebank.dat";

    // Recupera las cuentas guardadas cuando se vuelve a iniciar el servidor.
    private static void cargarDatos() throws Exception {
        File archivo = new File(ARCHIVO);
        if (archivo.exists()) {
            try (ObjectInputStream entrada = new ObjectInputStream(
                    new FileInputStream(archivo))) {
                banco = (Banco) entrada.readObject();
            }
        } else {
            banco = new Banco();
        }
    }

    // Guarda clientes, cuentas, PIN, saldos e historial con serialización.
    private static void guardarDatos() throws Exception {
        try (ObjectOutputStream salida = new ObjectOutputStream(
                new FileOutputStream(ARCHIVO))) {
            salida.writeObject(banco);
        }
    }

    public static void main(String[] args) {
        try {
            cargarDatos();
            try (ServerSocket servidor = new ServerSocket(5050)) {
                System.out.println("FideBank - Servidor iniciado en puerto 5050");
                System.out.println("Los datos se guardan en " + ARCHIVO);

                while (true) {
                    Socket cliente = servidor.accept();
                    // Un hilo diferente para cada cliente conectado.
                    new Thread(() -> atender(cliente)).start();
                }
            }
        } catch (Exception e) {
            System.out.println("Error al iniciar servidor: " + e.getMessage());
        }
    }

    private static void atender(Socket socket) {
        try (Socket conexion = socket;
             DataInputStream entrada = new DataInputStream(conexion.getInputStream());
             DataOutputStream salida = new DataOutputStream(conexion.getOutputStream())) {

            String[] datos = entrada.readUTF().split("\\|", -1);
            String respuesta;
            boolean correcto = true;

            // Evita que dos clientes cambien el saldo al mismo tiempo.
            synchronized (banco) {
                try {
                    switch (datos[0]) {
                        case "REGISTRAR":
                            respuesta = banco.abrirCuenta(datos[1], datos[2], datos[3],
                                    datos[4], datos[5], datos[6]);
                            break;
                        case "LOGIN":
                        case "CONSULTAR":
                            respuesta = banco.consultar(datos[1], datos[2]);
                            break;
                        case "DEPOSITO":
                            respuesta = banco.depositar(datos[1], datos[2],
                                    Double.parseDouble(datos[3]));
                            break;
                        case "RETIRO":
                            respuesta = banco.retirar(datos[1], datos[2],
                                    Double.parseDouble(datos[3]));
                            break;
                        case "HISTORIAL":
                            respuesta = banco.historial(datos[1], datos[2]);
                            break;
                        default:
                            throw new IllegalArgumentException("Operación desconocida.");
                    }
                } catch (Exception e) {
                    correcto = false;
                    respuesta = e.getMessage();
                }

                // También se guardan los intentos fallidos de PIN.
                try {
                    guardarDatos();
                } catch (Exception e) {
                    correcto = false;
                    respuesta = "Error al guardar los datos: " + e.getMessage();
                }
            }
            salida.writeUTF((correcto ? "OK|" : "ERROR|") + respuesta);
        } catch (Exception e) {
            System.out.println("Problema con un cliente: " + e.getMessage());
        }
    }
}
