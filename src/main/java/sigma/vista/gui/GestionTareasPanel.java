package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class GestionTareasPanel extends JPanel {

    private JComboBox<Integer> cbDiaInicio, cbMesInicio, cbAnioInicio;
    private JComboBox<Integer> cbDiaTermino, cbMesTermino, cbAnioTermino;

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

        JPanel pnlInicio = crearSelectorFecha(true, hoy.getDayOfMonth(), hoy.getMonthValue(), hoy.getYear());
        JPanel pnlTermino = crearSelectorFecha(false, semana.getDayOfMonth(), semana.getMonthValue(), semana.getYear());

        add(new JLabel("Título de Tarea:"));
        add(txtTitulo);
        add(new JLabel("Descripción:"));
        add(txtDescripcion);
        add(new JLabel("Meta:"));
        add(cmbMetas);
        add(new JLabel("Responsable asignado:"));
        add(cmbUsuarios);
        add(new JLabel("Fecha Inicio:"));
        add(pnlInicio);
        add(new JLabel("Fecha Término:"));
        add(pnlTermino);

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

            LocalDate hoyDefault = LocalDate.now();

            int diaIni = valorSeguro(cbDiaInicio, hoyDefault.getDayOfMonth());
            int mesIni = valorSeguro(cbMesInicio, hoyDefault.getMonthValue());
            int anioIni = valorSeguro(cbAnioInicio, hoyDefault.getYear());
            LocalDate fechaInicio = LocalDate.of(anioIni, mesIni, diaIni);

            LocalDate semanaDefault = hoyDefault.plusDays(7);

            int diaFin = valorSeguro(cbDiaTermino, semanaDefault.getDayOfMonth());
            int mesFin = valorSeguro(cbMesTermino, semanaDefault.getMonthValue());
            int anioFin = valorSeguro(cbAnioTermino, semanaDefault.getYear());
            LocalDate fechaTermino = LocalDate.of(anioFin, mesFin, diaFin);

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

    private JPanel crearSelectorFecha(boolean esInicio, int diaDefault, int mesDefault, int anioDefault) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));

        Integer[] dias = new Integer[31];
        for (int i = 0; i < 31; i++) dias[i] = i + 1;
        JComboBox<Integer> cbDia = new JComboBox<>(dias);
        cbDia.setSelectedItem(diaDefault);

        Integer[] meses = new Integer[12];
        for (int i = 0; i < 12; i++) meses[i] = i + 1;
        JComboBox<Integer> cbMes = new JComboBox<>(meses);
        cbMes.setSelectedItem(mesDefault);

        int anioActual = LocalDate.now().getYear();
        Integer[] anios = new Integer[6];
        for (int i = 0; i < 6; i++) anios[i] = anioActual + i;
        JComboBox<Integer> cbAnio = new JComboBox<>(anios);
        cbAnio.setSelectedItem(anioDefault);

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

    private int valorSeguro(JComboBox<Integer> combo, int valorPorDefecto) {
        Integer valor = (Integer) combo.getSelectedItem();
        return (valor != null) ? valor : valorPorDefecto;
    }
}