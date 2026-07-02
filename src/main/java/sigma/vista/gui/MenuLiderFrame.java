package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.EstadoTarea;
import sigma.modelo.Tarea;
import javax.swing.*;
import java.awt.*;

public class MenuLiderFrame extends JFrame {
    private GestorSigma controlador;

    public MenuLiderFrame(GestorSigma controlador) {
        this.controlador = controlador;
        setTitle("SIGMA - Menú Líder");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1, 10, 10));

        JButton btnCrearMeta = new JButton("Crear Meta");
        JButton btnAsignarTarea = new JButton("Asignar Tarea");
        JButton btnVerTareas   = new JButton("Ver Tareas");
        JButton btnCambiarEstado = new JButton("Cambiar Estado de Tarea");
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

        btnCambiarEstado.addActionListener(e -> {
            java.util.List<Tarea> todasLasTareas = new java.util.ArrayList<>();
            controlador.getMetas().forEach(m -> todasLasTareas.addAll(m.getTareas()));

            if (todasLasTareas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay tareas registradas.");
                return;
            }

            String[] opciones = todasLasTareas.stream()
                    .map(t -> {
                        String asignado = (t.getAsignado() != null) ? t.getAsignado().getNombre() : "Sin asignar";
                        return t.getTitulo() + " [" + t.getEstado() + "] → " + asignado;
                    })
                    .toArray(String[]::new);

            String seleccion = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleccione la tarea:",
                    "Cambiar Estado",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == null) return;

            String tituloSeleccionado = seleccion.split(" \\[")[0];

            EstadoTarea[] estados = EstadoTarea.values();
            EstadoTarea nuevoEstado = (EstadoTarea) JOptionPane.showInputDialog(
                    this,
                    "Nuevo estado para: " + tituloSeleccionado,
                    "Seleccionar Estado",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    estados,
                    estados[0]
            );

            if (nuevoEstado == null) return;

            if (controlador.cambiarEstadoTarea(tituloSeleccionado, nuevoEstado)) {
                JOptionPane.showMessageDialog(this, "Estado actualizado.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.");
            }
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
        add(btnCambiarEstado);
        add(btnVerUsuarios);
        add(btnCerrar);
    }
}