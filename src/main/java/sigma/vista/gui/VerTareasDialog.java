package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.Meta;
import sigma.modelo.Tarea;
import sigma.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class VerTareasDialog extends JDialog {

    private final GestorSigma controlador;
    private final Usuario usuario;

    public VerTareasDialog(JFrame parent, GestorSigma controlador, Usuario usuario) {
        super(parent, "Tareas de " + usuario.getNombre(), true);
        this.controlador = controlador;
        this.usuario = usuario;

        setSize(700, 400);
        setLocationRelativeTo(parent);

        List<Tarea> tareas = controlador.getTareasDeUsuario(usuario);
        List<Meta> metas = controlador.getMetas();

        String[] columnas = {"Tarea", "Meta", "Estado Tarea", "Estado Entrega", "Fecha Inicio", "Fecha Término"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Meta meta : metas) {
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getAsignado() != null && tarea.getAsignado().getNombre().equals(usuario.getNombre())) {
                    Object[] fila = new Object[6];
                    fila[0] = tarea.getTitulo() != null ? tarea.getTitulo() : "Sin título";
                    fila[1] = meta.getNombre();
                    fila[2] = tarea.getEstado() != null ? tarea.getEstado().toString() : "Sin estado";
                    fila[3] = tarea.getEstadoEntrega() != null ? tarea.getEstadoEntrega().toString() : "Sin definir";
                    fila[4] = tarea.getFechaInicio() != null ? tarea.getFechaInicio().toString() : "Sin fecha";
                    fila[5] = tarea.getFechaTermino() != null ? tarea.getFechaTermino().toString() : "Sin fecha";
                    model.addRow(fila);
                }
            }
        }

        JTable tabla = new JTable(model);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        btnPanel.add(btnCerrar);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public static void showDialog(JFrame parent, GestorSigma controlador) {
        List<Usuario> usuarios = controlador.getUsuarios().stream()
                .filter(u -> !u.esSuperusuario())
                .collect(Collectors.toList());

        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No hay usuarios disponibles para ver sus tareas.",
                    "Sin usuarios",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] opciones = usuarios.stream()
                .map(u -> u.getNombre() + " — " + u.getRol())
                .toArray(String[]::new);

        String seleccion = (String) JOptionPane.showInputDialog(
                parent,
                "Seleccione el usuario para ver sus tareas:",
                "Ver Tareas por Usuario",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion == null) return;

        String nombreUsuario = seleccion.split(" — ")[0];
        Usuario seleccionado = controlador.getUsuarios().stream()
                .filter(u -> u.getNombre().equals(nombreUsuario))
                .findFirst()
                .orElse(null);

        if (seleccionado == null) {
            JOptionPane.showMessageDialog(parent, "Usuario no encontrado.");
            return;
        }

        if (controlador.getTareasDeUsuario(seleccionado).isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "El usuario " + seleccionado.getNombre() + " no tiene tareas asignadas.",
                    "Sin tareas",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        new VerTareasDialog(parent, controlador, seleccionado).setVisible(true);
    }
}