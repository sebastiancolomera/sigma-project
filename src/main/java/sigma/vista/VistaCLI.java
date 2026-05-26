package sigma.vista;

import sigma.app.GestorSigma;
import sigma.modelo.Usuario;
import java.util.Scanner;
import java.util.List;

public class VistaCLI {
    private GestorSigma controlador;
    private Scanner scanner;
    private Usuario usuarioLogueado;

    public VistaCLI(GestorSigma controlador) {
        this.controlador = controlador;
        this.scanner = new Scanner(System.in);
    }

    // Método temporal para facilitar las pruebas
    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    public void iniciar() {
        System.out.println("=== BIENVENIDO A SIGMA ===");

        while (usuarioLogueado == null) {
            iniciarSesion();
        }

        if (usuarioLogueado.esSuperusuario()) {
            menuSuperusuario();
        } else {
            System.out.println("Funcionalidades para " + usuarioLogueado.getRol() + " en desarrollo para Avance 2.");
        }
    }

    private void iniciarSesion() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Contraseña: ");
        String pass = scanner.nextLine();

        usuarioLogueado = controlador.autenticarUsuario(nombre, pass);

        if (usuarioLogueado != null) {
            System.out.println("\nSesión iniciada correctamente.");
        } else {
            System.out.println("\nError: Usuario o contraseña incorrectos.");
        }
    }

    private void menuSuperusuario() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n========================================");
            System.out.println("           MENU SUPERUSUARIO            ");
            System.out.println("========================================");
            System.out.println("1 -> Registrar Usuario");
            System.out.println("2 -> Eliminar Usuario");
            System.out.println("3 -> Eliminar Registro Total");
            System.out.println("4 -> Cambiar Rol");
            System.out.println("5 -> Asignar Líder");
            System.out.println("6 -> Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> uiRegistrarUsuario();
                case "2" -> uiEliminarUsuario();
                case "3" -> uiEliminarRegistro();
                case "4" -> uiCambiarRol();
                case "5" -> uiAsignarLider();
                case "6" -> salir = true;
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private void uiRegistrarUsuario() {
        System.out.println("\n--- REGISTRO ---");
        System.out.print("Nombre: ");
        String n = scanner.nextLine();
        System.out.print("Pass: ");
        String p = scanner.nextLine();
        System.out.print("Rol (superusuario/lider/usuario): ");
        String r = scanner.nextLine();

        if (controlador.registrarUsuario(n, p, r)) {
            System.out.println("Éxito: Usuario creado.");
        } else {
            System.out.println("Error: Datos inválidos o usuario ya existe.");
        }
    }

    private void uiEliminarUsuario() {
        List<Usuario> lista = controlador.getUsuarios();
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios.");
            return;
        }

        System.out.println("\nSeleccione nombre a eliminar:");
        lista.forEach(u -> System.out.println("- " + u.getNombre()));
        String nombre = scanner.nextLine();

        if (controlador.eliminarUsuario(nombre)) {
            System.out.println("Usuario eliminado.");
        } else {
            System.out.println("No se encontró el usuario.");
        }
    }

    private void uiEliminarRegistro() {
        System.out.print("¿Confirmar borrado total? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            controlador.resetearSistema();
            System.out.println("Sistema reseteado.");
            usuarioLogueado = null;
        }
    }

    private void uiCambiarRol() {
        System.out.print("Nombre del usuario: ");
        String nombre = scanner.nextLine();
        System.out.print("Nuevo rol: ");
        String rol = scanner.nextLine();

        if (controlador.actualizarRol(nombre, rol)) {
            System.out.println("Rol actualizado.");
        } else {
            System.out.println("Error al actualizar.");
        }
    }

    private void uiAsignarLider() {
        System.out.print("Nombre para asignar como líder: ");
        String nombre = scanner.nextLine();
        if (controlador.actualizarRol(nombre, "lider")) {
            System.out.println("Asignación exitosa.");
        } else {
            System.out.println("Usuario no encontrado.");
        }
    }
}
