package sigma.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TareaTest {

    private Usuario usuario;
    private LocalDate fechaInicio;
    private LocalDate fechaTermino;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Juan", "pass", RolUsuario.USUARIO);
        fechaInicio = LocalDate.of(2026, 4, 1);
        fechaTermino = LocalDate.of(2026, 4, 30);
    }

    @Test
    @DisplayName("Debería crear una tarea con todos los campos correctamente")
    void testConstructorConParametros() {
        Tarea tarea = new Tarea("Estudiar Java", "Completar pruebas unitarias",
                usuario, fechaInicio, fechaTermino, EstadoTarea.PENDIENTE);

        assertEquals("Estudiar Java", tarea.getTitulo());
        assertEquals("Completar pruebas unitarias", tarea.getDescripcion());
        assertEquals(usuario, tarea.getAsignado());
        assertEquals(fechaInicio, tarea.getFechaInicio());
        assertEquals(fechaTermino, tarea.getFechaTermino());
        assertEquals(EstadoTarea.PENDIENTE, tarea.getEstado());
    }

    @Test
    @DisplayName("Debería crear una tarea vacía con el constructor por defecto")
    void testConstructorPorDefecto() {
        Tarea tarea = new Tarea();

        assertNull(tarea.getTitulo());
        assertNull(tarea.getDescripcion());
        assertNull(tarea.getAsignado());
        assertNull(tarea.getFechaInicio());
        assertNull(tarea.getFechaTermino());
        assertNull(tarea.getEstado());
    }

    @Test
    @DisplayName("Debería poder modificar el título de la tarea")
    void testSetTitulo() {
        Tarea tarea = new Tarea();
        tarea.setTitulo("Nueva tarea");
        assertEquals("Nueva tarea", tarea.getTitulo());
    }

    @Test
    @DisplayName("Debería poder modificar la descripción de la tarea")
    void testSetDescripcion() {
        Tarea tarea = new Tarea();
        tarea.setDescripcion("Descripción detallada");
        assertEquals("Descripción detallada", tarea.getDescripcion());
    }

    @Test
    @DisplayName("Debería poder modificar el usuario asignado")
    void testSetAsignado() {
        Usuario otroUsuario = new Usuario("María", "pass", RolUsuario.LIDER);
        Tarea tarea = new Tarea();
        tarea.setAsignado(otroUsuario);
        assertEquals(otroUsuario, tarea.getAsignado());
    }

    @Test
    @DisplayName("Debería poder modificar las fechas")
    void testSetFechas() {
        Tarea tarea = new Tarea();
        LocalDate nuevaInicio = LocalDate.of(2026, 5, 1);
        LocalDate nuevaTermino = LocalDate.of(2026, 5, 31);

        tarea.setFechaInicio(nuevaInicio);
        tarea.setFechaTermino(nuevaTermino);

        assertEquals(nuevaInicio, tarea.getFechaInicio());
        assertEquals(nuevaTermino, tarea.getFechaTermino());
    }

    @Test
    @DisplayName("Debería poder modificar el estado de la tarea usando el enum")
    void testSetEstado() {
        Tarea tarea = new Tarea();
        tarea.setEstado(EstadoTarea.COMPLETADA);
        assertEquals(EstadoTarea.COMPLETADA, tarea.getEstado());

        tarea.setEstado(EstadoTarea.EN_PROCESO);
        assertEquals(EstadoTarea.EN_PROCESO, tarea.getEstado());

        tarea.setEstado(EstadoTarea.PENDIENTE);
        assertEquals(EstadoTarea.PENDIENTE, tarea.getEstado());
    }

    @Test
    @DisplayName("Debería poder obtener el estado como String")
    void testGetEstadoComoString() {
        Tarea tarea = new Tarea();
        tarea.setEstado(EstadoTarea.COMPLETADA);
        assertEquals("COMPLETADA", tarea.getEstado().toString());
    }

    @Test
    @DisplayName("Debería obtener el estado de entrega correctamente")
    void testGetEstadoEntrega() {
        Tarea tarea = new Tarea("Titulo", "Desc", usuario, fechaInicio, fechaTermino,
                EstadoTarea.PENDIENTE);
        assertNotNull(tarea.getEstadoEntrega());
    }
}