package sigma.vista.gui;

import sigma.app.GestorSigma;
import sigma.modelo.EstadoTarea;
import sigma.modelo.Meta;
import sigma.modelo.Tarea;
import sigma.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestionTareasPanel extends JPanel {

    private final GestorSigma controlador;
    private final JComboBox<String> cmbMetas;
    private final JComboBox<String> cmbUsuarios;
    private final JTextField txtTitulo;
    private final JTextField txtDescripcion;
    private final JTextField txtFechaInicio;
    private final JTextField txtFechaTermino;

    public GestionTareasPanel(GestorSigma controlador) {
        this.controlador = controlador;
        setLayout(new GridLayout(8, 2, 10, 10));

        add(new JLabel("Título:"));
        txtTitulo = new JTextField();
        add(txtTitulo);

        add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField();
        add(txtDescripcion);

        add(new JLabel("Meta:"));
        cmbMetas = new JComboBox<>();
        cargarMetas();
        add(cmbMetas);

        add(new JLabel("Asignar a:"));
        cmbUsuarios = new JComboBox<>();
        cargarUsuarios();
        add(cmbUsuarios);

        add(new JLabel("Fecha inicio (YYYY-MM-DD):"));
        txtFechaInicio = new JTextField(LocalDate.now().toString());
        add(txtFechaInicio);

        add(new JLabel("Fecha término (YYYY-MM-DD):"));
        txtFechaTermino = new JTextField(LocalDate.now().plusDays(7).toString());
        add(txtFechaTermino);

        JButton btnGuardar = new JButton("Guardar Tarea");
        btnGuardar.addActionListener(e -> guardarTarea());
        add(btnGuardar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> {
            cargarMetas();
            cargarUsuarios();
        });
        add(btnRefrescar);
    }

    private void cargarMetas() {
        cmbMetas.removeAllItems();
        for (Meta meta : controlador.getMetas()) {
            cmbMetas.addItem(meta.getNombre());
        }
        if (cmbMetas.getItemCount() == 0) {
            cmbMetas.addItem("(Sin metas)");
        }
    }
    private void cargarUsuarios() {
        cmbUsuarios.removeAllItems();
        List<Usuario> usuariosFiltrados = controlador.getUsuariosSinSuperusuario();
        for (Usuario u : usuariosFiltrados) {
            cmbUsuarios.addItem(u.getNombre());
        }
        if (cmbUsuarios.getItemCount() == 0) {
            cmbUsuarios.addItem("(Sin usuarios)");
        }
    }

    private void guardarTarea() {
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String nombreMeta = (String) cmbMetas.getSelectedItem();
        String nombreUsuario = (String) cmbUsuarios.getSelectedItem();
        String fechaInicioStr = txtFechaInicio.getText().trim();
        String fechaTerminoStr = txtFechaTermino.getText().trim();

        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título es obligatorio.");
            return;
        }

        if (nombreMeta == null || nombreMeta.equals("(Sin metas)")) {
            JOptionPane.showMessageDialog(this, "No hay metas disponibles. Cree una meta primero.");
            return;
        }

        if (nombreUsuario == null || nombreUsuario.equals("(Sin usuarios)")) {
            JOptionPane.showMessageDialog(this, "No hay usuarios disponibles.");
            return;
        }

        Usuario asignado = controlador.getUsuarios().stream()
                .filter(u -> u.getNombre().equals(nombreUsuario))
                .findFirst()
                .orElse(null);

        if (asignado == null) {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado.");
            return;
        }

        try {
            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
            LocalDate fechaTermino = LocalDate.parse(fechaTerminoStr);

            if (fechaTermino.isBefore(fechaInicio)) {
                JOptionPane.showMessageDialog(this, "La fecha término no puede ser anterior a la fecha inicio.");
                return;
            }

            Tarea tarea = new Tarea(titulo, descripcion, asignado, fechaInicio, fechaTermino, EstadoTarea.PENDIENTE);
            boolean ok = controlador.agregarTareaAMeta(nombreMeta, tarea);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Tarea asignada exitosamente.");
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al asignar la tarea.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD.");
        }
    }

    private void limpiarCampos() {
        txtTitulo.setText("");
        txtDescripcion.setText("");
        txtFechaInicio.setText(LocalDate.now().toString());
        txtFechaTermino.setText(LocalDate.now().plusDays(7).toString());
        cargarMetas();
        cargarUsuarios();
    }
}