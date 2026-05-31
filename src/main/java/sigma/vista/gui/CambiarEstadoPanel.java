package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.EstadoTarea;
import sigma.modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class CambiarEstadoPanel extends JPanel {
    public CambiarEstadoPanel(GestorSigma controlador, Usuario u) {
        setLayout(new GridLayout(3, 2, 5, 5));

        JComboBox<String> cmbTareas = new JComboBox<>();
        controlador.getTareasDeUsuario(u).forEach(t -> cmbTareas.addItem(t.getTitulo()));

        JComboBox<EstadoTarea>  cmbEstado = new JComboBox<>(EstadoTarea.values());

        add(new JLabel("Seleccionar Tarea:"));
        add(cmbTareas);
        add(new JLabel("Seleccionar Estado:"));
        add(cmbEstado);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> {
            String titulo = (String) cmbTareas.getSelectedItem();
            EstadoTarea nuevoEstado = (EstadoTarea) cmbEstado.getSelectedItem();
            if (titulo != null) {
                controlador.cambiarEstadoTarea(titulo, nuevoEstado);
                JOptionPane.showMessageDialog(this, "Estado Actualizado");
            }
        });
        add(btnActualizar);
    }
}
