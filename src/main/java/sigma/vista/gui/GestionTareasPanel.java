package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.app.ValidadorFecha;
import sigma.modelo.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class GestionTareasPanel extends JPanel {

    private final SelectorFechaPanel selectorInicio;
    private final SelectorFechaPanel selectorTermino;

    public GestionTareasPanel(GestorSigma controlador) {
        setLayout(new GridLayout(8, 2, 5, 5));

        JTextField txtTitulo = new JTextField();
        JTextField txtDescripcion = new JTextField();

        JComboBox<String> cmbMetas = new JComboBox<>();
        controlador.getMetas().forEach(m -> cmbMetas.addItem(m.getNombre()));

        JComboBox<String> cmbUsuarios = new JComboBox<>();
        controlador.getUsuariosSinSuperusuario().forEach(u -> cmbUsuarios.addItem(u.getNombre()));

        LocalDate hoy = LocalDate.now();
        LocalDate semana = hoy.plusDays(7);

        selectorInicio = new SelectorFechaPanel(hoy);
        selectorTermino = new SelectorFechaPanel(semana);

        add(new JLabel("Título de Tarea:"));
        add(txtTitulo);
        add(new JLabel("Descripción:"));
        add(txtDescripcion);
        add(new JLabel("Meta:"));
        add(cmbMetas);
        add(new JLabel("Responsable asignado:"));
        add(cmbUsuarios);
        add(new JLabel("Fecha Inicio:"));
        add(selectorInicio);
        add(new JLabel("Fecha Término:"));
        add(selectorTermino);

        JButton btnGuardar = new JButton("Agregar");

        btnGuardar.addActionListener(e -> {
            String titulo = txtTitulo.getText();
            String descripcion = txtDescripcion.getText().trim();
            String metaSeleccionada = (String) cmbMetas.getSelectedItem();
            String userSeleccionado = (String) cmbUsuarios.getSelectedItem();

            if (titulo.isEmpty() || descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Título y descripción son obligatorios.",
                        "Datos incompletos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate fechaInicio = selectorInicio.getFecha();
            LocalDate fechaTermino = selectorTermino.getFecha();

            if (fechaInicio == null || fechaTermino == null) {
                JOptionPane.showMessageDialog(this,
                        "La fecha seleccionada no existe en el calendario.",
                        "Fecha inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorFecha.esFechaNoAnteriorAHoy(fechaInicio)) {
                JOptionPane.showMessageDialog(this,
                        "La fecha de inicio no puede ser anterior a la fecha actual.",
                        "Fecha inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidadorFecha.esFechaNoAnteriorAHoy(fechaTermino)) {
                JOptionPane.showMessageDialog(this,
                        "La fecha de término no puede ser anterior a la fecha actual.",
                        "Fecha inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (fechaTermino.isBefore(fechaInicio)) {
                JOptionPane.showMessageDialog(this,
                        "La fecha de término no puede ser anterior a la de inicio.",
                        "Fechas inválidas", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario resp = controlador.getUsuariosSinSuperusuario().stream()
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