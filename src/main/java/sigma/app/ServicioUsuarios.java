package sigma.app;

import sigma.modelo.RolUsuario;
import sigma.modelo.Usuario;
import sigma.persistencia.GestorJSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServicioUsuarios {

    private final GestorJSON gestorJSON;
    private final String rutaArchivo;
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private boolean dirty;
    private final Map<String, Usuario> indiceUsuarios = new HashMap<>();

    public ServicioUsuarios(GestorJSON gestorJSON, String rutaArchivo) {
        this.gestorJSON = gestorJSON;
        this.rutaArchivo = rutaArchivo;
    }

    public void cargar() {
        this.usuarios = gestorJSON.cargarUsuarios(rutaArchivo);
        for (Usuario u : usuarios) {
            if (!u.getContrasena().contains(":")) {
                u.setContrasena(SeguridadUtil.hashPassword(u.getContrasena()));
                marcarDirty();
            }
        }
        reindexar();
    }

    private void marcarDirty() {
        this.dirty = true;
    }

    private void reindexar() {
        indiceUsuarios.clear();
        for (Usuario u : usuarios) {
            indiceUsuarios.put(u.getNombre().toLowerCase(), u);
        }
    }

    public boolean guardar() {
        if (!dirty) return true;
        boolean ok = gestorJSON.guardarUsuarios(usuarios, rutaArchivo);
        if (ok) dirty = false;
        return ok;
    }

    public void resetear() {
        this.usuarios = new ArrayList<>();
        indiceUsuarios.clear();
        marcarDirty();
    }

    public boolean registrarUsuario(String nombre, String contrasena, RolUsuario rol) {
        if (nombre == null || nombre.isBlank() || contrasena == null || contrasena.isBlank()) {
            return false;
        }

        String nombreNormalizado = nombre.trim().toLowerCase();

        if (indiceUsuarios.containsKey(nombreNormalizado)) {
            return false;
        }

        String hash = SeguridadUtil.hashPassword(contrasena);
        Usuario usuario = new Usuario(nombre.trim(), hash, rol);
        usuarios.add(usuario);
        indiceUsuarios.put(nombreNormalizado, usuario);
        marcarDirty();
        return true;
    }

    public Usuario autenticarUsuario(String nombre, String contrasena) {
        if (nombre == null || contrasena == null) {
            return null;
        }
        Usuario u = indiceUsuarios.get(nombre.toLowerCase());
        if (u != null && SeguridadUtil.verificarPassword(contrasena, u.getContrasena())) {
            return u;
        }
        return null;
    }

    public ResultadoOperacion actualizarRol(String nombreUsuario, RolUsuario nuevoRol, Usuario ejecutor) {
        if (nombreUsuario == null || nuevoRol == null) {
            return new ResultadoOperacion(false, "Datos inválidos.");
        }

        Usuario objetivo = indiceUsuarios.get(nombreUsuario.toLowerCase());
        if (objetivo == null) {
            return new ResultadoOperacion(false, "El usuario indicado no existe.");
        }

        objetivo.setRol(nuevoRol);
        marcarDirty();
        return new ResultadoOperacion(true, "Rol actualizado correctamente.");
    }

    public boolean eliminarUsuario(String nombreUsuario) {
        Usuario u = indiceUsuarios.remove(nombreUsuario.toLowerCase());
        if (u == null) return false;
        usuarios.remove(u);
        marcarDirty();
        return true;
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios); // copia defensiva
    }

    public List<Usuario> getUsuariosSinSuperusuario() {
        return usuarios.stream()
                .filter(u -> !u.esSuperusuario())
                .collect(Collectors.toList());
    }
}