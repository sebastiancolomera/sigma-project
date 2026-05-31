package sigma.app;

import sigma.modelo.*;
import java.util.ArrayList;
import java.util.List;

public class GestorSigma {
    private List<Usuario> usuarios;
    private List<Meta> metas;

    public GestorSigma() {
        this.usuarios = new ArrayList<>();
        this.metas = new ArrayList<>();
    }

    public boolean registrarUsuario(String nombre, String contrasena, RolUsuario rol) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nombre)) return false;
        }
        usuarios.add(new Usuario(nombre, contrasena, rol));
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
                return true;
            }
        }
        return false;
    }

    public boolean eliminarUsuario(String nombreUsuario) {
        return usuarios.removeIf(u -> u.getNombre().equals(nombreUsuario));
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
        return true;
    }

    public boolean eliminarMeta(String nombre) {
        return metas.removeIf(m -> m.getNombre().equals(nombre));
    }

    public boolean agregarTareaAMeta(String nombreMeta, Tarea tarea) {
        Meta meta = buscarMeta(nombreMeta);
        if (meta != null) {
            meta.agregarTarea(tarea);
            return true;
        }
        return false;
    }

    public boolean cambiarEstadoTarea(String titulo, EstadoTarea nuevo) {
        for (Meta meta : metas) {
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getTitulo().equals(titulo)) {
                    tarea.setEstado(nuevo);
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