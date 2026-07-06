package sigma.app;

import sigma.modelo.*;
import sigma.persistencia.GestorJSON;

import java.time.LocalDate;
import java.util.List;

public class GestorSigma {

    private final ServicioUsuarios servicioUsuarios;
    private final ServicioMetas servicioMetas;

    public GestorSigma(String rutaUsuarios, String rutaMetas) {
        GestorJSON gestorJSON = new GestorJSON();
        this.servicioUsuarios = new ServicioUsuarios(gestorJSON, rutaUsuarios);
        this.servicioMetas = new ServicioMetas(gestorJSON, rutaMetas);
    }

    public GestorSigma() {
        this(SigmaConfig.RUTA_USUARIOS, SigmaConfig.RUTA_METAS);
    }

    public void cargarDatos() {
        servicioUsuarios.cargar();
        servicioMetas.cargar();
    }

    public boolean guardarDatos() {
        boolean okUsuarios = servicioUsuarios.guardar();
        boolean okMetas = servicioMetas.guardar();
        return okUsuarios && okMetas;
    }

    public void resetearSistema() {
        servicioUsuarios.resetear();
        servicioMetas.resetear();
    }

    public void actualizarEstadosVencidos() {
        servicioMetas.actualizarEstadosVencidos();
    }

    public boolean registrarUsuario(String nombre, String contrasena, RolUsuario rol) {
        return servicioUsuarios.registrarUsuario(nombre, contrasena, rol);
    }

    public Usuario autenticarUsuario(String nombre, String contrasena) {
        return servicioUsuarios.autenticarUsuario(nombre, contrasena);
    }

    public ResultadoOperacion actualizarRol(String nombreUsuario, RolUsuario nuevoRol, Usuario ejecutor) {
        return servicioUsuarios.actualizarRol(nombreUsuario, nuevoRol, ejecutor);
    }

    public boolean eliminarUsuario(String nombreUsuario) {
        boolean usuarioEliminado = servicioUsuarios.eliminarUsuario(nombreUsuario);
        if (usuarioEliminado) {
            try {
                servicioMetas.eliminarTareasDeUsuario(nombreUsuario);
            } catch (RuntimeException e) {
                System.err.println("El usuario '" + nombreUsuario
                        + "' fue eliminado, pero no se pudo completar la cascada de sus tareas:");
                e.printStackTrace();
            }
        }
        return usuarioEliminado;
    }

    public List<Usuario> getUsuarios() {
        return servicioUsuarios.getUsuarios();
    }

    public List<Usuario> getUsuariosSinSuperusuario() {
        return servicioUsuarios.getUsuariosSinSuperusuario();
    }

    public Meta buscarMeta(String nombre) {
        return servicioMetas.buscarMeta(nombre);
    }

    public boolean agregarMeta(String nombre) {
        return servicioMetas.agregarMeta(nombre);
    }

    public boolean eliminarMeta(String nombre) {
        return servicioMetas.eliminarMeta(nombre);
    }

    public boolean agregarTareaAMeta(String nombreMeta, Tarea tarea) {
        return servicioMetas.agregarTareaAMeta(nombreMeta, tarea);
    }

    public ResultadoOperacion cambiarEstadoTarea(String titulo, EstadoTarea nuevo, Usuario ejecutor) {
        return servicioMetas.cambiarEstadoTarea(titulo, nuevo, ejecutor);
    }

    public boolean actualizarFechasTarea(String titulo, LocalDate inicio, LocalDate termino) {
        return servicioMetas.actualizarFechasTarea(titulo, inicio, termino);
    }

    public boolean eliminarTareaDeMetaPorTitulo(String nombreMeta, String tituloTarea) {
        return servicioMetas.eliminarTareaDeMetaPorTitulo(nombreMeta, tituloTarea);
    }

    public List<Tarea> getTareasDeUsuario(Usuario u) {
        return servicioMetas.getTareasDeUsuario(u);
    }

    public List<Meta> getMetas() {
        return servicioMetas.getMetas();
    }
}