package sigma.app;

import sigma.modelo.RolUsuario;
import sigma.modelo.Usuario;
import sigma.persistencia.GestorJSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServicioUsuarios {

    private final GestorJSON gestorJSON;
    private final String rutaArchivo;
    private ArrayList<Usuario> usuarios = new ArrayList<>();

    public ServicioUsuarios(GestorJSON gestorJSON, String rutaArchivo) {
        this.gestorJSON = gestorJSON;
        this.rutaArchivo = rutaArchivo;
    }

    public void cargar() {
        this.usuarios = gestorJSON.cargarUsuarios(rutaArchivo);
    }

    public boolean guardar() {
        return gestorJSON.guardarUsuarios(usuarios, rutaArchivo);
    }

    public void resetear() {
        this.usuarios = new ArrayList<>();
    }

    public boolean registrarUsuario(String nombre, String contrasena, RolUsuario rol) {
        if (nombre == null || nombre.isBlank() || contrasena == null || contrasena.isBlank()) {
            return false;
        }

        String nombreNormalizado = nombre.trim();

        boolean existe = usuarios.stream()
                .anyMatch(u -> u.getNombre().equalsIgnoreCase(nombreNormalizado));
        if (existe) {
            return false;
        }

        usuarios.add(new Usuario(nombreNormalizado, contrasena, rol));
        return true;
    }

    public Usuario autenticarUsuario(String nombre, String contrasena) {
        if (nombre == null || contrasena == null) {
            return null;
        }
        return usuarios.stream()
                .filter(u -> u.getNombre().equalsIgnoreCase(nombre) && u.getContrasena().equals(contrasena))
                .findFirst()
                .orElse(null);
    }

    public ResultadoOperacion actualizarRol(String nombreUsuario, RolUsuario nuevoRol, Usuario ejecutor) {
        Optional<Usuario> objetivo = usuarios.stream()
                .filter(u -> u.getNombre().equalsIgnoreCase(nombreUsuario))
                .findFirst();

        if (objetivo.isEmpty()) {
            return new ResultadoOperacion(false, "El usuario indicado no existe.");
        }

        objetivo.get().setRol(nuevoRol);
        return new ResultadoOperacion(true, "Rol actualizado correctamente.");
    }

    public boolean eliminarUsuario(String nombreUsuario) {
        return usuarios.removeIf(u -> u.getNombre().equalsIgnoreCase(nombreUsuario));
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