package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.RolUsuario;
import javax.swing.*;
import java.awt.*;

public class MenuSuperUsuarioFrame extends JFrame {
    private GestorSigma controlador;

    public MenuSuperUsuarioFrame(GestorSigma controlador) {
        this.controlador = controlador;
        setTitle("SIGMA - Menú Superusuario");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton btnRegistrar = new JButton("Registrar Usuario");
        JButton btnEliminar = new JButton("Eliminar Usuario");
        JButton btnCambiarRol = new JButton("Cambiar Rol");
        JButton btnReset = new JButton("Eliminar Datos de Registro");
        JButton btnCerrar = new JButton("Cerrar sesión");

        btnReset.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar todos los " +
                    "datos de registro?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) controlador.resetearSistema();
        });

        btnCerrar.addActionListener(e -> {
            this.dispose();
            new LoginFrame(controlador).setVisible(true);
        });

        add(btnRegistrar);
        add(btnEliminar);
        add(btnCambiarRol);
        add(btnReset);
        add(btnCerrar);
    }
}
