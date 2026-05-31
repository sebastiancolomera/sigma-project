package sigma.app;

import sigma.modelo.*;
import sigma.persistencia.GestorJSON;

import java.util.ArrayList;
import java.util.List;

public class GestorSigma {

    private List<Usuario> usuarios;
    private List<Meta> metas;
    private final GestorJSON gestorJSON;

    public GestorSigma() {
        this.usuarios   = new ArrayList<>();
        this.metas      = new ArrayList<>();
        this.gestorJSON = new GestorJSON();
    }

    public void cargarDatos() {
        List<Usuario> usuariosCargados = gestorJSON.cargarUsuarios(SigmaConfig.RUTA_USUARIOS);
        List<Meta>    metasCargadas    = gestorJSON.cargarMetas(SigmaConfig.RUTA_METAS);

        if (usuariosCargados != null) this.usuarios.addAll(usuariosCargados);
        if (metasCargadas    != null) this.metas.addAll(metasCargadas);
    }

    public void guardarDatos() {
        gestorJSON.guardarUsuarios(new ArrayList<>(usuarios), SigmaConfig.RUTA_USUARIOS);
        gestorJSON.guardarMetas(new ArrayList<>(metas),       SigmaConfig.RUTA_METAS);
    }

    public boolean registrarUsuario(String nombre, String contrasena, RolUsuario rol) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nombre)) return false;
        }
        usuarios.add(new Usuario(nombre, contrasena, rol));
        guardarDatos();
        return true;
    }

    public Usuario autenticarUsuario(String nombre, String contrasena) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nombre) && u.getContrasena().equals(contrasena)) {
                return u;
            }
        }
        return null;
    }

    public boolean actualizarRol(String nombreUsuario, RolUsuario nuevoRol) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equalsIgnoreCase(nombreUsuario)) {
                u.setRol(nuevoRol);
                guardarDatos();
                return true;
            }
        }
        return false;
    }

    public boolean eliminarUsuario(String nombreUsuario) {
        boolean eliminado = usuarios.removeIf(u -> u.getNombre().equals(nombreUsuario));
        if (eliminado) guardarDatos();
        return eliminado;
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public List<Meta> getMetas() {
        return new ArrayList<>(metas);
    }

    public void resetearSistema() {
        usuarios.clear();
        metas.clear();
        guardarDatos();
    }

    public Meta buscarMeta(String nombre) {
        for (Meta m : metas) {
            if (m.getNombre().equals(nombre)) {
                return m;
            }
        }
        return null;
    }

    public boolean agregarMeta(String nombre) {
        if (buscarMeta(nombre) != null) return false;
        metas.add(new Meta(nombre));
        guardarDatos();
        return true;
    }

    public boolean eliminarMeta(String nombre) {
        boolean eliminada = metas.removeIf(m -> m.getNombre().equals(nombre));
        if (eliminada) guardarDatos();
        return eliminada;
    }

    public boolean agregarTareaAMeta(String nombreMeta, Tarea tarea) {
        Meta meta = buscarMeta(nombreMeta);
        if (meta != null) {
            meta.agregarTarea(tarea);
            guardarDatos();
            return true;
        }
        return false;
    }

    public boolean cambiarEstadoTarea(String titulo, EstadoTarea nuevo) {
        for (Meta meta : metas) {
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getTitulo().equals(titulo)) {
                    tarea.setEstado(nuevo);
                    guardarDatos();
                    return true;
                }
            }
        }
        return false;
    }

    public List<Tarea> getTareasDeUsuario(Usuario u) {
        List<Tarea> tareasUsuario = new ArrayList<>();
        for (Meta meta : metas) {
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getAsignado().getNombre().equals(u.getNombre())) {
                    tareasUsuario.add(tarea);
                }
            }
        }
        return tareasUsuario;
    }
}