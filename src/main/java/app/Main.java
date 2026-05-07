package app;

import vista.VistaCLI;

public class Main {
    public static void main(String[] args) {
        // 1. Creamos el cerebro (Controlador)
        GestorSigma controlador = new GestorSigma();

        // 2. Creamos un usuario inicial para poder entrar (ya que no hay persistencia aún)
        controlador.registrarUsuario("admin", "admin123", "superusuario");

        // 3. Iniciamos la interfaz pasándole el controlador
        VistaCLI vista = new VistaCLI(controlador);
        vista.iniciar();
    }
}