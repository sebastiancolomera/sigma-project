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
        usuario = new Usuario("Juan", "pass", RolUsuario.USUARIO);
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
        Tarea tarea1 = new Tarea("Tarea 1", "Desc 1", usuario, fechaInicio, fechaTermino, EstadoTarea.PENDIENTE);
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
        Tarea tarea = new Tarea("Tarea 1", "Desc", usuario, fechaInicio, fechaTermino, EstadoTarea.PENDIENTE);

        meta.agregarTarea(tarea);

        assertEquals(1, meta.getTareas().size());
        assertEquals(tarea, meta.getTareas().get(0));
    }

    @Test
    @DisplayName("Debería eliminar una tarea correctamente")
    void testEliminarTarea() {
        Meta meta = new Meta("Meta");
        Tarea tarea1 = new Tarea("Tarea 1", "Desc1", usuario, fechaInicio, fechaTermino, EstadoTarea.PENDIENTE);
        Tarea tarea2 = new Tarea("Tarea 2", "Desc2", usuario, fechaInicio, fechaTermino, EstadoTarea.PENDIENTE);

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

    @Test
    @DisplayName("Debería calcular 0% de progreso cuando no hay tareas")
    void testCalcularProgresoConListaVacia() {
        Meta meta = new Meta("Meta sin tareas");
        assertEquals(0, meta.calcularProgreso());
    }

    @Test
    @DisplayName("Debería calcular 0% de progreso cuando ninguna tarea está COMPLETADA")
    void testCalcularProgresoCeroPorciento() {
        Meta meta = new Meta("Meta");
        Tarea tarea1 = new Tarea("T1", "D1", usuario, fechaInicio, fechaTermino, EstadoTarea.PENDIENTE);
        Tarea tarea2 = new Tarea("T2", "D2", usuario, fechaInicio, fechaTermino, EstadoTarea.EN_PROCESO);

        meta.agregarTarea(tarea1);
        meta.agregarTarea(tarea2);

        assertEquals(0, meta.calcularProgreso());
    }

    @Test
    @DisplayName("Debería calcular 100% de progreso cuando todas las tareas están COMPLETADA")
    void testCalcularProgresoCienPorciento() {
        Meta meta = new Meta("Meta");
        Tarea tarea1 = new Tarea("T1", "D1", usuario, fechaInicio, fechaTermino, EstadoTarea.COMPLETADA);
        Tarea tarea2 = new Tarea("T2", "D2", usuario, fechaInicio, fechaTermino, EstadoTarea.COMPLETADA);

        meta.agregarTarea(tarea1);
        meta.agregarTarea(tarea2);

        assertEquals(100, meta.calcularProgreso());
    }

    @Test
    @DisplayName("Debería calcular progreso parcial correctamente")
    void testCalcularProgresoParcial() {
        Meta meta = new Meta("Meta");
        Tarea tarea1 = new Tarea("T1", "D1", usuario, fechaInicio, fechaTermino, EstadoTarea.COMPLETADA);
        Tarea tarea2 = new Tarea("T2", "D2", usuario, fechaInicio, fechaTermino, EstadoTarea.PENDIENTE);
        Tarea tarea3 = new Tarea("T3", "D3", usuario, fechaInicio, fechaTermino, EstadoTarea.COMPLETADA);
        Tarea tarea4 = new Tarea("T4", "D4", usuario, fechaInicio, fechaTermino, EstadoTarea.PENDIENTE);

        meta.agregarTarea(tarea1);
        meta.agregarTarea(tarea2);
        meta.agregarTarea(tarea3);
        meta.agregarTarea(tarea4);

        assertEquals(50, meta.calcularProgreso());
    }

    @Test
    @DisplayName("Debería ignorar estados diferentes a COMPLETADA en el cálculo")
    void testCalcularProgresoIgnoraOtrosEstados() {
        Meta meta = new Meta("Meta");
        Tarea tarea1 = new Tarea("T1", "D1", usuario, fechaInicio, fechaTermino, EstadoTarea.COMPLETADA);
        Tarea tarea2 = new Tarea("T2", "D2", usuario, fechaInicio, fechaTermino, EstadoTarea.POSTERGADA);
        Tarea tarea3 = new Tarea("T3", "D3", usuario, fechaInicio, fechaTermino, EstadoTarea.FUERA_DE_PLAZO);

        meta.agregarTarea(tarea1);
        meta.agregarTarea(tarea2);
        meta.agregarTarea(tarea3);

        // Solo 1 de 3 tareas COMPLETADA = 33% (redondea a 33)
        assertEquals(33, meta.calcularProgreso());
    }
}