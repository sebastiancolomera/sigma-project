package sigma.app;

import sigma.modelo.RolUsuario;
import sigma.vista.gui.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        final GestorSigma controlador = new GestorSigma();
        controlador.cargarDatos();
        controlador.actualizarEstadosVencidos();

        if (controlador.getUsuarios().isEmpty()) {
            controlador.registrarUsuario(
                    SigmaConfig.ADMIN_NOMBRE,
                    SigmaConfig.ADMIN_PASSWORD,
                    RolUsuario.SUPERUSUARIO
            );
        }

        Runtime.getRuntime().addShutdownHook(new Thread(controlador::guardarDatos, "sigma-shutdown-saver"));

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame(controlador);
            login.setVisible(true);
        });
    }
}