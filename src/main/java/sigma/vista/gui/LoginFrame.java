package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private GestorSigma controlador;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;

    public LoginFrame(GestorSigma controlador) {
        setTitle("SIGMA - INICIAR SESIÓN");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        add(txtUsuario);

        add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.addActionListener(e -> iniciarSesion());
        add(btnLogin);
    }

    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());
        Usuario u = controlador.autenticarUsuario(usuario, password);

        if (u != null) {
            this.dispose();
            if (u.esSuperusuario()) new MenuSuperusuarioFrame(controlador).setVisible(true);
            else if (u.esLider()) new MenuLiderFrame(controlador).setVisible(true);
            else new MenuUsuarioFrame(controlador, u).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }
}