package modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    @DisplayName("Debería devolver true cuando el rol es superusuario")
    void testEsSuperusuarioConRolSuperusuario() {
        Usuario usuario = new Usuario("Admin", "superusuario", "pass");
        assertTrue(usuario.esSuperusuario());
    }

    @Test
    @DisplayName("Debería devolver false cuando el rol no es superusuario")
    void testEsSuperusuarioConOtroRol() {
        Usuario usuario = new Usuario("Juan", "lider", "pass");
        assertFalse(usuario.esSuperusuario());

        usuario = new Usuario("Pedro", "usuario", "pass");
        assertFalse(usuario.esSuperusuario());
    }

    @Test
    @DisplayName("Debería devolver true cuando el rol es lider")
    void testEsLiderConRolLider() {
        Usuario usuario = new Usuario("Lider1", "lider", "pass");
        assertTrue(usuario.esLider());
    }

    @Test
    @DisplayName("Debería devolver false cuando el rol no es lider")
    void testEsLiderConOtroRol() {
        Usuario usuario = new Usuario("Admin", "superusuario", "pass");
        assertFalse(usuario.esLider());

        usuario = new Usuario("Juan", "usuario", "pass");
        assertFalse(usuario.esLider());
    }

    @Test
    @DisplayName("Debería devolver true cuando el rol es usuario")
    void testEsUsuarioConRolUsuario() {
        Usuario usuario = new Usuario("Normal", "usuario", "pass");
        assertTrue(usuario.esUsuario());
    }

    @Test
    @DisplayName("Debería devolver false cuando el rol no es usuario")
    void testEsUsuarioConOtroRol() {
        Usuario usuario = new Usuario("Admin", "superusuario", "pass");
        assertFalse(usuario.esUsuario());

        usuario = new Usuario("Lider1", "lider", "pass");
        assertFalse(usuario.esUsuario());
    }

    @Test
    @DisplayName("Debería lanzar NullPointerException cuando rol es null")
    void testEsSuperusuarioConRolNull() {
        Usuario usuario = new Usuario("SinRol", null, "pass");
        assertThrows(NullPointerException.class, () -> usuario.esSuperusuario());
    }

    @Test
    @DisplayName("Debería poder cambiar el rol después de crear el usuario")
    void testCambiarRol() {
        Usuario usuario = new Usuario("Juan", "usuario", "pass");
        assertTrue(usuario.esUsuario());

        usuario.setRol("lider");
        assertTrue(usuario.esLider());
        assertFalse(usuario.esUsuario());
    }
}
