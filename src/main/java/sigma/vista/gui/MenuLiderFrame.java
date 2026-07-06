package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.EstadoTarea;
import sigma.modelo.Meta;
import sigma.modelo.Tarea;
import sigma.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MenuLiderFrame extends JFrame {

    private final GestorSigma controlador;
    private final Usuario usuarioActual;

    public MenuLiderFrame(GestorSigma controlador, Usuario usuarioActual) {
        this.controlador = controlador;
        this.usuarioActual = usuarioActual;
        setTitle("SIGMA - Menú Líder");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(9, 1, 10, 10));

        JButton btnCrearMeta = new JButton("Crear Meta");
        JButton btnAsignarTarea = new JButton("Asignar Tarea");
        JButton btnVerTareas = new JButton("Ver Tareas");
        JButton btnProgreso = new JButton("Ver Progreso de Metas");
        JButton btnCambiarEstado = new JButton("Cambiar Estado de Tarea");
        JButton btnEditarFechas = new JButton("Editar Fechas de Tarea");
        JButton btnEliminarTarea = new JButton("Eliminar Tarea");
        JButton btnVerUsuarios = new JButton("Ver Usuarios");
        JButton btnCerrar = new JButton("Cerrar Sesión");

        btnCrearMeta.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog("Nombre de la meta:");
            if (nombre == null) return;
            if (nombre.isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "El nombre de la meta no puede estar vacío ni contener solo espacios.",
                        "Nombre inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean ok = controlador.agregarMeta(nombre);
            JOptionPane.showMessageDialog(this, ok ? "Meta creada." : "La meta ya existe.");
        });

        btnAsignarTarea.addActionListener(e -> {
            if (controlador.getMetas().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Primero debes crear al menos una meta antes de asignar tareas.",
                        "Sin metas", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JDialog dialog = new JDialog(this, "Asignar Tarea", true);
            dialog.add(new GestionTareasPanel(controlador));
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        btnVerTareas.addActionListener(e -> {
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
                    "Seleccione el usuario para ver sus tareas:",
                    "Ver Tareas por Usuario",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (seleccion == null) return;

            String nombreUsuario = seleccion.split(" — ")[0];
            Usuario seleccionado = controlador.getUsuarios().stream()
                    .filter(u -> u.getNombre().equals(nombreUsuario))
                    .findFirst()
                    .orElse(null);

            if (seleccionado == null) {
                JOptionPane.showMessageDialog(this, "Usuario no encontrado.");
                return;
            }

            if (controlador.getTareasDeUsuario(seleccionado).isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "El usuario " + seleccionado.getNombre() + " no tiene tareas asignadas.",
                        "Sin tareas",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            new VerTareasDialog(this, controlador, seleccionado).setVisible(true);
        });

        btnProgreso.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Progreso de Metas", true);
            dialog.add(new ProgresoMetasPanel(controlador));
            dialog.setPreferredSize(new Dimension(420, 300));
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        btnCambiarEstado.addActionListener(e -> {
            boolean hayTareas = controlador.getMetas().stream()
                    .anyMatch(m -> !m.getTareas().isEmpty());
            if (!hayTareas) {
                JOptionPane.showMessageDialog(this,
                        "No existen tareas asignadas en el sistema.",
                        "Sin tareas", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JDialog dialog = new JDialog(this, "Cambiar Estado de Tarea", true);
            dialog.add(new CambiarEstadoPanel(controlador, usuarioActual));
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        btnEditarFechas.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Editar Fechas de Tarea", true);
            dialog.add(new EditarFechasPanel(controlador, dialog));
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        btnEliminarTarea.addActionListener(e -> {
            List<Meta> metas = controlador.getMetas();
            if (metas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay metas registradas en el sistema.",
                        "Sin metas", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] nombresMetas = metas.stream()
                    .map(Meta::getNombre)
                    .toArray(String[]::new);

            String metaSeleccionada = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleccionar meta:",
                    "Eliminar Tarea",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    nombresMetas,
                    nombresMetas[0]);

            if (metaSeleccionada == null) return;

            Meta meta = controlador.buscarMeta(metaSeleccionada);
            if (meta == null || meta.getTareas().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La meta seleccionada no tiene tareas.",
                        "Sin tareas", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] titulosTareas = meta.getTareas().stream()
                    .map(Tarea::getTitulo)
                    .toArray(String[]::new);

            String tareaSeleccionada = (String) JOptionPane.showInputDialog(
                    this,
                    "Seleccionar tarea a eliminar:",
                    "Eliminar Tarea",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    titulosTareas,
                    titulosTareas[0]);

            if (tareaSeleccionada == null) return;

            int conf = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar la tarea \"" + tareaSeleccionada + "\" de la meta \"" + metaSeleccionada + "\"?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (conf == JOptionPane.YES_OPTION) {
                boolean ok = controlador.eliminarTareaDeMetaPorTitulo(metaSeleccionada, tareaSeleccionada);
                JOptionPane.showMessageDialog(this,
                        ok ? "Tarea eliminada correctamente." : "No se pudo eliminar la tarea.",
                        "Resultado", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVerUsuarios.addActionListener(e -> {
            new VerUsuariosDialog(this, controlador).setVisible(true);
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
            new LoginFrame(controlador).setVisible(true);
        });

        add(btnVerUsuarios);
        add(btnCrearMeta);
        add(btnProgreso);
        add(btnAsignarTarea);
        add(btnVerTareas);
        add(btnCambiarEstado);
        add(btnEditarFechas);
        add(btnEliminarTarea);
        add(btnCerrar);
    }
}