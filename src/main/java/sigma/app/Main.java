package sigma.app;

import sigma.vista.VistaCLI;
import sigma.modelo.Usuario;

public class Main {
    public static void main(String[] args) {

        GestorSigma controlador = new GestorSigma();

        controlador.registrarUsuario("admin", "admin123", "superusuario");

        Usuario usuarioAdmin = controlador.autenticarUsuario("admin", "admin123");

        VistaCLI vista = new VistaCLI(controlador);

        vista.setUsuarioLogueado(usuarioAdmin);

        vista.iniciar();
    }
}