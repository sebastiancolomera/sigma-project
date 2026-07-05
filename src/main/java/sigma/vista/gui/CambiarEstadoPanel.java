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

    public CambiarEstadoPanel(GestorSigma controlador, Usuario usuario) {
        this.controlador = controlador;
        this.usuario = usuario;

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
        }
    }
}