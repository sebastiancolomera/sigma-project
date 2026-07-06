package sigma.vista.gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class SelectorFechaPanel extends JPanel {

    private final JComboBox<Integer> cbDia;
    private final JComboBox<Integer> cbMes;
    private final JComboBox<Integer> cbAnio;

    public SelectorFechaPanel() {
        this(LocalDate.now());
    }

    public SelectorFechaPanel(LocalDate fechaDefecto) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));

        Integer[] dias = new Integer[31];
        for (int i = 0; i < 31; i++) dias[i] = i + 1;
        cbDia = new JComboBox<>(dias);

        Integer[] meses = new Integer[12];
        for (int i = 0; i < 12; i++) meses[i] = i + 1;
        cbMes = new JComboBox<>(meses);

        int anioActual = LocalDate.now().getYear();
        Integer[] anios = new Integer[6];
        for (int i = 0; i < 6; i++) anios[i] = anioActual + i;
        cbAnio = new JComboBox<>(anios);

        add(cbDia);
        add(new JLabel("/"));
        add(cbMes);
        add(new JLabel("/"));
        add(cbAnio);

        setFecha(fechaDefecto);
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) return;
        cbDia.setSelectedItem(fecha.getDayOfMonth());
        cbMes.setSelectedItem(fecha.getMonthValue());
        cbAnio.setSelectedItem(fecha.getYear());
    }

    public int getDia() {
        Integer valor = (Integer) cbDia.getSelectedItem();
        return valor != null ? valor : 1;
    }

    public int getMes() {
        Integer valor = (Integer) cbMes.getSelectedItem();
        return valor != null ? valor : 1;
    }

    public int getAnio() {
        Integer valor = (Integer) cbAnio.getSelectedItem();
        return valor != null ? valor : LocalDate.now().getYear();
    }

    public LocalDate getFecha() {
        try {
            return LocalDate.of(getAnio(), getMes(), getDia());
        } catch (Exception e) {
            return null;
        }
    }
}
