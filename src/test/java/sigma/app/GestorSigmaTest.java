package sigma.app;

import org.junit.jupiter.api.*;
import sigma.modelo.*;

import static org.junit.jupiter.api.Assertions.*;

class GestorSigmaTest {

    private GestorSigma gestor;

    @BeforeEach
    void setUp() {
        gestor = new GestorSigma();
    }

    @Test
    void testRegistrarUsuarioExitoso() {
    }

    @Test
    void testRegistrarUsuarioDuplicadoFalla() {
    }

    @Test
    void testRegistrarUsuarioNombreVacio() {
    }

    @Test
    void testAutenticarUsuarioExitoso() {
    }

    @Test
    void testAutenticarUsuarioFalla() {
    }

    @Test
    void testEliminarUsuarioExistente() {
    }

    @Test
    void testEliminarUsuarioInexistente() {
    }

    @Test
    void testActualizarRolExitoso() {
    }

    @Test
    void testResetearSistema() {
    }

    // Tests de metas
    @Test
    void testAgregarMetaExitosa() {
    }

    @Test
    void testAgregarMetaDuplicadaFalla() {
    }

    @Test
    void testAgregarTareaAMeta() {
    }

    @Test
    void testCambiarEstadoTarea() {
    }

    @Test
    void testCambiarEstadoTareaInexistente() {
    }

    @Test
    void testGetTareasDeUsuario() {
    }

    @Test
    void testProgresoMetaDespuesDeCompletarTareas() {
    }

    @Test
    void testGetUsuariosEsCopiaDefensiva() {
    }

    @Test
    void testGetMetasEsCopiaDefensiva() {
    }
}

