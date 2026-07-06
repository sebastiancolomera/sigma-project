package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.app.ResultadoOperacion;
import sigma.modelo.EstadoTarea;
import sigma.modelo.Meta;
import sigma.modelo.Tarea;
import sigma.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CambiarEstadoPanel extends JPanel {

    private final GestorSigma controlador;
    private final Usuario usuario;
    private final JComboBox<String> cmbTareas;
    private final JComboBox<EstadoTarea> cmbEstado;
    private final JLabel lblEstadoEntrega;

    public CambiarEstadoPanel(GestorSigma controlador, Usuario usuario) {
        this.controlador = controlador;
        this.usuario = usuario;

        setLayout(new GridLayout(4, 2, 5, 5));

        cmbTareas = new JComboBox<>();
        cmbEstado = new JComboBox<>(EstadoTarea.values());
        lblEstadoEntrega = new JLabel("Seleccione una tarea");

        add(new JLabel("Seleccionar Tarea:"));
        add(cmbTareas);
        add(new JLabel("Seleccionar Estado:"));
        add(cmbEstado);
        add(new JLabel("Estado de Entrega:"));
        add(lblEstadoEntrega);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> {
            String titulo = (String) cmbTareas.getSelectedItem();
            EstadoTarea nuevoEstado = (EstadoTarea) cmbEstado.getSelectedItem();
            if (titulo != null && nuevoEstado != null) {
                ResultadoOperacion resultado = controlador.cambiarEstadoTarea(titulo, nuevoEstado, usuario);
                if (resultado.isExito()) {
                    JOptionPane.showMessageDialog(this, resultado.getMensaje());
                    cargarTareas();
                } else {
                    JOptionPane.showMessageDialog(this, resultado.getMensaje(),
                            "No se pudo actualizar", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(new JLabel());
        add(btnActualizar);

        cmbTareas.addActionListener(e -> actualizarEstadoEntrega());

        cargarTareas();
    }

    private void cargarTareas() {
        cmbTareas.removeAllItems();

        List<Tarea> tareas = new ArrayList<>();

        if (usuario == null) {
            for (Meta meta : controlador.getMetas()) {
                tareas.addAll(meta.getTareas());
            }
        } else {
            tareas = controlador.getTareasDeUsuario(usuario);
        }

        for (Tarea t : tareas) {
            cmbTareas.addItem(t.getTitulo());
        }

        if (cmbTareas.getItemCount() == 0) {
            cmbTareas.addItem("(Sin tareas disponibles)");
            lblEstadoEntrega.setText("Sin tareas");
        } else {
            actualizarEstadoEntrega();
        }
    }

    private void actualizarEstadoEntrega() {
        String titulo = (String) cmbTareas.getSelectedItem();
        if (titulo == null || titulo.equals("(Sin tareas disponibles)")) {
            lblEstadoEntrega.setText("Seleccione una tarea");
            return;
        }

        for (Meta meta : controlador.getMetas()) {
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getTitulo().equals(titulo)) {
                    String estadoEntrega = tarea.getEstadoEntrega() != null
                            ? tarea.getEstadoEntrega().toString()
                            : "Sin definir";
                    lblEstadoEntrega.setText(estadoEntrega);
                    return;
                }
            }
        }
        lblEstadoEntrega.setText("No encontrada");
    }
}