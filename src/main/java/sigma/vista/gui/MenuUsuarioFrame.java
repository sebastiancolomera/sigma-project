package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class MenuUsuarioFrame extends JFrame {
    private GestorSigma controlador;
    private Usuario usuarioActual;

    public MenuUsuarioFrame(GestorSigma controlador, Usuario u) {
        this.controlador = controlador;
        this.usuarioActual = u;
        setTitle("SIGMA - Menú Usuario");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton btnMisTareas = new JButton("Ver mis tareas");
        JButton btnCambiarEstado = new JButton("Cambiar estado de tarea");
        btnCambiarEstado.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Cambiar Estado", true);
            dialog.add(new CambiarEstadoPanel(this.controlador, usuarioActual));
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton btnCerrar = new JButton("Cerrar Sesión");
        btnCerrar.addActionListener(e -> {
            this.dispose();
            new LoginFrame(this.controlador).setVisible(true);
        });

        add(btnMisTareas);
        add(btnCambiarEstado);
        add(btnCerrar);
    }
}