package sigma.app;

import sigma.modelo.RolUsuario;
import sigma.modelo.Usuario;
import sigma.persistencia.GestorJSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioUsuarios {

    private final GestorJSON gestorJSON;
    private final String rutaUsuarios;
    private List<Usuario> usuarios;

    public ServicioUsuarios(GestorJSON gestorJSON, String rutaUsuarios) {
        this.gestorJSON = gestorJSON;
        this.rutaUsuarios = rutaUsuarios;
        this.usuarios = new ArrayList<>();
    }

    public void cargar() {
        try {
            List<Usuario> u = gestorJSON.cargarUsuarios(rutaUsuarios);
            if (u != null) {
                this.usuarios.addAll(u);
            }
        } catch (Exception e) {
            System.err.println("No se pudieron cargar los usuarios desde " + rutaUsuarios + ": " + e.getMessage());
        }
    }

    public boolean guardar() {
        try {
            return gestorJSON.guardarUsuarios(new ArrayList<>(usuarios), rutaUsuarios);
        } catch (Exception e) {
            System.err.println("No se pudieron guardar los usuarios en " + rutaUsuarios + ": " + e.getMessage());
            return false;
        }
    }

    public void resetear() {
        usuarios.clear();
        String hash = SeguridadUtil.hashPassword(SigmaConfig.ADMIN_PASSWORD);
        usuarios.add(new Usuario(SigmaConfig.ADMIN_NOMBRE, hash, RolUsuario.SUPERUSUARIO));
        guardar();
    }

    public boolean registrarUsuario(String nombre, String contrasena, RolUsuario rol) {
        if (nombre == null || nombre.isBlank()) return false;
        if (contrasena == null || contrasena.isBlank()) return false;
        if (rol == null) return false;

        boolean existe = usuarios.stream().anyMatch(u -> u.getNombre().equals(nombre));
        if (existe) return false;

        String hash = SeguridadUtil.hashPassword(contrasena);
        usuarios.add(new Usuario(nombre, hash, rol));
        guardar();
        return true;
    }

    public Usuario autenticarUsuario(String nombre, String contrasena) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nombre)
                    && SeguridadUtil.verificarPassword(contrasena, u.getContrasena())) {
                return u;
            }
        }
        return null;
    }

    public ResultadoOperacion actualizarRol(String nombreUsuario, RolUsuario nuevoRol, Usuario ejecutor) {
        if (nombreUsuario == null || nombreUsuario.isBlank() || nuevoRol == null) {
            return new ResultadoOperacion(false, "Datos inválidos para actualizar el rol.");
        }

        if  (ejecutor != null && ejecutor.esSuperusuario() && ejecutor.getNombre().equalsIgnoreCase(nombreUsuario)) {
            return new ResultadoOperacion(false, "El superusuario no puede cambiar su propio rol.");
        }

        for (Usuario u : usuarios) {
            if (u.getNombre().equalsIgnoreCase(nombreUsuario)) {
                u.setRol(nuevoRol);
                guardar();
                return new ResultadoOperacion(true, "Rol actualizado.");
            }
        }
        return new ResultadoOperacion(false, "Usuario no encontrado.");
    }

    public boolean eliminarUsuario(String nombreUsuario) {
        boolean eliminado = usuarios.removeIf(u -> u.getNombre().equals(nombreUsuario));
        if (eliminado) guardar();
        return eliminado;
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public List<Usuario> getUsuariosSinSuperusuario() {
        return getUsuarios().stream()
                .filter(u -> !u.esSuperusuario())
                .collect(Collectors.toList());
    }
}