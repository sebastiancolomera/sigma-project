package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class MenuUsuarioFrame extends JFrame {

    private final GestorSigma controlador;
    private final Usuario usuarioActual;

    public MenuUsuarioFrame(GestorSigma controlador, Usuario usuario) {
        this.controlador = controlador;
        this.usuarioActual = usuario;

        setTitle("SIGMA - Menú Usuario");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 10, 10));

        JButton btnVerTareas = new JButton("Ver mis tareas");
        JButton btnCambiarEstado = new JButton("Cambiar estado de tarea");
        JButton btnCerrar = new JButton("Cerrar Sesión");

        btnVerTareas.addActionListener(e -> {
            if (controlador.getTareasDeUsuario(usuarioActual).isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No tienes tareas asignadas actualmente.",
                        "Sin tareas",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            new VerTareasDialog(this, controlador, usuarioActual).setVisible(true);
        });

        btnCambiarEstado.addActionListener(e -> {
            if (controlador.getTareasDeUsuario(usuarioActual).isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No tienes tareas asignadas en este momento.",
                        "Sin tareas",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            JDialog dialog = new JDialog(this, "Cambiar Estado", true);
            dialog.add(new CambiarEstadoPanel(controlador, usuarioActual));
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        btnCerrar.addActionListener(e -> {
            controlador.guardarDatos();
            this.dispose();
            new LoginFrame(controlador).setVisible(true);
        });

        add(btnVerTareas);
        add(btnCambiarEstado);
        add(btnCerrar);
    }
}