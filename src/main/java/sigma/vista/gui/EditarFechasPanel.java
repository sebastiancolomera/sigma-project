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

    private JComboBox<Integer> cbDiaInicio, cbMesInicio, cbAnioInicio;
    private JComboBox<Integer> cbDiaTermino, cbMesTermino, cbAnioTermino;

    private final List<Tarea> tareasDisponibles = new ArrayList<>();

    public EditarFechasPanel(GestorSigma controlador, JDialog dialogPadre) {
        this.controlador = controlador;
        this.dialogPadre = dialogPadre;

        setLayout(new GridLayout(4, 2, 5, 5));

        comboTareas = new JComboBox<>();
        btnGuardar = new JButton("Guardar");

        cargarTareas();

        JPanel pnlInicio = crearSelectorFecha(true);
        JPanel pnlTermino = crearSelectorFecha(false);

        add(new JLabel("Tarea:"));
        add(comboTareas);
        add(new JLabel("Fecha inicio:"));
        add(pnlInicio);
        add(new JLabel("Fecha término:"));
        add(pnlTermino);
        add(new JLabel());
        add(btnGuardar);

        comboTareas.addActionListener(e -> precargarFechas());

        btnGuardar.addActionListener(e -> guardar());

        precargarFechas();
    }

    private JPanel crearSelectorFecha(boolean esInicio) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));

        Integer[] dias = new Integer[31];
        for (int i = 0; i < 31; i++) dias[i] = i + 1;
        JComboBox<Integer> cbDia = new JComboBox<>(dias);

        Integer[] meses = new Integer[12];
        for (int i = 0; i < 12; i++) meses[i] = i + 1;
        JComboBox<Integer> cbMes = new JComboBox<>(meses);

        int anioActual = LocalDate.now().getYear();
        Integer[] anios = new Integer[6];
        for (int i = 0; i < 6; i++) anios[i] = anioActual + i;
        JComboBox<Integer> cbAnio = new JComboBox<>(anios);

        panel.add(cbDia);
        panel.add(new JLabel("/"));
        panel.add(cbMes);
        panel.add(new JLabel("/"));
        panel.add(cbAnio);

        if (esInicio) {
            cbDiaInicio = cbDia;
            cbMesInicio = cbMes;
            cbAnioInicio = cbAnio;
        } else {
            cbDiaTermino = cbDia;
            cbMesTermino = cbMes;
            cbAnioTermino = cbAnio;
        }

        return panel;
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
            cbDiaInicio.setSelectedItem(inicio.getDayOfMonth());
            cbMesInicio.setSelectedItem(inicio.getMonthValue());
            cbAnioInicio.setSelectedItem(inicio.getYear());
        }

        LocalDate termino = seleccionada.getFechaTermino();
        if (termino != null) {
            cbDiaTermino.setSelectedItem(termino.getDayOfMonth());
            cbMesTermino.setSelectedItem(termino.getMonthValue());
            cbAnioTermino.setSelectedItem(termino.getYear());
        }
    }

    private void guardar() {
        int index = comboTareas.getSelectedIndex();
        if (index < 0 || index >= tareasDisponibles.size()) {
            return;
        }
        String titulo = tareasDisponibles.get(index).getTitulo();

        LocalDate hoy = LocalDate.now();

        int diaIni = valorSeguro(cbDiaInicio, hoy.getDayOfMonth());
        int mesIni = valorSeguro(cbMesInicio, hoy.getMonthValue());
        int anioIni = valorSeguro(cbAnioInicio, hoy.getYear());

        int diaFin = valorSeguro(cbDiaTermino, hoy.getDayOfMonth());
        int mesFin = valorSeguro(cbMesTermino, hoy.getMonthValue());
        int anioFin = valorSeguro(cbAnioTermino, hoy.getYear());

        if (!ValidadorFecha.esFechaValida(diaIni, mesIni, anioIni)
                || !ValidadorFecha.esFechaValida(diaFin, mesFin, anioFin)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha seleccionada no existe en el calendario.",
                    "Fecha inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate nuevaFechaInicio = LocalDate.of(anioIni, mesIni, diaIni);
        LocalDate nuevaFechaTermino = LocalDate.of(anioFin, mesFin, diaFin);

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

    private int valorSeguro(JComboBox<Integer> combo, int valorPorDefecto) {
        Integer valor = (Integer) combo.getSelectedItem();
        return (valor != null) ? valor : valorPorDefecto;
    }
}