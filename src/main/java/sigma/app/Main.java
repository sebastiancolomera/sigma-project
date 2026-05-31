package sigma.app;

import sigma.modelo.RolUsuario;
import sigma.vista.gui.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        GestorSigma controlador = new GestorSigma();

        controlador.cargarDatos();

        if (comtrolador.getUsuario().isEmpty()) {
            controlador.registrarUsuario("admin", "admin123", RolUsuario.SUPERUSUARIO);
        }

        SwingUtilities.invokeLater(()-> {
            LoginFrame loginFrame = new LoginFrame(controlador);
            loginFrame.setVisible(true);
        });
    }
}