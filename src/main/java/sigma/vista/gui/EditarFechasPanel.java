package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.app.ValidadorFecha;
import sigma.modelo.Meta;
import sigma.modelo.Tarea;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EditarFechasPanel extends JPanel {

    private final GestorSigma controlador;
    private final JDialog dialogPadre;

    private final JComboBox<String> comboTareas;
    private final JButton btnGuardar;

    private final SelectorFechaPanel selectorInicio;
    private final SelectorFechaPanel selectorTermino;

    private final List<Tarea> tareasDisponibles = new ArrayList<>();

    public EditarFechasPanel(GestorSigma controlador, JDialog dialogPadre) {
        this.controlador = controlador;
        this.dialogPadre = dialogPadre;

        setLayout(new GridLayout(4, 2, 5, 5));

        comboTareas = new JComboBox<>();
        btnGuardar = new JButton("Guardar");
        selectorInicio = new SelectorFechaPanel();
        selectorTermino = new SelectorFechaPanel();

        cargarTareas();

        add(new JLabel("Tarea:"));
        add(comboTareas);
        add(new JLabel("Fecha inicio:"));
        add(selectorInicio);
        add(new JLabel("Fecha término:"));
        add(selectorTermino);
        add(new JLabel());
        add(btnGuardar);

        comboTareas.addActionListener(e -> precargarFechas());

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

        LocalDate inicio = seleccionada.getFechaInicio();
        if (inicio != null) {
            selectorInicio.setFecha(inicio);
        }

        LocalDate termino = seleccionada.getFechaTermino();
        if (termino != null) {
            selectorTermino.setFecha(termino);
        }
    }

    private void guardar() {
        int index = comboTareas.getSelectedIndex();
        if (index < 0 || index >= tareasDisponibles.size()) {
            return;
        }
        String titulo = tareasDisponibles.get(index).getTitulo();

        LocalDate nuevaFechaInicio = selectorInicio.getFecha();
        LocalDate nuevaFechaTermino = selectorTermino.getFecha();

        if (nuevaFechaInicio == null || nuevaFechaTermino == null) {
            JOptionPane.showMessageDialog(this,
                    "La fecha seleccionada no existe en el calendario.",
                    "Fecha inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidadorFecha.esFechaNoAnteriorAHoy(nuevaFechaTermino)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de término no puede ser anterior a la fecha actual.",
                    "Fecha inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
    }
}