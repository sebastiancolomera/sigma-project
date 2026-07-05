package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VerUsuariosDialog extends JDialog {

    public VerUsuariosDialog(JFrame parent, GestorSigma controlador) {
        super(parent, "Usuarios del Sistema", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        List<Usuario> usuarios = controlador.getUsuarios();

        if (usuarios.isEmpty()) {
            JLabel lblMensaje = new JLabel("No hay usuarios registrados.");
            lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
            add(lblMensaje);
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (Usuario u : usuarios) {
            String texto = u.getNombre() + " — " + u.getRol();
            JLabel label = new JLabel(texto);
            label.setFont(new Font("Monospaced", Font.PLAIN, 14));
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            panel.add(label);
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll);

        JPanel btnPanel = new JPanel();
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        btnPanel.add(btnCerrar);
        add(btnPanel, BorderLayout.SOUTH);
    }
}