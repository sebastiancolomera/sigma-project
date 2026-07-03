package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.RolUsuario;
import sigma.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MenuSuperusuarioFrame extends JFrame {

    private final GestorSigma controlador;

    public MenuSuperusuarioFrame(GestorSigma controlador) {
        this.controlador = controlador;
        setTitle("SIGMA - Menú Superusuario");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton btnRegistrar = new JButton("Registrar Usuario");
        JButton btnEliminar = new JButton("Eliminar Usuario");
        JButton btnCambiarRol = new JButton("Cambiar Rol de Usuario");
        JButton btnReset = new JButton("Resetear Sistema");
        JButton btnCerrar = new JButton("Cerrar Sesión");

        btnRegistrar.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Registrar Usuario", true);
            dialog.add(new RegistrarUsuarioPanel(controlador));
            dialog.pack();
            dialog.setVisible(true);
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
            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de eliminar al usuario \"" + nombre + "\"?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean ok = controlador.eliminarUsuario(nombre);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el usuario.");
                }
            }
        });
        btnCambiarRol.addActionListener(e -> {
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
                    "Seleccione el usuario para cambiar su rol:",
                    "Cambiar Rol",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == null) return;

            String nombre = seleccion.split(" — ")[0];

            RolUsuario[] roles = {RolUsuario.SUPERUSUARIO, RolUsuario.LIDER, RolUsuario.USUARIO};
            String[] rolesStr = {"SUPERUSUARIO", "LIDER", "USUARIO"};

            String nuevoRolStr = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleccione el nuevo rol para \"" + nombre + "\":",
                    "Nuevo Rol",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    rolesStr,
                    rolesStr[0]
            );

            if (nuevoRolStr == null) return;

            RolUsuario nuevoRol;
            switch (nuevoRolStr) {
                case "SUPERUSUARIO": nuevoRol = RolUsuario.SUPERUSUARIO; break;
                case "LIDER": nuevoRol = RolUsuario.LIDER; break;
                case "USUARIO": nuevoRol = RolUsuario.USUARIO; break;
                default: return;
            }

            boolean ok = controlador.actualizarRol(nombre, nuevoRol);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Rol actualizado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el rol.");
            }
        });
        btnReset.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de que desea resetear el sistema? Se eliminarán todos los usuarios y metas.",
                    "Confirmar Reseteo",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirmacion == JOptionPane.YES_OPTION) {
                controlador.resetearSistema();
                JOptionPane.showMessageDialog(this, "Sistema reseteado exitosamente.");
            }
        });
        btnCerrar.addActionListener(e -> {
            this.dispose();
            new LoginFrame(controlador).setVisible(true);
        });

        add(btnRegistrar);
        add(btnEliminar);
        add(btnCambiarRol);
        add(btnReset);
        add(btnCerrar);
    }
}
