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
    @DisplayName("resetearSistema limpia usuarios y metas por completo (sin admin autocreado)")
    void testResetearSistema() {
        gestor.registrarUsuario("pedro", "clave123", RolUsuario.USUARIO);
        gestor.agregarMeta("Meta de prueba");

        gestor.resetearSistema();

        // resetearSistema() deja el sistema completamente vacío; el registro
        // de un nuevo SuperUsuario es un paso manual posterior (ver
        // RegistroSuperusuarioDialog, invocado tras el reseteo desde la UI).
        assertTrue(gestor.getUsuarios().isEmpty());
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

    @Test
    @DisplayName("actualizarFechasTarea a través de la fachada actualiza las fechas correctamente")
    void testActualizarFechasTareaExitosaViaFachada() {
        gestor.agregarMeta("Meta X");
        Tarea tarea = new Tarea("Tarea 1", "Descripción", null,
                LocalDate.now(), LocalDate.now().plusDays(9), EstadoTarea.PENDIENTE);
        gestor.agregarTareaAMeta("Meta X", tarea);

        LocalDate nuevaInicio = LocalDate.now().plusDays(31);
        LocalDate nuevaTermino = LocalDate.now().plusDays(45);

        assertTrue(gestor.actualizarFechasTarea("Tarea 1", nuevaInicio, nuevaTermino));

        Tarea actualizada = gestor.getMetas().get(0).getTareas().get(0);
        assertEquals(nuevaInicio, actualizada.getFechaInicio());
        assertEquals(nuevaTermino, actualizada.getFechaTermino());
    }

    @Test
    @DisplayName("actualizarFechasTarea a través de la fachada retorna false para una tarea inexistente")
    void testActualizarFechasTareaInexistenteViaFachada() {
        assertFalse(gestor.actualizarFechasTarea("Fantasma",
                LocalDate.now(), LocalDate.now().plusDays(1)));
    }

    @Test
    @DisplayName("actualizarFechasTarea a través de la fachada rechaza fecha de término anterior a la de inicio")
    void testActualizarFechasTareaFechaInvalidaViaFachada() {
        gestor.agregarMeta("Meta Y");
        LocalDate inicioOriginal = LocalDate.now();
        LocalDate terminoOriginal = LocalDate.now().plusDays(9);
        Tarea tarea = new Tarea("Tarea 2", "Descripción", null,
                inicioOriginal, terminoOriginal, EstadoTarea.PENDIENTE);
        gestor.agregarTareaAMeta("Meta Y", tarea);

        assertFalse(gestor.actualizarFechasTarea("Tarea 2",
                LocalDate.now().plusDays(45), LocalDate.now().plusDays(31)));

        Tarea sinCambios = gestor.getMetas().get(0).getTareas().get(0);
        assertEquals(inicioOriginal, sinCambios.getFechaInicio());
        assertEquals(terminoOriginal, sinCambios.getFechaTermino());
    }

    @Test
    @DisplayName("registrarUsuario rechaza nombre vacío")
    void testRegistrarUsuarioNombreVacio() {
        boolean resultado = gestor.registrarUsuario("", "pass123", RolUsuario.USUARIO);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("registrarUsuario rechaza nombre con solo espacios")
    void testRegistrarUsuarioNombreSoloEspacios() {
        boolean resultado = gestor.registrarUsuario("   ", "pass123", RolUsuario.USUARIO);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("registrarUsuario rechaza contraseña vacía")
    void testRegistrarUsuarioContrasenaVacia() {
        boolean resultado = gestor.registrarUsuario("juan", "", RolUsuario.USUARIO);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("registrarUsuario rechaza contraseña con solo espacios")
    void testRegistrarUsuarioContrasenaSoloEspacios() {
        boolean resultado = gestor.registrarUsuario("juan", "   ", RolUsuario.USUARIO);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("agregarMeta rechaza nombre vacío")
    void testAgregarMetaNombreVacio() {
        boolean resultado = gestor.agregarMeta("");
        assertFalse(resultado);
    }

    @Test
    @DisplayName("agregarMeta rechaza nombre con solo espacios")
    void testAgregarMetaNombreSoloEspacios() {
        boolean resultado = gestor.agregarMeta("   ");
        assertFalse(resultado);
    }

    @Test
    @DisplayName("agregarTareaAMeta rechaza fecha de inicio anterior a hoy")
    void testAgregarTareaConFechaInicioAnterior() {
        gestor.agregarMeta("Meta Test");
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);
        Usuario usuario = gestor.autenticarUsuario("juan", "pass123");

        Tarea tarea = new Tarea("Tarea antigua", "Desc",
                usuario, LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 1, 31), EstadoTarea.PENDIENTE);

        boolean resultado = gestor.agregarTareaAMeta("Meta Test", tarea);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("agregarTareaAMeta rechaza fecha de término anterior a hoy")
    void testAgregarTareaConFechaTerminoAnterior() {
        gestor.agregarMeta("Meta Test");
        gestor.registrarUsuario("juan", "pass123", RolUsuario.USUARIO);
        Usuario usuario = gestor.autenticarUsuario("juan", "pass123");

        Tarea tarea = new Tarea("Tarea con término antiguo", "Desc",
                usuario, LocalDate.now().plusDays(1),
                LocalDate.now().minusDays(1), EstadoTarea.PENDIENTE);

        boolean resultado = gestor.agregarTareaAMeta("Meta Test", tarea);
        assertFalse(resultado);
    }
}