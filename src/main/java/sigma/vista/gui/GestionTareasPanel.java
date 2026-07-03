package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class GestionTareasPanel extends JPanel {
    public GestionTareasPanel(GestorSigma controlador) {
        setLayout(new GridLayout(8, 2, 5, 5));

        JTextField txtTitulo = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JTextField txtFechaInicio = new JTextField(LocalDate.now().toString());
        JTextField txtFechaTermino = new JTextField(LocalDate.now().plusDays(7).toString());

        JComboBox<String> cmbMetas = new JComboBox<>();
        controlador.getMetas().forEach(m -> cmbMetas.addItem(m.getNombre()));

        JComboBox<String> cmbUsuarios = new JComboBox<>();
        controlador.getUsuariosSinSuperusuario().forEach(u -> cmbUsuarios.addItem(u.getNombre()));

        add(new JLabel("Título de Tarea:"));
        add(txtTitulo);
        add(new JLabel("Descripción:"));
        add(txtDescripcion);
        add(new JLabel("Meta:"));
        add(cmbMetas);
        add(new JLabel("Responsable asignado:"));
        add(cmbUsuarios);
        add(new JLabel("Fecha Inicio (YYYY-MM-DD):"));
        add(txtFechaInicio);
        add(new JLabel("Fecha Término (YYYY-MM-DD):"));
        add(txtFechaTermino);

        JButton btnGuardar = new JButton("Agregar");

        btnGuardar.addActionListener(e -> {
            String titulo = txtTitulo.getText();
            String descripcion = txtDescripcion.getText().trim();
            String metaSeleccionada = (String) cmbMetas.getSelectedItem();
            String userSeleccionado = (String) cmbUsuarios.getSelectedItem();

            if (titulo.isEmpty() || descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Título y descripción son obligatorios.");
                return;
            }

            LocalDate fechaInicio;
            LocalDate fechaTermino;
            try {
                fechaInicio  = LocalDate.parse(txtFechaInicio.getText().trim());
                fechaTermino = LocalDate.parse(txtFechaTermino.getText().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD.");
                return;
            }

            if (fechaTermino.isBefore(fechaInicio)) {
                JOptionPane.showMessageDialog(this, "La fecha de término no puede ser anterior a la de inicio.");
                return;
            }

            Usuario resp = controlador.getUsuarios().stream()
                    .filter(u -> u.getNombre().equals(userSeleccionado))
                    .findFirst().orElse(null);

            if (resp != null && metaSeleccionada != null) {
                Tarea t = new Tarea(titulo, descripcion, resp, fechaInicio, fechaTermino, EstadoTarea.PENDIENTE);
                controlador.agregarTareaAMeta(metaSeleccionada, t);
                JOptionPane.showMessageDialog(this, "Tarea agregada.");
            }
        });

        add(new JLabel());
        add(btnGuardar);
    }
}