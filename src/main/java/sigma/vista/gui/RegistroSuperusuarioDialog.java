package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.RolUsuario;

import javax.swing.*;
import java.awt.*;

public class RegistroSuperusuarioDialog extends JDialog {

    public RegistroSuperusuarioDialog(GestorSigma controlador) {
        setTitle("Registro de SuperUsuario");
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setSize(380, 220);
        setLocationRelativeTo(null);
        setModal(true);
        setResizable(false);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmarSalida();
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsuario = new JLabel("Nombre de usuario:");
        JTextField txtUsuario = new JTextField(15);
        JLabel lblPass = new JLabel("Contraseña:");
        JPasswordField txtPass = new JPasswordField(15);
        JLabel lblPass2 = new JLabel("Confirmar contraseña:");
        JPasswordField txtPass2 = new JPasswordField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblUsuario, gbc);
        gbc.gridx = 1;
        panel.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblPass, gbc);
        gbc.gridx = 1;
        panel.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblPass2, gbc);
        gbc.gridx = 1;
        panel.add(txtPass2, gbc);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> {
            String nombre = txtUsuario.getText();
            String pass = new String(txtPass.getPassword());
            String pass2 = new String(txtPass2.getPassword());

            if (nombre.isBlank() || pass.isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "El nombre de usuario y la contraseña no pueden estar vacíos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!pass.equals(pass2)) {
                JOptionPane.showMessageDialog(this,
                        "Las contraseñas no coinciden.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean ok = controlador.registrarUsuario(nombre, pass, RolUsuario.SUPERUSUARIO);
            if (ok) {
                controlador.guardarDatos();
                JOptionPane.showMessageDialog(this,
                        "SuperUsuario registrado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo registrar. El nombre de usuario ya existe o los datos son inválidos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> confirmarSalida());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnPanel.add(btnRegistrar);
        btnPanel.add(btnCancelar);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void confirmarSalida() {
        int resp = JOptionPane.showConfirmDialog(this,
                "Debe registrar un SuperUsuario para usar el sistema.\n¿Desea salir de la aplicación?",
                "Registro requerido",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
