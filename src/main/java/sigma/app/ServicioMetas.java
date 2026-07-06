package sigma.app;

import sigma.modelo.EstadoTarea;
import sigma.modelo.EstadoEntrega;
import sigma.modelo.Meta;
import sigma.modelo.Tarea;
import sigma.modelo.Usuario;
import sigma.persistencia.GestorJSON;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicioMetas {

    private final GestorJSON gestorJSON;
    private final String rutaMetas;
    private List<Meta> metas;
    private boolean dirty;
    private final Map<String, Tarea> indiceTareas = new HashMap<>();

    public ServicioMetas(GestorJSON gestorJSON, String rutaMetas) {
        this.gestorJSON = gestorJSON;
        this.rutaMetas = rutaMetas;
        this.metas = new ArrayList<>();
    }

    public void cargar() {
        try {
            List<Meta> m = gestorJSON.cargarMetas(rutaMetas);
            if (m != null) {
                this.metas.addAll(m);
            }
            reindexar();
            migrarEstadoEntregaLegado();
        } catch (Exception e) {
            System.err.println("No se pudieron cargar las metas desde " + rutaMetas + ": " + e.getMessage());
        }
    }

    private void marcarDirty() {
        this.dirty = true;
    }

    private void reindexar() {
        indiceTareas.clear();
        for (Meta meta : metas) {
            for (Tarea t : meta.getTareas()) {
                if (t.getTitulo() != null) {
                    indiceTareas.put(t.getTitulo().trim().toLowerCase(), t);
                }
            }
        }
    }

    private void migrarEstadoEntregaLegado() {
        LocalDate hoy = LocalDate.now();
        boolean huboCambios = false;
        for (Meta meta : metas) {
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getEstadoEntrega() == null) {
                    if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
                        tarea.marcarEntregada(hoy);
                    } else {
                        calcularEstadoEntrega(tarea);
                    }
                    huboCambios = true;
                }
            }
        }
        if (huboCambios) guardar();
    }


    public boolean guardar() {
        if (!dirty) return true;
        try {
            boolean ok = gestorJSON.guardarMetas(new ArrayList<>(metas), rutaMetas);
            if (ok) dirty = false;
            return ok;
        } catch (Exception e) {
            System.err.println("No se pudieron guardar las metas en " + rutaMetas + ": " + e.getMessage());
            return false;
        }
    }

    public void resetear() {
        metas.clear();
        indiceTareas.clear();
        dirty = true;
        guardar();
    }

    public Meta buscarMeta(String nombre) {
        return metas.stream().filter(m -> m.getNombre().equals(nombre)).findFirst().orElse(null);
    }

    public boolean agregarMeta(String nombre) {
        if (nombre == null || nombre.isBlank()) return false;
        String nombreNormalizado = nombre.trim();
        if (buscarMeta(nombreNormalizado) != null) return false;
        metas.add(new Meta(nombreNormalizado));
        marcarDirty();
        return true;
    }

    public boolean eliminarMeta(String nombre) {
        Meta meta = buscarMeta(nombre);
        if (meta == null) return false;
        for (Tarea t : meta.getTareas()) {
            if (t.getTitulo() != null) {
                indiceTareas.remove(t.getTitulo().trim().toLowerCase());
            }
        }
        metas.remove(meta);
        marcarDirty();
        return true;
    }

    public boolean agregarTareaAMeta(String nombreMeta, Tarea tarea) {
        if (tarea == null || tarea.getTitulo() == null || tarea.getTitulo().isBlank()) return false;
        if (tarea.getFechaInicio() == null || !ValidadorFecha.esFechaNoAnteriorAHoy(tarea.getFechaInicio())) return false;
        if (tarea.getFechaTermino() == null || !ValidadorFecha.esFechaNoAnteriorAHoy(tarea.getFechaTermino())) return false;
        Meta meta = buscarMeta(nombreMeta);
        if (meta == null) return false;
        if (existeTareaConTitulo(tarea.getTitulo())) return false;
        meta.agregarTarea(tarea);
        indiceTareas.put(tarea.getTitulo().trim().toLowerCase(), tarea);
        marcarDirty();
        return true;
    }

    public ResultadoOperacion cambiarEstadoTarea(String titulo, EstadoTarea nuevo, Usuario ejecutor) {
        if (titulo == null || nuevo == null) {
            return new ResultadoOperacion(false, "Datos inválidos para cambiar el estado.");
        }
        Tarea tarea = indiceTareas.get(titulo.trim().toLowerCase());
        if (tarea == null) {
            return new ResultadoOperacion(false, "Tarea no encontrada.");
        }
        return aplicarCambioEstado(tarea, nuevo, ejecutor);
    }

    private ResultadoOperacion aplicarCambioEstado(Tarea tarea, EstadoTarea nuevo, Usuario ejecutor) {
        LocalDate hoy = LocalDate.now();

        // D-3: el líder solo puede cambiar el estado de sus propias tareas.
        // El superusuario (ejecutor == null o sin rol líder) no queda sujeto a esta restricción.
        if (ejecutor != null && ejecutor.esLider()
                && (tarea.getAsignado() == null
                || !tarea.getAsignado().getNombre().equals(ejecutor.getNombre()))) {
            return new ResultadoOperacion(false,
                    "No tienes permiso para cambiar el estado de una tarea que no te pertenece.");
        }

        if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
            return new ResultadoOperacion(false,
                    "La tarea ya ha sido completada y no puede modificarse.");
        }

        calcularEstadoEntrega(tarea);

        if (tarea.getEstadoEntrega() == EstadoEntrega.POSTERGADA) {
            return new ResultadoOperacion(false,
                    "No se puede cambiar el estado: la tarea aún no ha comenzado o ha sido postergada.");
        }

        tarea.setEstado(nuevo);
        if (nuevo == EstadoTarea.COMPLETADA) {
            tarea.marcarEntregada(hoy);
        }
        marcarDirty();
        return new ResultadoOperacion(true, "Estado de la tarea actualizado correctamente.");
    }

    public boolean existeTareaConTitulo(String titulo) {
        if (titulo == null) return false;
        return indiceTareas.containsKey(titulo.trim().toLowerCase());
    }

    public boolean actualizarFechasTarea(String titulo, LocalDate nuevaInicio, LocalDate nuevaTermino) {
        if (titulo == null || nuevaInicio == null || nuevaTermino == null) return false;
        if (nuevaTermino.isBefore(nuevaInicio)) return false;
        if (!ValidadorFecha.esFechaNoAnteriorAHoy(nuevaTermino)) return false;
        Tarea tarea = indiceTareas.get(titulo.trim().toLowerCase());
        if (tarea == null) return false;
        tarea.setFechaInicio(nuevaInicio);
        tarea.setFechaTermino(nuevaTermino);
        calcularEstadoEntrega(tarea);
        marcarDirty();
        return true;
    }

    public boolean eliminarTareaDeMetaPorTitulo(String nombreMeta, String tituloTarea) {
        if (nombreMeta == null || tituloTarea == null) return false;

        Meta meta = buscarMeta(nombreMeta);
        if (meta == null) return false;

        String clave = tituloTarea.trim().toLowerCase();
        Tarea objetivo = indiceTareas.get(clave);
        if (objetivo == null) return false;
        if (!meta.getTareas().contains(objetivo)) return false;

        meta.eliminarTarea(objetivo);
        indiceTareas.remove(clave);
        marcarDirty();
        return true;
    }


    public void actualizarEstadosVencidos() {
        boolean huboCambios = false;
        for (Meta meta : metas) {
            for (Tarea tarea : meta.getTareas()) {
                EstadoEntrega anterior = tarea.getEstadoEntrega();
                calcularEstadoEntrega(tarea);
                if(tarea.getEstadoEntrega() != anterior) {
                    huboCambios = true;
                }
            }
        }
        if (huboCambios) marcarDirty();
    }

    private void calcularEstadoEntrega(Tarea tarea) {
        tarea.recalcularEstadoEntregaPorFecha(LocalDate.now());
    }

    public List<Tarea> getTareasDeUsuario(Usuario u) {
        List<Tarea> resultado = new ArrayList<>();
        for (Meta meta : metas) {
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getAsignado() != null
                        && tarea.getAsignado().getNombre().equals(u.getNombre())) {
                    resultado.add(tarea);
                }
            }
        }
        return resultado;
    }

    public List<Meta> getMetas() {
        return new ArrayList<>(metas);
    }

    public int eliminarTareasDeUsuario(String nombreUsuario) {
        if (nombreUsuario == null) return 0;

        int eliminadas = 0;
        for (Meta meta : metas) {
            List<Tarea> aEliminar = new ArrayList<>();
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getAsignado() != null
                        && tarea.getAsignado().getNombre().equals(nombreUsuario)) {
                    aEliminar.add(tarea);
                }
            }
            for (Tarea tarea : aEliminar) {
                meta.eliminarTarea(tarea);
                if (tarea.getTitulo() != null) {
                    indiceTareas.remove(tarea.getTitulo().trim().toLowerCase());
                }
                eliminadas++;
            }
        }

        if (eliminadas > 0) marcarDirty();
        return eliminadas;
    }
}