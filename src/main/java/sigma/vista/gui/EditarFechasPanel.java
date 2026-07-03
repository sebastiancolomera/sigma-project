package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.Meta;
import sigma.modelo.Tarea;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class EditarFechasPanel extends JPanel {

    private final GestorSigma controlador;
    private final JDialog dialogPadre;

    private final JComboBox<String> comboTareas;
    private final JTextField txtFechaInicio;
    private final JTextField txtFechaTermino;
    private final JButton btnGuardar;

    private final List<Tarea> tareasDisponibles = new ArrayList<>();

    public EditarFechasPanel(GestorSigma controlador, JDialog dialogPadre) {
        this.controlador = controlador;
        this.dialogPadre = dialogPadre;

        setLayout(new GridLayout(4, 2));

        comboTareas = new JComboBox<>();
        txtFechaInicio = new JTextField();
        txtFechaTermino = new JTextField();
        btnGuardar = new JButton("Guardar");
        btnGuardar.setEnabled(false);

        cargarTareas();

        add(new JLabel("Tarea:"));
        add(comboTareas);
        add(new JLabel("Fecha inicio (AAAA-MM-DD):"));
        add(txtFechaInicio);
        add(new JLabel("Fecha término (AAAA-MM-DD):"));
        add(txtFechaTermino);
        add(new JLabel());
        add(btnGuardar);

        comboTareas.addActionListener(e -> precargarFechas());

        txtFechaInicio.getDocument().addDocumentListener(new SimpleDocumentListener(this::validarFechas));
        txtFechaTermino.getDocument().addDocumentListener(new SimpleDocumentListener(this::validarFechas));

        btnGuardar.addActionListener(e -> guardar());

        precargarFechas();
    }

    private void cargarTareas() {
        comboTareas.removeAllItems();
        tareasDisponibles.clear();
        for (Meta m : controlador.getMetas()) {
            for (Tarea t : m.getTareas()) {
                tareasDisponibles.add(t);
                comboTareas.addItem(t.getTitulo());
            }
        }
    }

    private void precargarFechas() {
        int index = comboTareas.getSelectedIndex();
        if (index < 0 || index >= tareasDisponibles.size()) {
            return;
        }
        Tarea seleccionada = tareasDisponibles.get(index);
        txtFechaInicio.setText(seleccionada.getFechaInicio() != null
                ? seleccionada.getFechaInicio().toString() : "");
        txtFechaTermino.setText(seleccionada.getFechaTermino() != null
                ? seleccionada.getFechaTermino().toString() : "");
        validarFechas();
    }

    private void validarFechas() {
        try {
            LocalDate inicio = LocalDate.parse(txtFechaInicio.getText().trim());
            LocalDate termino = LocalDate.parse(txtFechaTermino.getText().trim());
            btnGuardar.setEnabled(!termino.isBefore(inicio));
        } catch (DateTimeParseException ex) {
            btnGuardar.setEnabled(false);
        }
    }

    private void guardar() {
        int index = comboTareas.getSelectedIndex();
        if (index < 0 || index >= tareasDisponibles.size()) {
            return;
        }
        String titulo = tareasDisponibles.get(index).getTitulo();

        try {
            LocalDate nuevaFechaInicio = LocalDate.parse(txtFechaInicio.getText().trim());
            LocalDate nuevaFechaTermino = LocalDate.parse(txtFechaTermino.getText().trim());

            boolean exito = controlador.actualizarFechasTarea(titulo, nuevaFechaInicio, nuevaFechaTermino);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Fechas actualizadas correctamente.");
                if (dialogPadre != null) {
                    dialogPadre.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar la tarea.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private final Runnable accion;

        SimpleDocumentListener(Runnable accion) {
            this.accion = accion;
        }

        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) { accion.run(); }
        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) { accion.run(); }
        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) { accion.run(); }
    }
}