package sigma.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sigma.modelo.EstadoTarea;
import sigma.modelo.*;
import sigma.persistencia.GestorJSON;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicioMetasTest {

    @TempDir
    Path tempDir;

    private ServicioMetas servicio;
    private Usuario usuarioDePrueba;

    @BeforeEach
    void setUp() {
        servicio = new ServicioMetas(new GestorJSON(), tempDir.resolve("metas.json").toString());
        usuarioDePrueba = new Usuario("pedro", "hashDePrueba", RolUsuario.USUARIO);
    }

    @Test
    @DisplayName("Agregar una meta nueva tiene éxito")
    void testAgregarMetaExitosa() {
        assertTrue(servicio.agregarMeta("Meta 1"));
        assertEquals(1, servicio.getMetas().size());
    }

    @Test
    @DisplayName("Agregar una meta con nombre duplicado falla")
    void testAgregarMetaDuplicadaFalla() {
        servicio.agregarMeta("Meta 1");
        assertFalse(servicio.agregarMeta("Meta 1"));
        assertEquals(1, servicio.getMetas().size());
    }

    @Test
    @DisplayName("Eliminar una meta existente tiene éxito")
    void testEliminarMetaExistente() {
        servicio.agregarMeta("Meta 1");
        assertTrue(servicio.eliminarMeta("Meta 1"));
        assertTrue(servicio.getMetas().isEmpty());
    }

    @Test
    @DisplayName("Eliminar una meta inexistente falla")
    void testEliminarMetaInexistente() {
        assertFalse(servicio.eliminarMeta("Fantasma"));
    }

    @Test
    @DisplayName("buscarMeta encuentra una meta existente por nombre")
    void testBuscarMeta() {
        servicio.agregarMeta("Meta 1");
        Meta m = servicio.buscarMeta("Meta 1");
        assertNotNull(m);
        assertEquals("Meta 1", m.getNombre());
    }

    @Test
    @DisplayName("buscarMeta devuelve null para una meta inexistente")
    void testBuscarMetaInexistente() {
        assertNull(servicio.buscarMeta("Fantasma"));
    }

    @Test
    @DisplayName("Agregar una tarea a una meta existente tiene éxito")
    void testAgregarTareaAMeta() {
        servicio.agregarMeta("Meta 1");
        Tarea tarea = new Tarea("Tarea 1", "Descripción de la tarea 1", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().plusDays(5), EstadoTarea.PENDIENTE);

        assertTrue(servicio.agregarTareaAMeta("Meta 1", tarea));
        assertEquals(1, servicio.buscarMeta("Meta 1").getTareas().size());
    }

    @Test
    @DisplayName("Agregar una tarea a una meta inexistente falla")
    void testAgregarTareaAMetaInexistente() {
        Tarea tarea = new Tarea("Tarea 1", "Descripción de la tarea 1", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().plusDays(5), EstadoTarea.PENDIENTE);

        assertFalse(servicio.agregarTareaAMeta("Fantasma", tarea));
    }

    @Test
    @DisplayName("Cambiar el estado de una tarea existente tiene éxito")
    void testCambiarEstadoTarea() {
        servicio.agregarMeta("Meta 1");
        Tarea tarea = new Tarea("Tarea 1", "Descripción de la tarea 1", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().plusDays(5), EstadoTarea.PENDIENTE);
        servicio.agregarTareaAMeta("Meta 1", tarea);

        assertTrue(servicio.cambiarEstadoTarea("Tarea 1", EstadoTarea.COMPLETADA, null).isExito());
        assertEquals(EstadoTarea.COMPLETADA, tarea.getEstado());
    }

    @Test
    @DisplayName("Cambiar el estado de una tarea inexistente falla")
    void testCambiarEstadoTareaInexistente() {
        assertFalse(servicio.cambiarEstadoTarea("Fantasma", EstadoTarea.COMPLETADA, null).isExito());
    }

    @Test
    @DisplayName("getTareasDeUsuario devuelve solo las tareas asignadas a ese usuario")
    void testGetTareasDeUsuario() {
        servicio.agregarMeta("Meta 1");
        Usuario otro = new Usuario("ana", "hashDePrueba", RolUsuario.LIDER);

        Tarea tareaPedro = new Tarea("Tarea de Pedro", "Desc", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().plusDays(2), EstadoTarea.PENDIENTE);
        Tarea tareaAna = new Tarea("Tarea de Ana", "Desc", otro,
                LocalDate.now(), LocalDate.now().plusDays(2), EstadoTarea.PENDIENTE);

        servicio.agregarTareaAMeta("Meta 1", tareaPedro);
        servicio.agregarTareaAMeta("Meta 1", tareaAna);

        List<Tarea> tareasDePedro = servicio.getTareasDeUsuario(usuarioDePrueba);
        assertEquals(1, tareasDePedro.size());
        assertEquals("Tarea de Pedro", tareasDePedro.get(0).getTitulo());
    }

    @Test
    @DisplayName("El progreso de una meta refleja las tareas completadas")
    void testProgresoMetaDespuesDeCompletarTareas() {
        servicio.agregarMeta("Meta 1");
        Tarea t1 = new Tarea("Tarea 1", "Desc1", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().plusDays(2), EstadoTarea.PENDIENTE);
        Tarea t2 = new Tarea("Tarea 2", "Desc2", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().plusDays(2), EstadoTarea.PENDIENTE);
        servicio.agregarTareaAMeta("Meta 1", t1);
        servicio.agregarTareaAMeta("Meta 1", t2);

        assertEquals(0, servicio.buscarMeta("Meta 1").calcularProgreso());

        servicio.cambiarEstadoTarea("Tarea 1", EstadoTarea.COMPLETADA, null);

        assertEquals(50, servicio.buscarMeta("Meta 1").calcularProgreso());
    }

    @Test
    @DisplayName("getMetas() devuelve una copia defensiva")
    void testGetMetasEsCopiaDefensiva() {
        servicio.agregarMeta("Meta 1");
        List<Meta> copia = servicio.getMetas();
        copia.clear();
        assertEquals(1, servicio.getMetas().size());
    }

    @Test
    @DisplayName("Actualizar las fechas de una tarea existente con fechas válidas tiene éxito")
    void testActualizarFechasTareaExitosa() {
        servicio.agregarMeta("Meta 1");
        Tarea tarea = new Tarea("Tarea 1", "Descripción de la tarea 1", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().plusDays(5), EstadoTarea.PENDIENTE);
        servicio.agregarTareaAMeta("Meta 1", tarea);

        LocalDate nuevaInicio = LocalDate.now().plusDays(1);
        LocalDate nuevaTermino = LocalDate.now().plusDays(10);

        assertTrue(servicio.actualizarFechasTarea("Tarea 1", nuevaInicio, nuevaTermino));
        assertEquals(nuevaInicio, tarea.getFechaInicio());
        assertEquals(nuevaTermino, tarea.getFechaTermino());
    }

    @Test
    @DisplayName("Actualizar las fechas de una tarea inexistente falla")
    void testActualizarFechasTareaInexistente() {
        assertFalse(servicio.actualizarFechasTarea("Fantasma", LocalDate.now(), LocalDate.now().plusDays(1)));
    }

    @Test
    @DisplayName("Actualizar las fechas con fecha de término anterior a la de inicio falla")
    void testActualizarFechasTareaFechaInvalida() {
        servicio.agregarMeta("Meta 1");
        Tarea tarea = new Tarea("Tarea 1", "Descripción de la tarea 1", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().plusDays(5), EstadoTarea.PENDIENTE);
        servicio.agregarTareaAMeta("Meta 1", tarea);

        LocalDate inicioInvalido = LocalDate.now().plusDays(10);
        LocalDate terminoInvalido = LocalDate.now().plusDays(1);

        assertFalse(servicio.actualizarFechasTarea("Tarea 1", inicioInvalido, terminoInvalido));
    }

    @Test
    @DisplayName("Actualizar las fechas con parámetros nulos falla")
    void testActualizarFechasTareaConNulls() {
        servicio.agregarMeta("Meta 1");
        Tarea tarea = new Tarea("Tarea 1", "Descripción", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().plusDays(5), EstadoTarea.PENDIENTE);
        servicio.agregarTareaAMeta("Meta 1", tarea);

        assertFalse(servicio.actualizarFechasTarea(null, LocalDate.now(), LocalDate.now().plusDays(1)));
        assertFalse(servicio.actualizarFechasTarea("Tarea 1", null, LocalDate.now()));
        assertFalse(servicio.actualizarFechasTarea("Tarea 1", LocalDate.now(), null));
    }

    @Test
    @DisplayName("eliminarTareaDeMetaPorTitulo elimina una tarea existente correctamente")
    void testEliminarTareaDeMetaPorTituloExitosa() {
        servicio.agregarMeta("Meta Test");
        Usuario usuario = new Usuario("juan", "hash", RolUsuario.USUARIO);
        Tarea tarea = new Tarea("Tarea 1", "Descripcion", usuario,
                LocalDate.now(), LocalDate.now().plusDays(7), EstadoTarea.PENDIENTE);
        servicio.agregarTareaAMeta("Meta Test", tarea);

        assertEquals(1, servicio.buscarMeta("Meta Test").getTareas().size());

        boolean resultado = servicio.eliminarTareaDeMetaPorTitulo("Meta Test", "Tarea 1");

        assertTrue(resultado);
        assertEquals(0, servicio.buscarMeta("Meta Test").getTareas().size());
    }

    @Test
    @DisplayName("eliminarTareaDeMetaPorTitulo retorna false si la tarea no existe")
    void testEliminarTareaDeMetaPorTituloTareaInexistente() {
        servicio.agregarMeta("Meta Test");
        Usuario usuario = new Usuario("juan", "hash", RolUsuario.USUARIO);
        Tarea tarea = new Tarea("Tarea 1", "Descripcion", usuario,
                LocalDate.now(), LocalDate.now().plusDays(7), EstadoTarea.PENDIENTE);
        servicio.agregarTareaAMeta("Meta Test", tarea);

        boolean resultado = servicio.eliminarTareaDeMetaPorTitulo("Meta Test", "Tarea Inexistente");

        assertFalse(resultado);
        assertEquals(1, servicio.buscarMeta("Meta Test").getTareas().size());
    }

    @Test
    @DisplayName("eliminarTareaDeMetaPorTitulo retorna false si la meta no existe")
    void testEliminarTareaDeMetaPorTituloMetaInexistente() {
        boolean resultado = servicio.eliminarTareaDeMetaPorTitulo("Meta Inexistente", "Tarea 1");
        assertFalse(resultado);
    }

    @Test
    @DisplayName("actualizarEstadosVencidos marca como FUERA_DE_PLAZO las tareas vencidas no completadas")
    void testActualizarEstadosVencidosMarcaTareaVencida() {
        // agregarTareaAMeta() ya no permite crear tareas con fechas pasadas (D-2/D-3),
        // así que para simular una tarea que era válida al crearse pero venció con el
        // paso del tiempo, sembramos los datos directamente vía GestorJSON y los
        // cargamos con servicio.cargar(), tal como ocurre con datos persistidos reales.
        sembrarMetaConTarea("Meta 1", "Tarea vencida",
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(1), EstadoTarea.PENDIENTE);

        servicio.cargar();
        servicio.actualizarEstadosVencidos();

        Tarea tareaCargada = servicio.buscarMeta("Meta 1").getTareas().get(0);
        assertEquals(EstadoEntrega.FUERA_DE_PLAZO, tareaCargada.getEstadoEntrega());
    }

    @Test
    @DisplayName("actualizarEstadosVencidos no modifica tareas completadas aunque estén vencidas")
    void testActualizarEstadosVencidosNoTocaCompletadas() {
        sembrarMetaConTarea("Meta 1", "Tarea completada",
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(1), EstadoTarea.COMPLETADA);

        servicio.cargar();
        servicio.actualizarEstadosVencidos();

        Tarea tareaCargada = servicio.buscarMeta("Meta 1").getTareas().get(0);
        assertEquals(EstadoTarea.COMPLETADA, tareaCargada.getEstado());
        assertNotEquals(EstadoEntrega.FUERA_DE_PLAZO, tareaCargada.getEstadoEntrega());
    }

    /**
     * Guarda directamente en el archivo JSON de metas una meta con una tarea,
     * saltándose las validaciones de agregarTareaAMeta(). Simula datos que
     * fueron válidos al persistirse (por ejemplo, en una sesión anterior) pero
     * cuyas fechas ya pasaron al momento de ejecutar el test.
     */
    private void sembrarMetaConTarea(String nombreMeta, String tituloTarea,
                                     LocalDate fechaInicio, LocalDate fechaTermino, EstadoTarea estado) {
        Meta meta = new Meta(nombreMeta);
        Tarea tarea = new Tarea(tituloTarea, "Descripción", usuarioDePrueba,
                fechaInicio, fechaTermino, estado);
        meta.agregarTarea(tarea);

        ArrayList<Meta> metas = new ArrayList<>();
        metas.add(meta);
        new GestorJSON().guardarMetas(metas, tempDir.resolve("metas.json").toString());
    }

    @Test
    @DisplayName("actualizarEstadosVencidos no modifica tareas cuya fecha de término aún no ha pasado")
    void testActualizarEstadosVencidosNoTocaTareasVigentes() {
        servicio.agregarMeta("Meta 1");
        Tarea tarea = new Tarea("Tarea vigente", "Descripción", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().plusDays(5), EstadoTarea.PENDIENTE);
        servicio.agregarTareaAMeta("Meta 1", tarea);

        servicio.actualizarEstadosVencidos();

        assertEquals(EstadoTarea.PENDIENTE, tarea.getEstado());
    }

    @Test
    @DisplayName("Agregar una meta con nombre vacío falla y no queda agregada")
    void testAgregarMetaNombreVacioFalla() {
        assertFalse(servicio.agregarMeta(""));
        assertTrue(servicio.getMetas().isEmpty());
    }

    @Test
    @DisplayName("Agregar una meta con nombre compuesto solo por espacios falla y no queda agregada")
    void testAgregarMetaNombreSoloEspaciosFalla() {
        assertFalse(servicio.agregarMeta("   "));
        assertTrue(servicio.getMetas().isEmpty());
    }

    @Test
    @DisplayName("Agregar una meta con nombre null falla y no queda agregada")
    void testAgregarMetaNombreNullFalla() {
        assertFalse(servicio.agregarMeta(null));
        assertTrue(servicio.getMetas().isEmpty());
    }

    @Test
    @DisplayName("Agregar una tarea con fecha de inicio anterior a hoy falla y no queda agregada")
    void testAgregarTareaFechaInicioAnteriorAHoyFalla() {
        servicio.agregarMeta("Meta 1");
        Tarea tarea = new Tarea("Tarea con inicio pasado", "Descripción", usuarioDePrueba,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), EstadoTarea.PENDIENTE);

        assertFalse(servicio.agregarTareaAMeta("Meta 1", tarea));
        assertEquals(0, servicio.buscarMeta("Meta 1").getTareas().size());
    }

    @Test
    @DisplayName("Agregar una tarea con fecha de término anterior a hoy falla y no queda agregada")
    void testAgregarTareaFechaTerminoAnteriorAHoyFalla() {
        servicio.agregarMeta("Meta 1");
        Tarea tarea = new Tarea("Tarea con término pasado", "Descripción", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().minusDays(1), EstadoTarea.PENDIENTE);

        assertFalse(servicio.agregarTareaAMeta("Meta 1", tarea));
        assertEquals(0, servicio.buscarMeta("Meta 1").getTareas().size());
    }

    @Test
    @DisplayName("Agregar una tarea con fecha de inicio y término iguales a hoy tiene éxito")
    void testAgregarTareaFechasIgualesAHoyExitosa() {
        servicio.agregarMeta("Meta 1");
        Tarea tarea = new Tarea("Tarea con fechas de hoy", "Descripción", usuarioDePrueba,
                LocalDate.now(), LocalDate.now(), EstadoTarea.PENDIENTE);

        assertTrue(servicio.agregarTareaAMeta("Meta 1", tarea));
        assertEquals(1, servicio.buscarMeta("Meta 1").getTareas().size());
    }

    @Test
    @DisplayName("Actualizar las fechas con nueva fecha de término anterior a hoy falla")
    void testActualizarFechasTareaTerminoAnteriorAHoyFalla() {
        servicio.agregarMeta("Meta 1");
        Tarea tarea = new Tarea("Tarea 1", "Descripción de la tarea 1", usuarioDePrueba,
                LocalDate.now(), LocalDate.now().plusDays(5), EstadoTarea.PENDIENTE);
        servicio.agregarTareaAMeta("Meta 1", tarea);

        LocalDate nuevaInicio = LocalDate.now().minusDays(5);
        LocalDate nuevaTerminoAnteriorAHoy = LocalDate.now().minusDays(2);

        assertFalse(servicio.actualizarFechasTarea("Tarea 1", nuevaInicio, nuevaTerminoAnteriorAHoy));
        assertEquals(LocalDate.now(), tarea.getFechaInicio());
        assertEquals(LocalDate.now().plusDays(5), tarea.getFechaTermino());
    }

    @Test
    @DisplayName("esFechaNoAnteriorAHoy retorna false para una fecha pasada")
    void testEsFechaNoAnteriorAHoyConFechaPasada() {
        assertFalse(ValidadorFecha.esFechaNoAnteriorAHoy(LocalDate.now().minusDays(1)));
    }

    @Test
    @DisplayName("esFechaNoAnteriorAHoy retorna true para la fecha de hoy")
    void testEsFechaNoAnteriorAHoyConFechaDeHoy() {
        assertTrue(ValidadorFecha.esFechaNoAnteriorAHoy(LocalDate.now()));
    }

    @Test
    @DisplayName("esFechaNoAnteriorAHoy retorna true para una fecha futura")
    void testEsFechaNoAnteriorAHoyConFechaFutura() {
        assertTrue(ValidadorFecha.esFechaNoAnteriorAHoy(LocalDate.now().plusDays(1)));
    }
}