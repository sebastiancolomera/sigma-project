package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.Meta;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProgresoMetasPanel extends JPanel {

    public ProgresoMetasPanel(GestorSigma controlador) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        List<Meta> metas = controlador.getMetas();

        if (metas.isEmpty()) {
            add(new JLabel("No hay metas registradas."));
            return;
        }

        for (Meta meta : metas) {
            int progreso = (int) Math.round(meta.calcularProgreso());

            JLabel lblNombre = new JLabel(meta.getNombre() + " - " + progreso + "% completado");
            lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

            JProgressBar barra = new JProgressBar(0, 100);
            barra.setValue(progreso);
            barra.setStringPainted(true);
            barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            barra.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (progreso == 100) {
                barra.setForeground(new Color(56, 142, 60));
            } else if (progreso >= 50) {
                barra.setForeground(new Color(251, 192, 45));
            } else {
                barra.setForeground(new Color(211, 47, 47));
            }
            add(lblNombre);
            add(Box.createVerticalStrut(4));
            add(barra);
            add(Box.createVerticalStrut(14));
        }
    }
}