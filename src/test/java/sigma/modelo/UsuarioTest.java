package sigma.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    @DisplayName("Debería devolver true cuando el rol es SUPERUSUARIO")
    void testEsSuperusuarioConRolSuperusuario() {
        Usuario usuario = new Usuario("Admin", "pass", RolUsuario.SUPERUSUARIO);
        assertTrue(usuario.esSuperusuario());
    }

    @Test
    @DisplayName("Debería devolver false cuando el rol no es SUPERUSUARIO")
    void testEsSuperusuarioConOtroRol() {
        Usuario usuario = new Usuario("Juan", "pass", RolUsuario.LIDER);
        assertFalse(usuario.esSuperusuario());

        usuario = new Usuario("Pedro", "pass", RolUsuario.USUARIO);
        assertFalse(usuario.esSuperusuario());
    }

    @Test
    @DisplayName("Debería devolver true cuando el rol es LIDER")
    void testEsLiderConRolLider() {
        Usuario usuario = new Usuario("Lider1", "pass", RolUsuario.LIDER);
        assertTrue(usuario.esLider());
    }

    @Test
    @DisplayName("Debería devolver false cuando el rol no es LIDER")
    void testEsLiderConOtroRol() {
        Usuario usuario = new Usuario("Admin", "pass", RolUsuario.SUPERUSUARIO);
        assertFalse(usuario.esLider());

        usuario = new Usuario("Juan", "pass", RolUsuario.USUARIO);
        assertFalse(usuario.esLider());
    }

    @Test
    @DisplayName("Debería devolver true cuando el rol es USUARIO")
    void testEsUsuarioConRolUsuario() {
        Usuario usuario = new Usuario("Normal", "pass", RolUsuario.USUARIO);
        assertTrue(usuario.esUsuario());
    }

    @Test
    @DisplayName("Debería devolver false cuando el rol no es USUARIO")
    void testEsUsuarioConOtroRol() {
        Usuario usuario = new Usuario("Admin", "pass", RolUsuario.SUPERUSUARIO);
        assertFalse(usuario.esUsuario());

        usuario = new Usuario("Lider1", "pass", RolUsuario.LIDER);
        assertFalse(usuario.esUsuario());
    }

    @Test
    @DisplayName("Debería poder cambiar el rol después de crear el usuario")
    void testCambiarRol() {
        Usuario usuario = new Usuario("Juan", "pass", RolUsuario.USUARIO);
        assertTrue(usuario.esUsuario());

        usuario.setRol(RolUsuario.LIDER);
        assertTrue(usuario.esLider());
        assertFalse(usuario.esUsuario());
    }

    @Test
    @DisplayName("Debería cambiar de rol correctamente usando el enum")
    void testCambiarRolConEnum() {
        Usuario usuario = new Usuario("Juan", "pass", RolUsuario.USUARIO);

        usuario.setRol(RolUsuario.SUPERUSUARIO);
        assertTrue(usuario.esSuperusuario());

        usuario.setRol(RolUsuario.LIDER);
        assertTrue(usuario.esLider());
    }
}