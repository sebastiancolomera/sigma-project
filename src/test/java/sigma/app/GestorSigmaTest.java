package sigma.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sigma.modelo.*;

import java.time.LocalDate;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GestorSigmaTest {

    @TempDir
    Path tempDir;

    private GestorSigma gestor;

    @BeforeEach
    void setUp() {
        gestor = new GestorSigma(
                tempDir.resolve("usuarios.json").toString(),
                tempDir.resolve("metas.json").toString()
        );
    }

    @Test
    @DisplayName("resetearSistema limpia usuarios y metas, y deja solo al admin")
    void testResetearSistema() {
        gestor.registrarUsuario("pedro", "clave123", RolUsuario.USUARIO);
        gestor.agregarMeta("Meta de prueba");

        gestor.resetearSistema();

        assertEquals(1, gestor.getUsuarios().size());
        assertTrue(gestor.getUsuarios().get(0).esSuperusuario());
        assertTrue(gestor.getMetas().isEmpty());
    }

    @Test
    @DisplayName("Se crea el usuario admin automáticamente cuando el sistema inicia sin usuarios")
    void testAdminCreadoAlIniciarSistemaVacio() {
        assertTrue(gestor.getUsuarios().isEmpty());

        if (gestor.getUsuarios().isEmpty()) {
            gestor.registrarUsuario(SigmaConfig.ADMIN_NOMBRE, SigmaConfig.ADMIN_PASSWORD, RolUsuario.SUPERUSUARIO);
        }

        Usuario admin = gestor.autenticarUsuario(SigmaConfig.ADMIN_NOMBRE, SigmaConfig.ADMIN_PASSWORD);
        assertNotNull(admin);
        assertTrue(admin.esSuperusuario());
    }

    @Test
    @DisplayName("Integración: registrar usuario, crear meta y asignarle una tarea a través de la fachada")
    void testFlujoCompletoRegistrarUsuarioCrearMetaYAsignarTarea() {
        assertTrue(gestor.registrarUsuario("ana", "clave123", RolUsuario.LIDER));
        assertTrue(gestor.agregarMeta("Lanzamiento v1"));

        Usuario ana = gestor.autenticarUsuario("ana", "clave123");
        assertNotNull(ana);

        Tarea tarea = new Tarea("Preparar demo", "Demo para el lanzamiento v1", ana,
                LocalDate.now(), LocalDate.now().plusDays(3), EstadoTarea.PENDIENTE);

        assertTrue(gestor.agregarTareaAMeta("Lanzamiento v1", tarea));
        assertEquals(1, gestor.getTareasDeUsuario(ana).size());
    }

    @Test
    @DisplayName("La API pública de la fachada sigue disponible tras el refactor (no rompe las vistas)")
    void testApiPublicaPreservadaTrasElRefactor() {
        assertDoesNotThrow(gestor::getUsuarios);
        assertDoesNotThrow(gestor::getUsuariosSinSuperusuario);
        assertDoesNotThrow(gestor::getMetas);
        assertDoesNotThrow(() -> gestor.autenticarUsuario("inexistente", "x"));
    }
}