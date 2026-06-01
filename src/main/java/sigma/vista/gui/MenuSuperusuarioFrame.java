package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.RolUsuario;
import javax.swing.*;
import java.awt.*;

public class MenuSuperusuarioFrame extends JFrame {
    private GestorSigma controlador;

    public MenuSuperusuarioFrame(GestorSigma controlador) {
        this.controlador = controlador;
        setTitle("SIGMA - Menú Superusuario");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton btnRegistrar = new JButton("Registrar Usuario");
        JButton btnEliminar = new JButton("Eliminar Usuario");
        JButton btnCambiarRol = new JButton("Cambiar Rol");
        JButton btnReset = new JButton("Eliminar Datos de Registro");
        JButton btnCerrar = new JButton("Cerrar sesión");

        btnRegistrar.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Nombre del usuario:");
            if (nombre == null) return;
            String contrasena = JOptionPane.showInputDialog(this, "Contraseña:");
            if (contrasena == null) return;
            RolUsuario[] roles = RolUsuario.values();
            RolUsuario rol = (RolUsuario) JOptionPane.showInputDialog(this, "Rol:",
                    "Seleccionar Rol", JOptionPane.QUESTION_MESSAGE, null, roles, roles[2]);
            if (rol == null) return;
            boolean ok = controlador.registrarUsuario(nombre, contrasena, rol);
            JOptionPane.showMessageDialog(this, ok ? "Usuario registrado." : "El usuario ya existe.");
        });

        btnEliminar.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Nombre del usuario a eliminar:");
            if (nombre == null) return;
            boolean ok = controlador.eliminarUsuario(nombre);
            JOptionPane.showMessageDialog(this, ok ? "Usuario eliminado." : "Usuario no encontrado.");
        });

        btnCambiarRol.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Nombre del usuario:");
            if (nombre == null) return;
            RolUsuario[] roles = RolUsuario.values();
            RolUsuario rol = (RolUsuario) JOptionPane.showInputDialog(this, "Nuevo rol:",
                    "Cambiar Rol", JOptionPane.QUESTION_MESSAGE, null, roles, roles[2]);
            if (rol == null) return;
            boolean ok = controlador.actualizarRol(nombre, rol);
            JOptionPane.showMessageDialog(this, ok ? "Rol actualizado." : "Usuario no encontrado.");
        });

        btnReset.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar todos los " +
                    "datos de registro?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) this.controlador.resetearSistema();
        });

        btnCerrar.addActionListener(e -> {
            this.dispose();
            new LoginFrame(this.controlador).setVisible(true);
        });

        add(btnRegistrar);
        add(btnEliminar);
        add(btnCambiarRol);
        add(btnReset);
        add(btnCerrar);
    }
}
