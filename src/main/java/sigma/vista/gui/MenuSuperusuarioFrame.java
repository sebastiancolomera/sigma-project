package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.app.ResultadoOperacion;
import sigma.modelo.RolUsuario;
import sigma.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MenuSuperusuarioFrame extends JFrame {

    private final GestorSigma controlador;
    private final Usuario usuarioActual;

    public MenuSuperusuarioFrame(GestorSigma controlador, Usuario usuarioActual) {
        this.controlador = controlador;
        this.usuarioActual = usuarioActual;
        setTitle("SIGMA - Menú Superusuario");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton btnRegistrar = new JButton("Registrar Usuario");
        JButton btnEliminar = new JButton("Eliminar Usuario");
        JButton btnCambiarRol = new JButton("Cambiar Rol");
        JButton btnReset = new JButton("Resetear Sistema");
        JButton btnCerrar = new JButton("Cerrar sesión");

        btnRegistrar.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Nombre del usuario:");
            if (nombre == null) return;
            String contrasena = JOptionPane.showInputDialog(this, "Contraseña:");
            if (contrasena == null) return;
            RolUsuario[] roles = RolUsuario.values();
            RolUsuario rol = (RolUsuario) JOptionPane.showInputDialog(this, "Rol:",
                    "Seleccionar Rol", JOptionPane.QUESTION_MESSAGE, null, roles, roles[2]);
            if (rol == null) return;
            boolean ok = controlador.registrarUsuario(nombre, contrasena, rol);
            JOptionPane.showMessageDialog(this, ok ? "Usuario registrado." : "El usuario ya existe.");
        });

        btnEliminar.addActionListener(e -> {
            List<Usuario> usuarios = controlador.getUsuarios();
            if (usuarios.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay usuarios registrados.");
                return;
            }

            String[] opciones = usuarios.stream()
                    .map(u -> u.getNombre() + " — " + u.getRol())
                    .toArray(String[]::new);

            String seleccion = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleccione el usuario a eliminar:",
                    "Eliminar Usuario",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == null) return;

            String nombre = seleccion.split(" — ")[0];

            if (nombre.equalsIgnoreCase(usuarioActual.getNombre())) {
                JOptionPane.showMessageDialog(this,
                        "No puedes eliminarte a ti mismo.",
                        "Acción no permitida", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de eliminar al usuario \"" + nombre + "\"?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean ok = controlador.eliminarUsuario(nombre);
                JOptionPane.showMessageDialog(this, ok ? "Usuario eliminado." : "Usuario no encontrado.");
            }
        });

        btnCambiarRol.addActionListener(e -> {
            List<Usuario> usuarios = controlador.getUsuariosSinSuperusuario();
            if (usuarios.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay usuarios registrados.");
                return;
            }

            String[] opciones = usuarios.stream()
                    .map(u -> u.getNombre() + " — " + u.getRol())
                    .toArray(String[]::new);

            String seleccion = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleccione el usuario para cambiar su rol:",
                    "Cambiar Rol",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == null) return;

            String nombre = seleccion.split(" — ")[0];

            RolUsuario[] roles = RolUsuario.values();
            RolUsuario nuevoRol = (RolUsuario) JOptionPane.showInputDialog(
                    this,
                    "Seleccione el nuevo rol para \"" + nombre + "\":",
                    "Nuevo Rol",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    roles,
                    roles[0]
            );

            if (nuevoRol == null) return;

            ResultadoOperacion resultado = controlador.actualizarRol(nombre, nuevoRol, usuarioActual);
            JOptionPane.showMessageDialog(this, resultado.getMensaje());
        });

        btnReset.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas resetear el sistema? Se eliminarán todos los usuarios y metas.",
                    "Confirmar Reseteo", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) this.controlador.resetearSistema();
        });

        btnCerrar.addActionListener(e -> {
            boolean ok = controlador.guardarDatos();
            if (!ok) {
                int resp = JOptionPane.showConfirmDialog(this,
                        "No se pudieron guardar los datos correctamente.\n" +
                                "¿Deseas cerrar sesión de todas formas?",
                        "Error al guardar", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (resp != JOptionPane.YES_OPTION) return;
            }
            this.dispose();
            new LoginFrame(this.controlador).setVisible(true);
        });

        add(btnRegistrar);
        add(btnEliminar);
        add(btnCambiarRol);
        add(btnReset);
        add(btnCerrar);
    }
}