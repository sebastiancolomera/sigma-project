package sigma.vista.gui;

import sigma.app.GestorSigma;
import javax.swing.*;
import java.awt.*;

public class MenuLiderFrame extends JFrame {
    private GestorSigma controlador;

    public MenuLiderFrame(GestorSigma controlador) {
        this.controlador = controlador;
        setTitle("SIGMA - Menú Líder");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton btnCrearMeta = new JButton("Crear Meta");
        JButton btnAsignarTarea = new JButton("Asignar Tarea");
        JButton btnVerTareas   = new JButton("Ver Tareas");
        JButton btnVerUsuarios = new JButton("Ver Usuarios");
        JButton btnCerrar = new JButton("Cerrar Sesión");


        btnCrearMeta.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog("Nombre de la meta :");
            if (nombre != null) this.controlador.agregarMeta(nombre);
        });

        btnAsignarTarea.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Asignar Tarea", true);
            dialog.add(new GestionTareasPanel(this.controlador));
            dialog.pack();
            dialog.setVisible(true);
        });

        btnVerTareas.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            controlador.getMetas().forEach(m -> {
                sb.append("Meta: ").append(m.getNombre()).append("\n");
                m.getTareas().forEach(t -> {
                    String nombreAsignado = (t.getAsignado() != null)
                            ? t.getAsignado().getNombre()
                            : "Sin asignar";
                    sb.append(" - ").append(t.getTitulo())
                            .append(" [").append(t.getEstado()).append("]")
                            .append(" → ").append(nombreAsignado).append("\n");
                });
            });
            String contenido = sb.isEmpty() ? "No hay tareas registradas." : sb.toString();
            JOptionPane.showMessageDialog(this, contenido, "Tareas", JOptionPane.INFORMATION_MESSAGE);
        });

        btnVerUsuarios.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            controlador.getUsuarios().forEach(u ->
                    sb.append(u.getNombre()).append(" — ").append(u.getRol()).append("\n")
            );
            String contenido = sb.isEmpty() ? "No hay usuarios registrados." : sb.toString();
            JOptionPane.showMessageDialog(this, contenido, "Usuarios", JOptionPane.INFORMATION_MESSAGE);
        });

        btnCerrar.addActionListener(e -> {
            this.dispose();
            new LoginFrame(this.controlador).setVisible(true);
        });

        add(btnCrearMeta);
        add(btnAsignarTarea);
        add(btnVerTareas);
        add(btnVerUsuarios);
        add(btnCerrar);
    }
}