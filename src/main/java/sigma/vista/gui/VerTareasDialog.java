package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.Meta;
import sigma.modelo.Tarea;
import sigma.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

                    String estadoEntrega = "Sin definir";
                    try {
                        java.lang.reflect.Method m = tarea.getClass().getMethod("getEstadoEntrega");
                        Object resultado = m.invoke(tarea);
                        estadoEntrega = resultado != null ? resultado.toString() : "Sin definir";
                    } catch (Exception e) {
                        estadoEntrega = "No disponible";
                    }
                    fila[3] = estadoEntrega;

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
}