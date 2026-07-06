package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.app.ResultadoOperacion;
import sigma.modelo.RolUsuario;
import sigma.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class MenuSuperusuarioFrame extends JFrame {

    private final GestorSigma gestorSigma;
    private final Usuario usuarioActual;

    public MenuSuperusuarioFrame(GestorSigma gestorSigma, Usuario usuarioActual) {
        this.gestorSigma = gestorSigma;
        this.usuarioActual = usuarioActual;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("SIGMA - Menú Superusuario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));

        JLabel etiquetaBienvenida = new JLabel(
                "Bienvenido, " + usuarioActual.getNombre() + " (Superusuario)");
        etiquetaBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        etiquetaBienvenida.setFont(etiquetaBienvenida.getFont().deriveFont(Font.BOLD, 14f));

        JButton botonRegistrar = new JButton("Registrar Usuario");
        JButton botonEliminar = new JButton("Eliminar Usuario");
        JButton botonCambiarRol = new JButton("Cambiar Rol");
        JButton botonResetear = new JButton("Resetear Sistema");
        JButton botonCerrarSesion = new JButton("Cerrar Sesión");

        botonRegistrar.addActionListener(e -> accionRegistrarUsuario());
        botonEliminar.addActionListener(e -> accionEliminarUsuario());
        botonCambiarRol.addActionListener(e -> accionCambiarRol());
        botonResetear.addActionListener(e -> accionResetearSistema());
        botonCerrarSesion.addActionListener(e -> accionCerrarSesion());

        JButton[] botones = {botonRegistrar, botonEliminar, botonCambiarRol, botonResetear, botonCerrarSesion};
        for (JButton boton : botones) {
            boton.setAlignmentX(Component.CENTER_ALIGNMENT);
            boton.setMaximumSize(new Dimension(260, 36));
        }

        panelPrincipal.add(etiquetaBienvenida);
        panelPrincipal.add(Box.createVerticalStrut(24));
        panelPrincipal.add(botonRegistrar);
        panelPrincipal.add(Box.createVerticalStrut(10));
        panelPrincipal.add(botonEliminar);
        panelPrincipal.add(Box.createVerticalStrut(10));
        panelPrincipal.add(botonCambiarRol);
        panelPrincipal.add(Box.createVerticalStrut(10));
        panelPrincipal.add(botonResetear);
        panelPrincipal.add(Box.createVerticalStrut(24));
        panelPrincipal.add(botonCerrarSesion);

        setContentPane(panelPrincipal);
    }

    private RolUsuario[] obtenerRolesAsignables() {
        return Arrays.stream(RolUsuario.values())
                .filter(r -> r != RolUsuario.SUPERUSUARIO)
                .toArray(RolUsuario[]::new);
    }

    private void accionRegistrarUsuario() {
        JTextField campoNombre = new JTextField();
        JPasswordField campoContrasena = new JPasswordField();
        JComboBox<RolUsuario> comboRol = new JComboBox<>(obtenerRolesAsignables());

        Object[] mensaje = {
                "Nombre de usuario:", campoNombre,
                "Contraseña:", campoContrasena,
                "Rol:", comboRol
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Registrar Usuario",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        String nombre = campoNombre.getText();
        String contrasena = new String(campoContrasena.getPassword());
        RolUsuario rol = (RolUsuario) comboRol.getSelectedItem();

        if (nombre == null || nombre.isBlank() || contrasena == null || contrasena.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre de usuario y la contraseña no pueden estar vacíos ni contener solo espacios.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean registrado = gestorSigma.registrarUsuario(nombre, contrasena, rol);
        if (registrado) {
            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "El usuario ya existe.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminarUsuario() {
        List<Usuario> eliminables = gestorSigma.getUsuariosSinSuperusuario();

        if (eliminables.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay usuarios elegibles para eliminar (solo existe el Superusuario).",
                    "Sin usuarios disponibles", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Usuario seleccionado = (Usuario) JOptionPane.showInputDialog(
                this, "Selecciona el usuario a eliminar:", "Eliminar Usuario",
                JOptionPane.QUESTION_MESSAGE, null, eliminables.toArray(), eliminables.get(0));

        if (seleccionado == null) {
            return;
        }

        boolean eliminado = gestorSigma.eliminarUsuario(seleccionado.getNombre());
        if (eliminado) {
            JOptionPane.showMessageDialog(this,
                    "Usuario '" + seleccionado.getNombre() + "' eliminado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No fue posible eliminar al usuario indicado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionCambiarRol() {
        List<Usuario> objetivos = gestorSigma.getUsuariosSinSuperusuario();

        if (objetivos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay usuarios elegibles para cambiar de rol.",
                    "Sin usuarios disponibles", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Usuario seleccionado = (Usuario) JOptionPane.showInputDialog(
                this, "Selecciona el usuario:", "Cambiar Rol",
                JOptionPane.QUESTION_MESSAGE, null, objetivos.toArray(), objetivos.get(0));
        if (seleccionado == null) {
            return;
        }

        RolUsuario nuevoRol = (RolUsuario) JOptionPane.showInputDialog(
                this, "Nuevo rol para " + seleccionado.getNombre() + ":", "Cambiar Rol",
                JOptionPane.QUESTION_MESSAGE, null, obtenerRolesAsignables(), null);
        if (nuevoRol == null) {
            return;
        }

        ResultadoOperacion resultado = gestorSigma.actualizarRol(seleccionado.getNombre(), nuevoRol, usuarioActual);
        mostrarResultado(resultado);
    }

    private void accionResetearSistema() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "Esta acción eliminará TODOS los usuarios y metas del sistema y no se puede deshacer.\n"
                        + "¿Está seguro de que desea resetear el sistema?",
                "Confirmar Reseteo del Sistema",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        gestorSigma.resetearSistema();

        JOptionPane.showMessageDialog(this,
                "El sistema fue reseteado correctamente. Debe iniciar sesión nuevamente.",
                "Sistema Reseteado", JOptionPane.INFORMATION_MESSAGE);

        dispose();
        new LoginFrame(gestorSigma).setVisible(true);
    }

    private void accionCerrarSesion() {
        dispose();
        new LoginFrame(gestorSigma).setVisible(true);
    }

    private void mostrarResultado(ResultadoOperacion resultado) {
        if (resultado.isExito()) {
            JOptionPane.showMessageDialog(this, resultado.getMensaje(), "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, resultado.getMensaje(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}