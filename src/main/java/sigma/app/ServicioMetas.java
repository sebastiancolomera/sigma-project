package sigma.app;

import sigma.modelo.EstadoTarea;
import sigma.modelo.Meta;
import sigma.modelo.Tarea;
import sigma.modelo.Usuario;
import sigma.persistencia.GestorJSON;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicioMetas {

    private final GestorJSON gestorJSON;
    private final String rutaMetas;
    private List<Meta> metas;

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
        } catch (Exception e) {
            System.err.println("No se pudieron cargar las metas desde " + rutaMetas + ": " + e.getMessage());
        }
    }

    public boolean guardar() {
        try {
            return gestorJSON.guardarMetas(new ArrayList<>(metas), rutaMetas);
        } catch (Exception e) {
            System.err.println("No se pudieron guardar las metas en " + rutaMetas + ": " + e.getMessage());
            return false;
        }
    }

    public void resetear() {
        metas.clear();
        guardar();
    }

    public Meta buscarMeta(String nombre) {
        return metas.stream().filter(m -> m.getNombre().equals(nombre)).findFirst().orElse(null);
    }

    public boolean agregarMeta(String nombre) {
        if (nombre == null || nombre.isBlank()) return false;
        if (buscarMeta(nombre) != null) return false;
        metas.add(new Meta(nombre));
        guardar();
        return true;
    }

    public boolean eliminarMeta(String nombre) {
        boolean eliminado = metas.removeIf(m -> m.getNombre().equals(nombre));
        if (eliminado) guardar();
        return eliminado;
    }

    public boolean agregarTareaAMeta(String nombreMeta, Tarea tarea) {
        if (tarea == null || tarea.getTitulo() == null || tarea.getTitulo().isBlank()) return false;
        Meta meta = buscarMeta(nombreMeta);
        if (meta == null) return false;
        if (existeTareaConTitulo(tarea.getTitulo())) return false;
        meta.agregarTarea(tarea);
        guardar();
        return true;
    }


    public boolean existeTareaConTitulo(String titulo) {
        if (titulo == null) return false;
        String tituloNormalizado = titulo.trim();
        for (Meta meta : metas) {
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getTitulo() != null
                        && tarea.getTitulo().trim().equalsIgnoreCase(tituloNormalizado)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cambiarEstadoTarea(String titulo, EstadoTarea nuevo) {
        for (Meta meta : metas) {
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getTitulo().equals(titulo)) {
                    tarea.setEstado(nuevo);
                    guardar();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean actualizarFechasTarea(String titulo, LocalDate nuevaInicio, LocalDate nuevaTermino) {
        if (titulo == null || nuevaInicio == null || nuevaTermino == null) return false;
        if (nuevaTermino.isBefore(nuevaInicio)) return false;
        for (Meta meta : metas) {
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getTitulo().equals(titulo)) {
                    tarea.setFechaInicio(nuevaInicio);
                    tarea.setFechaTermino(nuevaTermino);
                    guardar();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean eliminarTareaDeMetaPorTitulo(String nombreMeta, String tituloTarea) {
        if (nombreMeta == null || tituloTarea == null) return false;

        Meta meta = buscarMeta(nombreMeta);
        if (meta == null) return false;

        Tarea objetivo = null;
        for (Tarea t : meta.getTareas()) {
            if (t.getTitulo().equals(tituloTarea)) {
                objetivo = t;
                break;
            }
        }

        if (objetivo == null) return false;

        meta.eliminarTarea(objetivo);
        guardar();
        return true;
    }


    public void actualizarEstadosVencidos() {
        LocalDate hoy = LocalDate.now();
        boolean huboCambios = false;

        for (Meta meta : metas) {
            for (Tarea tarea : meta.getTareas()) {
                if (tarea.getFechaTermino() != null
                        && hoy.isAfter(tarea.getFechaTermino())
                        && tarea.getEstado() != EstadoTarea.COMPLETADA) {
                    tarea.setEstado(EstadoTarea.FUERA_DE_PLAZO);
                    huboCambios = true;
                }
            }
        }

        if (huboCambios) guardar();
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
}