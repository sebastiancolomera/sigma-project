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
        setLayout(new GridLayout(3, 1, 10, 10));

        JButton btnMisTareas = new JButton("Ver mis tareas");
        JButton btnCambiarEstado = new JButton("Cambiar estado de tarea");
        JButton btnCerrar = new JButton("Cerrar Sesión");

        btnMisTareas.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            controlador.getTareasDeUsuario(usuarioActual).forEach(t ->
                    sb.append("- ").append(t.getTitulo())
                            .append(" [").append(t.getEstado()).append("]").append("\n")
            );
            String contenido = sb.isEmpty() ? "No tienes tareas asignadas." : sb.toString();
            JOptionPane.showMessageDialog(this, contenido, "Mis Tareas", JOptionPane.INFORMATION_MESSAGE);
        });


        btnCambiarEstado.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Cambiar Estado", true);
            dialog.add(new CambiarEstadoPanel(this.controlador, usuarioActual));
            dialog.pack();
            dialog.setVisible(true);
        });

        btnCerrar.addActionListener(e -> {
            this.dispose();
            new LoginFrame(this.controlador).setVisible(true);
        });

        add(btnMisTareas);
        add(btnCambiarEstado);
        add(btnCerrar);
    }
}