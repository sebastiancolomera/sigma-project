package sigma.persistencia;

import org.junit.jupiter.api.*;
import sigma.modelo.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GestorJSONTest {

    private static final String RUTA_TEST_USUARIOS = "test_usuarios.json";
    private static final String RUTA_TEST_METAS = "test_metas.json";
    private GestorJSON gestorJSON;

    @BeforeEach
    void setUp() {
        gestorJSON = new GestorJSON();
    }

    @AfterEach
    void tearDown() {
        new File(RUTA_TEST_USUARIOS).delete();
        new File(RUTA_TEST_METAS).delete();
    }

    @Test
    void testGuardarYCargarUsuarios() throws Exception {
        List<Usuario> usuariosOriginal = new ArrayList<>();
        usuariosOriginal.add(new Usuario("juan", "pass123", "superusuario"));
        usuariosOriginal.add(new Usuario("maria", "pass456", "usuario"));

        gestorJSON.guardarUsuarios(usuariosOriginal, RUTA_TEST_USUARIOS);
        List<Usuario> usuariosCargados = gestorJSON.cargarUsuarios(RUTA_TEST_USUARIOS);

        assertNotNull(usuariosCargados);
        assertEquals(2, usuariosCargados.size());
        assertEquals("juan", usuariosCargados.get(0).getNombre());
    }

    @Test
    void testCargarArchivoInexistente() throws Exception {
        List<Usuario> usuarios = gestorJSON.cargarUsuarios("archivo_que_no_existe.json");
        assertNotNull(usuarios);
        assertTrue(usuarios.isEmpty());
    }

    @Test
    void testGuardarYCargarMetas() throws Exception {
        List<Meta> metasOriginal = new ArrayList<>();
        Meta meta = new Meta("Proyecto SIGMA");
        Tarea tarea = new Tarea("Implementar tests", "Crear pruebas JUnit",
                new Usuario("felipe", "pass", "usuario"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31),
                "Pendiente");
        meta.agregarTarea(tarea);
        metasOriginal.add(meta);

        gestorJSON.guardarMetas(metasOriginal, RUTA_TEST_METAS);
        List<Meta> metasCargadas = gestorJSON.cargarMetas(RUTA_TEST_METAS);

        assertNotNull(metasCargadas);
        assertEquals(1, metasCargadas.size());
        assertEquals("Proyecto SIGMA", metasCargadas.get(0).getNombre());
    }

    @Test
    void testSerializacionFechas() throws Exception {
        List<Meta> metasOriginal = new ArrayList<>();
        Meta meta = new Meta("Meta con fechas");
        Tarea tarea = new Tarea("Tarea con fecha", "Descripcion",
                new Usuario("user", "pass", "usuario"),
                LocalDate.of(2025, 1, 15),
                LocalDate.of(2025, 12, 20),
                "Pendiente");
        meta.agregarTarea(tarea);
        metasOriginal.add(meta);

        gestorJSON.guardarMetas(metasOriginal, RUTA_TEST_METAS);
        List<Meta> metasCargadas = gestorJSON.cargarMetas(RUTA_TEST_METAS);

        Tarea tareaCargada = metasCargadas.get(0).getTareas().get(0);
        assertEquals(LocalDate.of(2025, 1, 15), tareaCargada.getFechaInicio());
        assertEquals(LocalDate.of(2025, 12, 20), tareaCargada.getFechaTermino());
    }

    @Test
    void testListaVaciaGuardaYCarga() throws Exception {
        List<Meta> metasVacio = new ArrayList<>();
        gestorJSON.guardarMetas(metasVacio, RUTA_TEST_METAS);
        List<Meta> metasCargadas = gestorJSON.cargarMetas(RUTA_TEST_METAS);

        assertNotNull(metasCargadas);
        assertTrue(metasCargadas.isEmpty());
    }
}
