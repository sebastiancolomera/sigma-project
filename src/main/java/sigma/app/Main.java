package sigma.app;

import sigma.modelo.Usuario;
import sigma.vista.gui.LoginFrame;
import sigma.vista.gui.RegistroSuperusuarioDialog;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        final GestorSigma controlador = new GestorSigma();
        controlador.cargarDatos();
        controlador.actualizarEstadosVencidos();
        Runtime.getRuntime().addShutdownHook(new Thread(controlador::guardarDatos, "sigma-shutdown-saver"));
        SwingUtilities.invokeLater(() -> {
            if (controlador.getUsuarios().stream().noneMatch(Usuario::esSuperusuario)) {
                new RegistroSuperusuarioDialog(controlador).setVisible(true);
            }
            LoginFrame login = new LoginFrame(controlador);
            login.setVisible(true);
        });
    }
}