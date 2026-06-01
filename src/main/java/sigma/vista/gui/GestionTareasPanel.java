package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class GestionTareasPanel extends JPanel {
    public GestionTareasPanel(GestorSigma controlador) {
        setLayout(new GridLayout(5, 2, 5, 5));

        JTextField txtTitulo = new JTextField();
        JComboBox<String> cmbMetas = new JComboBox<>();
        controlador.getMetas().forEach(m -> cmbMetas.addItem(m.getNombre()));

        JComboBox<String> cmbUsuarios = new JComboBox<>();
        controlador.getUsuarios().forEach(u -> cmbUsuarios.addItem(u.getNombre()));

        add(new JLabel("Título de Tarea:"));
        add(txtTitulo);
        add(new JLabel("Meta:"));
        add(cmbMetas);
        add(new JLabel("Responsable asignado:"));
        add(cmbUsuarios);

        JButton btnGuardar = new JButton("Agregar");
        btnGuardar.addActionListener(e -> {
            String titulo = txtTitulo.getText();
            String metaSeleccionada = (String) cmbMetas.getSelectedItem();
            String userSeleccionado = (String) cmbUsuarios.getSelectedItem();

            Usuario resp = controlador.getUsuarios().stream()
                    .filter(u -> u.getNombre().equals(userSeleccionado))
                    .findFirst().orElse(null);

            if(resp != null && metaSeleccionada != null) {
                Tarea t = new Tarea(titulo, "Desc", resp, LocalDate.now(), LocalDate.now().plusDays(7), EstadoTarea.PENDIENTE);
                controlador.agregarTareaAMeta(metaSeleccionada, t);
                JOptionPane.showMessageDialog(this, "Tarea agregada");
            }
        });

        add(btnGuardar);
    }
}