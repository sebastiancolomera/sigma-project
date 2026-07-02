package sigma.modelo;

import java.util.ArrayList;
import java.util.List;

public class Meta {
    private String nombre;
    private List<Tarea> tareas;

    public Meta() {
        this.tareas = new ArrayList<>();
    }

    public Meta(String nombre) {
        this.nombre = nombre;
        this.tareas = new ArrayList<>();
    }

    public Meta(String nombre, ArrayList<Tarea> tareas) {
        this.nombre = nombre;
        this.tareas = tareas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Tarea> getTareas() {
        return new ArrayList<>(tareas);
    }

    public void setTareas(ArrayList<Tarea> tareas) {
        this.tareas = tareas;
    }

    public void agregarTarea(Tarea tarea) {
        this.tareas.add(tarea);
    }

    public void eliminarTarea(Tarea tarea) {
        this.tareas.remove(tarea);
    }

    public double calcularProgreso() {
        if (tareas.isEmpty()) return 0.0;
        int completadas = 0;
        for (Tarea tarea : tareas) {
            if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
                completadas++;
            }
        }
        return Math.round((double) completadas / tareas.size() * 100.0);
    }
}