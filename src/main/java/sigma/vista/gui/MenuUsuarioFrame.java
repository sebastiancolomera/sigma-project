package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.EstadoTarea;
import sigma.modelo.Meta;
import sigma.modelo.Tarea;
import sigma.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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
            StringBuilder sb = new StringBuilder();

            for (Meta meta : controlador.getMetas()) {
                for (Tarea tarea : meta.getTareas()) {
                    if (tarea.getAsignado() != null &&
                            tarea.getAsignado().getNombre().equals(usuarioActual.getNombre())) {

                        sb.append("[Meta: ").append(meta.getNombre()).append("]\n");
                        sb.append("  Tarea: ").append(tarea.getTitulo()).append("\n");
                        sb.append("  Estado: ").append(tarea.getEstado()).append("\n");

                        if (tarea.getFechaInicio() != null && tarea.getFechaTermino() != null) {
                            sb.append("  Inicio: ").append(tarea.getFechaInicio()).append("\n");
                            sb.append("  Termino: ").append(tarea.getFechaTermino()).append("\n");
                        } else {
                            sb.append("  Fechas: Sin definir\n");
                        }
                        sb.append("\n");
                    }
                }
            }

            String contenido = sb.isEmpty()
                    ? "No tienes tareas asignadas."
                    : sb.toString();

            JTextArea area = new JTextArea(contenido);
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(450, 300));

            JOptionPane.showMessageDialog(this, scroll, "Mis Tareas", JOptionPane.INFORMATION_MESSAGE);
        });

        btnCambiarEstado.addActionListener(e -> {
            if (controlador.getTareasDeUsuario(usuarioActual).isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No tienes tareas asignadas en este momento.",
                        "Sin tareas", JOptionPane.INFORMATION_MESSAGE);
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