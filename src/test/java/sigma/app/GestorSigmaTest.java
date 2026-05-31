package sigma.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sigma.modelo.Usuario;
import sigma.modelo.Meta;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GestorSigmaTest {

    private GestorSigma gestor;

    @BeforeEach
    void setUp() {
        gestor = new GestorSigma();
    }

    // ========== TESTS DE REGISTRO ==========

    @Test
    @DisplayName("Debería registrar un usuario exitosamente")
    void testRegistrarUsuarioExitoso() {
        boolean resultado = gestor.registrarUsuario("juan", "pass123", "usuario");

        assertTrue(resultado);
        assertEquals(1, gestor.getUsuarios().size());
        assertEquals("juan", gestor.getUsuarios().get(0).getNombre());
    }

    @Test
    @DisplayName("Debería fallar al registrar usuario duplicado")
    void testRegistrarUsuarioDuplicadoFalla() {
        gestor.registrarUsuario("juan", "pass123", "usuario");
        boolean resultado = gestor.registrarUsuario("juan", "pass456", "lider");

        assertFalse(resultado);
        assertEquals(1, gestor.getUsuarios().size());
    }

    @Test
    @DisplayName("Debería fallar al registrar con rol inválido")
    void testRegistrarUsuarioRolInvalido() {
        boolean resultado = gestor.registrarUsuario("juan", "pass123", "rol_invalido");

        assertFalse(resultado);
        assertEquals(0, gestor.getUsuarios().size());
    }

    @Test
    @DisplayName("Debería registrar superusuario, lider y usuario correctamente")
    void testRegistrarUsuariosConRolesValidos() {
        assertTrue(gestor.registrarUsuario("admin", "pass", "superusuario"));
        assertTrue(gestor.registrarUsuario("lider1", "pass", "lider"));
        assertTrue(gestor.registrarUsuario("user1", "pass", "usuario"));

        assertEquals(3, gestor.getUsuarios().size());
    }

    @Test
    @DisplayName("Debería fallar al registrar con nombre vacío")
    void testRegistrarUsuarioNombreVacio() {
        boolean resultado = gestor.registrarUsuario("", "pass123", "usuario");

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debería fallar al registrar con nombre null")
    void testRegistrarUsuarioNombreNull() {
        boolean resultado = gestor.registrarUsuario(null, "pass123", "usuario");

        assertFalse(resultado);
    }

    // ========== TESTS DE AUTENTICACIÓN ==========

    @Test
    @DisplayName("Debería autenticar usuario con credenciales correctas")
    void testAutenticarUsuarioExitoso() {
        gestor.registrarUsuario("juan", "pass123", "usuario");
        Usuario usuario = gestor.autenticarUsuario("juan", "pass123");

        assertNotNull(usuario);
        assertEquals("juan", usuario.getNombre());
        assertEquals("usuario", usuario.getRol());
    }

    @Test
    @DisplayName("Debería fallar autenticación con contraseña incorrecta")
    void testAutenticarUsuarioContrasenaIncorrecta() {
        gestor.registrarUsuario("juan", "pass123", "usuario");
        Usuario usuario = gestor.autenticarUsuario("juan", "contrasena_incorrecta");

        assertNull(usuario);
    }

    @Test
    @DisplayName("Debería fallar autenticación con usuario inexistente")
    void testAutenticarUsuarioInexistente() {
        Usuario usuario = gestor.autenticarUsuario("usuario_que_no_existe", "pass123");

        assertNull(usuario);
    }

    // ========== TESTS DE ACTUALIZACIÓN DE ROL ==========

    @Test
    @DisplayName("Debería actualizar el rol de un usuario existente")
    void testActualizarRolExitoso() {
        gestor.registrarUsuario("juan", "pass123", "usuario");
        boolean resultado = gestor.actualizarRol("juan", "lider");

        assertTrue(resultado);
        assertEquals("lider", gestor.getUsuarios().get(0).getRol());
    }

    @Test
    @DisplayName("Debería fallar al actualizar rol de usuario inexistente")
    void testActualizarRolUsuarioInexistente() {
        boolean resultado = gestor.actualizarRol("usuario_que_no_existe", "lider");

        assertFalse(resultado);
    }

    // ========== TESTS DE ELIMINACIÓN ==========

    @Test
    @DisplayName("Debería eliminar un usuario existente")
    void testEliminarUsuarioExistente() {
        gestor.registrarUsuario("juan", "pass123", "usuario");
        boolean resultado = gestor.eliminarUsuario("juan");

        assertTrue(resultado);
        assertEquals(0, gestor.getUsuarios().size());
    }

    @Test
    @DisplayName("Debería fallar al eliminar usuario inexistente")
    void testEliminarUsuarioInexistente() {
        boolean resultado = gestor.eliminarUsuario("usuario_que_no_existe");

        assertFalse(resultado);
    }

    // ========== TESTS DE RESETEO ==========

    @Test
    @DisplayName("Debería resetear el sistema y crear usuario admin")
    void testResetearSistema() {
        gestor.registrarUsuario("juan", "pass123", "usuario");
        gestor.registrarUsuario("maria", "pass456", "lider");

        assertEquals(2, gestor.getUsuarios().size());

        gestor.resetearSistema();

        assertEquals(1, gestor.getUsuarios().size());
        assertEquals("admin", gestor.getUsuarios().get(0).getNombre());
        assertEquals("superusuario", gestor.getUsuarios().get(0).getRol());
        assertTrue(gestor.getMetas().isEmpty());
    }

    // ========== TESTS DE COPIA DEFENSIVA ==========

    @Test
    @DisplayName("getUsuarios debe retornar una copia, no la lista original")
    void testGetUsuariosEsCopiaDefensiva() {
        gestor.registrarUsuario("juan", "pass123", "usuario");

        List<Usuario> listaExterna = gestor.getUsuarios();
        listaExterna.clear();

        // La lista interna no debe modificarse
        assertEquals(1, gestor.getUsuarios().size());
    }

    @Test
    @DisplayName("getMetas debe retornar una copia, no la lista original")
    void testGetMetasEsCopiaDefensiva() {
        List<Meta> listaExterna = gestor.getMetas();
        listaExterna.add(new Meta("Meta falsa"));

        // La lista interna no debe modificarse
        assertEquals(0, gestor.getMetas().size());
    }

    // ========== TESTS DE CASOS BORDE ==========

    @Test
    @DisplayName("Registrar usuario con nombre en mayúsculas debe normalizarse?")
    void testRegistrarUsuarioNombreMayusculas() {
        boolean resultado = gestor.registrarUsuario("JUAN", "pass123", "usuario");

        assertTrue(resultado);
        assertEquals("juan", gestor.getUsuarios().get(0).getNombre());
    }

    @Test
    @DisplayName("Actualizar rol con mayúsculas debe funcionar")
    void testActualizarRolConMayusculas() {
        gestor.registrarUsuario("juan", "pass123", "usuario");
        boolean resultado = gestor.actualizarRol("juan", "SUPERUSUARIO");

        assertTrue(resultado);
        assertEquals("superusuario", gestor.getUsuarios().get(0).getRol());
    }

    @Test
    @DisplayName("Eliminar usuario con nombre en mayúsculas debe funcionar")
    void testEliminarUsuarioNombreMayusculas() {
        gestor.registrarUsuario("juan", "pass123", "usuario");
        boolean resultado = gestor.eliminarUsuario("JUAN");

        assertTrue(resultado);
        assertEquals(0, gestor.getUsuarios().size());
    }
}