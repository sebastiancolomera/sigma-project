package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class MenuLiderFrame extends JFrame {

    private final GestorSigma controlador;

    public MenuLiderFrame(GestorSigma controlador) {
        this.controlador = controlador;

        setTitle("Menú Líder");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 1));

        JButton btnCrearMeta = new JButton("Crear Meta");
        JButton btnAsignarTarea = new JButton("Asignar Tarea");
        JButton btnVerTareas = new JButton("Ver Tareas");
        JButton btnCambiarEstado = new JButton("Cambiar Estado de Tarea");
        JButton btnEditarFechas = new JButton("Editar Fechas de Tarea");
        JButton btnVerUsuarios = new JButton("Ver Usuarios");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");

        btnCrearMeta.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Nombre de la meta:");
            if (nombre != null && !nombre.trim().isEmpty()) {
                controlador.agregarMeta(nombre.trim());
                JOptionPane.showMessageDialog(this, "Meta creada correctamente.");
            }
        });

        btnAsignarTarea.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Asignar Tarea", true);
            dialog.setSize(400, 350);
            dialog.setLocationRelativeTo(this);
            dialog.add(new GestionTareasPanel(controlador));
            dialog.setVisible(true);
        });

        btnVerTareas.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            controlador.getMetas().forEach(m -> {
                sb.append("Meta: ").append(m.getNombre())
                        .append(" (").append(m.calcularProgreso()).append("% completado)\n");
                m.getTareas().forEach(t -> {
                    Usuario asignado = t.getAsignado();
                    String nombreAsignado = (asignado != null) ? asignado.getNombre() : "Sin asignar";
                    String fechaInicio = (t.getFechaInicio() != null) ? t.getFechaInicio().toString() : "sin fecha";
                    String fechaTermino = (t.getFechaTermino() != null) ? t.getFechaTermino().toString() : "sin fecha";
                    sb.append(" - ").append(t.getTitulo())
                            .append(" [").append(t.getEstado()).append("]")
                            .append(" (").append(fechaInicio).append(" → ")
                            .append(fechaTermino).append(")")
                            .append(" → ").append(nombreAsignado).append("\n");
                });
            });
            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Tareas", JOptionPane.INFORMATION_MESSAGE);
        });

        btnCambiarEstado.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Cambiar Estado de Tarea", true);
            dialog.setSize(350, 200);
            dialog.setLocationRelativeTo(this);
            dialog.add(new CambiarEstadoPanel(controlador, null));
            dialog.setVisible(true);
        });

        btnEditarFechas.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Editar Fechas de Tarea", true);
            dialog.setSize(350, 220);
            dialog.setLocationRelativeTo(this);
            dialog.add(new EditarFechasPanel(controlador, dialog));
            dialog.setVisible(true);
        });

        btnVerUsuarios.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (Usuario u : controlador.getUsuarios()) {
                sb.append(u.getNombre()).append(" [").append(u.getRol()).append("]\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Usuarios", JOptionPane.INFORMATION_MESSAGE);
        });

        btnCerrarSesion.addActionListener(e -> {
            dispose();
            new LoginFrame(controlador).setVisible(true);
        });

        add(btnCrearMeta);
        add(btnAsignarTarea);
        add(btnVerTareas);
        add(btnCambiarEstado);
        add(btnEditarFechas);
        add(btnVerUsuarios);
        add(btnCerrarSesion);
    }
}