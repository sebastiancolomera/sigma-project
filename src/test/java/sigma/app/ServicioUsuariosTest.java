package sigma.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sigma.modelo.RolUsuario;
import sigma.modelo.Usuario;
import sigma.persistencia.GestorJSON;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicioUsuariosTest {

    @TempDir
    Path tempDir;

    private ServicioUsuarios servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioUsuarios(new GestorJSON(), tempDir.resolve("usuarios.json").toString());
    }

    @Test
    @DisplayName("Registrar un usuario válido tiene éxito")
    void testRegistrarUsuarioExitoso() {
        assertTrue(servicio.registrarUsuario("pedro", "clave123", RolUsuario.USUARIO));
        assertEquals(1, servicio.getUsuarios().size());
    }

    @Test
    @DisplayName("Registrar un usuario con nombre duplicado falla")
    void testRegistrarUsuarioDuplicadoFalla() {
        servicio.registrarUsuario("pedro", "clave123", RolUsuario.USUARIO);
        assertFalse(servicio.registrarUsuario("pedro", "otraClave", RolUsuario.LIDER));
        assertEquals(1, servicio.getUsuarios().size());
    }

    @Test
    @DisplayName("Se pueden registrar usuarios con cualquier rol válido")
    void testRegistrarUsuariosConRolesValidos() {
        assertTrue(servicio.registrarUsuario("admin2", "clave123", RolUsuario.SUPERUSUARIO));
        assertTrue(servicio.registrarUsuario("lider1", "clave123", RolUsuario.LIDER));
        assertTrue(servicio.registrarUsuario("user1", "clave123", RolUsuario.USUARIO));
        assertEquals(3, servicio.getUsuarios().size());
    }

    @Test
    @DisplayName("Registrar un usuario con nombre vacío falla")
    void testRegistrarUsuarioNombreVacio() {
        assertFalse(servicio.registrarUsuario("   ", "clave123", RolUsuario.USUARIO));
    }

    @Test
    @DisplayName("Registrar un usuario con nombre null falla")
    void testRegistrarUsuarioNombreNull() {
        assertFalse(servicio.registrarUsuario(null, "clave123", RolUsuario.USUARIO));
    }

    @Test
    @DisplayName("Autenticar un usuario existente con credenciales correctas tiene éxito")
    void testAutenticarUsuarioExitoso() {
        servicio.registrarUsuario("pedro", "clave123", RolUsuario.USUARIO);
        Usuario u = servicio.autenticarUsuario("pedro", "clave123");
        assertNotNull(u);
        assertEquals("pedro", u.getNombre());
    }

    @Test
    @DisplayName("Autenticar con contraseña incorrecta falla")
    void testAutenticarUsuarioContrasenaIncorrecta() {
        servicio.registrarUsuario("pedro", "clave123", RolUsuario.USUARIO);
        assertNull(servicio.autenticarUsuario("pedro", "claveIncorrecta"));
    }

    @Test
    @DisplayName("Autenticar un usuario inexistente falla")
    void testAutenticarUsuarioInexistente() {
        assertNull(servicio.autenticarUsuario("fantasma", "clave123"));
    }

    @Test
    @DisplayName("Actualizar el rol de un usuario existente tiene éxito (equalsIgnoreCase)")
    void testActualizarRolExitoso() {
        servicio.registrarUsuario("pedro", "clave123", RolUsuario.USUARIO);
        assertTrue(servicio.actualizarRol("PEDRO", RolUsuario.LIDER));
        Usuario u = servicio.autenticarUsuario("pedro", "clave123");
        assertEquals(RolUsuario.LIDER, u.getRol());
    }

    @Test
    @DisplayName("Actualizar el rol de un usuario inexistente falla")
    void testActualizarRolUsuarioInexistente() {
        assertFalse(servicio.actualizarRol("fantasma", RolUsuario.LIDER));
    }

    @Test
    @DisplayName("Eliminar un usuario existente tiene éxito")
    void testEliminarUsuarioExistente() {
        servicio.registrarUsuario("pedro", "clave123", RolUsuario.USUARIO);
        assertTrue(servicio.eliminarUsuario("pedro"));
        assertTrue(servicio.getUsuarios().isEmpty());
    }

    @Test
    @DisplayName("Eliminar un usuario inexistente falla")
    void testEliminarUsuarioInexistente() {
        assertFalse(servicio.eliminarUsuario("fantasma"));
    }

    @Test
    @DisplayName("getUsuarios() devuelve una copia defensiva")
    void testGetUsuariosEsCopiaDefensiva() {
        servicio.registrarUsuario("pedro", "clave123", RolUsuario.USUARIO);
        List<Usuario> copia = servicio.getUsuarios();
        copia.clear();
        assertEquals(1, servicio.getUsuarios().size());
    }

    @Test
    @DisplayName("getUsuariosSinSuperusuario filtra correctamente al admin")
    void testGetUsuariosSinSuperusuarioFiltraAdmin() {
        servicio.registrarUsuario("admin", "gatomiau", RolUsuario.SUPERUSUARIO);
        servicio.registrarUsuario("pedro", "clave123", RolUsuario.USUARIO);
        servicio.registrarUsuario("ana", "clave123", RolUsuario.LIDER);

        List<Usuario> sinSuper = servicio.getUsuariosSinSuperusuario();

        assertEquals(2, sinSuper.size());
        assertTrue(sinSuper.stream().noneMatch(Usuario::esSuperusuario));
    }
}