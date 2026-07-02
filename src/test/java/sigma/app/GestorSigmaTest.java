package sigma.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sigma.modelo.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GestorSigmaTest {

    private GestorSigma gestor;

    @BeforeEach
    void setUp() {
        gestor = new GestorSigma();
        gestor.resetearSistema();
    }

    @Test
    @DisplayName("Debería registrar un usuario exitosamente")
    void testRegistrarUsuarioExitoso() {
        boolean resultado = gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);

        assertTrue(resultado);
        assertEquals(1, gestor.getUsuarios().size());
        assertEquals("juan", gestor.getUsuarios().get(0).getNombre());
    }

    @Test
    @DisplayName("Debería fallar al registrar usuario duplicado")
    void testRegistrarUsuarioDuplicadoFalla() {
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);
        boolean resultado = gestor.registrarUsuario("juan", "pass456", RolUsuario.LIDER);

        assertFalse(resultado);
        assertEquals(1, gestor.getUsuarios().size());
    }

    @Test
    @DisplayName("Debería registrar superusuario, lider y usuario correctamente")
    void testRegistrarUsuariosConRolesValidos() {
        assertTrue(gestor.registrarUsuario("admin", "pass", RolUsuario.SUPERUSUARIO));
        assertTrue(gestor.registrarUsuario("lider1", "pass", RolUsuario.LIDER));
        assertTrue(gestor.registrarUsuario("user1", "pass", RolUsuario.USUARIO));

        assertEquals(3, gestor.getUsuarios().size());
    }

    @Test
    @DisplayName("Debería fallar al registrar con nombre vacío")
    void testRegistrarUsuarioNombreVacio() {
        boolean resultado = gestor.registrarUsuario("", "pass", RolUsuario.USUARIO);
        assertFalse(resultado, "Nombre vacio no debe permitirse");
    }

    @Test
    @DisplayName("Debería fallar al registrar con nombre null")
    void testRegistrarUsuarioNombreNull() {
        boolean resultado = gestor.registrarUsuario(null, "pass", RolUsuario.USUARIO);
        assertFalse(resultado, "Nombre null no debe permitirse");
    }

    @Test
    @DisplayName("Debería autenticar usuario con credenciales correctas")
    void testAutenticarUsuarioExitoso() {
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);
        Usuario usuario = gestor.autenticarUsuario("juan", "pass123");

        assertNotNull(usuario);
        assertEquals("juan", usuario.getNombre());
        assertEquals(RolUsuario.USUARIO, usuario.getRol());
    }

    @Test
    @DisplayName("Debería fallar autenticación con contraseña incorrecta")
    void testAutenticarUsuarioContrasenaIncorrecta() {
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);
        Usuario usuario = gestor.autenticarUsuario("juan", "contrasena_incorrecta");

        assertNull(usuario);
    }

    @Test
    @DisplayName("Debería fallar autenticación con usuario inexistente")
    void testAutenticarUsuarioInexistente() {
        Usuario usuario = gestor.autenticarUsuario("usuario_que_no_existe", "pass123");

        assertNull(usuario);
    }

    @Test
    @DisplayName("Debería actualizar el rol de un usuario existente")
    void testActualizarRolExitoso() {
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);
        boolean resultado = gestor.actualizarRol("juan", RolUsuario.LIDER);

        assertTrue(resultado);
        assertEquals(RolUsuario.LIDER, gestor.getUsuarios().get(0).getRol());
    }

    @Test
    @DisplayName("Debería fallar al actualizar rol de usuario inexistente")
    void testActualizarRolUsuarioInexistente() {
        boolean resultado = gestor.actualizarRol("usuario_que_no_existe", RolUsuario.LIDER);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debería eliminar un usuario existente")
    void testEliminarUsuarioExistente() {
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);
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

    @Test
    @DisplayName("Debería resetear el sistema")
    void testResetearSistema() {
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);
        gestor.registrarUsuario("maria", "pass456", RolUsuario.LIDER);
        gestor.agregarMeta("Proyecto SIGMA");
        gestor.resetearSistema();
        List<Usuario> usuarios = gestor.getUsuarios();
        List<Meta> metas = gestor.getMetas();
        assertEquals(1, usuarios.size(), "Debe quedar solo el admin");
        assertEquals("admin", usuarios.get(0).getNombre());
        assertEquals(RolUsuario.SUPERUSUARIO, usuarios.get(0).getRol());
        assertEquals(0, metas.size(), "Metas deben quedar vacias");
    }


    @Test
    @DisplayName("Debería agregar una meta exitosamente")
    void testAgregarMetaExitosa() {
        boolean resultado = gestor.agregarMeta("Proyecto SIGMA");

        assertTrue(resultado);
        assertEquals(1, gestor.getMetas().size());
        assertEquals("Proyecto SIGMA", gestor.getMetas().get(0).getNombre());
    }

    @Test
    @DisplayName("Debería fallar al agregar una meta duplicada")
    void testAgregarMetaDuplicadaFalla() {
        gestor.agregarMeta("Proyecto SIGMA");
        boolean resultado = gestor.agregarMeta("Proyecto SIGMA");

        assertFalse(resultado);
        assertEquals(1, gestor.getMetas().size());
    }

    @Test
    @DisplayName("Debería eliminar una meta existente")
    void testEliminarMetaExistente() {
        gestor.agregarMeta("Proyecto SIGMA");
        boolean resultado = gestor.eliminarMeta("Proyecto SIGMA");

        assertTrue(resultado);
        assertEquals(0, gestor.getMetas().size());
    }

    @Test
    @DisplayName("Debería fallar al eliminar una meta inexistente")
    void testEliminarMetaInexistente() {
        boolean resultado = gestor.eliminarMeta("Meta que no existe");

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debería buscar una meta por nombre")
    void testBuscarMeta() {
        gestor.agregarMeta("Proyecto SIGMA");
        Meta meta = gestor.buscarMeta("Proyecto SIGMA");

        assertNotNull(meta);
        assertEquals("Proyecto SIGMA", meta.getNombre());
    }

    @Test
    @DisplayName("Debería retornar null al buscar meta inexistente")
    void testBuscarMetaInexistente() {
        Meta meta = gestor.buscarMeta("Meta que no existe");

        assertNull(meta);
    }

    @Test
    @DisplayName("Debería agregar una tarea a una meta existente")
    void testAgregarTareaAMeta() {
        gestor.agregarMeta("Proyecto SIGMA");
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);

        Usuario usuario = gestor.autenticarUsuario("juan", "pass123");
        Tarea tarea = new Tarea("Implementar tests", "Crear pruebas JUnit",
                usuario, LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31), EstadoTarea.PENDIENTE);

        boolean resultado = gestor.agregarTareaAMeta("Proyecto SIGMA", tarea);

        assertTrue(resultado);
        assertEquals(1, gestor.getMetas().get(0).getTareas().size());
        assertEquals("Implementar tests", gestor.getMetas().get(0).getTareas().get(0).getTitulo());
    }

    @Test
    @DisplayName("Debería fallar al agregar tarea a meta inexistente")
    void testAgregarTareaAMetaInexistente() {
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);
        Usuario usuario = gestor.autenticarUsuario("juan", "pass123");

        Tarea tarea = new Tarea("Implementar tests", "Descripcion",
                usuario, LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31), EstadoTarea.PENDIENTE);

        boolean resultado = gestor.agregarTareaAMeta("Meta inexistente", tarea);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debería cambiar el estado de una tarea")
    void testCambiarEstadoTarea() {
        gestor.agregarMeta("Proyecto SIGMA");
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);

        Usuario usuario = gestor.autenticarUsuario("juan", "pass123");
        Tarea tarea = new Tarea("Implementar tests", "Descripcion",
                usuario, LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31), EstadoTarea.PENDIENTE);
        gestor.agregarTareaAMeta("Proyecto SIGMA", tarea);

        boolean resultado = gestor.cambiarEstadoTarea("Implementar tests", EstadoTarea.COMPLETADA);

        assertTrue(resultado);
        assertEquals(EstadoTarea.COMPLETADA, gestor.getMetas().get(0).getTareas().get(0).getEstado());
    }

    @Test
    @DisplayName("Debería fallar al cambiar estado de tarea inexistente")
    void testCambiarEstadoTareaInexistente() {
        boolean resultado = gestor.cambiarEstadoTarea("Tarea que no existe", EstadoTarea.COMPLETADA);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debería obtener las tareas de un usuario")
    void testGetTareasDeUsuario() {
        gestor.agregarMeta("Proyecto SIGMA");
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);

        Usuario usuario = gestor.autenticarUsuario("juan", "pass123");
        Tarea tarea1 = new Tarea("Tarea 1", "Desc1", usuario,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), EstadoTarea.PENDIENTE);
        Tarea tarea2 = new Tarea("Tarea 2", "Desc2", usuario,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30), EstadoTarea.PENDIENTE);

        gestor.agregarTareaAMeta("Proyecto SIGMA", tarea1);
        gestor.agregarTareaAMeta("Proyecto SIGMA", tarea2);

        List<Tarea> tareas = gestor.getTareasDeUsuario(usuario);

        assertEquals(2, tareas.size());
        assertEquals("Tarea 1", tareas.get(0).getTitulo());
        assertEquals("Tarea 2", tareas.get(1).getTitulo());
    }

    @Test
    @DisplayName("Debería calcular el progreso de una meta después de completar tareas")
    void testProgresoMetaDespuesDeCompletarTareas() {
        gestor.agregarMeta("Proyecto SIGMA");
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);

        Usuario usuario = gestor.autenticarUsuario("juan", "pass123");
        Tarea tarea1 = new Tarea("Tarea 1", "Desc1", usuario,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), EstadoTarea.PENDIENTE);
        Tarea tarea2 = new Tarea("Tarea 2", "Desc2", usuario,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), EstadoTarea.PENDIENTE);

        gestor.agregarTareaAMeta("Proyecto SIGMA", tarea1);
        gestor.agregarTareaAMeta("Proyecto SIGMA", tarea2);

        assertEquals(0, gestor.getMetas().get(0).calcularProgreso());

        gestor.cambiarEstadoTarea("Tarea 1", EstadoTarea.COMPLETADA);

        assertEquals(50, gestor.getMetas().get(0).calcularProgreso());
    }


    @Test
    @DisplayName("getUsuarios debe retornar una copia, no la lista original")
    void testGetUsuariosEsCopiaDefensiva() {
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);

        List<Usuario> listaExterna = gestor.getUsuarios();
        listaExterna.clear();

        assertEquals(1, gestor.getUsuarios().size());
    }

    @Test
    @DisplayName("getMetas debe retornar una copia, no la lista original")
    void testGetMetasEsCopiaDefensiva() {
        List<Meta> listaExterna = gestor.getMetas();
        listaExterna.add(new Meta("Meta falsa"));

        assertEquals(0, gestor.getMetas().size());
    }
}