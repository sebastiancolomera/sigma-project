package sigma.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class MetaTest {

    private Usuario usuario;
    private LocalDate fechaInicio;
    private LocalDate fechaTermino;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Juan", "usuario", "pass");
        fechaInicio = LocalDate.of(2026, 4, 1);
        fechaTermino = LocalDate.of(2026, 4, 30);
    }

    @Test
    @DisplayName("Debería crear una meta vacía con constructor por defecto")
    void testConstructorPorDefecto() {
        Meta meta = new Meta();
        assertNull(meta.getNombre());
        assertNotNull(meta.getTareas());
        assertTrue(meta.getTareas().isEmpty());
    }

    @Test
    @DisplayName("Debería crear una meta con nombre pero sin tareas")
    void testConstructorConNombre() {
        Meta meta = new Meta("Aprender Java");
        assertEquals("Aprender Java", meta.getNombre());
        assertNotNull(meta.getTareas());
        assertTrue(meta.getTareas().isEmpty());
    }

    @Test
    @DisplayName("Debería crear una meta con nombre y lista de tareas")
    void testConstructorConNombreYTareas() {
        ArrayList<Tarea> tareas = new ArrayList<>();
        Tarea tarea1 = new Tarea("Tarea 1", "Desc 1", usuario, fechaInicio, fechaTermino, "Pendiente");
        tareas.add(tarea1);

        Meta meta = new Meta("Proyecto", tareas);
        assertEquals("Proyecto", meta.getNombre());
        assertEquals(1, meta.getTareas().size());
        assertEquals(tarea1, meta.getTareas().get(0));
    }

    @Test
    @DisplayName("Debería agregar una tarea correctamente")
    void testAgregarTarea() {
        Meta meta = new Meta("Meta");
        Tarea tarea = new Tarea("Tarea 1", "Desc", usuario, fechaInicio, fechaTermino, "Pendiente");

        meta.agregarTarea(tarea);

        assertEquals(1, meta.getTareas().size());
        assertEquals(tarea, meta.getTareas().get(0));
    }

    @Test
    @DisplayName("Debería eliminar una tarea correctamente")
    void testEliminarTarea() {
        Meta meta = new Meta("Meta");
        Tarea tarea1 = new Tarea("Tarea 1", "Desc1", usuario, fechaInicio, fechaTermino, "Pendiente");
        Tarea tarea2 = new Tarea("Tarea 2", "Desc2", usuario, fechaInicio, fechaTermino, "Pendiente");

        meta.agregarTarea(tarea1);
        meta.agregarTarea(tarea2);
        assertEquals(2, meta.getTareas().size());

        meta.eliminarTarea(tarea1);
        assertEquals(1, meta.getTareas().size());
        assertEquals(tarea2, meta.getTareas().get(0));
    }

    @Test
    @DisplayName("Debería poder modificar el nombre de la meta")
    void testSetNombre() {
        Meta meta = new Meta("Nombre original");
        assertEquals("Nombre original", meta.getNombre());

        meta.setNombre("Nuevo nombre");
        assertEquals("Nuevo nombre", meta.getNombre());
    }
}
