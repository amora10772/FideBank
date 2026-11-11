package fidebank;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Ventana extends JFrame {
    private final Color azul = new Color(35, 47, 116);
    private final Color amarillo = new Color(255, 211, 0);
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel centro = new JPanel(tarjetas);

    private JTextField numeroLogin = new JTextField();
    private JPasswordField pinLogin = new JPasswordField();
    private JTextField cedula = new JTextField();
    private JTextField nombre = new JTextField();
    private JTextField correo = new JTextField();
    private JTextField telefono = new JTextField();
    private JPasswordField pinNuevo = new JPasswordField();
    private JPasswordField confirmarPin = new JPasswordField();
    private JComboBox<String> tipo = new JComboBox<>(new String[]{"Ahorros", "Corriente"});
    private JLabel bienvenida = new JLabel("Bienvenido");
    private JLabel cuentaTexto = new JLabel("Cuenta:");
    private JLabel saldoTexto = new JLabel("Saldo:");

    private String cuentaActual;
    private String pinActual;

    public Ventana() {
        setTitle("FideBank - Cajero Automático");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(azul);
        encabezado.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel titulo = new JLabel("FIDEBANK");
        titulo.setFont(new Font("Arial", Font.BOLD, 21));
        titulo.setForeground(Color.WHITE);
        encabezado.add(titulo, BorderLayout.NORTH);
        JLabel lema = new JLabel("Su banco donde quiera que esté");
        lema.setForeground(Color.WHITE);
        encabezado.add(lema, BorderLayout.SOUTH);
        add(encabezado, BorderLayout.NORTH);

        centro.add(pantallaIngreso(), "INGRESO");
        centro.add(new JScrollPane(pantallaRegistro()), "REGISTRO");
        centro.add(pantallaMenu(), "MENU");
        add(centro, BorderLayout.CENTER);
        tarjetas.show(centro, "INGRESO");
    }

    private JPanel panel() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(22, 32, 22, 32));
        return p;
    }

    private void texto(JPanel p, String texto) {
        JLabel label = new JLabel(texto);
        label.setAlignmentX(LEFT_ALIGNMENT);
        p.add(label);
        p.add(Box.createVerticalStrut(5));
    }

    private void campo(JPanel p, String nombre, javax.swing.JComponent c) {
        texto(p, nombre);
        c.setMaximumSize(new java.awt.Dimension(1000, 32));
        c.setAlignmentX(LEFT_ALIGNMENT);
        p.add(c);
        p.add(Box.createVerticalStrut(11));
    }

    private JButton boton(JPanel p, String texto, boolean destacado, Runnable accion) {
        JButton b = new JButton(texto);
        b.setBackground(destacado ? amarillo : azul);
        b.setForeground(destacado ? Color.BLACK : Color.WHITE);
        b.setFocusPainted(false);
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setMaximumSize(new java.awt.Dimension(1000, 36));
        b.addActionListener(e -> accion.run());
        p.add(b);
        p.add(Box.createVerticalStrut(10));
        return b;
    }

    private JPanel pantallaIngreso() {
        JPanel p = panel();
        texto(p, "Bienvenido a FideBank");
        campo(p, "Número de cuenta", numeroLogin);
        campo(p, "PIN de 4 dígitos", pinLogin);
        boton(p, "Ingresar", true, this::ingresar);
        boton(p, "Abrir cuenta", false, () -> tarjetas.show(centro, "REGISTRO"));
        return p;
    }

    private JPanel pantallaRegistro() {
        JPanel p = panel();
        texto(p, "Abra su cuenta");
        campo(p, "Cédula", cedula);
        campo(p, "Nombre completo", nombre);
        campo(p, "Correo electrónico", correo);
        campo(p, "Teléfono", telefono);
        campo(p, "Tipo de cuenta", tipo);
        campo(p, "Crear PIN", pinNuevo);
        campo(p, "Confirmar PIN", confirmarPin);
        boton(p, "Crear cuenta", true, this::registrar);
        boton(p, "Cancelar", false, () -> tarjetas.show(centro, "INGRESO"));
        return p;
    }

    private JPanel pantallaMenu() {
        JPanel p = panel();
        texto(p, "Menú principal");
        p.add(bienvenida);
        p.add(Box.createVerticalStrut(10));
        p.add(cuentaTexto);
        p.add(Box.createVerticalStrut(10));
        saldoTexto.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(saldoTexto);
        p.add(Box.createVerticalStrut(20));
        boton(p, "Retirar dinero", true, () -> movimiento("RETIRO"));
        boton(p, "Depositar dinero", false, () -> movimiento("DEPOSITO"));
        boton(p, "Ver historial", false, this::verHistorial);
        boton(p, "Actualizar saldo", false, this::actualizar);
        boton(p, "Cerrar sesión", false, () -> {
            cuentaActual = null;
            pinActual = null;
            pinLogin.setText("");
            tarjetas.show(centro, "INGRESO");
        });
        return p;
    }

    private String enviar(String... campos) {
        for (String c : campos) {
            if (c == null || c.contains("|")) {
                throw new IllegalArgumentException("No utilice el símbolo | en los datos.");
            }
        }
        try (Socket socket = new Socket("127.0.0.1", 5050);
             DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
             DataInputStream entrada = new DataInputStream(socket.getInputStream())) {
            socket.setSoTimeout(3000);
            salida.writeUTF(String.join("|", campos));
            String[] respuesta = entrada.readUTF().split("\\|", 2);
            if (respuesta[0].equals("ERROR")) {
                throw new IllegalArgumentException(respuesta[1]);
            }
            return respuesta[1];
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo conectar. Ejecute primero Servidor.java.");
        }
    }

    private void registrar() {
        try {
            String pin = new String(pinNuevo.getPassword());
            if (!pin.equals(new String(confirmarPin.getPassword()))) {
                throw new IllegalArgumentException("Los PIN no coinciden.");
            }
            String resultado = enviar("REGISTRAR", cedula.getText().trim(),
                    nombre.getText().trim(), correo.getText().trim(), telefono.getText().trim(),
                    pin, tipo.getSelectedItem().toString());
            JOptionPane.showMessageDialog(this, resultado);
            cedula.setText(""); nombre.setText(""); correo.setText("");
            telefono.setText(""); pinNuevo.setText(""); confirmarPin.setText("");
            tarjetas.show(centro, "INGRESO");
        } catch (Exception e) {
            error(e);
        }
    }

    private void ingresar() {
        try {
            String numero = numeroLogin.getText().trim().toUpperCase();
            String pin = new String(pinLogin.getPassword());
            String datos = enviar("LOGIN", numero, pin);
            cuentaActual = numero;
            pinActual = pin;
            mostrarDatos(datos);
            tarjetas.show(centro, "MENU");
        } catch (Exception e) {
            error(e);
        }
    }

    private void mostrarDatos(String datos) {
        String[] partes = datos.split("\\|");
        bienvenida.setText("Bienvenido, " + partes[0]);
        cuentaTexto.setText("Cuenta " + partes[3] + ": " + partes[1]);
        saldoTexto.setText("Saldo disponible: ₡" + String.format("%,.0f", Double.parseDouble(partes[2])));
    }

    private void actualizar() {
        try {
            mostrarDatos(enviar("CONSULTAR", cuentaActual, pinActual));
        } catch (Exception e) {
            error(e);
        }
    }

    private void movimiento(String operacion) {
        String valor = JOptionPane.showInputDialog(this,
                "Monto en colones para " + (operacion.equals("RETIRO") ? "retirar" : "depositar") + ":");
        if (valor == null) return;
        try {
            double monto = Double.parseDouble(valor.trim());
            String resultado = enviar(operacion, cuentaActual, pinActual, String.valueOf(monto));
            JOptionPane.showMessageDialog(this, resultado);
            actualizar();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Escriba un número válido.");
        } catch (Exception e) {
            error(e);
        }
    }

    private void verHistorial() {
        try {
            String historial = enviar("HISTORIAL", cuentaActual, pinActual);
            JOptionPane.showMessageDialog(this, historial, "Historial de movimientos",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            error(e);
        }
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "FideBank",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Ventana().setVisible(true));
    }
}
