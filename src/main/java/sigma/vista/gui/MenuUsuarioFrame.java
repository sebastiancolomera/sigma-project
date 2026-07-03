package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class MenuUsuarioFrame extends JFrame {

    private final GestorSigma controlador;
    private final Usuario usuarioActual;

    public MenuUsuarioFrame(GestorSigma controlador, Usuario usuarioActual) {
        this.controlador = controlador;
        this.usuarioActual = usuarioActual;

        setTitle("Menú Usuario");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1));

        JButton btnMisTareas = new JButton("Ver mis tareas");
        JButton btnCambiarEstado = new JButton("Cambiar estado de tarea");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");

        btnMisTareas.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();

            controlador.getTareasDeUsuario(usuarioActual).forEach(t -> {
                String titulo = (t.getTitulo() != null) ? t.getTitulo() : "Sin título";
                String fechaInicio = (t.getFechaInicio() != null) ? t.getFechaInicio().toString() : "sin fecha";
                String fechaTermino = (t.getFechaTermino() != null) ? t.getFechaTermino().toString() : "sin fecha";
                sb.append("- ").append(titulo)
                        .append(" [").append(t.getEstado()).append("]")
                        .append(" (").append(fechaInicio).append(" → ")
                        .append(fechaTermino).append(")\n");
            });

            controlador.getMetas().forEach(m -> {
                boolean tieneTareas = m.getTareas().stream()
                        .anyMatch(t -> t.getAsignado() != null
                                && t.getAsignado().getNombre().equals(usuarioActual.getNombre()));
                if (tieneTareas) {
                    sb.append("Progreso de la meta '").append(m.getNombre())
                            .append("': ").append(m.calcularProgreso()).append("%\n");
                }
            });

            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Mis Tareas", JOptionPane.INFORMATION_MESSAGE);
        });

        btnCambiarEstado.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Cambiar Estado de Tarea", true);
            dialog.setSize(350, 200);
            dialog.setLocationRelativeTo(this);
            dialog.add(new CambiarEstadoPanel(controlador, usuarioActual));
            dialog.setVisible(true);
        });

        btnCerrarSesion.addActionListener(e -> {
            dispose();
            new LoginFrame(controlador).setVisible(true);
        });

        add(btnMisTareas);
        add(btnCambiarEstado);
        add(btnCerrarSesion);
    }
}