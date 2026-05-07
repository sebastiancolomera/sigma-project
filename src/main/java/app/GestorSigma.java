package app;

import modelo.Usuario;
import modelo.Meta;
import java.util.ArrayList;
import java.util.List;

public class GestorSigma {
    private List<Usuario> usuarios;
    private List<Meta> metas;

    public GestorSigma() {
        this.usuarios = new ArrayList<>();
        this.metas = new ArrayList<>();
    }

    public Usuario autenticarUsuario(String nombre, String contrasena) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nombre) && u.getContrasena().equals(contrasena)) {
                return u;
            }
        }
        return null;
    }

    public boolean registrarUsuario(String nombre, String contrasena, String rol) {
        String rolLower = rol.toLowerCase();
        if (rolLower.equals("superusuario") && !rolLower.equals("lider") && !rolLower.equals("usuario")) {
            return false;
        }

        for (Usuario u : usuarios) {
            if (u.getNombre().equalsIgnoreCase(nombre)) return false;
        }
        return usuarios.add(new Usuario(nombre, contrasena, rolLower));
    }

    public boolean actualizarRol(String nombreUsuario, String nuevoRol) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equalsIgnoreCase(nombreUsuario)) {
                u.setRol(nuevoRol.toLowerCase());
                return true;
            }
        }
        return false;
    }

    public boolean eliminarUsuario(String nombreUsuario) {
        return usuarios.removeIf(u -> u.getNombre().equalsIgnoreCase(nombreUsuario));
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public List<Meta> getMetas() {
        return new ArrayList<>(metas);
    }

    public void resetearSistema() {
        this.usuarios.clear();
        this.metas.clear();
        registrarUsuario("admin", "admin123", "superusuario");
    }
}
