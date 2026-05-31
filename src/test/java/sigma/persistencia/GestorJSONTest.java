package sigma.persistencia;

import org.junit.jupiter.api.*;
import sigma.modelo.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        File f1 = new File(RUTA_TEST_USUARIOS);
        File f2 = new File(RUTA_TEST_METAS);
        if (f1.exists()) f1.delete();
        if (f2.exists()) f2.delete();
    }

    @Test
    @DisplayName("Debería guardar y cargar una lista de usuarios correctamente")
    void testGuardarYCargarUsuarios() {
        ArrayList<Usuario> usuariosOriginal = new ArrayList<>();
        usuariosOriginal.add(new Usuario("juan", "pass123", RolUsuario.SUPERUSUARIO));
        usuariosOriginal.add(new Usuario("maria", "pass456", RolUsuario.USUARIO));
        usuariosOriginal.add(new Usuario("pedro", "pass789", RolUsuario.LIDER));

        gestorJSON.guardarUsuarios(usuariosOriginal, RUTA_TEST_USUARIOS);
        ArrayList<Usuario> usuariosCargados = gestorJSON.cargarUsuarios(RUTA_TEST_USUARIOS);

        assertNotNull(usuariosCargados);
        assertEquals(3, usuariosCargados.size());
        assertEquals("juan", usuariosCargados.get(0).getNombre());
        assertEquals(RolUsuario.SUPERUSUARIO, usuariosCargados.get(0).getRol());
    }

    @Test
    @DisplayName("Debería retornar lista vacía al cargar archivo de usuarios inexistente")
    void testCargarUsuariosArchivoInexistente() {
        ArrayList<Usuario> usuarios = gestorJSON.cargarUsuarios("archivo_que_no_existe.json");

        assertNotNull(usuarios);
        assertTrue(usuarios.isEmpty());
    }

    @Test
    @DisplayName("Debería guardar y cargar lista vacía de usuarios")
    void testGuardarYCargarUsuariosListaVacia() {
        ArrayList<Usuario> usuariosVacio = new ArrayList<>();

        gestorJSON.guardarUsuarios(usuariosVacio, RUTA_TEST_USUARIOS);
        ArrayList<Usuario> usuariosCargados = gestorJSON.cargarUsuarios(RUTA_TEST_USUARIOS);

        assertNotNull(usuariosCargados);
        assertTrue(usuariosCargados.isEmpty());
    }

    @Test
    @DisplayName("Debería guardar y cargar una meta sin tareas correctamente")
    void testGuardarYCargarMetaSinTareas() {
        ArrayList<Meta> metasOriginal = new ArrayList<>();
        Meta meta = new Meta("Proyecto SIGMA");
        metasOriginal.add(meta);

        gestorJSON.guardarMetas(metasOriginal, RUTA_TEST_METAS);
        ArrayList<Meta> metasCargadas = gestorJSON.cargarMetas(RUTA_TEST_METAS);

        assertNotNull(metasCargadas);
        assertEquals(1, metasCargadas.size());
        assertEquals("Proyecto SIGMA", metasCargadas.get(0).getNombre());
        assertTrue(metasCargadas.get(0).getTareas().isEmpty());
    }

    @Test
    @DisplayName("Debería guardar y cargar una meta con tareas correctamente")
    void testGuardarYCargarMetaConTareas() {
        ArrayList<Meta> metasOriginal = new ArrayList<>();
        Meta meta = new Meta("Proyecto SIGMA");

        Usuario usuario = new Usuario("felipe", "pass123", RolUsuario.USUARIO);
        Tarea tarea = new Tarea("Implementar tests", "Crear pruebas JUnit",
                usuario,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31),
                EstadoTarea.PENDIENTE);
        meta.agregarTarea(tarea);
        metasOriginal.add(meta);

        gestorJSON.guardarMetas(metasOriginal, RUTA_TEST_METAS);
        ArrayList<Meta> metasCargadas = gestorJSON.cargarMetas(RUTA_TEST_METAS);

        assertNotNull(metasCargadas);
        assertEquals(1, metasCargadas.size());
        assertEquals("Proyecto SIGMA", metasCargadas.get(0).getNombre());

        List<Tarea> tareasCargadas = metasCargadas.get(0).getTareas();
        assertEquals(1, tareasCargadas.size());
        assertEquals("Implementar tests", tareasCargadas.get(0).getTitulo());
        assertEquals(EstadoTarea.PENDIENTE, tareasCargadas.get(0).getEstado());
    }

    @Test
    @DisplayName("Debería retornar lista vacía al cargar archivo de metas inexistente")
    void testCargarMetasArchivoInexistente() {
        ArrayList<Meta> metas = gestorJSON.cargarMetas("archivo_que_no_existe.json");

        assertNotNull(metas);
        assertTrue(metas.isEmpty());
    }

    @Test
    @DisplayName("Debería guardar y cargar lista vacía de metas")
    void testGuardarYCargarMetasListaVacia() {
        ArrayList<Meta> metasVacio = new ArrayList<>();

        gestorJSON.guardarMetas(metasVacio, RUTA_TEST_METAS);
        ArrayList<Meta> metasCargadas = gestorJSON.cargarMetas(RUTA_TEST_METAS);

        assertNotNull(metasCargadas);
        assertTrue(metasCargadas.isEmpty());
    }

    @Test
    @DisplayName("Debería guardar múltiples metas y cargarlas correctamente")
    void testGuardarYCargarMultiplesMetas() {
        ArrayList<Meta> metasOriginal = new ArrayList<>();
        metasOriginal.add(new Meta("Meta 1"));
        metasOriginal.add(new Meta("Meta 2"));
        metasOriginal.add(new Meta("Meta 3"));

        gestorJSON.guardarMetas(metasOriginal, RUTA_TEST_METAS);
        ArrayList<Meta> metasCargadas = gestorJSON.cargarMetas(RUTA_TEST_METAS);

        assertEquals(3, metasCargadas.size());
        assertEquals("Meta 1", metasCargadas.get(0).getNombre());
        assertEquals("Meta 2", metasCargadas.get(1).getNombre());
        assertEquals("Meta 3", metasCargadas.get(2).getNombre());
    }

    @Test
    @DisplayName("Debería sobrescribir archivo al guardar nuevamente")
    void testSobrescrituraDeArchivo() {
        ArrayList<Meta> metasPrimera = new ArrayList<>();
        metasPrimera.add(new Meta("Meta original"));
        gestorJSON.guardarMetas(metasPrimera, RUTA_TEST_METAS);

        ArrayList<Meta> metasSegunda = new ArrayList<>();
        metasSegunda.add(new Meta("Meta nueva"));
        gestorJSON.guardarMetas(metasSegunda, RUTA_TEST_METAS);

        ArrayList<Meta> metasCargadas = gestorJSON.cargarMetas(RUTA_TEST_METAS);
        assertEquals(1, metasCargadas.size());
        assertEquals("Meta nueva", metasCargadas.get(0).getNombre());
    }

    @Test
    @DisplayName("Debería serializar y deserializar fechas LocalDate correctamente")
    void testSerializacionFechas() throws Exception {
        ArrayList<Meta> metasOriginal = new ArrayList<>();
        Meta meta = new Meta("Meta con fechas");

        Usuario usuario = new Usuario("user", "pass", RolUsuario.USUARIO);
        Tarea tarea = new Tarea("Tarea con fecha", "Descripcion",
                usuario,
                LocalDate.of(2025, 1, 15),
                LocalDate.of(2025, 12, 20),
                EstadoTarea.PENDIENTE);
        meta.agregarTarea(tarea);
        metasOriginal.add(meta);

        gestorJSON.guardarMetas(metasOriginal, RUTA_TEST_METAS);

        String contenido = new String(Files.readAllBytes(Paths.get(RUTA_TEST_METAS)));
        assertTrue(contenido.contains("2025-01-15"));
        assertTrue(contenido.contains("2025-12-20"));

        ArrayList<Meta> metasCargadas = gestorJSON.cargarMetas(RUTA_TEST_METAS);
        Tarea tareaCargada = metasCargadas.get(0).getTareas().get(0);

        assertEquals(LocalDate.of(2025, 1, 15), tareaCargada.getFechaInicio());
        assertEquals(LocalDate.of(2025, 12, 20), tareaCargada.getFechaTermino());
    }

    @Test
    @DisplayName("Debería serializar y deserializar enums correctamente")
    void testSerializacionEnum() throws Exception {
        ArrayList<Usuario> usuariosOriginal = new ArrayList<>();
        usuariosOriginal.add(new Usuario("juan", "pass123", RolUsuario.SUPERUSUARIO));
        usuariosOriginal.add(new Usuario("maria", "pass456", RolUsuario.LIDER));
        usuariosOriginal.add(new Usuario("pedro", "pass789", RolUsuario.USUARIO));

        gestorJSON.guardarUsuarios(usuariosOriginal, RUTA_TEST_USUARIOS);

        String contenido = new String(Files.readAllBytes(Paths.get(RUTA_TEST_USUARIOS)));
        assertTrue(contenido.contains("SUPERUSUARIO"));
        assertTrue(contenido.contains("LIDER"));
        assertTrue(contenido.contains("USUARIO"));

        ArrayList<Usuario> usuariosCargados = gestorJSON.cargarUsuarios(RUTA_TEST_USUARIOS);
        assertEquals(RolUsuario.SUPERUSUARIO, usuariosCargados.get(0).getRol());
        assertEquals(RolUsuario.LIDER, usuariosCargados.get(1).getRol());
        assertEquals(RolUsuario.USUARIO, usuariosCargados.get(2).getRol());
    }
}