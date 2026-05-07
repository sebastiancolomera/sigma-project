package modelo;

import java.util.ArrayList;

public class Meta {
    private String nombre;
    private ArrayList<Tarea> tareas;

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

    public ArrayList<Tarea> getTareas() {
        return tareas;
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

    public int calcularProgreso() {
        if (tareas.isEmpty()) {
            return 0;
        }

        int totalTareas = tareas.size();
        int tareasCompletadas = 0;

        for (int i = 0; i < tareas.size(); i++) {
            Tarea tareaActual = tareas.get(i);
            if (tareaActual.getEstado().equals("Completado")) {
                tareasCompletadas++;
            }
        }

        return (tareasCompletadas * 100) / totalTareas;
    }
}