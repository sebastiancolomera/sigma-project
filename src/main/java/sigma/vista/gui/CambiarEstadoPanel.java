package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.EstadoTarea;
import sigma.modelo.Usuario;
import sigma.modelo.Tarea;

import javax.swing.*;
import java.awt.*;

public class CambiarEstadoPanel extends JPanel {

    private final GestorSigma controlador;
    private final Usuario usuario;
    private final JComboBox<String> cmbTareas;
    private final JComboBox<EstadoTarea> cmbEstado;

    public CambiarEstadoPanel(GestorSigma controlador, Usuario u) {
        this.controlador = controlador;
        this.usuario = u;

        setLayout(new GridLayout(3, 2, 5, 5));

        cmbTareas = new JComboBox<>();
        cmbEstado = new JComboBox<>(EstadoTarea.values());

        add(new JLabel("Seleccionar Tarea:"));
        add(cmbTareas);
        add(new JLabel("Seleccionar Estado:"));
        add(cmbEstado);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> {
            String titulo = (String) cmbTareas.getSelectedItem();
            EstadoTarea nuevoEstado = (EstadoTarea) cmbEstado.getSelectedItem();
            if (titulo != null && nuevoEstado != null) {
                controlador.cambiarEstadoTarea(titulo, nuevoEstado);
                JOptionPane.showMessageDialog(this, "Estado Actualizado");
                cargarTareas();
            }
        });

        add(btnActualizar);
        cargarTareas();
    }

    private void cargarTareas() {
        cmbTareas.removeAllItems();
        for (Tarea t : controlador.getTareasDeUsuario(usuario)) {
            cmbTareas.addItem(t.getTitulo());
        }
    }
}